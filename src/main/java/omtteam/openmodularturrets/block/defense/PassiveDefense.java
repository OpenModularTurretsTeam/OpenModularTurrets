package omtteam.openmodularturrets.block.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import omtteam.openmodularturrets.blockentity.defense.OwnedBlockEntity;

/**
 * Behaviour shared by the OMPD blocks (OMPD {@code BlockHelper}).
 */
public final class PassiveDefense {
    private PassiveDefense() {
    }

    /** OMPD: stone sound, pickaxe level 2 (iron, via tags), hardness = blast resistance per tier. */
    public static BlockBehaviour.Properties properties(DefenseTier tier) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .sound(SoundType.STONE)
                .strength(tier.getStrength(), tier.getStrength())
                .requiresCorrectToolForDrops();
    }

    /**
     * OMPD {@code BlockHelper.onBlockActivated}: sneak-right-click with an empty main hand picks the block up (it
     * drops as an item) if the player owns it (or nobody does); otherwise a "not yours" message is shown.
     */
    public static InteractionResult tryPickUp(Level level, BlockPos pos, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown() || hand != InteractionHand.MAIN_HAND || !player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            OwnedBlockEntity owned = level.getBlockEntity(pos) instanceof OwnedBlockEntity be ? be : null;
            if (owned == null || owned.isOwner(player)) {
                level.destroyBlock(pos, true);
            } else {
                player.sendSystemMessage(Component.translatable("message.openmodularturrets.not_owner"));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
