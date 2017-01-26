package omtteam.openmodularturrets;


import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import omtteam.openmodularturrets.client.gui.ModularTurretsTab;
import omtteam.openmodularturrets.compatability.ModCompatibility;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.handler.GuiHandler;
import omtteam.openmodularturrets.proxy.CommonProxy;
import omtteam.openmodularturrets.reference.Reference;

import java.util.logging.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = "1.7.10", dependencies = Reference.DEPENDENCIES)
public class OpenModularTurrets {
    @SuppressWarnings("unused")
    @Mod.Instance(Reference.MOD_ID)
    public static OpenModularTurrets instance;

    @SuppressWarnings({"CanBeFinal", "unused"})
    @SidedProxy(clientSide = "omtteam.openmodularturrets.proxy.ClientProxy", serverSide = "omtteam.openmodularturrets.proxy" + "" + ".CommonProxy")
    private static CommonProxy proxy;

    public static CreativeTabs modularTurretsTab;
    @SuppressWarnings("FieldCanBeLocal")
    private GuiHandler gui;
    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = Logger.getLogger(Reference.NAME);
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        gui = new GuiHandler();
        modularTurretsTab = new ModularTurretsTab(Reference.MOD_ID);
        proxy.preInit();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, gui);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModCompatibility.checkForMods();
        ModCompatibility.performModCompat();
        proxy.init();
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ConfigHandler.parseLists();
    }
}