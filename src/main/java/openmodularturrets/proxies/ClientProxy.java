package openmodularturrets.proxies;

import openmodularturrets.models.renderers.TileEntityRenderers;
import openmodularturrets.models.renderers.itemRenderers.ItemRenderers;
import openmodularturrets.projectiles.renderers.ProjectileRenderers;

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