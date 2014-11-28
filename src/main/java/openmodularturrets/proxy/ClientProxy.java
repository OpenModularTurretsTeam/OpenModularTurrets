package openmodularturrets.proxy;

import openmodularturrets.client.render.renderers.blockitem.TileEntityRenderers;
import openmodularturrets.client.render.renderers.items.ItemRenderers;
import openmodularturrets.client.render.renderers.projectiles.ProjectileRenderers;

public class ClientProxy extends CommonProxy {

    @Override
    public void initRenderers() {
        TileEntityRenderers.init();
        ItemRenderers.init();
        ProjectileRenderers.init();

        //ToolTips tooltips = new ToolTips();
        //MinecraftForge.EVENT_BUS.register(tooltips);
    }
}