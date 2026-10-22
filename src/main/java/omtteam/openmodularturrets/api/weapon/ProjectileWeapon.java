package omtteam.openmodularturrets.api.weapon;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
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

    /** @param speed blocks per tick */
    public ProjectileWeapon(SoundEvent launchSound, float speed, ProjectileFactory factory) {
        this.launchSound = Objects.requireNonNull(launchSound, "launchSound");
        this.speed = speed;
        this.factory = Objects.requireNonNull(factory, "factory");
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
}
