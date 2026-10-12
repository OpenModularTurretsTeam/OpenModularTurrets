package omtteam.openmodularturrets.block.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import omtteam.openmodularturrets.block.InteractiveBlock;
import omtteam.openmodularturrets.blockentity.defense.CamoTrapBlockEntity;
import omtteam.openmodularturrets.blockentity.defense.OwnedBlockEntity;
import org.jetbrains.annotations.Nullable;
//? if >=1.21 {
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.LevelReader;
//?}

/**
 * Camouflage trap (OMPD {@code BlockCamoTrap} + OMLib {@code BlockAbstractCamoTileEntity}): looks like another full
 * block, but only its owner can stand on it; everyone else falls through.
 * <p>
 * The owner sets the disguise by right-clicking with a full, simple block (no block entity, not another trap) and
 * resets it by sneak-right-clicking with an empty hand. It mines like the block it imitates (OMPD switched the
 * harvest tool to the camouflage's tool).
 */
public class CamoTrapBlock extends BaseEntityBlock implements InteractiveBlock {
    //? if >=1.21 {
    private static final MapCodec<CamoTrapBlock> CODEC = simpleCodec(CamoTrapBlock::new);
    //?}

    public CamoTrapBlock(Properties properties) {
        super(properties);
    }

    /**
     * OMPD: wood material without explicit hardness (instant break when not disguised), ground sounds. The shape
     * depends on who touches it, so it must not be cached.
     */
    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.WOOD)
                .sound(SoundType.GRAVEL)
                .noOcclusion()
                .dynamicShape()
                .isViewBlocking((state, level, pos) -> false);
    }

    //? if >=1.21 {
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    //?}

    /** Drawn by the block entity renderer (camouflage or the trap's own model). */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CamoTrapBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            OwnedBlockEntity.setOwnerOnPlace(level.getBlockEntity(pos) instanceof OwnedBlockEntity be ? be : null, placer);
        }
    }

    /** OMPD {@code addCollisionBoxToList}: solid only for the owner. */
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof Player player
                && level.getBlockEntity(pos) instanceof CamoTrapBlockEntity trap && trap.isOwner(player)) {
            return Shapes.block();
        }
        return Shapes.empty();
    }

    /** Mines like the camouflage. */
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof CamoTrapBlockEntity trap && trap.getCamo() != null) {
            return trap.getCamo().getDestroyProgress(player, level, pos);
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    //? if >=1.21 {
    /** OMPD {@code getPickBlock}: picking gives the camouflage, so the trap is not revealed. */
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return cloneItem(level, pos, state);
    }
    //?} else {
    /*/^* OMPD {@code getPickBlock}: picking gives the camouflage, so the trap is not revealed. ^/
    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return cloneItem(level, pos, state);
    }
    *///?}

    private ItemStack cloneItem(BlockGetter level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof CamoTrapBlockEntity trap && trap.getCamo() != null) {
            return new ItemStack(trap.getCamo().getBlock());
        }
        return new ItemStack(this);
    }

    /** OMLib {@code handleCamoActivation}, main hand only. */
    @Override
    public InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || !(level.getBlockEntity(pos) instanceof CamoTrapBlockEntity trap)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getMainHandItem();
        BlockState camo = null;
        boolean reset = player.isShiftKeyDown() && held.isEmpty();
        if (!reset) {
            if (player.isShiftKeyDown() || !(held.getItem() instanceof BlockItem blockItem)) return InteractionResult.PASS;
            camo = camoFor(blockItem.getBlock(), new BlockPlaceContext(player, hand, held, hit), level, pos);
            if (camo == null) return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            if (trap.isOwner(player)) {
                trap.setCamo(camo);
            } else {
                player.sendSystemMessage(Component.translatable("message.openmodularturrets.not_owner"));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    /** The disguise for a held block, if allowed: a full, non-air block without block entity, not another trap. */
    @Nullable
    private static BlockState camoFor(Block block, BlockPlaceContext context, Level level, BlockPos pos) {
        if (block instanceof EntityBlock || block instanceof CamoTrapBlock) return null;
        BlockState state = block.getStateForPlacement(context);
        if (state == null) state = block.defaultBlockState();
        if (state.isAir() || !state.isCollisionShapeFullBlock(level, pos)) return null;
        return state;
    }
}
