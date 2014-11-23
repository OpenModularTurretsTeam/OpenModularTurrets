package modularTurrets.proxies;

import modularTurrets.models.renderers.TileEntityRenderers;
import modularTurrets.models.renderers.itemRenderers.ItemRenderers;
import modularTurrets.projectiles.renderers.ProjectileRenderers;

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