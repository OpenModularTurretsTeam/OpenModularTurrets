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
import omtteam.openmodularturrets.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

/**
 * Generator (new block, not in oldmod): burns furnace fuel into energy and pushes it into all neighbouring energy
 * storages (turret bases, cables). One burn tick of a fuel produces {@code generatorEnergyPerTick}. Burning pauses
 * while the buffer is full, so no fuel is wasted.
 */
public class GeneratorBlockEntity extends CompatBlockEntity {
    private final SimpleContainer fuel = new SimpleContainer(1) {
        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return isFuel(stack.getItem());
        }
    };
    private final SimpleEnergyStorage energy;
    private int burnTime;
    private int burnDuration;
    @Nullable
    private Storage<ItemVariant> itemStorage;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GENERATOR, pos, state);
        ConfigData.Miscellaneous config = OMTConfig.get().miscellaneous;
        energy = new SimpleEnergyStorage(config.generatorCapacity, 0, config.generatorMaxOutput) {
            @Override
            protected void onFinalCommit() {
                setChanged();
            }
        };
        fuel.addListener(container -> setChanged());
    }

    public static boolean isFuel(Item item) {
        Integer ticks = FuelRegistry.INSTANCE.get(item);
        return ticks != null && ticks > 0;
    }

    public SimpleContainer getFuelContainer() {
        return fuel;
    }

    public EnergyStorage getEnergyStorage() {
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
        return energy.amount;
    }

    public long getCapacity() {
        return energy.capacity;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getBurnDuration() {
        return burnDuration;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GeneratorBlockEntity generator) {
        generator.pushEnergy(level, pos);
        boolean lit = generator.burn();
        if (state.getValue(GeneratorBlock.LIT) != lit) {
            level.setBlock(pos, state.setValue(GeneratorBlock.LIT, lit), Block.UPDATE_ALL);
        }
    }

    /** Burns one tick of fuel if the buffer has room; returns whether it burned. */
    private boolean burn() {
        if (energy.amount >= energy.capacity) return false;
        if (burnTime <= 0 && !consumeFuel()) return false;
        burnTime--;
        energy.amount = Math.min(energy.capacity, energy.amount + OMTConfig.get().miscellaneous.generatorEnergyPerTick);
        setChanged();
        return true;
    }

    /** Starts burning the next fuel item, leaving its remainder (lava bucket → bucket) like a furnace. */
    private boolean consumeFuel() {
        ItemStack stack = fuel.getItem(0);
        if (stack.isEmpty() || !isFuel(stack.getItem())) return false;
        Item remainder = stack.getItem().getCraftingRemainingItem();
        burnDuration = burnTime = FuelRegistry.INSTANCE.get(stack.getItem());
        stack.shrink(1);
        if (stack.isEmpty() && remainder != null) {
            fuel.setItem(0, new ItemStack(remainder));
        } else {
            fuel.setChanged();
        }
        return true;
    }

    /** Pushes up to {@code generatorMaxOutput} per tick into all neighbours together. */
    private void pushEnergy(Level level, BlockPos pos) {
        long remaining = energy.maxExtract;
        for (Direction direction : Direction.values()) {
            if (remaining <= 0 || energy.amount <= 0) return;
            EnergyStorage target = EnergyStorage.SIDED.find(level, pos.relative(direction), direction.getOpposite());
            remaining -= EnergyStorageUtil.move(energy, target, remaining, null);
        }
    }

    @Override
    protected void readData(CompoundTag tag, HolderLookup.Provider registries) {
        NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
        VersionCompat.loadItems(tag, items, registries);
        fuel.setItem(0, items.get(0));
        energy.amount = Math.min(energy.capacity, tag.getLong("Energy"));
        burnTime = tag.getInt("BurnTime");
        burnDuration = tag.getInt("BurnDuration");
    }

    @Override
    protected void writeData(CompoundTag tag, HolderLookup.Provider registries) {
        VersionCompat.saveItems(tag, NonNullList.of(ItemStack.EMPTY, fuel.getItem(0)), registries);
        tag.putLong("Energy", energy.amount);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("BurnDuration", burnDuration);
    }

    /** The GUI gets its values from the menu and the flame from the block state. */
    @Override
    protected void writeClientData(CompoundTag tag, HolderLookup.Provider registries) {
    }
}
