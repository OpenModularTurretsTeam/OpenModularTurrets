package omtteam.openmodularturrets.api.weapon;

import net.minecraft.sounds.SoundEvent;

/**
 * How a turret fires. The turret block entity handles aiming, cooldown, energy and ammo (via its base); the weapon
 * decides what a shot does. There are four kinds:
 * <ul>
 *     <li>{@link ProjectileWeapon}: spawns projectile entities aimed at the base's target</li>
 *     <li>{@link RayWeapon}: hits instantly along a ray towards the base's target</li>
 *     <li>{@link EffectWeapon}: acts on the base's target without aiming</li>
 *     <li>{@link AreaWeapon}: ignores the base's target and strikes everything in an area around the turret</li>
 * </ul>
 */
public sealed interface TurretWeapon permits ProjectileWeapon, RayWeapon, EffectWeapon, AreaWeapon {
    /** Played for every shot. */
    SoundEvent launchSound();

    /** Whether the turret turns towards its target (1.12.2 {@code AbstractDirectedTurret}). */
    boolean aims();
}
