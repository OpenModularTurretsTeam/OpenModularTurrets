package omtteam.openmodularturrets.block;

import omtteam.omlib.block.InteractiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import omtteam.omlib.compat.VersionCompat;
import omtteam.omlib.compat.block.BlockCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.security.OwnedHost;
import omtteam.openmodularturrets.security.PlayerAdapter;

/**
 * Claiming: a main-hand right-click without sneaking on an owned block without owner (on a turret: its base) claims it
 * and the unowned blocks connected to it ({@link ClaimSpread}); the click does nothing else. The loaders' fake players
 * only claim if {@code general.fakePlayersCanClaim} is set. Registered as an {@code InteractiveBlock.Interceptor}.
 */
public final class ClaimInteraction {
    private ClaimInteraction() {
    }

    public static InteractionResult use(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || player.isShiftKeyDown()) return InteractionResult.PASS;
        BlockPos target = ClaimSpread.ownerPos(level, hit.getBlockPos());
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
