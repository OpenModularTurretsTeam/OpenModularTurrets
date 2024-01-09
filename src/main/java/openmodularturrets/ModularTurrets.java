package openmodularturrets;

import net.minecraft.creativetab.CreativeTabs;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.client.gui.ModularTurretsTab;
import openmodularturrets.compatability.ModCompatibility;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.handler.DungeonLootHandler;
import openmodularturrets.handler.GuiHandler;
import openmodularturrets.handler.NetworkingHandler;
import openmodularturrets.handler.ProjectileEntityHandler;
import openmodularturrets.handler.TileEntityHandler;
import openmodularturrets.handler.recipes.RecipeHandler;
import openmodularturrets.items.Items;
import openmodularturrets.proxy.CommonProxy;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.util.CommandChangeOwner;

@Mod(
        modid = ModInfo.ID,
        name = ModInfo.NAME,
        version = ModInfo.VERSION,
        acceptedMinecraftVersions = "1.7.10",
        dependencies = ModInfo.DEPENDENCIES)
public class ModularTurrets {

    @Instance(ModInfo.ID)
    public static ModularTurrets instance;

    @SidedProxy(
            clientSide = "openmodularturrets.proxy.ClientProxy",
            serverSide = "openmodularturrets.proxy" + "" + ".CommonProxy")
    public static CommonProxy proxy;

    public static CreativeTabs modularTurretsTab;
    private GuiHandler gui;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        gui = new GuiHandler();
        modularTurretsTab = new ModularTurretsTab(ModInfo.ID);
        ModCompatibility.checkForMods();
        Items.init();
        Blocks.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModCompatibility.performModCompat();
        NetworkingHandler.initNetworking();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, gui);
        TileEntityHandler.init();
        proxy.initRenderers();
        proxy.initHandlers();
        RecipeHandler.initRecipes();
        ProjectileEntityHandler.registerProjectiles(this);
        DungeonLootHandler.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ModCompatibility.fixIC2Loading();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandChangeOwner());
    }
}
