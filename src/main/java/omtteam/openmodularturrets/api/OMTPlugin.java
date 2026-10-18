package omtteam.openmodularturrets.api;

/**
 * Entrypoint for mods that add turrets. Declare it in your {@code fabric.mod.json}:
 * <pre>{@code
 * "entrypoints": {
 *   "openmodularturrets": ["com.example.MyTurrets"]
 * }
 * }</pre>
 * Open Modular Turrets calls {@link #registerTurrets} during its own initialisation, before it loads its config and
 * registers the turret blocks, so it does not matter whether your mod initialises before or after it. Open Modular
 * Turrets then registers a turret block and item with your turret's id; you provide its assets and data (see
 * {@code docs/turret-api.md}).
 */
@FunctionalInterface
public interface OMTPlugin {
    /** Entrypoint key in {@code fabric.mod.json}. */
    String ENTRYPOINT = "openmodularturrets";

    void registerTurrets(TurretRegistrar registrar);
}
