package omtteam.openmodularturrets.blockentity.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.block.misc.GeneratorBlock;
import omtteam.openmodularturrets.block.misc.InventoryExpanderBlock;
import omtteam.omlib.blockentity.TransferHost;
import omtteam.omlib.compat.CompatBlockEntity;
import omtteam.omlib.compat.VersionCompat;
import omtteam.omlib.compat.nbt.NbtCompat;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.omlib.core.energy.EnergyAccess;
import omtteam.omlib.core.energy.EnergyStore;
import omtteam.openmodularturrets.core.generator.GeneratorLogic;
import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;
import omtteam.openmodularturrets.core.storage.SlotCapacity;
import omtteam.omlib.inventory.ItemAccess;
import omtteam.openmodularturrets.inventory.FuelContainer;
import omtteam.openmodularturrets.item.GeneratorUpgradeItem;
import omtteam.openmodularturrets.item.ModuleToggles;
import omtteam.omlib.platform.Platform;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.IntStream;

/**
 * Generator (new block, not in the 1.12.2 version): burns furnace fuel into energy and pushes it into all neighbouring energy
 * storages (turret bases, cables). One burn tick of a fuel produces {@code generatorEnergyPerTick}. Burning pauses
 * while the buffer is full, so no fuel is wasted. Its {@value #FUEL_SLOTS} fuel slots let it run for a long time
 * where there is no other power; attached inventory expanders let each slot hold more. Energy multiplier upgrades in its upgrade slot make it burn faster
 * ({@link GeneratorLogic#multiplier}).
 */
public class GeneratorBlockEntity extends CompatBlockEntity implements TransferHost, Clearable {
    public static final int FUEL_SLOTS = 9;

    private final FuelContainer fuel = new FuelContainer(FUEL_SLOTS, stack -> isFuel(level, stack),
            GeneratorBlockEntity::leavesRemainder, this::getSlotCapacity);
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
    /** Tiers of the inventory expanders on all faces; {@code null} until the neighbours were looked at. */
    @Nullable
    private List<Integer> expanderTiers;
    private final GeneratorLogic logic = new GeneratorLogic();
    /** The fuel slots for {@link GeneratorLogic#burn}, in container order. */
    private final List<GeneratorLogic.FuelSlot> fuelSlots = IntStream.range(0, FUEL_SLOTS)
            .<GeneratorLogic.FuelSlot>mapToObj(slot -> () -> consumeFuel(slot))
            .toList();
    /** Hoppers and pipes may only insert fuel and only extract leftovers (empty buckets). */
    private final ItemAccess itemAccess = new ItemAccess(fuel, 0, FUEL_SLOTS,
            stack -> isFuel(level, stack), stack -> !isFuel(level, stack));

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

    /** Whether the stack is furnace fuel; the level holds the fuel values (nothing is fuel while there is none). */
    public static boolean isFuel(@Nullable Level level, ItemStack stack) {
        return level != null && Platform.fuel().burnTime(level, stack) > 0;
    }

    /** Fuel with a remainder (lava bucket → bucket) is not stacked beyond its max stack size ({@link FuelContainer}). */
    public static boolean leavesRemainder(ItemStack stack) {
        return !Platform.fuel().remainder(stack).isEmpty();
    }

    public FuelContainer getFuelContainer() {
        return fuel;
    }

    public SimpleContainer getUpgradeContainer() {
        return upgrades;
    }

    /**
     * Empties fuel and upgrade slots. Whoever moves or replaces the block after saving its data ({@code /setblock},
     * Sable contraptions) calls this first, so {@link #onBlockRemoved} does not drop a second copy of the items.
     */
    @Override
    public void clearContent() {
        fuel.clearContent();
        upgrades.clearContent();
    }

    @Override
    protected void onBlockRemoved(Level level) {
        Containers.dropContents(level, worldPosition, fuel);
        Containers.dropContents(level, worldPosition, upgrades);
    }

    /** What the attached inventory expanders add to every fuel slot (the config in effect, see {@link SlotCapacity}). */
    public SlotCapacity getSlotCapacity() {
        List<Integer> tiers = expanderTiers;
        return tiers == null || tiers.isEmpty() ? SlotCapacity.NONE
                : SlotCapacity.of(tiers, ConfigLogicSettings.INSTANCE::expanderStacks);
    }

    /** Called by the block when a neighbour changed: attached expanders. */
    public void onNeighbourChanged() {
        if (level == null || level.isClientSide()) return;
        scanExpanders(level);
    }

    /**
     * Collects the plates of the expanders next to every face ({@link InventoryExpanderBlock#plateTiers}); waits while
     * a neighbour is not loaded, so an expander in a loading chunk does not shrink the slots for a moment.
     */
    private void scanExpanders(Level level) {
        List<Integer> tiers = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            BlockPos neighbour = worldPosition.relative(direction);
            if (!level.isLoaded(neighbour)) return;
            tiers.addAll(InventoryExpanderBlock.plateTiers(level.getBlockState(neighbour)));
        }
        expanderTiers = List.copyOf(tiers);
    }

    /** Number of energy multiplier upgrades in the upgrade slot; 0 while the upgrade is disabled in the config. */
    public int getMultiplierUpgrades() {
        ItemStack stack = upgrades.getItem(0);
        return stack.getItem() instanceof GeneratorUpgradeItem upgrade
                && upgrade.getUpgradeType() == GeneratorUpgradeType.ENERGY_MULTIPLIER
                && ModuleToggles.isEnabled(upgrade.getUpgradeType()) ? stack.getCount() : 0;
    }

    /** Energy as exposed to automation on all sides (extract only). */
    @Override
    public EnergyAccess getEnergyAccess(@Nullable Direction side) {
        return energy;
    }

    /** The fuel slots as exposed to automation on all sides (the loader adapters wrap it). */
    @Override
    public ItemAccess getItemAccess(@Nullable Direction side) {
        return itemAccess;
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
        if (generator.expanderTiers == null) generator.scanExpanders(level);
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
        if (level == null) return 0;
        int ticks = Platform.fuel().burnTime(level, stack);
        if (ticks <= 0) return 0;
        ItemStack remainder = Platform.fuel().remainder(stack);
        stack.shrink(1);
        if (stack.isEmpty() && !remainder.isEmpty()) {
            fuel.setItem(slot, remainder);
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
        VersionCompat.loadItems(NbtCompat.getCompound(tag, "Upgrades"), upgradeItems, registries);
        upgrades.setItem(0, upgradeItems.get(0));
        energy.setAmountUnchecked(Math.min(energy.getCapacity(), NbtCompat.getLong(tag, "Energy", 0)));
        logic.load(NbtCompat.getInt(tag, "BurnTime", 0), NbtCompat.getInt(tag, "BurnDuration", 0));
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
