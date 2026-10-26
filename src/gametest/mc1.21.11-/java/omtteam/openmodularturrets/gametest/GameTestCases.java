package omtteam.openmodularturrets.gametest;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * The game tests as registry entries, from 1.21.11 on: since 1.21.5 a test is a test function plus a data-driven test
 * instance, and the framework batches tests by their test environment. Every {@link GameTest} method becomes a test
 * function and a test instance {@code openmodularturrets_gametest:<class>/<method>} (snake case), and every batch an
 * empty test environment {@code openmodularturrets_gametest:<batch>}. The loader entrypoints pass in the test classes
 * and register the entries through their loader's hooks ({@code platform/<loader>}).
 */
public final class GameTestCases {
    /** Batch of the tests that name none. */
    static final String DEFAULT_BATCH = "default";

    private final List<Case> cases = new ArrayList<>();

    /** @param tests one instance of each test class */
    public GameTestCases(Collection<?> tests) {
        for (Object test : tests) {
            String testClass = snakeCase(test.getClass().getSimpleName());
            Arrays.stream(test.getClass().getMethods())
                    .filter(method -> method.isAnnotationPresent(GameTest.class))
                    .sorted(Comparator.comparing(Method::getName))
                    .forEach(method -> cases.add(new Case(id(testClass + "/" + snakeCase(method.getName())),
                            function(test, method), method.getAnnotation(GameTest.class))));
        }
    }

    /** Registers one test function per test ({@code Registries.TEST_FUNCTION}, a static registry). */
    public void registerFunctions(BiConsumer<ResourceLocation, Consumer<GameTestHelper>> functions) {
        cases.forEach(test -> functions.accept(test.id(), test.function()));
    }

    /**
     * Registers the test environments and the test instances, both data-driven registries: the loader calls this while
     * the server loads them.
     *
     * @param environments registers a test environment and returns its holder
     * @param tests        registers a test instance
     */
    public void registerTests(
            BiFunction<ResourceLocation, TestEnvironmentDefinition, Holder<TestEnvironmentDefinition>> environments,
            BiConsumer<ResourceLocation, GameTestInstance> tests) {
        Map<String, Holder<TestEnvironmentDefinition>> batches = new HashMap<>();
        for (Case test : cases) {
            Holder<TestEnvironmentDefinition> environment = batches.computeIfAbsent(test.annotation().batch(),
                    batch -> environments.apply(id(batch), new TestEnvironmentDefinition.AllOf(List.of())));
            TestData<Holder<TestEnvironmentDefinition>> data = new TestData<>(environment,
                    ResourceLocation.parse(test.annotation().template()), test.annotation().timeoutTicks(), 0, true);
            ResourceKey<Consumer<GameTestHelper>> function = ResourceKey.create(Registries.TEST_FUNCTION, test.id());
            tests.accept(test.id(), new FunctionGameTestInstance(function, data));
        }
    }

    private static Consumer<GameTestHelper> function(Object test, Method method) {
        return helper -> {
            try {
                method.invoke(test, helper);
            } catch (InvocationTargetException e) {
                // failed assertions reach the framework as they are, so it reports their message
                if (e.getCause() instanceof RuntimeException failure) throw failure;
                if (e.getCause() instanceof Error error) throw error;
                throw new IllegalStateException("Game test " + method.getName() + " threw", e.getCause());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Game test " + method.getName() + " is not accessible", e);
            }
        };
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(OMTGameTestMod.MOD_ID, path);
    }

    private static String snakeCase(String name) {
        return name.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
    }

    private record Case(ResourceLocation id, Consumer<GameTestHelper> function, GameTest annotation) {
    }
}
