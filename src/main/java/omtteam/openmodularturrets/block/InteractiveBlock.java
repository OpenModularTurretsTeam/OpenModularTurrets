package omtteam.openmodularturrets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import omtteam.omlib.compat.VersionCompat;
import omtteam.omlib.compat.block.BlockCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.security.OwnedHost;
import omtteam.openmodularturrets.security.PlayerAdapter;

/**
 * Right-click handling through the loader's block-use event (Fabric {@code UseBlockCallback}, NeoForge
 * {@code PlayerInteractEvent.RightClickBlock}), which is the same on 1.20.1 and 1.21 (vanilla's {@code use} /
 * {@code useWithoutItem} / {@code useItemOn} differ). Runs before vanilla, also while sneaking; returning
 * {@link InteractionResult#PASS} lets vanilla continue (e.g. placing a held block).
 */
public interface InteractiveBlock {
    InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit);

    /**
     * Handles a right-click on any block: the ownership wand in hand first, then claiming a block without owner, then
     * a memory card in hand, then {@link #onUse} if the block is an {@code InteractiveBlock}. Called by the loader's
     * block-use event on both sides; {@link InteractionResult#PASS} if not handled.
     */
    static InteractionResult handleUse(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        if (player.isSpectator()) return InteractionResult.PASS;
        InteractionResult wand = WandInteraction.use(player, level, hand, hit.getBlockPos());
        if (wand != InteractionResult.PASS) return wand;
        InteractionResult claim = claim(player, level, hand, hit.getBlockPos());
        if (claim != InteractionResult.PASS) return claim;
        // a memory card in hand takes priority over what the block does on right-click
        InteractionResult card = MemoryCardInteraction.use(player, level, hand, hit.getBlockPos());
        if (card != InteractionResult.PASS) return card;
        BlockState state = level.getBlockState(hit.getBlockPos());
        if (state.getBlock() instanceof InteractiveBlock block) {
            return block.onUse(state, level, hit.getBlockPos(), player, hand, hit);
        }
        return InteractionResult.PASS;
    }

    /**
     * A main-hand right-click without sneaking on an owned block without owner (on a turret: its base) claims it and
     * the unowned blocks connected to it ({@link ClaimSpread}); the click does nothing else. The loaders' fake players
     * only claim if {@code general.fakePlayersCanClaim} is set.
     */
    private static InteractionResult claim(Player player, Level level, InteractionHand hand, BlockPos pos) {
        if (hand != InteractionHand.MAIN_HAND || player.isShiftKeyDown()) return InteractionResult.PASS;
        BlockPos target = ClaimSpread.ownerPos(level, pos);
        if (!(level.getBlockEntity(target) instanceof OwnedHost host) || host.getOwner() != null) return InteractionResult.PASS;
        if (VersionCompat.isFakePlayer(player) && !OMTConfig.get().general.fakePlayersCanClaim) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            ClaimSpread.Result result = ClaimSpread.claim(level, target, PlayerAdapter.identity(player));
            String key = result.limitReached() ? "message.openmodularturrets.claimed_limit" : "message.openmodularturrets.claimed";
            player.displayClientMessage(Component.translatable(key, result.claimed()), true);
        }
        return BlockCompat.sidedSuccess(level.isClientSide());
    }
}
