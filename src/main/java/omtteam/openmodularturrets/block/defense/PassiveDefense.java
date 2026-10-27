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
import omtteam.omlib.blockentity.OwnedBlockEntity;
import omtteam.omlib.compat.block.BlockCompat;
import omtteam.openmodularturrets.config.DefenseSetting;
import omtteam.openmodularturrets.config.OMTConfig;

/**
 * Behaviour shared by the OMPD blocks (OMPD {@code BlockHelper}).
 */
public final class PassiveDefense {
    private PassiveDefense() {
    }

    /**
     * OMPD: stone sound, pickaxe level 2 (iron, via tags), hardness = blast resistance per tier. The blocks replace
     * hardness and blast resistance with the config values while the game runs ({@link #setting}).
     */
    public static BlockBehaviour.Properties properties(DefenseTier tier) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .sound(SoundType.STONE)
                .strength(tier.getStrength(), tier.getStrength())
                .requiresCorrectToolForDrops();
    }

    /**
     * Hardness, blast resistance and fence damage of the tier from the config in effect (the server's on clients,
     * which predict the mining progress), so a changed config needs no restart.
     */
    public static DefenseSetting setting(DefenseTier tier) {
        return OMTConfig.get().passiveDefense.tier(tier.getNumber());
    }

    /**
     * The mining progress with the configured hardness: {@code progress} is the vanilla (or loader) progress, computed
     * from the hardness the block was registered with, so it is scaled by registered / configured hardness. Tool and
     * harvest checks stay as the loader made them.
     */
    public static float destroyProgress(float progress, DefenseTier tier) {
        float hardness = (float) setting(tier).hardness;
        // hardness 0 breaks at once, as for vanilla blocks
        return hardness > 0.0F ? progress * tier.getStrength() / hardness : 1.0F;
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
                player.displayClientMessage(Component.translatable("message.openmodularturrets.not_owner"), false);
            }
        }
        return BlockCompat.sidedSuccess(level.isClientSide());
    }
}
