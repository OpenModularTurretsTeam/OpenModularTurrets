package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

import javax.annotation.Nullable;

/**
 * Created by Keridos on 02/06/17.
 * This is the abstract DamageSource for OMT
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractOMTDamageSource extends DamageSource {
    private final TurretBase base;
    private FakePlayer player;

    /**
     * @param fakeDrops the looting level of the base's fake drops addon, -1 without one
     * @param victim    the entity the damage is dealt to: only a non-player is hurt "by" the fake player (for player
     *                  kill loot); players never are, so PvP rules do not apply to turret damage (1.20.1+
     *                  {@code TurretDamage.hurt})
     */
    public AbstractOMTDamageSource(String damageTypeIn, int fakeDrops, TurretBase base, WorldServer worldServer,
                                   Entity victim) {
        super(damageTypeIn);
        if (fakeDrops > -1 && !(victim instanceof EntityPlayer)) {
            player = OMTFakePlayer.getFakePlayer(worldServer);
            player.getEntityAttribute(SharedMonsterAttributes.LUCK).setBaseValue(fakeDrops);
        }
        this.base = base;
    }

    @Nullable
    @Override
    public Entity getTrueSource() {
        return player;
    }

    public TurretBase getBase() {
        return base;
    }
}
