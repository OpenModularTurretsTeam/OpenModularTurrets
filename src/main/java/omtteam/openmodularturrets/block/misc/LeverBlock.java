package omtteam.openmodularturrets.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import omtteam.openmodularturrets.block.InteractiveBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.misc.LeverBlockEntity;
import omtteam.openmodularturrets.compat.BlockCodec;
import omtteam.openmodularturrets.compat.CompatEntityBlock;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.registry.ModSounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Basic turret base crank (1.12.2 {@code LeverBlock}): placed next to a tier 1 base, each turn adds 50 energy.
 * {@link #FACING} points towards the base.
 */
public class LeverBlock extends CompatEntityBlock implements InteractiveBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    /** 1.12.2 {@code storage.receiveEnergy(50, false)} per turn (limited by the base's max input). */
    private static final long ENERGY_PER_TURN = 50;
    /** Bounding box 0.1–0.9. */
    private static final VoxelShape SHAPE = Block.box(1.6, 1.6, 1.6, 14.4, 14.4, 14.4);
    /** Placement check order: a base to the north wins over south, west and east. */
    private static final Direction[] SEARCH_ORDER = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    private static final BlockCodec<LeverBlock> CODEC = codecOf(LeverBlock::new);

    public LeverBlock(Properties properties) {
        super(CODEC, properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH));
    }

    /** Hardness 2, resistance 15, stone sound. */
    public static Properties createProperties() {
        return Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(2.0F, 15.0F).noOcclusion();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /**
     * Turns {@link #FACING} with the block (structures, mods that move blocks such as Sable contraptions), so a
     * moved crank still faces its base.
     */
    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LeverBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.LEVER, LeverBlockEntity::tick);
    }

    @Nullable
    private static TurretBaseBlockEntity tierOneBase(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TurretBaseBlockEntity base && base.getTier() == 1 ? base : null;
    }

    /** 1.12.2 {@code canPlaceBlockAt} + {@code onBlockPlacedBy}: needs a tier 1 base next to it and faces it. */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for (Direction direction : SEARCH_ORDER) {
            if (tierOneBase(context.getLevel(), context.getClickedPos().relative(direction)) != null) {
                return defaultBlockState().setValue(FACING, direction);
            }
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return tierOneBase(level, pos.relative(state.getValue(FACING))) != null;
    }

    /** 1.12.2 {@code neighborChanged}: drops when the base is gone. */
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbourBlock, BlockPos neighbourPos,
                                boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighbourBlock, neighbourPos, movedByPiston);
        if (!level.isClientSide() && !canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    /**
     * 1.12.2 {@code onBlockActivated}: a new turn (and its energy) only starts when the previous one finished. Each
     * turn that charges the base plays the windup sound.
     */
    @Override
    public InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof LeverBlockEntity lever && !lever.isTurning()) {
            TurretBaseBlockEntity base = tierOneBase(level, pos.relative(state.getValue(FACING)));
            if (base != null) {
                base.getLogic().getEnergy().insert(ENERGY_PER_TURN, false);
                level.blockEvent(pos, this, LeverBlockEntity.EVENT_TURN, 0);
                level.playSound(null, pos, ModSounds.WINDUP, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
