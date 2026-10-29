package omtteam.openmodularturrets.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.registry.ModItems;

@SuppressWarnings("deprecation")
public class ProjectileRenderers {
    private static final ResourceLocation BOLT_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/projectiles/bolt.png");
    /** The bolt is shorter than an arrow (10 instead of 16 pixels). */
    private static final int BOLT_HALF_LENGTH = 5;

    @SideOnly(Side.CLIENT)
    public static void init() {
        RenderingRegistry.registerEntityRenderingHandler(DisposableTurretProjectile.class, new ItemProjectileRenderer(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(RocketProjectile.class, new RocketRenderer());
        RenderingRegistry.registerEntityRenderingHandler(BulletProjectile.class, new RenderSnowball<>(Minecraft.getMinecraft().getRenderManager(), new ItemStack(ModItems.usableMetaItem, 1, 0).getItem(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(GrenadeProjectile.class, new RenderSnowball<>(Minecraft.getMinecraft().getRenderManager(), new ItemStack(ModItems.ammoMetaItem, 1, 3).getItem(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(BlazingClayProjectile.class, new RenderSnowball<>(Minecraft.getMinecraft().getRenderManager(), new ItemStack(ModItems.ammoMetaItem, 1, 0).getItem(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(PlasmaProjectile.class, new PlasmaRenderer());
        // the crossbow bolt in an arrow shape, as on 1.20.1+
        RenderingRegistry.registerEntityRenderingHandler(ArrowProjectile.class, new ArrowShapedRenderer<>(Minecraft.getMinecraft().getRenderManager(), BOLT_TEXTURE, BOLT_HALF_LENGTH));
    }
}
