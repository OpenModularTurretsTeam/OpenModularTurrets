package omtteam.openmodularturrets.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import omtteam.openmodularturrets.compat.VersionCompat;

/**
 * The map maker's ownership wand (creative only): right-click an owned block to become its owner, sneak-right-click to
 * remove its owner (see {@link omtteam.openmodularturrets.block.WandInteraction}); sneak-right-click into the air
 * switches whether this spreads to the connected blocks of the same owner (on by default). Only game masters
 * (operators in creative mode) can use it.
 */
public class OwnershipWandItem extends Item {
    private static final String NO_SPREAD = "OwnershipWandNoSpread";

    public OwnershipWandItem(Properties properties) {
        super(properties);
    }

    /** Whether the wand changes the connected blocks of the same owner too (the default), or only the clicked one. */
    public static boolean isSpreading(ItemStack stack) {
        return !VersionCompat.getCustomData(stack).getBoolean(NO_SPREAD);
    }

    public static void setSpreading(ItemStack stack, boolean spreading) {
        VersionCompat.updateCustomData(stack, tag -> {
            if (spreading) {
                tag.remove(NO_SPREAD);
            } else {
                tag.putBoolean(NO_SPREAD, true);
            }
        });
    }

    /** Operators in creative mode, like command and structure blocks. */
    public static boolean mayUse(Player player) {
        return player.canUseGameMasterBlocks();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    /** Sneak-right-click into the air switches spreading. */
    @Override
    @SuppressWarnings("NullableProblems")
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()
                || getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE).getType() != HitResult.Type.MISS) {
            return super.use(level, player, hand);
        }
        if (!level.isClientSide()) {
            if (mayUse(player)) {
                boolean spreading = !isSpreading(stack);
                setSpreading(stack, spreading);
                player.displayClientMessage(Component.translatable("message.openmodularturrets.wand."
                        + (spreading ? "spread_on" : "spread_off")), true);
            } else {
                player.displayClientMessage(Component.translatable("message.openmodularturrets.wand.game_masters_only"), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
