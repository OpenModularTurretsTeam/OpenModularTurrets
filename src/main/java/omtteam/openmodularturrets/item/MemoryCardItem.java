package omtteam.openmodularturrets.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.11 {
import net.minecraft.world.InteractionResultHolder;
//?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.compat.block.BlockCompat;
import omtteam.openmodularturrets.compat.nbt.NbtCompat;
import omtteam.openmodularturrets.core.card.MemoryCardData;

import java.util.Optional;

/**
 * Turret memory card (1.12.2 {@code UsableMetaItem} meta 2): sneak-click a turret base or camouflage trap to copy its
 * settings onto the card, click another one to write them (see {@link omtteam.openmodularturrets.block.MemoryCardInteraction}, which checks the
 * permissions). Sneak-clicking anything else clears the card.
 */
public class MemoryCardItem extends Item {
    private static final String KEY = "MemoryCard";

    public MemoryCardItem(Properties properties) {
        super(properties);
    }

    public static Optional<MemoryCardData> getData(ItemStack stack) {
        var tag = VersionCompat.getCustomData(stack);
        return tag.contains(KEY) ? MemoryCardNbt.read(NbtCompat.getCompound(tag, KEY)) : Optional.empty();
    }

    public static void setData(ItemStack stack, MemoryCardData data) {
        VersionCompat.updateCustomData(stack, tag -> tag.put(KEY, MemoryCardNbt.write(data)));
    }

    public static void clear(ItemStack stack) {
        VersionCompat.updateCustomData(stack, tag -> tag.remove(KEY));
    }

    /** Sneak-click in the air clears the card. */
    //? if >=1.21.11 {
    /*@Override
    @SuppressWarnings("NullableProblems")
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (clearInAir(level, player, player.getItemInHand(hand))) {
            return BlockCompat.sidedSuccess(level.isClientSide());
        }
        return super.use(level, player, hand);
    }
    *///?} else {
    @Override
    @SuppressWarnings("NullableProblems")
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (clearInAir(level, player, stack)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return super.use(level, player, hand);
    }
    //?}

    /** @return whether the card was cleared (a sneak-click with data on it) */
    private static boolean clearInAir(Level level, Player player, ItemStack stack) {
        if (!player.isShiftKeyDown() || getData(stack).isEmpty()) return false;
        if (!level.isClientSide()) {
            clearWithMessage(stack, player);
        }
        return true;
    }

    /** Sneak-click on any other block clears the card (turret bases and traps are handled before this). */
    @Override
    @SuppressWarnings("NullableProblems")
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player != null && player.isShiftKeyDown() && getData(stack).isPresent()) {
            if (!context.getLevel().isClientSide()) {
                clearWithMessage(stack, player);
            }
            return BlockCompat.sidedSuccess(context.getLevel().isClientSide());
        }
        return super.useOn(context);
    }

    private static void clearWithMessage(ItemStack stack, Player player) {
        clear(stack);
        player.displayClientMessage(Component.translatable("message.openmodularturrets.card_cleared"), true);
    }
}
