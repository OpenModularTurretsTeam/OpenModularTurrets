package omtteam.openmodularturrets.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import omtteam.openmodularturrets.block.base.TurretBaseBlock;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.ExpanderSetting;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.storage.SlotCapacity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Inventory expander (tiers 1–3, the 1.12.2 expander model): it has no inventory of its own, but every ammo slot of
 * a turret base and fuel slot of a generator next to it holds more ({@code expanders.tier_N.stacks}, see
 * {@link SlotCapacity}). It is placed against a turret base or generator ({@link #isMachine}) and counts for every
 * machine next to it; a block behind it does not count. Two expanders of one tier fit into one block space, like
 * slabs ({@link #DOUBLE}: a second plate stacked onto the first); such a block counts as two
 * ({@value SlotCapacity#MAX_STACKED} plates on a face at most). {@link #FACING} points at the block the plates are
 * placed against.
 */
public class InventoryExpanderBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    /** A second plate stacked onto the first, on its side away from {@link #FACING}. */
    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");

    /** Depth of a plate in pixels. */
    private static final int PLATE_DEPTH = 6;
    /** The 12 × 12 × 6 plate against the {@link #FACING} side. */
    private static final Map<Direction, VoxelShape> SHAPES = shapes(PLATE_DEPTH);
    /** Both plates of a double block, 12 deep from the {@link #FACING} side. */
    private static final Map<Direction, VoxelShape> DOUBLE_SHAPES = shapes(2 * PLATE_DEPTH);
    /** The defaults the blocks are registered with; the configured values replace them while the game runs. */
    private static final ConfigData.Expanders DEFAULTS = new ConfigData.Expanders();

    private final int tier;

    public InventoryExpanderBlock(int tier, Properties properties) {
        super(properties);
        this.tier = tier;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(DOUBLE, false));
    }

    /** Stone like the turret bases, hardness and blast resistance of the tier's defaults (see {@link #setting}). */
    public static Properties createProperties(int tier) {
        ExpanderSetting defaults = DEFAULTS.tier(tier);
        return Properties.of()
                .mapColor(MapColor.STONE)
                .sound(SoundType.STONE)
                .strength((float) defaults.hardness, (float) defaults.explosionResistance)
                .requiresCorrectToolForDrops()
                .noOcclusion();
    }

    /** A 12 × 12 box {@code depth} pixels deep against each side. */
    private static Map<Direction, VoxelShape> shapes(int depth) {
        int far = 16 - depth;
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        shapes.put(Direction.NORTH, Block.box(2, 2, 0, 14, 14, depth));
        shapes.put(Direction.SOUTH, Block.box(2, 2, far, 14, 14, 16));
        shapes.put(Direction.WEST, Block.box(0, 2, 2, depth, 14, 14));
        shapes.put(Direction.EAST, Block.box(far, 2, 2, 16, 14, 14));
        shapes.put(Direction.DOWN, Block.box(2, 0, 2, 14, depth, 14));
        shapes.put(Direction.UP, Block.box(2, far, 2, 14, 16, 14));
        return shapes;
    }

    /** 2 for a double block, otherwise 1. */
    public static int plates(BlockState state) {
        return state.getValue(DOUBLE) ? 2 : 1;
    }

    /** 1–3. */
    public int getTier() {
        return tier;
    }

    /** Stacks, hardness and blast resistance of the tier from the config in effect (the server's on clients). */
    public ExpanderSetting setting() {
        return OMTConfig.get().expanders.tier(tier);
    }

    /**
     * The tiers of the plates of {@code state} as a machine next to it counts them: one or two (a double block), empty
     * if it is no expander.
     */
    public static List<Integer> plateTiers(BlockState state) {
        if (!(state.getBlock() instanceof InventoryExpanderBlock expander)) return List.of();
        return Collections.nCopies(plates(state), expander.tier);
    }

    /** Whether an expander may be placed against {@code state}: a turret base or a generator. */
    public static boolean isMachine(BlockState state) {
        return state.getBlock() instanceof TurretBaseBlock || state.getBlock() instanceof GeneratorBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, DOUBLE);
    }

    /**
     * The plate goes against the clicked block, which must be a turret base or generator ({@link #isMachine}); placed
     * into a single expander of this tier (see {@link #canBeReplaced}), it becomes that block's second plate.
     */
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState existing = context.getLevel().getBlockState(context.getClickedPos());
        if (existing.is(this)) return existing.setValue(DOUBLE, true);
        Direction facing = context.getClickedFace().getOpposite();
        if (!isMachine(context.getLevel().getBlockState(context.getClickedPos().relative(facing)))) return null;
        return defaultBlockState().setValue(FACING, facing);
    }

    /**
     * Like a slab: an expander of the same tier stacks onto a single one when the player clicks its open side, that is
     * the plate's open face or the face of the block it lies against around the plate (with the clicked face pointing
     * away from {@link #FACING} either way).
     */
    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (state.getValue(DOUBLE) || !context.getItemInHand().is(asItem())) return false;
        return context.getClickedFace() == state.getValue(FACING).getOpposite();
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return (state.getValue(DOUBLE) ? DOUBLE_SHAPES : SHAPES).get(state.getValue(FACING));
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    /** Mines with the configured hardness: vanilla's progress scaled by registered / configured hardness. */
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float progress = super.getDestroyProgress(state, player, level, pos);
        float hardness = (float) setting().hardness;
        // hardness 0 breaks at once, as for vanilla blocks
        return hardness > 0.0F ? progress * (float) DEFAULTS.tier(tier).hardness / hardness : 1.0F;
    }

    /** The configured blast resistance. */
    @Override
    public float getExplosionResistance() {
        return (float) setting().explosionResistance;
    }
}
