package omtteam.openmodularturrets.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.entity.RenderTippedArrow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.entity.projectile.ArrowProjectile;
import omtteam.openmodularturrets.entity.projectile.BlazingClayProjectile;
import omtteam.openmodularturrets.entity.projectile.BulletProjectile;
import omtteam.openmodularturrets.entity.projectile.DisposableTurretProjectile;
import omtteam.openmodularturrets.entity.projectile.GrenadeProjectile;
import omtteam.openmodularturrets.entity.projectile.PlasmaProjectile;
import omtteam.openmodularturrets.entity.projectile.RocketProjectile;
import omtteam.openmodularturrets.registry.ModItems;

@SuppressWarnings("deprecation")
public class ProjectileRenderers {
    @SideOnly(Side.CLIENT)
    public static void init() {
        RenderingRegistry.registerEntityRenderingHandler(DisposableTurretProjectile.class, new ItemProjectileRenderer(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(RocketProjectile.class, new RocketRenderer());
        RenderingRegistry.registerEntityRenderingHandler(BulletProjectile.class, new RenderSnowball<>(Minecraft.getMinecraft().getRenderManager(), new ItemStack(ModItems.usableMetaItem, 1, 0).getItem(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(GrenadeProjectile.class, new RenderSnowball<>(Minecraft.getMinecraft().getRenderManager(), new ItemStack(ModItems.ammoMetaItem, 1, 3).getItem(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(BlazingClayProjectile.class, new RenderSnowball<>(Minecraft.getMinecraft().getRenderManager(), new ItemStack(ModItems.ammoMetaItem, 1, 0).getItem(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(PlasmaProjectile.class, new PlasmaRenderer());
        RenderingRegistry.registerEntityRenderingHandler(ArrowProjectile.class, new RenderTippedArrow(Minecraft.getMinecraft().getRenderManager()));
    }
}
