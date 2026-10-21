package omtteam.openmodularturrets.platform.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import omtteam.openmodularturrets.core.energy.EnergyStore;
import omtteam.openmodularturrets.platform.Energy;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

public final class FabricEnergy implements Energy {
    @Override
    public long pushToBlock(Level level, BlockPos pos, Direction side, EnergyStore source, long maxAmount) {
        if (!level.isLoaded(pos)) return 0;
        EnergyStorage target = EnergyStorage.SIDED.find(level, pos, side);
        return target == null ? 0 : EnergyStorageUtil.move(FabricEnergyStorages.of(source), target, maxAmount, null);
    }
}
