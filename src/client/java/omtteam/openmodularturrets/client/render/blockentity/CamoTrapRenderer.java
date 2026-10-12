package omtteam.openmodularturrets.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.blockentity.defense.CamoTrapBlockEntity;

/**
 * Draws the camouflage trap as the block it imitates (OMLib {@code BlockAbstractCamoTileEntity} rendered the
 * camouflage via an extended block state), or its own model without camouflage. Biome tints (grass, leaves) are
 * applied; the lighting is flat (no ambient occlusion), unlike real blocks.
 */
public class CamoTrapRenderer implements BlockEntityRenderer<CamoTrapBlockEntity> {
    private final BlockRenderDispatcher blocks;

    public CamoTrapRenderer(BlockEntityRendererProvider.Context context) {
        this.blocks = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(CamoTrapBlockEntity trap, float partialTick, PoseStack poseStack, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        BlockState shown = trap.getCamo() != null ? trap.getCamo() : trap.getBlockState();
        BakedModel model = blocks.getBlockModel(shown);
        int color = Minecraft.getInstance().getBlockColors().getColor(shown, trap.getLevel(), trap.getBlockPos(), 0);
        float r = color == -1 ? 1F : ((color >> 16) & 0xFF) / 255F;
        float g = color == -1 ? 1F : ((color >> 8) & 0xFF) / 255F;
        float b = color == -1 ? 1F : (color & 0xFF) / 255F;
        blocks.getModelRenderer().renderModel(poseStack.last(), buffers.getBuffer(RenderType.cutout()), shown, model,
                r, g, b, packedLight, packedOverlay);
    }
}
