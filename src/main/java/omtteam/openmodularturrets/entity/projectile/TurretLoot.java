package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Loot rule for turret kills (user decision, inverting 1.12.2): a non-player killed by a turret drops its loot table
 * and equipment only if the base has a fake drops addon. Players keep their normal death drops, and experience is not
 * affected (mobs only drop it after player damage, which the fake player provides).
 */
public final class TurretLoot {
    private TurretLoot() {
    }

    public static boolean mayDropLoot(LivingEntity entity, DamageSource killingBlow) {
        if (entity instanceof Player) return true;
        return !(killingBlow instanceof TurretDamageSource turretSource) || turretSource.allowsLoot();
    }
}
