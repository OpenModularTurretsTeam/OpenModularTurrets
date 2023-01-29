package openmodularturrets.entity.projectiles.damagesources;

import net.minecraft.util.DamageSource;

/**
 * Created by Keridos on 11.04.2015. This Class
 */
public class ArmorBypassDamageSource extends DamageSource {

    public ArmorBypassDamageSource(String p_i1566_1_) {
        super(p_i1566_1_);
        this.setProjectile();
        this.setDamageIsAbsolute();
    }
}
