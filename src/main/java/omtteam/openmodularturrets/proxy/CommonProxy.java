package omtteam.openmodularturrets.proxy;

import net.minecraftforge.common.MinecraftForge;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.OMTEventHandler;
import omtteam.openmodularturrets.handler.OMTNetworkingHandler;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModEntities;
import omtteam.openmodularturrets.util.OMTFakePlayer;

public class CommonProxy {
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(OMTEventHandler.getInstance());
        ModBlocks.initTileEntities();
        ModEntities.registerProjectiles(OpenModularTurrets.instance);
        initHandlers();
    }

    protected void initTileRenderers() {

    }

    public void initModelLoaders() {

    }

    protected void initEntityRenderers() {

    }

    protected void initHandlers() {
        OMTNetworkingHandler.initNetworking();
    }

    public void init() {
        initEntityRenderers();
        OMTFakePlayer.init();
    }

    public void postInit() {
        OMTConfig.parseLists();
    }
}