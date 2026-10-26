package omtteam.openmodularturrets.gametest.platform.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import omtteam.openmodularturrets.gametest.OMTGameTestMod;
import omtteam.openmodularturrets.gametest.ProjectileTurretGameTests;

/**
 * Fabric entrypoint of the test mod. The test classes are listed under the {@code fabric-gametest} entrypoint; before
 * 1.21.11 Fabric registers them, from 1.21.11 on {@code FabricGameTestRegistration} does.
 */
public final class OMTFabricGameTests implements ModInitializer {
    @Override
    public void onInitialize() {
        OMTGameTestMod.registrationSteps().forEach(step -> step.register().run());
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> ProjectileTurretGameTests.onEntityLoad(entity));
        //? if >=1.21.11 {
        /*FabricGameTestRegistration.register();
        *///?}
    }
}
