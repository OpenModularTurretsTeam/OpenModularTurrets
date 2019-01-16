package omtteam.openmodularturrets.entity.projectiles.damagesources;

import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.tileentity.TurretBase;

/**
 * Created by Keridos on 11.04.2015.
 * This is the armor bypassing damage source
 */
public class ArmorBypassDamageSource extends AbstractOMTDamageSource {
    public ArmorBypassDamageSource(String damageSource, int fakeDrops, TurretBase base, WorldServer worldServer, boolean projectile) {
        super(damageSource, fakeDrops, base, worldServer);
        if (projectile) {
            this.setProjectile();
        }
        this.setDamageBypassesArmor();
    }
}
