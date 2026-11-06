package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.entity.Entity;
import omtteam.omlib.item.ItemOwnershipWand;

/**
 * Entities turret weapons must not affect although turrets may still target and fire at them (1.20.1+
 * {@code TurretImmunity}): players holding OMLib's {@link ItemOwnershipWand} in either hand, so map makers can watch
 * their turrets work. Checked by {@link TurretDamage} (and so {@code OMTApi.canDamage}), by the effect weapon dispatch,
 * by {@code OMTEventHandler} for turret damage sources and for the explosions of turret projectiles.
 */
public final class TurretImmunity {
    private TurretImmunity() {
    }

    public static boolean isImmune(Entity entity) {
        return ItemOwnershipWand.isHeldBy(entity);
    }
}
