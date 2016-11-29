package omtteam.openmodularturrets.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import omtteam.openmodularturrets.ModularTurrets;
import omtteam.openmodularturrets.handler.DungeonLootHandler;
import omtteam.openmodularturrets.handler.NetworkingHandler;
import omtteam.openmodularturrets.handler.ProjectileEntityHandler;
import omtteam.openmodularturrets.handler.recipes.RecipeHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.tileentity.turrets.DisposableItemTurretTileEntity;
import omtteam.openmodularturrets.tileentity.turrets.PotatoCannonTurretTileEntity;

public class CommonProxy {
    public void preInit(){
        ModItems.init();
        ModBlocks.initTileEntities();
        ModBlocks.initBlocks();
        ModSounds.init();
        RecipeHandler.initRecipes();
        ProjectileEntityHandler.registerProjectiles(ModularTurrets.instance);

        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.disposableItemTurret), 0, DisposableItemTurretTileEntity.class);
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.disposableItemTurret), 0, new ModelResourceLocation(ModBlocks.disposableItemTurret.getRegistryName(), "inventory"));

        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.potatoCannonTurret), 0, PotatoCannonTurretTileEntity.class);
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.potatoCannonTurret), 0, new ModelResourceLocation(ModBlocks.potatoCannonTurret.getRegistryName(), "inventory"));
    }
    public void initRenderers() {

    }

    public void initHandlers() {

        DungeonLootHandler.init();
        NetworkingHandler.initNetworking();
    }
}