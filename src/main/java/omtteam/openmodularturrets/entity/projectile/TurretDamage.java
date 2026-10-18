package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.base.LivingTargetEntity;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.security.DamageRules;
import omtteam.openmodularturrets.core.security.DamageRules.Victim;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.TrustManager;
import omtteam.openmodularturrets.security.PlayerAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Damage rules shared by projectiles, rays and the melee turret (1.12.2 {@code OMTUtil.canDamageEntity} and the
 * {@code attackEntityFrom} calls).
 */
public final class TurretDamage {
    private TurretDamage() {
    }

    /**
     * Deals damage of the given type and removes the hurt cooldown (1.12.2 {@code hurtResistantTime = -1}).
     * Non-positive amounts are skipped (1.12.2 also "dealt" 0 damage, e.g. the laser's armor-piercing part).
     * <p>
     * With a fake drops addon ({@code lootingLevel} ≥ 0) non-player victims are hurt "by" the {@link TurretKiller}
     * fake player holding a sword with that Looting level, so a kill drops player-kill loot; without the addon a kill
     * drops nothing (see {@code LivingEntityMixin}). Players are never hurt by the fake player, so PvP rules do not
     * apply to turret damage.
     */
    public static void hurt(Level level, Entity target, ResourceKey<DamageType> type, float amount,
                            @Nullable Entity direct, @Nullable BlockPos basePos, int lootingLevel) {
        if (amount <= 0) return;
        Entity causing = null;
        if (lootingLevel != TurretBaseLogic.NO_LOOT && !(target instanceof Player) && level instanceof ServerLevel serverLevel) {
            causing = TurretKiller.withLooting(serverLevel, lootingLevel);
        }
        target.hurt(TurretDamageSource.of(level, type, direct, causing, basePos, lootingLevel), amount);
        target.invulnerableTime = 0;
    }

    /** {@link DamageRules#canDamage} with the trust list of the base at {@code basePos}. */
    public static boolean canDamage(Level level, @Nullable BlockPos basePos, Entity entity) {
        return DamageRules.canDamage(trustManager(level, basePos), victim(entity));
    }

    private static Victim victim(Entity entity) {
        if (entity instanceof TurretProjectile) return new Victim.Projectile();
        if (entity instanceof Player player) return new Victim.Player(PlayerAdapter.identity(player));
        if (entity instanceof LivingEntity living) {
            Optional<PlayerIdentity> owner = new LivingTargetEntity(living).getOwner();
            if (owner.isPresent()) return new Victim.Pet(owner.get());
        }
        return new Victim.Other();
    }

    private static Optional<TrustManager> trustManager(Level level, @Nullable BlockPos basePos) {
        if (basePos == null || !level.isLoaded(basePos)) return Optional.empty();
        return level.getBlockEntity(basePos) instanceof TurretBaseBlockEntity base
                ? Optional.of(base.getTrustManager()) : Optional.empty();
    }
}
