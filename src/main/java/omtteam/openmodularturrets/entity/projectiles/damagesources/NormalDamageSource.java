package omtteam.openmodularturrets.entity.projectiles.damagesources;

import net.minecraft.world.WorldServer;

/**
 * Created by Keridos on 11.04.2015.
 * This Class
 */
public class NormalDamageSource extends AbstractOMTDamageSource {
    public NormalDamageSource(String damageSource, int fakeDrops, WorldServer worldServer) {
        super(damageSource, fakeDrops, worldServer);
        this.setProjectile();
    }


}
