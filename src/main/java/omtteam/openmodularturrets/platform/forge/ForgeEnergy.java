package omtteam.openmodularturrets.platform.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import omtteam.openmodularturrets.core.energy.EnergyStore;
import omtteam.openmodularturrets.platform.Energy;
import org.jetbrains.annotations.Nullable;

public final class ForgeEnergy implements Energy {
    @Override
    public boolean isBattery(ItemStack stack) {
        IEnergyStorage storage = itemEnergy(stack);
        return storage != null && storage.canExtract();
    }

    @Override
    public long dischargeItem(Container container, int slot, EnergyStore target, long maxAmount) {
        IEnergyStorage battery = itemEnergy(container.getItem(slot));
        if (battery == null) return 0;
        // the item capability changes the stack in the slot directly (its NBT)
        long accepted = target.insert(battery.extractEnergy(ForgeEnergyHandler.clamp(maxAmount), true), true);
        int moved = battery.extractEnergy(ForgeEnergyHandler.clamp(accepted), false);
        target.insert(moved, false);
        if (moved > 0) container.setChanged();
        return moved;
    }

    @Override
    public long pushToBlock(Level level, BlockPos pos, Direction side, EnergyStore source, long maxAmount) {
        if (!level.isLoaded(pos)) return 0;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return 0;
        IEnergyStorage target = blockEntity.getCapability(ForgeCapabilities.ENERGY, side).resolve().orElse(null);
        if (target == null) return 0;
        int accepted = target.receiveEnergy(ForgeEnergyHandler.clamp(source.extract(maxAmount, true)), false);
        source.extract(accepted, false);
        return accepted;
    }

    @Nullable
    private static IEnergyStorage itemEnergy(ItemStack stack) {
        return stack.isEmpty() ? null : stack.getCapability(ForgeCapabilities.ENERGY).resolve().orElse(null);
    }
}
