package omtteam.openmodularturrets.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.misc.GeneratorBlockEntity;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.ExpanderSetting;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.storage.SlotCapacity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
//? if >=1.21.11 {
/*import net.minecraft.world.level.redstone.Orientation;
*///?}

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Inventory expander (tiers 1–3, the 1.12.2 expander model): it has no inventory of its own, but every ammo slot of
 * a turret base and fuel slot of a generator next to it holds more ({@code expanders.tier_N.stacks}, see
 * {@link SlotCapacity}). Up to {@value SlotCapacity#MAX_STACKED} expanders count in a line from a face, and an
 * expander counts for every machine it reaches that way. Two expanders of one tier fit into one block space, like
 * slabs ({@link #DOUBLE}: a second plate on the open side); such a block counts as two. {@link #FACING} points at the
 * block the (first) plate is placed against; it is only the look, not part of the rules.
 */
public class InventoryExpanderBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    /** A second plate against the side opposite {@link #FACING}. */
    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");

    /** The 12 × 12 × 6 plate against the {@link #FACING} side. */
    private static final Map<Direction, VoxelShape> SHAPES = shapes();
    /** Both plates of a double block, by {@link #FACING}. */
    private static final Map<Direction, VoxelShape> DOUBLE_SHAPES = doubleShapes();
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

    private static Map<Direction, VoxelShape> shapes() {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        shapes.put(Direction.NORTH, Block.box(2, 2, 0, 14, 14, 6));
        shapes.put(Direction.SOUTH, Block.box(2, 2, 10, 14, 14, 16));
        shapes.put(Direction.WEST, Block.box(0, 2, 2, 6, 14, 14));
        shapes.put(Direction.EAST, Block.box(10, 2, 2, 16, 14, 14));
        shapes.put(Direction.DOWN, Block.box(2, 0, 2, 14, 6, 14));
        shapes.put(Direction.UP, Block.box(2, 10, 2, 14, 16, 14));
        return shapes;
    }

    private static Map<Direction, VoxelShape> doubleShapes() {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            shapes.put(direction, Shapes.or(SHAPES.get(direction), SHAPES.get(direction.getOpposite())));
        }
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
     * The tiers of the expander plates in line from {@code machine} towards {@code direction}, nearest first: the
     * neighbour's, and the block's behind it if the neighbour is a single expander; at most
     * {@value SlotCapacity#MAX_STACKED}, empty if there is none. Blocks that are not loaded count as no expander (see
     * {@link #isChainLoaded}).
     */
    public static List<Integer> chainTiers(Level level, BlockPos machine, Direction direction) {
        List<Integer> tiers = new ArrayList<>(SlotCapacity.MAX_STACKED);
        BlockPos pos = machine;
        while (tiers.size() < SlotCapacity.MAX_STACKED) {
            pos = pos.relative(direction);
            if (!level.isLoaded(pos)) break;
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof InventoryExpanderBlock expander)) break;
            for (int plate = 0; plate < plates(state) && tiers.size() < SlotCapacity.MAX_STACKED; plate++) {
                tiers.add(expander.tier);
            }
        }
        return tiers;
    }

    /**
     * Whether every block {@link #chainTiers} would look at is loaded, so a machine can wait instead of losing an
     * expander in a chunk that is still loading. A line that leaves the world ends there.
     */
    public static boolean isChainLoaded(Level level, BlockPos machine, Direction direction) {
        BlockPos pos = machine;
        int plates = 0;
        while (plates < SlotCapacity.MAX_STACKED) {
            pos = pos.relative(direction);
            if (level.isOutsideBuildHeight(pos)) return true;
            if (!level.isLoaded(pos)) return false;
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof InventoryExpanderBlock)) return true;
            plates += plates(state);
        }
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, DOUBLE);
    }

    /**
     * The plate goes against the clicked block; placed into a single expander of this tier (see
     * {@link #canBeReplaced}), it becomes that block's second plate.
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState existing = context.getLevel().getBlockState(context.getClickedPos());
        if (existing.is(this)) return existing.setValue(DOUBLE, true);
        return defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }

    /**
     * Like a slab: an expander of the same tier fits into a single one if its plate would go to the free side, that
     * is when the player clicks the plate's open face or places it against the block behind the free side.
     */
    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (state.getValue(DOUBLE) || !context.getItemInHand().is(asItem())) return false;
        Direction open = state.getValue(FACING).getOpposite();
        return context.replacingClickedOnBlock() ? context.getClickedFace() == open
                : context.getClickedFace() == state.getValue(FACING);
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

    //? if >=1.21.11 {
    /*@Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbourBlock,
                                   @Nullable Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighbourBlock, orientation, movedByPiston);
        notifyMachines(level, pos);
    }
    *///?} else {
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbourBlock, BlockPos neighbourPos,
                                boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighbourBlock, neighbourPos, movedByPiston);
        notifyMachines(level, pos);
    }
    //?}

    /**
     * A neighbour of this expander changed, maybe the second expander of a chain: the machines next to this one
     * look again (a machine only hears of changes next to itself).
     */
    private static void notifyMachines(Level level, BlockPos pos) {
        if (level.isClientSide()) return;
        for (Direction direction : Direction.values()) {
            BlockPos neighbour = pos.relative(direction);
            if (!level.isLoaded(neighbour)) continue;
            if (level.getBlockEntity(neighbour) instanceof TurretBaseBlockEntity base) {
                base.onNeighbourChanged();
            } else if (level.getBlockEntity(neighbour) instanceof GeneratorBlockEntity generator) {
                generator.onNeighbourChanged();
            }
        }
    }
}
