package omtteam.openmodularturrets.client;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.client.model.DisabledModuleModel;
import omtteam.openmodularturrets.client.render.TurretPlacementLabel;

/** OMT's client-only events, registered by the client proxy. */
@SideOnly(Side.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public void renderRegisterEvent(ModelRegistryEvent event) {
        OpenModularTurrets.proxy.initModelLoaders();
    }

    /** The "no" symbol of disabled upgrades and addons; vanilla's barrier item stitches it too, this makes sure. */
    @SubscribeEvent
    public void textureStitchEvent(TextureStitchEvent.Pre event) {
        if (event.getMap() == net.minecraft.client.Minecraft.getMinecraft().getTextureMapBlocks()) {
            event.getMap().registerSprite(DisabledModuleModel.SYMBOL);
        }
    }

    /** Upgrade and addon item models that mark disabled stacks. */
    @SubscribeEvent
    public void modelBakeEvent(ModelBakeEvent event) {
        DisabledModuleModel.wrapModels(event.getModelRegistry(), event.getModelManager().getTextureMap());
    }

    /** The reason next to OMLib's red turret placement outline. */
    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        TurretPlacementLabel.render(event.getPartialTicks());
    }
}
