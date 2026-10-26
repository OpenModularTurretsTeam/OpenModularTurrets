package omtteam.openmodularturrets.gametest.platform.fabric;

import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import omtteam.openmodularturrets.gametest.GameTestCases;

import java.util.Optional;

/**
 * Registers the game tests on Fabric from 1.21.11 on ({@link GameTestCases}). The test classes are still the
 * {@code fabric-gametest} entrypoints. Fabric's own test registration reads them too, but only looks for its own
 * annotation: it logs a warning for each class that it found no test methods in.
 */
final class FabricGameTestRegistration {
    private FabricGameTestRegistration() {
    }

    static void register() {
        GameTestCases cases = new GameTestCases(FabricLoader.getInstance().getEntrypoints("fabric-gametest", Object.class));
        cases.registerFunctions((id, function) -> Registry.register(BuiltInRegistries.TEST_FUNCTION, id, function));
        // called on the server before it loads the data-driven registries
        DynamicRegistrySetupCallback.EVENT.register(registries -> {
            Optional<Registry<TestEnvironmentDefinition>> environments = registries.getOptional(Registries.TEST_ENVIRONMENT);
            Optional<Registry<GameTestInstance>> tests = registries.getOptional(Registries.TEST_INSTANCE);
            if (environments.isPresent() && tests.isPresent()) {
                cases.registerTests((id, environment) -> Registry.registerForHolder(environments.get(), id, environment),
                        (id, test) -> Registry.register(tests.get(), id, test));
            }
        });
    }
}
