package omtteam.openmodularturrets.client.render.entity;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.client.render.EntityRendererRegistrar;
import omtteam.openmodularturrets.registry.ModEntities;

/**
 * Projectile renderers, following the 1.12.2 {@code ProjectileRenderers}.
 */
public final class ProjectileRenderers {
    private static final ResourceLocation BOLT_TEXTURE = OMT.id("textures/entity/projectile/bolt.png");
    /** The bolt is shorter than an arrow (10 instead of 16 pixels). */
    private static final int BOLT_HALF_LENGTH = 5;
    private static final ResourceLocation ROCKET_TEXTURE = OMT.id("textures/entity/projectile/rocket.png");
    private static final ResourceLocation BULLET_TEXTURE = OMT.id("textures/entity/projectile/bullet.png");
    /** Vanilla's thrown snowball is drawn at half a block. */
    private static final float BULLET_SIZE = 0.5F;

    private ProjectileRenderers() {
    }

    /** The plasma shot's model layer is registered with the other layers ({@code OMTClient#registerModelLayers}). */
    public static void register(EntityRendererRegistrar registrar) {
        registrar.register(ModEntities.BULLET, context -> new BillboardRenderer<>(context, BULLET_TEXTURE, BULLET_SIZE));
        // 1.12.2 RenderSnowball with the ammo item
        registrar.register(ModEntities.GRENADE, ThrownItemRenderer::new);
        registrar.register(ModEntities.BLAZING_CLAY, ThrownItemRenderer::new);
        registrar.register(ModEntities.ARROW, context -> new ArrowShapedRenderer<>(context, BOLT_TEXTURE, 0, BOLT_HALF_LENGTH));
        registrar.register(ModEntities.ROCKET, context -> new ArrowShapedRenderer<>(context, ROCKET_TEXTURE, 2));
        registrar.register(ModEntities.PLASMA, PlasmaRenderer::new);
        // shown by the item entity they carry (1.12.2: the item itself was the visual)
        registrar.register(ModEntities.POTATO, NoopRenderer::new);
        registrar.register(ModEntities.DISPOSABLE, NoopRenderer::new);
    }
}
