package omtteam.openmodularturrets.gametest.platform.neoforge;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import omtteam.openmodularturrets.gametest.GameTest;
import omtteam.openmodularturrets.gametest.GameTestCases;
import omtteam.openmodularturrets.gametest.OMTGameTestMod;

import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Registers the game tests on NeoForge from 1.21.11 on ({@link GameTestCases}), where {@code @GameTestHolder} is
 * gone: the test classes are the classes of the test mod with {@link GameTest} methods.
 */
final class NeoForgeGameTestRegistration {
    private NeoForgeGameTestRegistration() {
    }

    static void register(IEventBus modBus) {
        GameTestCases cases = new GameTestCases(testClasses());
        modBus.addListener((RegisterEvent event) -> event.register(Registries.TEST_FUNCTION,
                registry -> cases.registerFunctions(registry::register)));
        // posted on game test servers while the data-driven registries load
        modBus.addListener((RegisterGameTestsEvent event) -> cases.registerTests(event::registerEnvironment,
                event::registerTest));
    }

    /** One instance of each class of the test mod that has {@link GameTest} methods. */
    private static List<Object> testClasses() {
        return ModList.get().getModFileById(OMTGameTestMod.MOD_ID).getFile().getScanResult()
                .getAnnotatedBy(GameTest.class, ElementType.METHOD)
                .map(annotation -> annotation.clazz().getClassName())
                .distinct()
                .map(NeoForgeGameTestRegistration::create)
                .toList();
    }

    private static Object create(String className) {
        try {
            return Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                 | InvocationTargetException e) {
            throw new IllegalStateException("Cannot create game test class " + className, e);
        }
    }
}
