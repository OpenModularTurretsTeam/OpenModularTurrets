package omtteam.openmodularturrets.client.render.renderers.projectiles;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.entity.projectiles.LaserProjectile;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
class LaserRenderer extends Render {

    LaserRenderer() {
        super(Minecraft.getMinecraft().getRenderManager());
    }

    private final ResourceLocation texture = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/laser.png"));

    @SuppressWarnings("unused")
    private void renderLaser(LaserProjectile par1EntityLaser, double x, double y, double z, float entityYaw, float partialTicks) {

        if (par1EntityLaser.getFramesRendered() <= 1) {
            par1EntityLaser.setFramesRendered(par1EntityLaser.getFramesRendered() + 1);
            return;
        }

        for (int i = 0; i <= 20; i++) {
            par1EntityLaser.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 1.0D, 1.0D, 1.0D);
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(par1EntityLaser.prevRotationYaw + (par1EntityLaser.rotationYaw - par1EntityLaser.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(par1EntityLaser.prevRotationPitch + (par1EntityLaser.rotationPitch - par1EntityLaser.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();

        GlStateManager.enableRescaleNormal();

        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.05625F, 0.05625F, 0.05625F);
        GlStateManager.translate(-4.0F, 0.0F, 0.0F);

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
    @Nonnull
    @ParametersAreNonnullByDefault
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return texture;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.renderLaser((LaserProjectile) par1Entity, par2, par4, par6, par8, par9);
    }
}
