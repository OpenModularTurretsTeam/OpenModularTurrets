package omtteam.openmodularturrets.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Entrypoint for mods that add turrets (1.20.1+ {@code api.OMTPlugin}). Annotate the class with {@link Register} (it
 * needs a public no-argument constructor); Open Modular Turrets finds it in the mod scan data and calls
 * {@link #registerTurrets} during its own preInit, before it registers the turret blocks, so it does not matter
 * whether your mod loads before or after it. Open Modular Turrets then registers a turret block and item with your
 * turret's id; you provide its assets (see {@code docs/turret-api.md}).
 */
@FunctionalInterface
public interface OMTPlugin {
    void registerTurrets(TurretRegistrar registrar);

    /** Marks an {@link OMTPlugin}; the plugin counts as added by the mod whose jar contains it. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Register {
    }
}
