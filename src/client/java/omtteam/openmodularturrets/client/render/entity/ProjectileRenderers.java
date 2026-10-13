package omtteam.openmodularturrets.client.render.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.registry.ModEntities;

/**
 * Projectile renderers, following the 1.12.2 {@code ProjectileRenderers}.
 */
public final class ProjectileRenderers {
    private static final ResourceLocation ARROW_TEXTURE = VersionCompat.id("minecraft", "textures/entity/projectiles/arrow.png");
    private static final ResourceLocation ROCKET_TEXTURE = OMT.id("textures/entity/projectile/rocket.png");
    private static final ResourceLocation BULLET_TEXTURE = OMT.id("textures/entity/projectile/bullet.png");
    /** Vanilla's thrown snowball is drawn at half a block. */
    private static final float BULLET_SIZE = 0.5F;

    private ProjectileRenderers() {
    }

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(PlasmaRenderer.LAYER, PlasmaRenderer::createLayer);
        EntityRendererRegistry.register(ModEntities.BULLET, context -> new BillboardRenderer<>(context, BULLET_TEXTURE, BULLET_SIZE));
        // 1.12.2 RenderSnowball with the ammo item
        EntityRendererRegistry.register(ModEntities.GRENADE, ThrownItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.BLAZING_CLAY, ThrownItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.ARROW, context -> new ArrowShapedRenderer<>(context, ARROW_TEXTURE, 0));
        EntityRendererRegistry.register(ModEntities.ROCKET, context -> new ArrowShapedRenderer<>(context, ROCKET_TEXTURE, 2));
        EntityRendererRegistry.register(ModEntities.PLASMA, PlasmaRenderer::new);
        // shown by the item entity they carry (1.12.2: the item itself was the visual)
        EntityRendererRegistry.register(ModEntities.POTATO, NoopRenderer::new);
        EntityRendererRegistry.register(ModEntities.DISPOSABLE, NoopRenderer::new);
    }
}
