package omtteam.openmodularturrets.entity.projectiles.damagesources;

import net.minecraft.util.DamageSource;

/**
 * Created by Keridos on 02/06/17.
 * This Class
 */
public abstract class AbstractOMTDamageSource extends DamageSource {
    public AbstractOMTDamageSource(String damageTypeIn) {
        super(damageTypeIn);
    }
}
