package openmodularturrets.client.render.renderers.projectiles;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.renderer.entity.RenderSnowball;
import openmodularturrets.entity.projectiles.*;
import openmodularturrets.items.Items;

public class ProjectileRenderers {
    public static void init() {
        RenderingRegistry.registerEntityRenderingHandler(DisposableTurretProjectile.class,
                                                         new ItemProjectileRenderer(null));
        RenderingRegistry.registerEntityRenderingHandler(RocketProjectile.class, new RocketRenderer());
        RenderingRegistry.registerEntityRenderingHandler(BulletProjectile.class,
                                                         new RenderSnowball(Items.bulletThrowable));
        RenderingRegistry.registerEntityRenderingHandler(GrenadeProjectile.class,
                                                         new RenderSnowball(Items.grenadeThrowable));
        RenderingRegistry.registerEntityRenderingHandler(BlazingClayProjectile.class,
                                                         new RenderSnowball(Items.blazingClayCraftable));
        RenderingRegistry.registerEntityRenderingHandler(LaserProjectile.class, new LaserRenderer());
    }
}
