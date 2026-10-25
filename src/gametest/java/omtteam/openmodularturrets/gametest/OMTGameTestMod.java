package omtteam.openmodularturrets.gametest;

import net.minecraft.core.registries.Registries;
import omtteam.openmodularturrets.registry.RegistrationStep;

import java.util.List;

/**
 * Test mod (never shipped): one test turret per weapon kind, added through the Turret API ({@link ApiTestTurrets},
 * Fabric entrypoint / NeoForge {@code @OMTPlugin.Register}) with its own projectile ({@link ApiTestEntities}). The
 * loader entrypoints in {@code platform/<loader>} run the registration steps and forward entity loads to
 * {@link ProjectileTurretGameTests#onEntityLoad}.
 */
public final class OMTGameTestMod {
    /** With an underscore: NeoForge mod ids cannot contain hyphens. */
    public static final String MOD_ID = "openmodularturrets_gametest";

    private OMTGameTestMod() {
    }

    /** Registration steps of the test content, like {@code OMTStartup#registrationSteps}. */
    public static List<RegistrationStep> registrationSteps() {
        return List.of(new RegistrationStep(Registries.ENTITY_TYPE, ApiTestEntities::init));
    }
}
