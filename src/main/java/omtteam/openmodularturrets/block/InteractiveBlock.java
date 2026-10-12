package omtteam.openmodularturrets.block;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Right-click handling through Fabric's {@link UseBlockCallback}, whose signature is the same on 1.20.1 and 1.21
 * (vanilla's {@code use} / {@code useWithoutItem} / {@code useItemOn} differ). Runs before vanilla, also while
 * sneaking; returning {@link InteractionResult#PASS} lets vanilla continue (e.g. placing a held block).
 */
public interface InteractiveBlock {
    InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit);

    static void registerCallback() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.isSpectator()) return InteractionResult.PASS;
            BlockState state = level.getBlockState(hit.getBlockPos());
            if (state.getBlock() instanceof InteractiveBlock block) {
                return block.onUse(state, level, hit.getBlockPos(), player, hand, hit);
            }
            return InteractionResult.PASS;
        });
    }
}
