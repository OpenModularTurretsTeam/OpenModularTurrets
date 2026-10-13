package omtteam.openmodularturrets.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.blockentity.defense.CamoTrapBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Draws the camouflage trap as the block it imitates (OMLib {@code BlockAbstractCamoTileEntity} rendered the
 * camouflage via an extended block state), or its own model without camouflage. It is drawn the way pistons draw
 * moving blocks, so biome tints (grass, leaves) and lighting match the surrounding blocks.
 */
public class CamoTrapRenderer implements BlockEntityRenderer<CamoTrapBlockEntity, CamoTrapRenderer.State> {
    public static class State extends BlockEntityRenderState {
        @Nullable
        MovingBlockRenderState block;
    }

    public CamoTrapRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CamoTrapBlockEntity trap, State state, float partialTick, Vec3 cameraPos,
                                   @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(trap, state, partialTick, cameraPos, breakProgress);
        if (!(trap.getLevel() instanceof ClientLevel level)) {
            state.block = null;
            return;
        }
        BlockState shown = trap.getCamo() != null ? trap.getCamo() : trap.getBlockState();
        MovingBlockRenderState block = new MovingBlockRenderState();
        block.randomSeedPos = trap.getBlockPos();
        block.blockPos = trap.getBlockPos();
        block.blockState = shown;
        block.biome = level.getBiome(trap.getBlockPos());
        block.cardinalLighting = level.cardinalLighting();
        block.lightEngine = level.getLightEngine();
        state.block = block;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.block != null) {
            // light comes from the level (like a piston's moving block); 0 = no outline
            collector.submitMovingBlock(poseStack, state.block, 0);
        }
    }
}
