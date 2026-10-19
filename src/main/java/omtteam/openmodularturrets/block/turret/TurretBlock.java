package omtteam.openmodularturrets.block.turret;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.compat.BlockCodec;
import omtteam.openmodularturrets.compat.CompatEntityBlock;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A turret, one block per {@link TurretType}. Ported from 1.12.2 {@code BlockAbstractTurretHead}.
 * <p>
 * {@link #FACING} points away from the base, which is at {@code pos.relative(FACING.getOpposite())}. Turrets are
 * unbreakable (1.12.2 {@code setBlockUnbreakable}) unless {@code turrets.turretBreakable}; they are removed
 * with the base GUI's "Drop turrets" button or drop when their base disappears.
 */
public class TurretBlock extends CompatEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    /** 1.12.2 {@code CONCEALED}: retracted into the base by the concealer addon (no shape). */
    public static final BooleanProperty CONCEALED = BooleanProperty.create("concealed");
    /** Bounding box 0.2–0.8 on every axis. */
    private static final VoxelShape SHAPE = Block.box(3.2, 3.2, 3.2, 12.8, 12.8, 12.8);
    /** Hardness when {@code turretBreakable}. */
    private static final float BREAKABLE_HARDNESS = 3.0F;

    private static final BlockCodec<TurretBlock> CODEC = codecOf("turret",
            Codec.STRING.xmap(id -> TurretType.byId(id).orElseThrow(), TurretType::getId),
            TurretBlock::getTurretType, TurretBlock::new);

    private final TurretType turretType;

    public TurretBlock(TurretType turretType, Properties properties) {
        super(CODEC, properties);
        this.turretType = turretType;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP).setValue(CONCEALED, false));
    }

    /**
     * Unbreakable unless {@code turretBreakable} (read once at startup, like the base's {@code baseBreakable}); when
     * breakable, it mines like the lever (hardness 3, pickaxe). Blast resistance stays at the 1.12.2 6000000.
     */
    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .strength(OMTConfig.get().turrets.turretBreakable ? BREAKABLE_HARDNESS : -1.0F, 6000000.0F)
                .noOcclusion()
                .pushReaction(PushReaction.BLOCK)
                .isValidSpawn((state, level, pos, entityType) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false);
    }

    public TurretType getTurretType() {
        return turretType;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CONCEALED);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(CONCEALED) ? Shapes.empty() : SHAPE;
    }

    /** Drawn only by the block entity renderer (1.12.2: invisible block + TESR). */
    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TurretBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.TURRET,
                level.isClientSide() ? TurretBlockEntity::clientTick : TurretBlockEntity::serverTick);
    }

    /**
     * 1.12.2 {@code canPlaceBlockAt}: there must be a base next to the turret with a free turret slot for this type.
     * The clicked base is preferred; otherwise the first neighbouring base in {@link Direction} order is used.
     * Disabled turrets, turrets whose tier the base cannot hold and turrets on a face they cannot mount on
     * ({@link TurretType#canMountOn}) cannot be placed.
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!OMTConfig.get().turrets.get(turretType).enabled) return null;
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction clicked = context.getClickedFace();
        if (canAttachTo(level, pos, clicked)) {
            return defaultBlockState().setValue(FACING, clicked);
        }
        for (Direction direction : Direction.values()) {
            if (canAttachTo(level, pos, direction.getOpposite())) {
                return defaultBlockState().setValue(FACING, direction.getOpposite());
            }
        }
        return null;
    }

    /** @param facing direction from the base to the turret */
    private boolean canAttachTo(Level level, BlockPos pos, Direction facing) {
        if (!turretType.canMountOn(Face.valueOf(facing.name()))) return false;
        if (!(level.getBlockEntity(pos.relative(facing.getOpposite())) instanceof TurretBaseBlockEntity base)) return false;
        TurretBaseLogic logic = base.getLogic();
        return logic.supportsTurret(turretType) && logic.getRemainingTurretSlots(turretType) > 0;
    }

    /** 1.12.2 {@code onNeighborChange}: a turret whose base is gone drops itself. */
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbourBlock, BlockPos neighbourPos,
                                boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighbourBlock, neighbourPos, movedByPiston);
        BlockPos basePos = pos.relative(state.getValue(FACING).getOpposite());
        if (!level.isClientSide() && neighbourPos.equals(basePos)
                && !(level.getBlockEntity(basePos) instanceof TurretBaseBlockEntity)) {
            level.destroyBlock(pos, true);
        }
    }
}
