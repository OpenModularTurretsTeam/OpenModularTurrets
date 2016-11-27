package omtteam.openmodularturrets.proxy;

import omtteam.openmodularturrets.ModularTurrets;
import omtteam.openmodularturrets.handler.DungeonLootHandler;
import omtteam.openmodularturrets.handler.NetworkingHandler;
import omtteam.openmodularturrets.handler.ProjectileEntityHandler;
import omtteam.openmodularturrets.handler.recipes.RecipeHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;

public class CommonProxy {
    public void preInit(){
        ModItems.init();
        ModBlocks.initTileEntities();
        ModBlocks.initBlocks();
        ModSounds.init();
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