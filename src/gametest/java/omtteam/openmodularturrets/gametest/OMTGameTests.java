package omtteam.openmodularturrets.gametest;

/**
 * Implemented by every test class (replaces Fabric's {@code FabricGameTest}, so the tests run on both loaders).
 * NeoForge test classes also carry {@code @GameTestHolder(OMT.MOD_ID)} and {@code @PrefixGameTestTemplate(false)}.
 */
public interface OMTGameTests {
    /**
     * The empty 8 × 8 × 8 test structure ({@code data/openmodularturrets/structure/empty.nbt}, {@code structures/} on
     * 1.20.1). Fabric takes the full id; NeoForge puts the namespace of {@code @GameTestHolder} in front.
     */
    //? if fabric {
    String EMPTY_STRUCTURE = "openmodularturrets:empty";
    //?} else {
    /*String EMPTY_STRUCTURE = "empty";
    *///?}
}
