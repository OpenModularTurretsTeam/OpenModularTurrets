package omtteam.openmodularturrets.gametest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a game test method. Vanilla's {@code @GameTest} is gone (since 1.21.5 tests are registry entries); this
 * annotation has its name and the attributes the tests use, so the test classes are the same on every version branch.
 * {@link GameTestCases} turns the methods into registry entries.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GameTest {
    /** Id of the test structure ({@link OMTGameTests#EMPTY_STRUCTURE}). */
    String template();

    /**
     * Tests of one batch run side by side; a test that must not see the others gets its own. Each batch is a test
     * environment, because the framework batches tests by environment.
     */
    String batch() default GameTestCases.DEFAULT_BATCH;

    /** Time limit in ticks; the test fails if it has not succeeded by then (the old annotation's default). */
    int timeoutTicks() default 100;
}
