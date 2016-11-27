package omtteam.openmodularturrets;


import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import omtteam.openmodularturrets.client.gui.ModularTurretsTab;
import omtteam.openmodularturrets.compatability.ModCompatibility;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.handler.GuiHandler;
import omtteam.openmodularturrets.proxy.CommonProxy;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.util.CommandChangeOwner;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = "1.7.10", dependencies = Reference.DEPENDENCIES)
public class ModularTurrets {
    @Mod.Instance(Reference.MOD_ID)
    public static ModularTurrets instance;

    @SidedProxy(clientSide = "omtteam.openmodularturrets.proxy.ClientProxy", serverSide = "omtteam.openmodularturrets.proxy" + "" + ".CommonProxy")
    private static CommonProxy proxy;

    public static CreativeTabs modularTurretsTab;
    private GuiHandler gui;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        gui = new GuiHandler();
        modularTurretsTab = new ModularTurretsTab(Reference.MOD_ID);
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModCompatibility.checkForMods();
        ModCompatibility.performModCompat();
        proxy.initRenderers();
        proxy.initHandlers();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, gui);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandChangeOwner());
    }
}