package omtteam.openmodularturrets.compat.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block and block state differences between the supported versions.
 * <p>
 * Depends on Minecraft only (like {@code compat.nbt.NbtCompat}), so packages outside the package cycle around
 * {@code compat} (e.g. {@code camo}) may use it.
 */
public final class BlockCompat {
    private BlockCompat() {
    }

    /** Whether the state renders as a full opaque cube (1.21.11 caches it per state, older versions ask at a position). */
    public static boolean isSolidRender(BlockState state, BlockGetter level, BlockPos pos) {
        //? if >=1.21.11 {
        /*return state.isSolidRender();
        *///?} else {
        return state.isSolidRender(level, pos);
        //?}
    }

    /** How much light the state blocks, 0 to 15 (1.21.11 caches it per state, older versions ask at a position). */
    public static int lightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        //? if >=1.21.11 {
        /*return state.getLightBlock();
        *///?} else {
        return state.getLightBlock(level, pos);
        //?}
    }

    /**
     * The render shape of a block that only a block entity renderer draws (1.21.11 removed
     * {@code ENTITYBLOCK_ANIMATED}; its renderers draw whatever the shape is, so the block is invisible there).
     */
    public static RenderShape entityRenderShape() {
        //? if >=1.21.11 {
        /*return RenderShape.INVISIBLE;
        *///?} else {
        return RenderShape.ENTITYBLOCK_ANIMATED;
        //?}
    }

    /**
     * A handled interaction that swings the player's arm (1.21.11 removed {@code InteractionResult.sidedSuccess}; its
     * {@code SUCCESS} swings on the client and is the same result on both sides).
     */
    public static InteractionResult sidedSuccess(boolean clientSide) {
        //? if >=1.21.11 {
        /*return InteractionResult.SUCCESS;
        *///?} else {
        return InteractionResult.sidedSuccess(clientSide);
        //?}
    }
}
