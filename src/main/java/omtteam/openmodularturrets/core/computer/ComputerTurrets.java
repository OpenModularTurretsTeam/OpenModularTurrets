package omtteam.openmodularturrets.core.computer;

import omtteam.omlib.core.world.Face;

import java.util.Set;

/**
 * The turrets mounted on a turret base, as the computer commands control them (1.12.2 {@code TurretBase}'s
 * {@code setTurretYawPitch}, {@code setTurretForceFire} and {@code forceShootTurret}). Implemented by the Minecraft
 * layer.
 */
public interface ComputerTurrets {
    /**
     * Aims the turret on a face; the angles are relative to the face, in degrees, and limited like the turret's own
     * aim.
     *
     * @return false: no turret on that face
     */
    boolean setYawPitch(Face face, float yaw, float pitch);

    /**
     * Lets the turret on a face fire along its aim whenever it is ready (1.12.2 {@code autoFire}).
     *
     * @return false: no turret on that face
     */
    boolean setAutoFire(Face face, boolean autoFire);

    /**
     * Fires one shot of the turret on a face along its aim.
     *
     * @return false: no turret on that face, or it could not fire (cooldown, energy, ammo, inactive base, a weapon
     *         that does not aim)
     */
    boolean forceShot(Face face);

    /** The faces with a turret. */
    Set<Face> faces();
}
