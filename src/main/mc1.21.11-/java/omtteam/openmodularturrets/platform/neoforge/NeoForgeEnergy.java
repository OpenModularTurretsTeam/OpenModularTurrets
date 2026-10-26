package omtteam.openmodularturrets.platform.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import omtteam.openmodularturrets.core.energy.EnergyStore;
import omtteam.openmodularturrets.platform.Energy;

public final class NeoForgeEnergy implements Energy {
    @Override
    public long pushToBlock(Level level, BlockPos pos, Direction side, EnergyStore source, long maxAmount) {
        if (!level.isLoaded(pos)) return 0;
        EnergyHandler target = level.getCapability(Capabilities.Energy.BLOCK, pos, side);
        if (target == null) return 0;
        int accepted;
        try (Transaction transaction = Transaction.openRoot()) {
            accepted = target.insert(NeoForgeEnergyHandler.clamp(source.extract(maxAmount, true)), transaction);
            transaction.commit();
        }
        source.extract(accepted, false);
        return accepted;
    }
}
