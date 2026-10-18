package omtteam.openmodularturrets.api.weapon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.core.base.AmmoDraw;
import omtteam.openmodularturrets.entity.projectile.TurretProjectile;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Spawns projectiles (1.12.2 {@code ProjectileTurret}): one per scattershot level, aimed at the predicted position of
 * the target (lead from {@link TurretProjectile#getGravityEffect()}) and scattered by the turret's inaccuracy. The
 * projectile is linked to the base ({@link TurretProjectile#setBase}) and placed at the turret by the turret.
 *
 * @param speed      blocks per tick
 * @param hitHandler called when one of the weapon's projectiles hits an entity, or null; see {@link #onHit}
 */
public record ProjectileWeapon(SoundEvent launchSound, float speed, ProjectileFactory factory,
                               @Nullable HitHandler hitHandler) implements TurretWeapon {
    /** 1.12.2 {@code doTargetedShot}: projectiles are shot with speed 3.0 (times the lead correction factor). */
    public static final float DEFAULT_SPEED = 3.0F;

    public ProjectileWeapon {
        Objects.requireNonNull(launchSound, "launchSound");
        Objects.requireNonNull(factory, "factory");
    }

    /** A projectile weapon with the {@link #DEFAULT_SPEED}. */
    public static ProjectileWeapon of(SoundEvent launchSound, ProjectileFactory factory) {
        return new ProjectileWeapon(launchSound, DEFAULT_SPEED, factory, null);
    }

    /**
     * This weapon with a hit handler: called once per projectile and entity, when the projectile damages the entity
     * (a direct hit, or the explosion of a grenade, rocket or plasma bolt), after the damage. Projectiles that deal no
     * damage call it through {@link TurretProjectile#notifyHit}.
     */
    public ProjectileWeapon onHit(HitHandler handler) {
        return new ProjectileWeapon(launchSound, speed, factory, Objects.requireNonNull(handler, "handler"));
    }

    @Override
    public boolean aims() {
        return true;
    }

    /** Creates one projectile of a shot. */
    @FunctionalInterface
    public interface ProjectileFactory {
        /**
         * @param ammo   the ammo drawn for the shot; null if the turret needs none (energy turret, creative upgrade,
         *               ammo disabled in the config)
         * @param target the base's target
         * @return the projectile, or null to skip it
         */
        @Nullable
        TurretProjectile create(ServerLevel level, @Nullable AmmoDraw ammo, @Nullable Entity target);
    }

    /** What happens when a projectile of this weapon hits an entity. */
    @FunctionalInterface
    public interface HitHandler {
        /** @param base the base that fired the projectile, null if it is gone or unloaded */
        void onHit(ServerLevel level, TurretProjectile projectile, @Nullable TurretBaseBlockEntity base, Entity hit);
    }
}
