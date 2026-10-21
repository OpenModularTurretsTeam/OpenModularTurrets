package omtteam.openmodularturrets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import omtteam.openmodularturrets.item.MemoryCardInteraction;

/**
 * Right-click handling through the loader's block-use event (Fabric {@code UseBlockCallback}, NeoForge
 * {@code PlayerInteractEvent.RightClickBlock}), which is the same on 1.20.1 and 1.21 (vanilla's {@code use} /
 * {@code useWithoutItem} / {@code useItemOn} differ). Runs before vanilla, also while sneaking; returning
 * {@link InteractionResult#PASS} lets vanilla continue (e.g. placing a held block).
 */
public interface InteractiveBlock {
    InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit);

    /**
     * Handles a right-click on any block: a memory card in hand first, then {@link #onUse} if the block is an
     * {@code InteractiveBlock}. Called by the loader's block-use event on both sides; {@link InteractionResult#PASS}
     * if not handled.
     */
    static InteractionResult handleUse(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        if (player.isSpectator()) return InteractionResult.PASS;
        // a memory card in hand takes priority over what the block does on right-click
        InteractionResult card = MemoryCardInteraction.use(player, level, hand, hit.getBlockPos());
        if (card != InteractionResult.PASS) return card;
        BlockState state = level.getBlockState(hit.getBlockPos());
        if (state.getBlock() instanceof InteractiveBlock block) {
            return block.onUse(state, level, hit.getBlockPos(), player, hand, hit);
        }
        return InteractionResult.PASS;
    }
}
