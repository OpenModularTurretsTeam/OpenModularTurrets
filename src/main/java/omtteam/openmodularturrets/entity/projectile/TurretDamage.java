package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.UsernameCache;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.TrustManager;
import omtteam.omlib.util.player.OMLibSecurityEnvironment;
import omtteam.omlib.util.player.Player;
import omtteam.openmodularturrets.core.security.DamageRules;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Turret damage in the world: who it may hit ({@link DamageRules}) and the tags a hit leaves for the loot rules
 * (turret hit, fake drops level).
 */
public class TurretDamage {
    public static int getFakeDropsLevel(EntityLivingBase entity) {
        Set<String> tags = entity.getTags();
        return (tags.contains("openmodularturrets:fake_drops_0") ? 0 : tags.contains("openmodularturrets:fake_drops_1") ? 1 :
                tags.contains("openmodularturrets:fake_drops_2") ? 2 : tags.contains("openmodularturrets:fake_drops_3") ? 3 : -1);
    }

    /** The trust rules of a base, empty without a (loaded) base. */
    private static Optional<TrustManager> trust(@Nullable TurretBase base) {
        return base == null ? Optional.<TrustManager>empty() : Optional.of(base.getTrustManager().core());
    }

    public static boolean canDamagePlayer(EntityPlayer entityPlayer, @Nullable TurretBase base) {
        if (TurretImmunity.isImmune(entityPlayer)) return false;
        return DamageRules.canDamage(trust(base), new DamageRules.Victim.Player(
                new PlayerIdentity(entityPlayer.getUniqueID(), entityPlayer.getName())));
    }

    public static boolean canDamagePlayer(Player player, @Nullable TurretBase base) {
        PlayerIdentity identity = OMLibSecurityEnvironment.toIdentity(player);
        return identity != null && DamageRules.canDamage(trust(base), new DamageRules.Victim.Player(identity));
    }

    /**
     * Who turret damage may hit ({@link DamageRules}): never turret projectiles; players and pets of players only
     * if the base's trust rules allow it; everything else; never an {@linkplain TurretImmunity immune} player (one
     * holding the ownership wand).
     */
    public static boolean canDamageEntity(Entity entity, @Nullable TurretBase base) {
        if (entity != null && TurretImmunity.isImmune(entity)) return false;
        return entity == null || DamageRules.canDamage(trust(base), victim(entity));
    }

    private static DamageRules.Victim victim(Entity entity) {
        if (entity instanceof TurretProjectile) {
            return new DamageRules.Victim.Projectile();
        }
        if (entity instanceof EntityPlayer) {
            return new DamageRules.Victim.Player(new PlayerIdentity(entity.getUniqueID(), entity.getName()));
        }
        if (entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwnerId() != null) {
            UUID owner = ((IEntityOwnable) entity).getOwnerId();
            String name = UsernameCache.getLastKnownUsername(owner);
            return new DamageRules.Victim.Pet(new PlayerIdentity(owner, name == null ? "" : name));
        }
        return new DamageRules.Victim.Other();
    }

    public static void setTagsForTurretHit(Entity entity, TurretBase base) {
        EntityLivingBase entityLivingBase;
        if (entity instanceof EntityLivingBase) {
            entityLivingBase = (EntityLivingBase) entity;
            int fakeDrops = base.getLogic().getLootingLevel(3);
            if (!(entityLivingBase instanceof EntityPlayer) && !entityLivingBase.getTags().contains("openmodularturrets:turret_hit")) {
                entityLivingBase.addTag("openmodularturrets:turret_hit");
            }
            if (!(entityLivingBase instanceof EntityPlayer) && fakeDrops > -1) {
                entityLivingBase.addTag("openmodularturrets:fake_drops_" + fakeDrops);
            }
        }
    }
}
