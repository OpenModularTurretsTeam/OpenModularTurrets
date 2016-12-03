package omtteam.openmodularturrets.proxy;

import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.NetworkingHandler;
import omtteam.openmodularturrets.handler.ProjectileEntityHandler;
import omtteam.openmodularturrets.handler.recipes.RecipeHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.init.ModTESRItems;

public class CommonProxy {
    public void preInit(){
        ModItems.init();
        ModBlocks.initTileEntities();
        ModBlocks.initBlocks();
        ModSounds.init();
        RecipeHandler.initRecipes();
        ProjectileEntityHandler.registerProjectiles(OpenModularTurrets.instance);
        ModTESRItems.init();
    }
    public void initRenderers() {

    }

    public void initHandlers() {
        NetworkingHandler.initNetworking();
    }
}