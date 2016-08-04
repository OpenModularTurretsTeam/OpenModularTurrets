package openmodularturrets.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import openmodularturrets.client.render.renderers.blockitem.TileEntityRenderers;
import openmodularturrets.client.render.renderers.items.ItemRenderers;
import openmodularturrets.client.render.renderers.projectiles.ProjectileRenderers;
import openmodularturrets.compatability.IGWHandler;
import openmodularturrets.compatability.ModCompatibility;

public class ClientProxy extends CommonProxy {
    @Override
    public void initRenderers() {
        TileEntityRenderers.init();
        ItemRenderers.init();
        ProjectileRenderers.init();

        //ToolTips tooltips = new ToolTips();
        //MinecraftForge.EVENT_BUS.register(tooltips);
    }

    @Override
    public void initHandlers() {
        if (ModCompatibility.IGWModLoaded) {
            ModCompatibility.igwHandler = IGWHandler.getInstance();
        }
    }

    @Override
    public World getWorld() {
        return Minecraft.getMinecraft().theWorld;
    }
}