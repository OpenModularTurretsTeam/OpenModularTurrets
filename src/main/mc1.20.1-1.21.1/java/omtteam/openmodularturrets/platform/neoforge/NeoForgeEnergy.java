package omtteam.openmodularturrets.platform.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import omtteam.openmodularturrets.core.energy.EnergyStore;
import omtteam.openmodularturrets.platform.Energy;

public final class NeoForgeEnergy implements Energy {
    @Override
    public boolean isBattery(ItemStack stack) {
        if (stack.isEmpty()) return false;
        IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return storage != null && storage.canExtract();
    }

    @Override
    public long dischargeItem(Container container, int slot, EnergyStore target, long maxAmount) {
        ItemStack stack = container.getItem(slot);
        IEnergyStorage battery = stack.isEmpty() ? null : stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (battery == null) return 0;
        // the item capability changes the stack in the slot directly (data components)
        long accepted = target.insert(battery.extractEnergy(NeoForgeEnergyHandler.clamp(maxAmount), true), true);
        int moved = battery.extractEnergy(NeoForgeEnergyHandler.clamp(accepted), false);
        target.insert(moved, false);
        if (moved > 0) container.setChanged();
        return moved;
    }

    @Override
    public long pushToBlock(Level level, BlockPos pos, Direction side, EnergyStore source, long maxAmount) {
        if (!level.isLoaded(pos)) return 0;
        IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
        if (target == null) return 0;
        int accepted = target.receiveEnergy(NeoForgeEnergyHandler.clamp(source.extract(maxAmount, true)), false);
        source.extract(accepted, false);
        return accepted;
    }
}
