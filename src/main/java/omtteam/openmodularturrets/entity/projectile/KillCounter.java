package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;

/**
 * 1.12.2 {@code OMTEventHandler.entityDeathEvent}: deaths caused by turret damage count as kills of the base.
 */
public final class KillCounter {
    private KillCounter() {
    }

    /**
     * Counts the death if turret damage caused it; called by the loader's death event on the server, once the death is
     * certain (Fabric {@code AFTER_DEATH}, NeoForge {@code LivingDeathEvent} at the lowest priority).
     */
    public static void onDeath(LivingEntity entity, DamageSource source) {
        if (!(source instanceof TurretDamageSource turretSource)) return;
        BlockPos basePos = turretSource.getBasePos();
        if (basePos == null || !entity.level().isLoaded(basePos)) return;
        if (entity.level().getBlockEntity(basePos) instanceof TurretBaseBlockEntity base) {
            base.getLogic().increaseKillCounter();
            if (entity instanceof Player) {
                base.getLogic().increasePlayerKillCounter();
            }
        }
    }
}
