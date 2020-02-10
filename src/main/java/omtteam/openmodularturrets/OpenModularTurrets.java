package omtteam.openmodularturrets;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import omtteam.openmodularturrets.client.gui.ModularTurretsTab;
import omtteam.openmodularturrets.compatibility.ModCompatibility;
import omtteam.openmodularturrets.fixes.TENameFix;
import omtteam.openmodularturrets.handler.OMTGuiHandler;
import omtteam.openmodularturrets.proxy.CommonProxy;
import omtteam.openmodularturrets.reference.Reference;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.ACCEPTED_MINECRAFT_VERSION, dependencies = Reference.DEPENDENCIES)
public class OpenModularTurrets {
    public static final CreativeTabs modularTurretsTab = ModularTurretsTab.getInstance();
    public final static int DATA_VERSION = 1;
    @SuppressWarnings("unused")
    @Mod.Instance(Reference.MOD_ID)
    public static OpenModularTurrets instance;
    @SuppressWarnings({"CanBeFinal", "unused"})
    @SidedProxy(clientSide = "omtteam.openmodularturrets.proxy.ClientProxy", serverSide = "omtteam.openmodularturrets.proxy.ServerProxy")
    public static CommonProxy proxy;
    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit();
        ModCompatibility.checkForMods();
        ModCompatibility.preinit();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, OMTGuiHandler.getInstance());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModCompatibility.init();
        proxy.init();

        // Fixers
        CompoundDataFixer fixer = FMLCommonHandler.instance().getDataFixer();
        ModFixs modFixer = fixer.init(Reference.MOD_ID, DATA_VERSION);
        modFixer.registerFix(FixTypes.BLOCK_ENTITY, new TENameFix());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }
}