package omtteam.openmodularturrets.client.api;

/**
 * Client entrypoint for mods that add turrets: registers their models. Declare it in your {@code fabric.mod.json}:
 * <pre>{@code
 * "entrypoints": {
 *   "openmodularturrets_client": ["com.example.client.MyTurretModels"]
 * }
 * }</pre>
 * Called during the client initialisation of Open Modular Turrets. A turret without a model is invisible.
 */
@FunctionalInterface
public interface OMTClientPlugin {
    /** Entrypoint key in {@code fabric.mod.json}. */
    String ENTRYPOINT = "openmodularturrets_client";

    void registerTurretModels(TurretModelRegistrar registrar);
}
