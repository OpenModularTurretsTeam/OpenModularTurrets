package omtteam.openmodularturrets.proxy;

import net.minecraftforge.common.MinecraftForge;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.EventHandler;
import omtteam.openmodularturrets.handler.NetworkingHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModEntities;
import omtteam.openmodularturrets.util.OMTFakePlayer;

public class CommonProxy {
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(EventHandler.getInstance());
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
        NetworkingHandler.initNetworking();
    }

    public void init() {
        //RecipeHandler.initRecipes();
        initEntityRenderers();
        OMTFakePlayer.init();
    }
}