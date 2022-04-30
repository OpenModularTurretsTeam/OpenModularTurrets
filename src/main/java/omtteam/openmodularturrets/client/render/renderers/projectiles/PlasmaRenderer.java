package omtteam.openmodularturrets.client.render.renderers.projectiles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.client.render.models.ModelPlasmaProjectile;
import omtteam.openmodularturrets.reference.Reference;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
public class PlasmaRenderer extends Render {
    final ModelPlasmaProjectile model = new ModelPlasmaProjectile();

    private final ResourceLocation texture = (new ResourceLocation(Reference.MOD_ID + ":textures/projectiles/plasma.png"));

    public PlasmaRenderer() {
        super(Minecraft.getMinecraft().getRenderManager());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void doRender(Entity projectile, double x, double y, double z, float f3, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y - 0.9f, (float) z);
        GlStateManager.rotate(projectile.prevRotationYaw + (projectile.rotationYaw - projectile.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(projectile.prevRotationPitch + (projectile.rotationPitch - projectile.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(0.675f, 0.675f, 0.675f);
        this.model.render(projectile, 0, 0, 0, 0, 0, 0.0625f);
        GL11.glPopMatrix();
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }
}
