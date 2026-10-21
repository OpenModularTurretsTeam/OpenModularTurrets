package omtteam.openmodularturrets.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Entrypoint for mods that add turrets. On Fabric, declare it in your {@code fabric.mod.json}:
 * <pre>{@code
 * "entrypoints": {
 *   "openmodularturrets": ["com.example.MyTurrets"]
 * }
 * }</pre>
 * On NeoForge, annotate the class with {@link Register} instead (it needs a public no-argument constructor). A mod for
 * both loaders does both.
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

    /**
     * Marks an {@link OMTPlugin} for NeoForge, which finds it in the mod scan data (Fabric ignores it and uses the
     * entrypoint). The plugin counts as added by the mod whose jar contains it.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Register {
    }
}
