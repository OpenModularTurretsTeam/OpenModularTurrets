package omtteam.openmodularturrets.camo;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import omtteam.openmodularturrets.core.camo.CamoLight;
import net.minecraft.core.BlockPos;

/**
 * The light settings of a camouflaged block as block state properties (as in 1.12.2): the light engine reads them
 * directly, and a change travels to the clients with the normal block update.
 */
public final class CamoLightProperties {
    public static final IntegerProperty LIGHT_VALUE = IntegerProperty.create("light_value", 0, CamoLight.MAX);
    public static final IntegerProperty LIGHT_OPACITY = IntegerProperty.create("light_opacity", 0, CamoLight.MAX);

    private CamoLightProperties() {
    }

    /** Call from {@code createBlockStateDefinition} of blocks with adjustable light. */
    public static void addTo(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIGHT_VALUE, LIGHT_OPACITY);
    }

    /** The state with the default light settings; use for {@code registerDefaultState}. */
    public static BlockState withDefaults(BlockState state) {
        return write(state, CamoLight.DEFAULT);
    }

    public static boolean has(BlockState state) {
        return state.hasProperty(LIGHT_VALUE) && state.hasProperty(LIGHT_OPACITY);
    }

    public static CamoLight read(BlockState state) {
        return has(state) ? new CamoLight(state.getValue(LIGHT_VALUE), state.getValue(LIGHT_OPACITY)) : CamoLight.DEFAULT;
    }

    public static BlockState write(BlockState state, CamoLight light) {
        return has(state) ? state.setValue(LIGHT_VALUE, light.value()).setValue(LIGHT_OPACITY, light.opacity()) : state;
    }

    /** For {@code Properties.lightLevel}. */
    public static int lightLevel(BlockState state) {
        return read(state).value();
    }

    /** Light absorbed by the block (vanilla {@code getLightBlock}). */
    public static int lightBlock(BlockState state) {
        return read(state).opacity();
    }

    /** Applies new light settings to the block in the world (updates the light engine and the clients). */
    public static void apply(Level level, BlockPos pos, CamoLight light) {
        BlockState state = level.getBlockState(pos);
        if (!has(state)) return;
        BlockState changed = write(state, light);
        if (changed != state) {
            level.setBlock(pos, changed, Block.UPDATE_ALL);
        }
    }
}
