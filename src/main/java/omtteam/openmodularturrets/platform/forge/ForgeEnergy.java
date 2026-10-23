package omtteam.openmodularturrets.platform.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import omtteam.openmodularturrets.core.energy.EnergyStore;
import omtteam.openmodularturrets.platform.Energy;

public final class ForgeEnergy implements Energy {
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
}
