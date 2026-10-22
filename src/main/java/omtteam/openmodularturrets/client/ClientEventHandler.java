package omtteam.openmodularturrets.client;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.OpenModularTurrets;

/** OMT's client-only events, registered by the client proxy. */
@SideOnly(Side.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public void renderRegisterEvent(ModelRegistryEvent event) {
        OpenModularTurrets.proxy.initModelLoaders();
    }
}
