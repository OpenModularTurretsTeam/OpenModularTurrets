package omtteam.openmodularturrets.gametest;

/**
 * Implemented by every test class (replaces Fabric's {@code FabricGameTest}, so the tests run on every loader).
 * The test mod registers the tests itself ({@code GameTestCases}).
 */
public interface OMTGameTests {
    /**
     * The empty 8 × 8 × 8 test structure ({@code data/openmodularturrets/structure/empty.nbt}), taken with its full id.
     */
    String EMPTY_STRUCTURE = "openmodularturrets:empty";
}
