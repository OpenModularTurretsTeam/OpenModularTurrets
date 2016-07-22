package openmodularturrets.proxy;

import openmodularturrets.ModularTurrets;
import openmodularturrets.handler.DungeonLootHandler;
import openmodularturrets.handler.NetworkingHandler;
import openmodularturrets.handler.ProjectileEntityHandler;
import openmodularturrets.handler.recipes.RecipeHandler;
import openmodularturrets.init.ModBlocks;
import openmodularturrets.init.ModItems;

public class CommonProxy {
    public void preInit(){
        ModItems.init();
        ModBlocks.initTileEntities();
        ModBlocks.initBlocks();
        RecipeHandler.initRecipes();
        ProjectileEntityHandler.registerProjectiles(ModularTurrets.instance);
    }
    public void initRenderers() {

    }

    public void initHandlers() {
        DungeonLootHandler.init();
        NetworkingHandler.initNetworking();
    }
}