package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

/**
 * Created by Keridos on 11.04.2015.
 * This is the armor bypassing damage source
 */
public class ArmorBypassDamageSource extends AbstractOMTDamageSource {
    /** @param victim the entity this damage is dealt to, see {@link AbstractOMTDamageSource} */
    public ArmorBypassDamageSource(String damageSource, int fakeDrops, TurretBase base, WorldServer worldServer, boolean projectile,
                                   Entity victim) {
        super(damageSource, fakeDrops, base, worldServer, victim);
        if (projectile) {
            this.setProjectile();
        }
        this.setDamageBypassesArmor();
    }
}
