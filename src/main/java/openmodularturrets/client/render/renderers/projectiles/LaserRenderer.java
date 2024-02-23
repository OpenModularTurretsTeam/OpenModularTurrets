package openmodularturrets.client.render.renderers.projectiles;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import openmodularturrets.entity.projectiles.LaserProjectile;
import openmodularturrets.reference.ModInfo;

@SideOnly(Side.CLIENT)
class LaserRenderer extends Render {

    private static final ResourceLocation laserTextures = new ResourceLocation(
            ModInfo.ID.toLowerCase() + ":textures/blocks/laser.png");

    private void renderLaser(LaserProjectile par1EntityRocket, double par2, double par4, double par6, float par9) {
        for (int i = 0; i <= 20; i++) {
            par1EntityRocket.worldObj.spawnParticle("reddust", par2, par4, par6, 1.0D, 1.0D, 1.0D);
        }

        this.bindEntityTexture(par1EntityRocket);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) par2, (float) par4 + 0.30F, (float) par6);
        GL11.glRotatef(
                par1EntityRocket.prevRotationYaw
                        + (par1EntityRocket.rotationYaw - par1EntityRocket.prevRotationYaw) * par9
                        - 90.0F,
                0.0F,
                1.0F,
                0.0F);
        GL11.glRotatef(
                par1EntityRocket.prevRotationPitch
                        + (par1EntityRocket.rotationPitch - par1EntityRocket.prevRotationPitch) * par9,
                0.0F,
                0.0F,
                1.0F);

        Tessellator tessellator = Tessellator.instance;
        byte b0 = 0;
        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = (float) (b0 * 10) / 32.0F;
        float f5 = (float) (5 + b0 * 10) / 32.0F;

        float f10 = 0.05625F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        float f11 = (float) par1EntityRocket.arrowShake - par9;

        if (f11 > 0.0F) {
            float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
            GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(0.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);

        for (int i = 0; i < 4; ++i) {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-16.0D, -2.0D, 0.0D, (double) f2, (double) f4);
            tessellator.addVertexWithUV(16.0D, -2.0D, 0.0D, (double) f3, (double) f4);
            tessellator.addVertexWithUV(16.0D, 2.0D, 0.0D, (double) f3, (double) f5);
            tessellator.addVertexWithUV(-16.0D, 2.0D, 0.0D, (double) f2, (double) f5);
            tessellator.draw();
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    private ResourceLocation getLaserTextures() {
        return laserTextures;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return this.getLaserTextures();
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.renderLaser((LaserProjectile) par1Entity, par2, par4, par6, par9);
    }
}
