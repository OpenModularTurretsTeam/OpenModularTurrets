package openmodularturrets.client.render.renderers.projectiles;

import net.minecraft.client.renderer.entity.RenderSnowball;

import cpw.mods.fml.client.registry.RenderingRegistry;
import openmodularturrets.entity.projectiles.BlazingClayProjectile;
import openmodularturrets.entity.projectiles.BulletProjectile;
import openmodularturrets.entity.projectiles.DisposableTurretProjectile;
import openmodularturrets.entity.projectiles.GrenadeProjectile;
import openmodularturrets.entity.projectiles.LaserProjectile;
import openmodularturrets.entity.projectiles.RocketProjectile;
import openmodularturrets.items.Items;

public class ProjectileRenderers {

    public static void init() {
        RenderingRegistry
                .registerEntityRenderingHandler(DisposableTurretProjectile.class, new ItemProjectileRenderer(null));
        RenderingRegistry.registerEntityRenderingHandler(RocketProjectile.class, new RocketRenderer());
        RenderingRegistry
                .registerEntityRenderingHandler(BulletProjectile.class, new RenderSnowball(Items.bulletThrowable));
        RenderingRegistry
                .registerEntityRenderingHandler(GrenadeProjectile.class, new RenderSnowball(Items.grenadeThrowable));
        RenderingRegistry.registerEntityRenderingHandler(
                BlazingClayProjectile.class,
                new RenderSnowball(Items.blazingClayCraftable));
        RenderingRegistry.registerEntityRenderingHandler(LaserProjectile.class, new LaserRenderer());
    }
}
