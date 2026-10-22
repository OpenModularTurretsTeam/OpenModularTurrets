package omtteam.openmodularturrets.api;

/**
 * Passed to {@link OMTPlugin#registerTurrets}. Only valid during that call.
 */
public interface TurretRegistrar {
    /**
     * Adds a turret. Its type must not be registered yet; Open Modular Turrets registers a turret block and item with
     * the type's id.
     *
     * @throws IllegalArgumentException if a turret with the same id exists
     */
    void registerTurret(TurretDefinition turret);

    /**
     * Defines which items count as an ammo type.
     *
     * @throws IllegalArgumentException if the ammo type already has a definition
     */
    void registerAmmo(AmmoDefinition ammo);
}
