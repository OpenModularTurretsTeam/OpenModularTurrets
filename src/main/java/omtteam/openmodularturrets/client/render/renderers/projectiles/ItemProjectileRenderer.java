package omtteam.openmodularturrets.client.render.renderers.projectiles;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import omtteam.openmodularturrets.entity.projectiles.DisposableTurretProjectile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

class ItemProjectileRenderer extends Render {

    private final RenderItem itemRenderer;

    public ItemProjectileRenderer(RenderManager renderManagerIn, RenderItem itemRendererIn) {
        super(renderManagerIn);
        this.itemRenderer = itemRendererIn;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */

    @SuppressWarnings({"unchecked", "unused"})
    public void doRender(DisposableTurretProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {

        if (entity.getFramesRendered() <= 1) {
            entity.setFramesRendered(entity.getFramesRendered() + 1);
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.itemRenderer.renderItem(entity.getItemBound().getItem(), ItemCameraTransforms.TransformType.GROUND);

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    protected ResourceLocation getEntityTexture(Entity entity) {
        return new ResourceLocation("");
    }
}
