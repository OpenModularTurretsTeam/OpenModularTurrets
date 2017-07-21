package omtteam.openmodularturrets.entity.projectiles.damagesources;

import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.tileentity.TurretBase;

/**
 * Created by Keridos on 11.04.2015.
 * This Class
 */
public class ArmorBypassDamageSource extends AbstractOMTDamageSource {
    public ArmorBypassDamageSource(String damageSource, int fakeDrops, TurretBase base, WorldServer worldServer) {
        super(damageSource, fakeDrops, base, worldServer);
        this.setProjectile();
    }
}
