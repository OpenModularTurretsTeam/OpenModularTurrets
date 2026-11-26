package omtteam.openmodularturrets.gametest.platform.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import omtteam.openmodularturrets.gametest.OMTGameTestMod;
import omtteam.openmodularturrets.gametest.ProjectileTurretGameTests;
import omtteam.openmodularturrets.gametest.TestBattery;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyItem;

/**
 * Fabric entrypoint of the test mod. The test classes are listed under the {@code fabric-gametest} entrypoint, which
 * Fabric reads to register them.
 */
public final class OMTFabricGameTests implements ModInitializer {
    @Override
    public void onInitialize() {
        OMTGameTestMod.registrationSteps().forEach(step -> step.register().run());
        EnergyStorage.ITEM.registerForItems((stack, context) -> SimpleEnergyItem.createStorage(context,
                TestBattery.CAPACITY, TestBattery.CAPACITY, TestBattery.CAPACITY), TestBattery.ITEM);
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> ProjectileTurretGameTests.onEntityLoad(entity));
    }
}
