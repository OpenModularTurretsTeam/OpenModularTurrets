package omtteam.openmodularturrets.client.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Arrow geometry (two crossed fins at the back and four crossed side quads), as vanilla's {@code RenderArrow}, for
 * projectiles that are no {@code EntityArrow} (1.20.1+ {@code ArrowShapedRenderer}). The geometry is stretched to
 * {@code halfLength}, so shorter projectiles such as the crossbow bolt can use it as well.
 */
@SideOnly(Side.CLIENT)
public class ArrowShapedRenderer<T extends Entity> extends Render<T> {
    /** Vanilla's arrow texture is 32 pixels wide; the shaft takes {@code 2 * halfLength} of them. */
    private static final float TEXTURE_WIDTH = 32.0F;
    private final ResourceLocation texture;
    /** Half the length of the shaft in texture pixels (vanilla arrow: 8). */
    private final int halfLength;

    public ArrowShapedRenderer(RenderManager renderManager, ResourceLocation texture, int halfLength) {
        super(renderManager);
        this.texture = texture;
        this.halfLength = halfLength;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.bindEntityTexture(entity);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks
                - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch)
                * partialTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.enableRescaleNormal();
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.05625F, 0.05625F, 0.05625F);
        GlStateManager.translate(-halfLength / 2.0F, 0.0F, 0.0F);

        double fin = 1 - halfLength; // the fins sit one pixel in front of the back end (vanilla: -7)
        GlStateManager.glNormal3f(0.05625F, 0.0F, 0.0F);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(fin, -2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
        buffer.pos(fin, -2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
        buffer.pos(fin, 2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
        buffer.pos(fin, 2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
        tessellator.draw();
        GlStateManager.glNormal3f(-0.05625F, 0.0F, 0.0F);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(fin, 2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
        buffer.pos(fin, 2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
        buffer.pos(fin, -2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
        buffer.pos(fin, -2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
        tessellator.draw();

        double shaftU = halfLength * 2 / TEXTURE_WIDTH; // vanilla: 0.5
        for (int i = 0; i < 4; i++) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.glNormal3f(0.0F, 0.0F, 0.05625F);
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-halfLength, -2.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
            buffer.pos(halfLength, -2.0D, 0.0D).tex(shaftU, 0.0D).endVertex();
            buffer.pos(halfLength, 2.0D, 0.0D).tex(shaftU, 0.15625D).endVertex();
            buffer.pos(-halfLength, 2.0D, 0.0D).tex(0.0D, 0.15625D).endVertex();
            tessellator.draw();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    protected ResourceLocation getEntityTexture(T entity) {
        return texture;
    }
}
