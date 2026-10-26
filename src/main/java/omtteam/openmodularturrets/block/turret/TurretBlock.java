package omtteam.openmodularturrets.block.turret;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import omtteam.openmodularturrets.block.UnownedMarker;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.compat.BlockCodec;
import omtteam.openmodularturrets.compat.CompatEntityBlock;
import omtteam.omlib.compat.block.BlockCompat;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.TurretPlacement;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.security.PlayerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * A turret, one block per {@link TurretType}. Ported from 1.12.2 {@code BlockAbstractTurretHead}.
 * <p>
 * {@link #FACING} points away from the base, which is at {@code pos.relative(FACING.getOpposite())}. Turrets are
 * unbreakable (1.12.2 {@code setBlockUnbreakable}) unless {@code turrets.turretBreakable}; they are removed
 * with the base GUI's "Drop turrets" button or drop when their base disappears.
 */
public class TurretBlock extends CompatEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
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

    /**
     * Turns {@link #FACING} with the block (structures, mods that move blocks such as Sable contraptions), so a
     * moved turret still faces away from its base.
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
        return state.getValue(CONCEALED) ? Shapes.empty() : SHAPE;
    }

    /** Drawn only by the block entity renderer (1.12.2: invisible block + TESR). */
    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return BlockCompat.entityRenderShape();
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
     * 1.12.2 {@code canPlaceBlockAt}: there must be a base next to the turret that takes this type on that face
     * ({@link TurretPlacement#canAttach}) and whose admins include the placing player. The clicked base is preferred;
     * otherwise the first neighbouring base in {@link Direction} order is used. Also asked on the client, by the
     * placement outline.
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction clicked = context.getClickedFace();
        Player player = context.getPlayer();
        if (canAttachTo(level, pos, clicked, player)) {
            return defaultBlockState().setValue(FACING, clicked);
        }
        for (Direction direction : Direction.values()) {
            if (canAttachTo(level, pos, direction.getOpposite(), player)) {
                return defaultBlockState().setValue(FACING, direction.getOpposite());
            }
        }
        return null;
    }

    /** @param facing direction from the base to the turret */
    private boolean canAttachTo(Level level, BlockPos pos, Direction facing, @Nullable Player player) {
        TurretPlacement.Result result = attachResult(level, pos, facing, player);
        return result != null && result.allowed();
    }

    /**
     * Whether {@code player} may attach this turret at {@code pos} to the base behind it: only the base's admins may
     * (like dropping turrets), on a base without owner only players who may use game master blocks (map makers;
     * everyone else claims it first), and placing without a player is refused; then {@link TurretPlacement#canAttach}. Also
     * asked on the client, which knows the trust list, for the reason the placement outline shows.
     *
     * @param facing direction from the base to the turret
     * @return {@code null} if there is no base at {@code pos.relative(facing.getOpposite())}
     */
    @Nullable
    public TurretPlacement.Result attachResult(Level level, BlockPos pos, Direction facing, @Nullable Player player) {
        BlockPos basePos = pos.relative(facing.getOpposite());
        if (!(level.getBlockEntity(basePos) instanceof TurretBaseBlockEntity base)) return null;
        if (player == null || !mayAttach(base, player)) {
            return TurretPlacement.Result.NO_PERMISSION;
        }
        return TurretPlacement.canAttach(turretType, Face.valueOf(facing.name()), base.getLogic().getTier(),
                attachedTurrets(level, basePos), ConfigLogicSettings.INSTANCE);
    }

    private static boolean mayAttach(TurretBaseBlockEntity base, Player player) {
        if (base.getOwner() == null) return player.canUseGameMasterBlocks();
        return base.getTrustManager().isAdmin(PlayerAdapter.info(player));
    }

    /**
     * The turrets facing away from the base at {@code basePos}, read from the world: the base's logic only knows them
     * on the server, but the placement outline asks on the client. Unloaded neighbours count as empty.
     */
    private static Map<Face, TurretType> attachedTurrets(Level level, BlockPos basePos) {
        Map<Face, TurretType> turrets = new EnumMap<>(Face.class);
        for (Direction direction : Direction.values()) {
            BlockPos neighbourPos = basePos.relative(direction);
            if (!level.isLoaded(neighbourPos)) continue;
            BlockState state = level.getBlockState(neighbourPos);
            if (state.getBlock() instanceof TurretBlock turret && state.getValue(FACING) == direction) {
                turrets.put(Face.valueOf(direction.name()), turret.getTurretType());
            }
        }
        return turrets;
    }

    /** 1.12.2 {@code onNeighborChange}: a turret whose base is gone drops itself. */
    @Override
    protected void onNeighborChanged(BlockState state, Level level, BlockPos pos, @Nullable BlockPos neighbourPos) {
        BlockPos basePos = pos.relative(state.getValue(FACING).getOpposite());
        // 1.21.11 does not tell which neighbour changed, so any update checks the base
        if (!level.isClientSide() && (neighbourPos == null || neighbourPos.equals(basePos))
                && !(level.getBlockEntity(basePos) instanceof TurretBaseBlockEntity)) {
            level.destroyBlock(pos, true);
        }
    }

    /** Sparkles while its base has no owner ({@link UnownedMarker}); not while concealed. */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (state.getValue(CONCEALED)) return;
        UnownedMarker.animateTick(level, pos, pos.relative(state.getValue(FACING).getOpposite()), random);
    }
}
