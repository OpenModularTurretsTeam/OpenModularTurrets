package openmodularturrets.client.render.renderers.projectiles;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

class ItemProjectileRenderer extends RenderSnowball {
    public ItemProjectileRenderer(Item par1Item, RenderManager renderManager) {
        super(renderManager,par1Item, null); //TODO: fixme
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
    }
}
