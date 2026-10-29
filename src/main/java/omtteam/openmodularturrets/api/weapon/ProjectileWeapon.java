package omtteam.openmodularturrets.api.weapon;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.entity.projectile.TurretProjectile;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Spawns projectiles (1.12.2 {@code ProjectileTurret}): one per scattershot level, aimed at the predicted position of
 * the target (lead from the projectile's gravity, {@code TurretMath.lead}) and scattered by the turret's inaccuracy.
 */
public final class ProjectileWeapon implements TurretWeapon {
    /** 1.12.2 {@code doTargetedShot}: projectiles are shot with speed 3.0 (times the lead correction factor). */
    public static final float DEFAULT_SPEED = 3.0F;

    private final SoundEvent launchSound;
    private final float speed;
    private final ProjectileFactory factory;
    @Nullable
    private final HitHandler hitHandler;

    /** @param speed blocks per tick */
    public ProjectileWeapon(SoundEvent launchSound, float speed, ProjectileFactory factory) {
        this(launchSound, speed, factory, null);
    }

    /**
     * @param speed      blocks per tick
     * @param hitHandler called when one of the weapon's projectiles hits an entity, or null; see {@link #onHit}
     */
    public ProjectileWeapon(SoundEvent launchSound, float speed, ProjectileFactory factory,
                            @Nullable HitHandler hitHandler) {
        this.launchSound = Objects.requireNonNull(launchSound, "launchSound");
        this.speed = speed;
        this.factory = Objects.requireNonNull(factory, "factory");
        this.hitHandler = hitHandler;
    }

    /** A projectile weapon with the {@link #DEFAULT_SPEED}. */
    public static ProjectileWeapon of(SoundEvent launchSound, ProjectileFactory factory) {
        return new ProjectileWeapon(launchSound, DEFAULT_SPEED, factory);
    }

    @Override
    public SoundEvent launchSound() {
        return launchSound;
    }

    /** Launch speed in blocks per tick. */
    public float speed() {
        return speed;
    }

    public ProjectileFactory factory() {
        return factory;
    }

    /** Called when one of the weapon's projectiles hits an entity, or null. */
    @Nullable
    public HitHandler hitHandler() {
        return hitHandler;
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

    /** Creates one projectile of a shot, linked to the base. */
    @FunctionalInterface
    public interface ProjectileFactory {
        /**
         * @param ammo   the ammo drawn for the shot (the fired item with the count drawn); empty if the turret needs
         *               none (energy turret, ammo disabled in the config)
         * @param target the base's target, null for forced shots
         * @return the projectile, or null to skip it
         */
        @Nullable
        TurretProjectile create(World world, ItemStack ammo, @Nullable Entity target, TurretBase base);
    }

    /** Called when one of the weapon's projectiles hits an entity (e.g. to add an effect). */
    @FunctionalInterface
    public interface HitHandler {
        /** @param base the base that fired the projectile, null if it is gone or unloaded */
        void onHit(WorldServer world, TurretProjectile projectile, @Nullable TurretBase base, Entity hit);
    }
}
