package omtteam.openmodularturrets.proxy;

import net.minecraftforge.common.MinecraftForge;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.OMTEventHandler;
import omtteam.openmodularturrets.handler.OMTNetworkingHandler;
import omtteam.openmodularturrets.handler.recipes.RecipeHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModEntities;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.util.OMTFakePlayer;

public class CommonProxy {
    public void preInit() {
        ModItems.init();
        ModBlocks.initBlocks();
        ModBlocks.initTileEntities();
        ModSounds.init();
        ModEntities.registerProjectiles(OpenModularTurrets.instance);
        initTileRenderers();
        initHandlers();
    }

    protected void initTileRenderers() {

    }

    protected void initEntityRenderers() {

    }

    protected void initHandlers() {
        OMTNetworkingHandler.initNetworking();
    }

    public void init() {
        RecipeHandler.initRecipes();
        initEntityRenderers();
        MinecraftForge.EVENT_BUS.register(OMTEventHandler.getInstance());
        OMTFakePlayer.init();
    }
}