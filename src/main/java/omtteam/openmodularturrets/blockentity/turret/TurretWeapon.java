package omtteam.openmodularturrets.blockentity.turret;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omtteam.openmodularturrets.core.base.AmmoDraw;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.ItemCarryingProjectile;
import omtteam.openmodularturrets.entity.projectile.RocketProjectile;
import omtteam.openmodularturrets.entity.projectile.TurretProjectile;
import omtteam.openmodularturrets.registry.ModEntities;
import omtteam.openmodularturrets.registry.ModSounds;
import org.jetbrains.annotations.Nullable;

/**
 * How a turret type fires: the projectile it spawns (1.12.2 {@code createProjectile}) or a ray, its launch sound and
 * projectile speed.
 */
public record TurretWeapon(Kind kind, @Nullable ProjectileFactory factory, SoundEvent launchSound, float speed) {
    public enum Kind {
        /** Spawns projectile entities (1.12.2 {@code ProjectileTurret}). */
        PROJECTILE,
        /** Hits instantly along a ray (1.12.2 {@code RayTracingTurret}: laser, railgun, arc), see {@link RayWeapons}. */
        RAY,
        /**
         * Acts on the target without aiming (1.12.2 {@code TurretHead.updateChecks} turrets: slowing,
         * teleporter, melee), see {@link EffectWeapons}.
         */
        EFFECT;

        /** Whether the turret turns towards its target (1.12.2 {@code AbstractDirectedTurret}). */
        public boolean aims() {
            return this != EFFECT;
        }
    }

    @FunctionalInterface
    public interface ProjectileFactory {
        TurretProjectile create(ServerLevel level, @Nullable AmmoDraw ammo, @Nullable Entity target);
    }

    /** 1.12.2 {@code doTargetedShot}: projectiles are shot with speed 3.0 (times the lead correction factor). */
    private static final float PROJECTILE_SPEED = 3.0F;

    public static TurretWeapon of(TurretType type) {
        return switch (type) {
            case DISPOSABLE_TURRET -> projectile(ModSounds.DISPOSABLE_LAUNCH, (level, ammo, target) ->
                    withItem(ModEntities.DISPOSABLE.create(level), ammo, Items.COBBLESTONE));
            case POTATO_CANNON_TURRET -> projectile(ModSounds.POTATO_LAUNCH, (level, ammo, target) ->
                    withItem(ModEntities.POTATO.create(level), ammo, Items.POTATO));
            // 1.12.2 CrossbowTurretTileEntity used the potato launch sound
            case CROSSBOW_TURRET -> projectile(ModSounds.POTATO_LAUNCH, (level, ammo, target) -> ModEntities.ARROW.create(level));
            case MACHINE_GUN_TURRET -> projectile(ModSounds.MACHINE_GUN_LAUNCH, (level, ammo, target) -> ModEntities.BULLET.create(level));
            case INCENDIARY_TURRET -> projectile(ModSounds.INCENDIARY_LAUNCH, (level, ammo, target) -> ModEntities.BLAZING_CLAY.create(level));
            case GRENADE_TURRET -> projectile(ModSounds.GRENADE_LAUNCH, (level, ammo, target) -> ModEntities.GRENADE.create(level));
            case ROCKET_TURRET -> projectile(ModSounds.ROCKET_LAUNCH, (level, ammo, target) -> {
                RocketProjectile rocket = ModEntities.ROCKET.create(level);
                if (rocket != null) rocket.setTarget(target);
                return rocket;
            });
            case PLASMA_TURRET -> projectile(ModSounds.PLASMA_LAUNCH, (level, ammo, target) -> ModEntities.PLASMA.create(level));
            // 1.12.2 MeleeTurretTileEntity used the relativistic (now slowing) launch sound
            case MELEE_TURRET -> effect(ModSounds.SLOWING_LAUNCH);
            case SLOWING_TURRET -> effect(ModSounds.SLOWING_LAUNCH);
            case TELEPORTER_TURRET -> effect(ModSounds.TELEPORT_LAUNCH);
            case ARC_TURRET -> ray(ModSounds.ARC_LAUNCH);
            case LASER_TURRET -> ray(ModSounds.LASER_LAUNCH);
            case RAILGUN_TURRET -> ray(ModSounds.RAIL_GUN_LAUNCH);
        };
    }

    private static TurretWeapon projectile(SoundEvent sound, ProjectileFactory factory) {
        return new TurretWeapon(Kind.PROJECTILE, factory, sound, PROJECTILE_SPEED);
    }

    private static TurretWeapon ray(SoundEvent sound) {
        return new TurretWeapon(Kind.RAY, null, sound, 0);
    }

    private static TurretWeapon effect(SoundEvent sound) {
        return new TurretWeapon(Kind.EFFECT, null, sound, 0);
    }

    /**
     * Shows the fired item on the projectile; without drawn ammo (creative upgrade, ammo disabled in the config)
     * {@code fallback} is shown so the projectile is not invisible.
     */
    private static TurretProjectile withItem(@Nullable ItemCarryingProjectile projectile, @Nullable AmmoDraw ammo,
                                             Item fallback) {
        if (projectile != null) {
            projectile.setItem(ammo != null && ammo.item() instanceof ItemStack stack ? stack : new ItemStack(fallback));
        }
        return projectile;
    }
}
