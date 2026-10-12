package omtteam.openmodularturrets.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.config.BaseSetting;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.security.PlayerInfo;
import omtteam.openmodularturrets.core.security.TrustManager;
import omtteam.openmodularturrets.network.BasePacketHandler;
import omtteam.openmodularturrets.network.PacketValidation;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.security.PlayerAdapter;
import org.jetbrains.annotations.Nullable;
//? if <1.21 {
/*import net.minecraft.world.InteractionHand;
*///?}
//? if >=1.21 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?}

/**
 * Turret base, one block per tier (1–5). Ported from oldmod {@code BlockTurretBase} (meta-based tiers flattened).
 */
public class TurretBaseBlock extends BaseEntityBlock {
    //? if >=1.21 {
    public static final MapCodec<TurretBaseBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, 5).fieldOf("tier").forGetter(TurretBaseBlock::getTier),
            propertiesCodec()
    ).apply(instance, TurretBaseBlock::new));
    //?}

    private final int tier;

    public TurretBaseBlock(int tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    /**
     * Unbreakable unless {@code bases.baseBreakable}; hardness and blast resistance per tier from the config
     * (read once at startup, like oldmod's {@code @Config.RequiresMcRestart}).
     */
    public static Properties createProperties(int tier) {
        ConfigData.Bases bases = OMTConfig.get().bases;
        BaseSetting settings = bases.tier(tier);
        Properties properties = Properties.of()
                .mapColor(MapColor.STONE)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .pushReaction(PushReaction.BLOCK)
                .isValidSpawn((state, level, pos, entityType) -> false);
        if (bases.baseBreakable) {
            properties.strength(settings.baseHardness, settings.baseBlastResistance);
        } else {
            properties.strength(-1.0F, 3600000.0F);
        }
        return properties;
    }

    //? if >=1.21 {
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    //?}

    public int getTier() {
        return tier;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TurretBaseBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.TURRET_BASE, TurretBaseBlockEntity::serverTick);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return canPlaceAt(context.getLevel(), context.getClickedPos()) ? defaultBlockState() : null;
    }

    /**
     * oldmod {@code canPlaceBlockAt}: no other base may touch the position or any of its neighbours, so a turret can
     * never be attached to two bases.
     */
    public static boolean canPlaceAt(LevelReader level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighbour = pos.relative(direction);
            if (isBase(level, neighbour)) return false;
            for (Direction second : Direction.values()) {
                if (isBase(level, neighbour.relative(second))) return false;
            }
        }
        return true;
    }

    private static boolean isBase(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof TurretBaseBlock;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TurretBaseBlockEntity base) {
            if (placer instanceof Player player) {
                base.getTrustManager().setOwner(PlayerAdapter.identity(player));
            }
            base.onNeighbourChanged();
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbourBlock, BlockPos neighbourPos,
                                boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighbourBlock, neighbourPos, movedByPiston);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TurretBaseBlockEntity base) {
            base.onNeighbourChanged();
        }
    }

    //? if >=1.21 {
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        return onUse(level, pos, player);
    }
    //?} else {
    /*@Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        return hand == InteractionHand.MAIN_HAND ? onUse(level, pos, player) : InteractionResult.PASS;
    }
    *///?}

    /**
     * oldmod {@code onBlockActivated}: right-click (not sneaking) opens the GUI if the player has access, otherwise
     * a permission message is shown. The check runs on the server, which then tells the client what to open.
     */
    private InteractionResult onUse(Level level, BlockPos pos, Player player) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)
                || !(level.getBlockEntity(pos) instanceof TurretBaseBlockEntity base)) {
            return InteractionResult.PASS;
        }
        PlayerInfo info = PlayerAdapter.info(serverPlayer);
        TrustManager trust = base.getTrustManager();
        trust.refreshName(info);
        if (trust.canOpenGui(info)) {
            BasePacketHandler.openMenu(serverPlayer, base);
        } else {
            PacketValidation.sendNoPermission(serverPlayer);
        }
        return InteractionResult.CONSUME;
    }

    /** Drops the whole inventory (ammo, addons, upgrades) when the base is removed (OMLib {@code dropItems}). */
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof TurretBaseBlockEntity base) {
            Containers.dropContents(level, pos, base.getInventory());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
