package omtteam.openmodularturrets.client.api;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Client entrypoint for mods that add turrets (1.20.1+ {@code client.api.OMTClientPlugin}): registers the models of
 * their turrets. Annotate the class with {@link Register} (it needs a public no-argument constructor); Open Modular
 * Turrets calls it on the client during its preInit, after all turrets are registered. Keep it in a separate class
 * from your {@code OMTPlugin}, it is only loaded on the client.
 */
@SideOnly(Side.CLIENT)
@FunctionalInterface
public interface OMTClientPlugin {
    void registerModels(TurretModelRegistrar registrar);

    /** Marks an {@link OMTClientPlugin}. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Register {
    }
}
