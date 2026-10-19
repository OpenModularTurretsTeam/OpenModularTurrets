package omtteam.openmodularturrets.blockentity.misc;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.block.misc.GeneratorBlock;
import omtteam.openmodularturrets.compat.CompatBlockEntity;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.energy.EnergyAccess;
import omtteam.openmodularturrets.core.energy.EnergyStore;
import omtteam.openmodularturrets.core.generator.GeneratorLogic;
import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;
import omtteam.openmodularturrets.item.GeneratorUpgradeItem;
import omtteam.openmodularturrets.platform.Platform;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.OptionalLong;
import java.util.stream.IntStream;

/**
 * Generator (new block, not in the 1.12.2 version): burns furnace fuel into energy and pushes it into all neighbouring energy
 * storages (turret bases, cables). One burn tick of a fuel produces {@code generatorEnergyPerTick}. Burning pauses
 * while the buffer is full, so no fuel is wasted. Its {@value #FUEL_SLOTS} fuel slots let it run for a long time
 * where there is no other power. Energy multiplier upgrades in its upgrade slot make it burn faster
 * ({@link GeneratorLogic#multiplier}).
 */
public class GeneratorBlockEntity extends CompatBlockEntity {
    public static final int FUEL_SLOTS = 9;

    private final SimpleContainer fuel = new SimpleContainer(FUEL_SLOTS) {
        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return isFuel(stack.getItem());
        }
    };
    /** One upgrade slot holding up to 4 generator upgrades. */
    private final SimpleContainer upgrades = new SimpleContainer(1) {
        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return isUpgrade(stack);
        }

        @Override
        public int getMaxStackSize() {
            return GeneratorUpgradeType.ENERGY_MULTIPLIER.maxPerSlot();
        }
    };
    private final EnergyStore energy;
    private final GeneratorLogic logic = new GeneratorLogic();
    /** The fuel slots for {@link GeneratorLogic#burn}, in container order. */
    private final List<GeneratorLogic.FuelSlot> fuelSlots = IntStream.range(0, FUEL_SLOTS)
            .<GeneratorLogic.FuelSlot>mapToObj(slot -> () -> consumeFuel(slot))
            .toList();
    @Nullable
    private Storage<ItemVariant> itemStorage;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GENERATOR, pos, state);
        ConfigData.Miscellaneous config = OMTConfig.get().miscellaneous;
        energy = new EnergyStore(config.generatorCapacity, 0, config.generatorMaxOutput, this::setChanged);
        fuel.addListener(container -> setChanged());
        upgrades.addListener(container -> setChanged());
    }

    public static boolean isUpgrade(ItemStack stack) {
        return stack.getItem() instanceof GeneratorUpgradeItem;
    }

    public static boolean isFuel(Item item) {
        Integer ticks = FuelRegistry.INSTANCE.get(item);
        return ticks != null && ticks > 0;
    }

    public SimpleContainer getFuelContainer() {
        return fuel;
    }

    public SimpleContainer getUpgradeContainer() {
        return upgrades;
    }

    /** Number of energy multiplier upgrades in the upgrade slot. */
    public int getMultiplierUpgrades() {
        ItemStack stack = upgrades.getItem(0);
        return stack.getItem() instanceof GeneratorUpgradeItem upgrade
                && upgrade.getUpgradeType() == GeneratorUpgradeType.ENERGY_MULTIPLIER ? stack.getCount() : 0;
    }

    /** Energy as exposed to automation on all sides (extract only). */
    public EnergyAccess getEnergyAccess(@Nullable Direction side) {
        return energy;
    }

    /** Hoppers and pipes may only insert fuel and only extract leftovers (empty buckets). */
    public Storage<ItemVariant> getItemStorage() {
        if (itemStorage == null) {
            itemStorage = new FilteringStorage<>(InventoryStorage.of(fuel, null)) {
                @Override
                protected boolean canInsert(ItemVariant resource) {
                    return isFuel(resource.getItem());
                }

                @Override
                protected boolean canExtract(ItemVariant resource) {
                    return !isFuel(resource.getItem());
                }
            };
        }
        return itemStorage;
    }

    public long getEnergy() {
        return energy.getAmount();
    }

    public long getCapacity() {
        return energy.getCapacity();
    }

    public int getBurnTime() {
        return logic.getBurnTime();
    }

    public int getBurnDuration() {
        return logic.getBurnDuration();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GeneratorBlockEntity generator) {
        generator.pushEnergy(level, pos);
        boolean lit = generator.burn();
        if (state.getValue(GeneratorBlock.LIT) != lit) {
            level.setBlock(pos, state.setValue(GeneratorBlock.LIT, lit), Block.UPDATE_ALL);
        }
    }

    /** Burns one tick of fuel ({@link GeneratorLogic#burn}); returns whether it burned. */
    private boolean burn() {
        OptionalLong produced = logic.burn(energy.getFreeSpace(),
                OMTConfig.get().miscellaneous.generatorEnergyPerTick,
                GeneratorLogic.multiplier(getMultiplierUpgrades()), fuelSlots);
        if (produced.isEmpty()) return false;
        energy.generate(produced.getAsLong());
        setChanged(); // the burn time changed, even if nothing was produced
        return true;
    }

    /**
     * Removes the next fuel item from a slot, leaving its remainder (lava bucket → bucket) like a furnace.
     *
     * @return its burn time, 0 if the slot holds no fuel
     */
    private int consumeFuel(int slot) {
        ItemStack stack = fuel.getItem(slot);
        if (stack.isEmpty() || !isFuel(stack.getItem())) return 0;
        Item remainder = stack.getItem().getCraftingRemainingItem();
        int ticks = FuelRegistry.INSTANCE.get(stack.getItem());
        stack.shrink(1);
        if (stack.isEmpty() && remainder != null) {
            fuel.setItem(slot, new ItemStack(remainder));
        } else {
            fuel.setChanged();
        }
        return ticks;
    }

    /** Pushes up to {@code generatorMaxOutput} per tick into all neighbours together. */
    private void pushEnergy(Level level, BlockPos pos) {
        long remaining = energy.getMaxExtract();
        for (Direction direction : Direction.values()) {
            if (remaining <= 0 || energy.getAmount() <= 0) return;
            remaining -= Platform.energy().pushToBlock(level, pos.relative(direction), direction.getOpposite(), energy, remaining);
        }
    }

    @Override
    protected void readData(CompoundTag tag, HolderLookup.Provider registries) {
        // saves from before the 9 slots hold slot 0 only, which still loads into the first slot
        NonNullList<ItemStack> items = NonNullList.withSize(FUEL_SLOTS, ItemStack.EMPTY);
        VersionCompat.loadItems(tag, items, registries);
        for (int slot = 0; slot < FUEL_SLOTS; slot++) {
            fuel.setItem(slot, items.get(slot));
        }
        NonNullList<ItemStack> upgradeItems = NonNullList.withSize(1, ItemStack.EMPTY);
        VersionCompat.loadItems(tag.getCompound("Upgrades"), upgradeItems, registries);
        upgrades.setItem(0, upgradeItems.get(0));
        energy.setAmountUnchecked(Math.min(energy.getCapacity(), tag.getLong("Energy")));
        logic.load(tag.getInt("BurnTime"), tag.getInt("BurnDuration"));
    }

    @Override
    protected void writeData(CompoundTag tag, HolderLookup.Provider registries) {
        NonNullList<ItemStack> items = NonNullList.withSize(FUEL_SLOTS, ItemStack.EMPTY);
        for (int slot = 0; slot < FUEL_SLOTS; slot++) {
            items.set(slot, fuel.getItem(slot));
        }
        VersionCompat.saveItems(tag, items, registries);
        CompoundTag upgradeTag = new CompoundTag();
        VersionCompat.saveItems(upgradeTag, NonNullList.of(ItemStack.EMPTY, upgrades.getItem(0)), registries);
        tag.put("Upgrades", upgradeTag);
        tag.putLong("Energy", energy.getAmount());
        tag.putInt("BurnTime", logic.getBurnTime());
        tag.putInt("BurnDuration", logic.getBurnDuration());
    }

    /** The GUI gets its values from the menu and the flame from the block state. */
    @Override
    protected void writeClientData(CompoundTag tag, HolderLookup.Provider registries) {
    }
}
