package modularTurrets.projectiles.renderers;

import modularTurrets.items.Items;
import modularTurrets.projectiles.BulletProjectile;
import modularTurrets.projectiles.DisposableTurretProjectile;
import modularTurrets.projectiles.GrenadeProjectile;
import modularTurrets.projectiles.LaserProjectile;
import modularTurrets.projectiles.RocketProjectile;
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
