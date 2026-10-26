package omtteam.openmodularturrets.gametest;

/**
 * Implemented by every test class (replaces Fabric's {@code FabricGameTest}, so the tests run on every loader).
 * Before 1.21.11, NeoForge and Forge test classes also carry {@code @GameTestHolder(OMT.MOD_ID)} and
 * {@code @PrefixGameTestTemplate(false)}; from 1.21.11 on the test mod registers the tests itself
 * ({@code GameTestCases}).
 */
public interface OMTGameTests {
    /**
     * The empty 8 × 8 × 8 test structure ({@code data/openmodularturrets/structure/empty.nbt}, {@code structures/} on
     * 1.20.1). Fabric and 1.21.11 take the full id; NeoForge and Forge before put the namespace of
     * {@code @GameTestHolder} in front.
     */
    //? if fabric || >=1.21.11 {
    String EMPTY_STRUCTURE = "openmodularturrets:empty";
    //?} else {
    /*String EMPTY_STRUCTURE = "empty";
    *///?}
}
