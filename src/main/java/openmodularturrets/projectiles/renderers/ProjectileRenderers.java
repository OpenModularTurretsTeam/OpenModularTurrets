package openmodularturrets.projectiles.renderers;

import openmodularturrets.items.Items;
import openmodularturrets.projectiles.BulletProjectile;
import openmodularturrets.projectiles.DisposableTurretProjectile;
import openmodularturrets.projectiles.GrenadeProjectile;
import openmodularturrets.projectiles.LaserProjectile;
import openmodularturrets.projectiles.RocketProjectile;
import net.minecraft.client.renderer.entity.RenderSnowball;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ProjectileRenderers {
	
	public static void init()
	{
		RenderingRegistry.registerEntityRenderingHandler(DisposableTurretProjectile.class, new CrapProjectileRenderer(null));
		RenderingRegistry.registerEntityRenderingHandler(RocketProjectile.class, new RocketRenderer());
		RenderingRegistry.registerEntityRenderingHandler(BulletProjectile.class, new RenderSnowball(Items.bulletThrowable));
		RenderingRegistry.registerEntityRenderingHandler(GrenadeProjectile.class, new RenderSnowball(Items.grenadeThrowable));
		RenderingRegistry.registerEntityRenderingHandler(LaserProjectile.class, new LaserRenderer());
	}

}
