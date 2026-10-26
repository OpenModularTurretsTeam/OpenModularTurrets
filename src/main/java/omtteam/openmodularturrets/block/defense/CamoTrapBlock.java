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
import omtteam.openmodularturrets.camo.CamoBlock;
import omtteam.openmodularturrets.camo.CamoLightProperties;
import omtteam.openmodularturrets.camo.CamoRules;
import omtteam.openmodularturrets.compat.BlockCodec;
import omtteam.openmodularturrets.compat.CompatEntityBlock;
import omtteam.openmodularturrets.compat.block.BlockCompat;
import omtteam.openmodularturrets.menu.CamoMenus;
import omtteam.openmodularturrets.security.PlayerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Camouflage trap (OMPD {@code BlockCamoTrap} + OMLib {@code BlockAbstractCamoTileEntity}): looks like another full
 * block, but only its owner and trusted players can stand on it; everyone else falls through. Its admins manage the
 * trusted players.
 * <p>
 * An admin right-clicks it to open the camouflage menu, where the disguise, its light value and its light opacity
 * are set (the same menu as on turret bases). It mines like the block it imitates (OMPD switched the harvest tool
 * to the camouflage's tool). Sneaking skips the menu, so blocks can still be placed against it.
 */
public class CamoTrapBlock extends CompatEntityBlock implements InteractiveBlock, CamoBlock {
    private static final BlockCodec<CamoTrapBlock> CODEC = codecOf(CamoTrapBlock::new);

    public CamoTrapBlock(Properties properties) {
        super(CODEC, properties);
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
    protected int lightAbsorbed(BlockState state, int vanilla) {
        return CamoLightProperties.lightBlock(state);
    }

    /** Fully transparent traps let sky light through, like glass. */
    @Override
    protected boolean skylightPropagates(BlockState state, boolean vanilla) {
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
            if (placer instanceof Player player && level.getBlockEntity(pos) instanceof CamoTrapBlockEntity trap) {
                trap.getTrustManager().setOwner(PlayerAdapter.identity(player));
            }
        }
    }

    /** OMPD {@code addCollisionBoxToList}: solid only for the owner and trusted players. */
    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof Player player
                && level.getBlockEntity(pos) instanceof CamoTrapBlockEntity trap && trap.isTrusted(player)) {
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

    /** OMPD {@code getPickBlock}: picking gives the camouflage, so the trap is not revealed. */
    @Override
    protected ItemStack pickItem(BlockGetter level, BlockPos pos, ItemStack picked) {
        return CamoRules.pickItem(level, pos, picked);
    }

    /**
     * Admins (the owner always is one) open the camouflage menu with the main hand; everyone else gets nothing, so a
     * trap does not reveal itself.
     */
    @Override
    public InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || player.isShiftKeyDown()
                || !(level.getBlockEntity(pos) instanceof CamoTrapBlockEntity trap) || !trap.canSetCamo(player)) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            CamoMenus.open(serverPlayer, trap);
        }
        return BlockCompat.sidedSuccess(level.isClientSide());
    }
}
