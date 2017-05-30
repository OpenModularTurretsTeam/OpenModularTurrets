package omtteam.openmodularturrets.proxy;

import net.minecraftforge.common.MinecraftForge;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.EventHandler;
import omtteam.openmodularturrets.handler.NetworkingHandler;
import omtteam.openmodularturrets.handler.recipes.RecipeHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModEntities;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;

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
        NetworkingHandler.initNetworking();
    }

    public void init() {
        RecipeHandler.initRecipes();
        initEntityRenderers();
        MinecraftForge.EVENT_BUS.register(EventHandler.getInstance());
    }
}