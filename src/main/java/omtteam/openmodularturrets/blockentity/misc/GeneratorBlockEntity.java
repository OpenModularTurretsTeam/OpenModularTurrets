package omtteam.openmodularturrets.blockentity.misc;

import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.block.misc.GeneratorBlock;
import omtteam.openmodularturrets.compat.CompatBlockEntity;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.Optional;

/**
 * Generator (new block, not in oldmod): burns furnace fuel into energy and pushes it into all neighbouring energy
 * storages (turret bases, cables). One burn tick of a fuel produces {@code generatorEnergyPerTick}. Burning pauses
 * while the buffer is full, so no fuel is wasted.
 */
public class GeneratorBlockEntity extends CompatBlockEntity {
    private final SimpleContainer fuel = new SimpleContainer(1) {
        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return isFuel(stack);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            GeneratorBlockEntity.this.setChanged();
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
    }

    /** Furnace fuel: items with the {@code cooking_fuel} component. */
    public static boolean isFuel(ItemStack stack) {
        return stack.has(DataComponents.COOKING_FUEL);
    }

    /** Burn time of a fuel in ticks, resolved like a furnace does. */
    private int fuelBurnTime(ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel)) return 0;
        LootContext context = new LootContext.Builder(new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.BLOCK_STATE, getBlockState())
                .withParameter(LootContextParams.BLOCK_ENTITY, this)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .withParameter(LootContextParams.CONTAINER, fuel)
                .create(LootContextParamSets.CONTAINER_PROCESS)).create(Optional.empty());
        return ResolvableInt.getFromItem(stack, DataComponents.COOKING_FUEL, CookingFuel::burnTime, context, 0);
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
            itemStorage = new FilteringStorage<>(ContainerStorage.of(fuel, null)) {
                @Override
                protected boolean canInsert(ItemVariant resource) {
                    return isFuel(resource.toStack());
                }

                @Override
                protected boolean canExtract(ItemVariant resource) {
                    return !isFuel(resource.toStack());
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
        if (stack.isEmpty() || !isFuel(stack)) return false;
        int ticks = fuelBurnTime(stack);
        if (ticks <= 0) return false;
        ItemStackTemplate remainder = stack.getItem().getCraftingRemainder();
        burnDuration = burnTime = ticks;
        stack.shrink(1);
        if (stack.isEmpty() && remainder != null) {
            fuel.setItem(0, remainder.create());
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
    protected void readData(CompoundTag tag) {
        energy.amount = Math.min(energy.capacity, tag.getLongOr("Energy", 0));
        burnTime = tag.getIntOr("BurnTime", 0);
        burnDuration = tag.getIntOr("BurnDuration", 0);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            Containers.dropContents(level, pos, fuel);
        }
    }

    @Override
    protected void readItems(ValueInput input) {
        NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        fuel.setItem(0, items.get(0));
    }

    @Override
    protected void writeItems(ValueOutput output) {
        ContainerHelper.saveAllItems(output, NonNullList.of(ItemStack.EMPTY, fuel.getItem(0)));
    }

    @Override
    protected void writeData(CompoundTag tag) {
        tag.putLong("Energy", energy.amount);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("BurnDuration", burnDuration);
    }

    /** The GUI gets its values from the menu and the flame from the block state. */
    @Override
    protected void writeClientData(CompoundTag tag) {
    }
}
