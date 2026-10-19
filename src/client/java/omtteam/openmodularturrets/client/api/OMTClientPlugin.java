package omtteam.openmodularturrets.client.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Client entrypoint for mods that add turrets: registers their models. On NeoForge, annotate the class with
 * {@link Register} instead (it needs a public no-argument constructor); on Fabric, declare it in your
 * {@code fabric.mod.json}:
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

    /**
     * Marks an {@link OMTClientPlugin} for NeoForge, which finds it in the mod scan data on the client only (Fabric
     * ignores it and uses the entrypoint). The plugin counts as added by the mod whose jar contains it.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Register {
    }
}
