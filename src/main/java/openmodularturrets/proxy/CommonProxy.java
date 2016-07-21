package openmodularturrets.proxy;

import openmodularturrets.ModularTurrets;
import openmodularturrets.handler.DungeonLootHandler;
import openmodularturrets.handler.NetworkingHandler;
import openmodularturrets.handler.ProjectileEntityHandler;
import openmodularturrets.handler.TileEntityHandler;
import openmodularturrets.handler.recipes.RecipeHandler;

public class CommonProxy {
    public void preInit(){

    }
    public void initRenderers() {

    }

    public void initHandlers() {
        TileEntityHandler.init();
        RecipeHandler.initRecipes();
        ProjectileEntityHandler.registerProjectiles(ModularTurrets.instance);
        DungeonLootHandler.init();
        NetworkingHandler.initNetworking();
    }
}