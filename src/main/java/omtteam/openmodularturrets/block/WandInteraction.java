package omtteam.openmodularturrets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import omtteam.omlib.compat.block.BlockCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.item.OwnershipWandItem;
import omtteam.openmodularturrets.security.OwnedHost;
import omtteam.openmodularturrets.security.PlayerAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * What the {@link OwnershipWandItem} does to an owned block (on a turret: its base). Right-click makes the player the
 * owner with an empty trust list, sneak-right-click removes the owner (and the trust list). With spreading on, the
 * connected blocks with the same owner as the clicked one change too ({@link ClaimSpread}, up to
 * {@code bases.claimSpreadLimit}); other players' blocks next to them stay as they are. Only game masters may use it.
 */
public final class WandInteraction {
    private WandInteraction() {
    }

    /** Called before any other block interaction; {@link InteractionResult#PASS} if no wand or no owned block. */
    public static InteractionResult use(Player player, Level level, InteractionHand hand, BlockPos pos) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof OwnershipWandItem)) return InteractionResult.PASS;
        BlockPos target = ClaimSpread.ownerPos(level, pos);
        if (!(level.getBlockEntity(target) instanceof OwnedHost clicked)) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            if (OwnershipWandItem.mayUse(player)) {
                apply(player, level, target, clicked, player.isShiftKeyDown(), OwnershipWandItem.isSpreading(stack));
            } else {
                player.displayClientMessage(Component.translatable("message.openmodularturrets.wand.game_masters_only"), true);
            }
        }
        return BlockCompat.sidedSuccess(level.isClientSide());
    }

    private static void apply(Player player, Level level, BlockPos target, OwnedHost clicked, boolean reset, boolean spread) {
        UUID owner = ownerUuid(clicked);
        List<OwnedHost> hosts = spread
                ? ClaimSpread.connected(level, target, OMTConfig.get().bases.claimSpreadLimit,
                host -> sameOwner(ownerUuid(host), owner))
                : List.of(clicked);
        PlayerIdentity identity = PlayerAdapter.identity(player);
        for (OwnedHost host : hosts) {
            // clearing first also empties the trust list of a block that gets a new owner
            host.clearOwner();
            if (!reset) host.setOwner(identity);
        }
        String key = reset ? "message.openmodularturrets.wand.reset" : "message.openmodularturrets.wand.claimed";
        player.displayClientMessage(Component.translatable(key, hosts.size()), true);
    }

    @Nullable
    private static UUID ownerUuid(OwnedHost host) {
        PlayerIdentity owner = host.getOwner();
        return owner == null ? null : owner.uuid();
    }

    private static boolean sameOwner(@Nullable UUID a, @Nullable UUID b) {
        return a == null ? b == null : a.equals(b);
    }
}
