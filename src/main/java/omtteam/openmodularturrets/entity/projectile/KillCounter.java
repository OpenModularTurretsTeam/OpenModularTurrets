package omtteam.openmodularturrets.entity.projectile;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;

/**
 * oldmod {@code OMTEventHandler.entityDeathEvent}: deaths caused by turret damage count as kills of the base.
 */
public final class KillCounter {
    private KillCounter() {
    }

    public static void init() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!(source instanceof TurretDamageSource turretSource)) return;
            BlockPos basePos = turretSource.getBasePos();
            if (basePos == null || !entity.level().isLoaded(basePos)) return;
            if (entity.level().getBlockEntity(basePos) instanceof TurretBaseBlockEntity base) {
                base.getLogic().increaseKillCounter();
                if (entity instanceof Player) {
                    base.getLogic().increasePlayerKillCounter();
                }
            }
        });
    }
}
