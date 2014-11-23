package modularTurrets.proxies;

import modularTurrets.misc.RegisterSounds;
import modularTurrets.misc.ToolTips;
import modularTurrets.models.renderers.TileEntityRenderers;
import modularTurrets.models.renderers.itemRenderers.ItemRenderers;
import modularTurrets.projectiles.renderers.ProjectileRenderers;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void initRenderers() {

	TileEntityRenderers.init();
	ItemRenderers.init();
	ProjectileRenderers.init();

	ToolTips tooltips = new ToolTips();
	MinecraftForge.EVENT_BUS.register(tooltips);

    }

    @Override
    public void initSounds() {
	MinecraftForge.EVENT_BUS.register(new RegisterSounds());

    }

}