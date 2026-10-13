package omtteam.openmodularturrets.blockentity.turret;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import omtteam.openmodularturrets.core.base.AmmoDraw;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.ItemCarryingProjectile;
import omtteam.openmodularturrets.entity.projectile.RocketProjectile;
import omtteam.openmodularturrets.entity.projectile.TurretProjectile;
import omtteam.openmodularturrets.registry.ModEntities;
import omtteam.openmodularturrets.registry.ModSounds;
import org.jetbrains.annotations.Nullable;

/**
 * How a turret type fires: the projectile it spawns (oldmod {@code createProjectile}) or a ray, its launch sound and
 * projectile speed.
 */
public record TurretWeapon(Kind kind, @Nullable ProjectileFactory factory, SoundEvent launchSound, float speed,
                           boolean leadWithGravity) {
    public enum Kind {
        /** Spawns projectile entities (oldmod {@code ProjectileTurret}). */
        PROJECTILE,
        /** Hits instantly along a ray (oldmod {@code RayTracingTurret}: laser, railgun, arc), see {@link RayWeapons}. */
        RAY,
        /**
         * Acts on the target without aiming (oldmod {@code TurretHead.updateChecks} turrets: relativistic,
         * teleporter, melee), see {@link EffectWeapons}.
         */
        EFFECT;

        /** Whether the turret turns towards its target (oldmod {@code AbstractDirectedTurret}). */
        public boolean aims() {
            return this != EFFECT;
        }
    }

    @FunctionalInterface
    public interface ProjectileFactory {
        TurretProjectile create(ServerLevel level, @Nullable AmmoDraw ammo, @Nullable Entity target);
    }

    /** oldmod {@code doTargetedShot}: projectiles are shot with speed 3.0 (times the lead correction factor). */
    private static final float PROJECTILE_SPEED = 3.0F;

    public static TurretWeapon of(TurretType type) {
        return switch (type) {
            case DISPOSABLE_TURRET -> projectile(ModSounds.DISPOSABLE_LAUNCH, true, (level, ammo, target) ->
                    withItem(ModEntities.DISPOSABLE.create(level, EntitySpawnReason.TRIGGERED), ammo));
            case POTATO_CANNON_TURRET -> projectile(ModSounds.POTATO_LAUNCH, true, (level, ammo, target) ->
                    withItem(ModEntities.POTATO.create(level, EntitySpawnReason.TRIGGERED), ammo));
            // oldmod CrossbowTurretTileEntity used the potato launch sound
            case CROSSBOW_TURRET -> projectile(ModSounds.POTATO_LAUNCH, true, (level, ammo, target) -> ModEntities.ARROW.create(level, EntitySpawnReason.TRIGGERED));
            case MACHINE_GUN_TURRET -> projectile(ModSounds.MACHINE_GUN_LAUNCH, false, (level, ammo, target) -> ModEntities.BULLET.create(level, EntitySpawnReason.TRIGGERED));
            case INCENDIARY_TURRET -> projectile(ModSounds.INCENDIARY_LAUNCH, true, (level, ammo, target) -> ModEntities.BLAZING_CLAY.create(level, EntitySpawnReason.TRIGGERED));
            case GRENADE_TURRET -> projectile(ModSounds.GRENADE_LAUNCH, true, (level, ammo, target) -> ModEntities.GRENADE.create(level, EntitySpawnReason.TRIGGERED));
            case ROCKET_TURRET -> projectile(ModSounds.ROCKET_LAUNCH, false, (level, ammo, target) -> {
                RocketProjectile rocket = ModEntities.ROCKET.create(level, EntitySpawnReason.TRIGGERED);
                if (rocket != null) rocket.setTarget(target);
                return rocket;
            });
            case PLASMA_TURRET -> projectile(ModSounds.PLASMA_LAUNCH, true, (level, ammo, target) -> ModEntities.PLASMA.create(level, EntitySpawnReason.TRIGGERED));
            // oldmod MeleeTurretTileEntity used the relativistic launch sound
            case MELEE_TURRET -> effect(ModSounds.RELATIVISTIC_LAUNCH);
            case RELATIVISTIC_TURRET -> effect(ModSounds.RELATIVISTIC_LAUNCH);
            case TELEPORTER_TURRET -> effect(ModSounds.TELEPORT_LAUNCH);
            case ARC_TURRET -> ray(ModSounds.ARC_LAUNCH);
            case LASER_TURRET -> ray(ModSounds.LASER_LAUNCH);
            case RAILGUN_TURRET -> ray(ModSounds.RAIL_GUN_LAUNCH);
        };
    }

    /**
     * @param leadWithGravity oldmod {@code getProjectileGravity() != 0} of the turret: the aim lead assumes a slower
     *                        projectile (the incendiary turret declared gravity although its projectile has none)
     */
    private static TurretWeapon projectile(SoundEvent sound, boolean leadWithGravity, ProjectileFactory factory) {
        return new TurretWeapon(Kind.PROJECTILE, factory, sound, PROJECTILE_SPEED, leadWithGravity);
    }

    private static TurretWeapon ray(SoundEvent sound) {
        return new TurretWeapon(Kind.RAY, null, sound, 0, false);
    }

    private static TurretWeapon effect(SoundEvent sound) {
        return new TurretWeapon(Kind.EFFECT, null, sound, 0, false);
    }

    private static TurretProjectile withItem(@Nullable ItemCarryingProjectile projectile, @Nullable AmmoDraw ammo) {
        if (projectile != null && ammo != null && ammo.item() instanceof ItemStack stack) {
            projectile.setItem(stack);
        }
        return projectile;
    }
}
