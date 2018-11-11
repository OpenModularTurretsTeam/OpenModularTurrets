package omtteam.openmodularturrets.client.render.renderers.projectiles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.entity.projectiles.RocketProjectile;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
class RocketRenderer extends Render {

    private final ResourceLocation texture = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/rocket.png"));

    public RocketRenderer() {
        super(Minecraft.getMinecraft().getRenderManager());
    }

    @SuppressWarnings("unused")
    private void renderRocket(RocketProjectile par1EntityRocket, double x, double y, double z, float entityYaw, float partialTicks) {

        if (par1EntityRocket.getFramesRendered() <= 1) {
            par1EntityRocket.setFramesRendered(par1EntityRocket.getFramesRendered() + 1);
            return;
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(par1EntityRocket.prevRotationYaw + (par1EntityRocket.rotationYaw - par1EntityRocket.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(par1EntityRocket.prevRotationPitch + (par1EntityRocket.rotationPitch - par1EntityRocket.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();

        GlStateManager.enableRescaleNormal();
        float f9 = (float) par1EntityRocket.arrowShake - partialTicks;

        if (f9 > 0.0F) {
            float f10 = -MathHelper.sin(f9 * 3.0F) * f9;
            GlStateManager.rotate(f10, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.05625F, 0.05625F, 0.05625F);
        GlStateManager.translate(-4.0F, 0.0F, 0.0F);

        GlStateManager.glNormal3f(0.05625F, 0.0F, 0.0F);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(-7.0D, -2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
        vertexbuffer.pos(-7.0D, -2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
        vertexbuffer.pos(-7.0D, 2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
        vertexbuffer.pos(-7.0D, 2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
        tessellator.draw();
        GlStateManager.glNormal3f(-0.05625F, 0.0F, 0.0F);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(-7.0D, 2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
        vertexbuffer.pos(-7.0D, 2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
        vertexbuffer.pos(-7.0D, -2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
        vertexbuffer.pos(-7.0D, -2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
        tessellator.draw();

        for (int j = 0; j < 4; ++j) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.glNormal3f(0.0F, 0.0F, 0.05625F);
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos(-8.0D, -2.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
            vertexbuffer.pos(8.0D, -2.0D, 0.0D).tex(0.5D, 0.0D).endVertex();
            vertexbuffer.pos(8.0D, 2.0D, 0.0D).tex(0.5D, 0.15625D).endVertex();
            vertexbuffer.pos(-8.0D, 2.0D, 0.0D).tex(0.0D, 0.15625D).endVertex();
            tessellator.draw();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.renderRocket((RocketProjectile) par1Entity, par2, par4, par6, par8, par9);
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }
}
