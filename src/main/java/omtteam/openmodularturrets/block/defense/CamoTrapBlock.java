package omtteam.openmodularturrets.block.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import omtteam.openmodularturrets.block.InteractiveBlock;
import omtteam.openmodularturrets.blockentity.defense.CamoTrapBlockEntity;
import omtteam.openmodularturrets.blockentity.defense.OwnedBlockEntity;
import omtteam.openmodularturrets.camo.CamoBlock;
import omtteam.openmodularturrets.camo.CamoLightProperties;
import omtteam.openmodularturrets.camo.CamoMenus;
import omtteam.openmodularturrets.camo.CamoRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
//? if >=1.21 {
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.LevelReader;
//?}

/**
 * Camouflage trap (OMPD {@code BlockCamoTrap} + OMLib {@code BlockAbstractCamoTileEntity}): looks like another full
 * block, but only its owner can stand on it; everyone else falls through.
 * <p>
 * The owner right-clicks it to open the camouflage menu, where the disguise, its light value and its light opacity
 * are set (the same menu as on turret bases). It mines like the block it imitates (OMPD switched the harvest tool
 * to the camouflage's tool). Sneaking skips the menu, so blocks can still be placed against it.
 */
public class CamoTrapBlock extends BaseEntityBlock implements InteractiveBlock, CamoBlock {
    //? if >=1.21 {
    private static final MapCodec<CamoTrapBlock> CODEC = simpleCodec(CamoTrapBlock::new);
    //?}

    public CamoTrapBlock(Properties properties) {
        super(properties);
        registerDefaultState(CamoLightProperties.withDefaults(defaultBlockState()));
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
                .lightLevel(CamoLightProperties::lightLevel)
                .isViewBlocking((state, level, pos) -> false);
    }

    //? if >=1.21 {
    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    //?}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        CamoLightProperties.addTo(builder);
    }

    /** Drawn from its model, which shows the camouflage when there is one. */
    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return CamoLightProperties.lightBlock(state);
    }

    /** Fully transparent traps let sky light through, like glass. */
    @Override
    @SuppressWarnings("deprecation")
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return CamoLightProperties.lightBlock(state) == 0;
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
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return CamoRules.pickItem(level, pos, super.getCloneItemStack(level, pos, state));
    }
    //?} else {
    /*/^* OMPD {@code getPickBlock}: picking gives the camouflage, so the trap is not revealed. ^/
    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return CamoRules.pickItem(level, pos, super.getCloneItemStack(level, pos, state));
    }
    *///?}

    /**
     * The owner opens the camouflage menu with the main hand; everyone else gets nothing, so a trap does not reveal
     * itself.
     */
    @Override
    public InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || player.isShiftKeyDown()
                || !(level.getBlockEntity(pos) instanceof CamoTrapBlockEntity trap) || !trap.isOwner(player)) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            CamoMenus.open(serverPlayer, trap);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
