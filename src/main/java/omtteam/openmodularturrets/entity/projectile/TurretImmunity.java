package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import omtteam.openmodularturrets.item.OwnershipWandItem;

/**
 * Entities turret weapons must not affect although turrets may still target and fire at them: players holding the
 * map maker's {@link OwnershipWandItem} in either hand, so map makers can watch their turrets work. Checked by
 * {@link TurretDamage#canDamage} (and so {@code OMTApi.canDamage}), by the effect weapon dispatch and, for turret
 * explosions, by {@code LivingEntityMixin}.
 */
public final class TurretImmunity {
    private TurretImmunity() {
    }

    public static boolean isImmune(Entity entity) {
        return entity instanceof Player player && (player.getMainHandItem().getItem() instanceof OwnershipWandItem
                || player.getOffhandItem().getItem() instanceof OwnershipWandItem);
    }
}
