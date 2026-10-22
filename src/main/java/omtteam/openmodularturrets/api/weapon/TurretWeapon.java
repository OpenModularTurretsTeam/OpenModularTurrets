package omtteam.openmodularturrets.api.weapon;

import net.minecraft.util.SoundEvent;

/**
 * How a turret fires (1.20.1+ {@code api.weapon.TurretWeapon}). The turret
 * tile entity handles aiming, cooldown, energy and ammo (via its base); the weapon decides what a shot does:
 * <ul>
 *     <li>{@link ProjectileWeapon}: spawns projectile entities aimed at the base's target</li>
 *     <li>{@link RayWeapon}: hits instantly along a ray towards the base's target</li>
 *     <li>{@link EffectWeapon}: acts on the base's target without aiming</li>
 *     <li>{@link AreaWeapon}: ignores the base's target and strikes everything in an area around the turret</li>
 *     <li>{@link SelfTargetingWeapon}: ignores the base's target and finds its own</li>
 * </ul>
 * Only these five classes may implement it (Java 8 has no sealed types); other implementations are rejected when the
 * turret is registered.
 */
public interface TurretWeapon {
    /** Played for every shot. */
    SoundEvent launchSound();

    /** Whether the turret turns towards its target (1.12.2 {@code AbstractDirectedTurret}). */
    boolean aims();
}
