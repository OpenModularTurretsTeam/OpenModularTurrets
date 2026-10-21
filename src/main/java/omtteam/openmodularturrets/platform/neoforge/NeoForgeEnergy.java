package omtteam.openmodularturrets.platform.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import omtteam.openmodularturrets.core.energy.EnergyStore;
import omtteam.openmodularturrets.platform.Energy;

public final class NeoForgeEnergy implements Energy {
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
