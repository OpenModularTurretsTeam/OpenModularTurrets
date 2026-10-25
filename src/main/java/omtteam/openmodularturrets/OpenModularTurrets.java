package omtteam.openmodularturrets;

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
import omtteam.openmodularturrets.compat.ModCompatibility;
import omtteam.openmodularturrets.config.ConfigMigration;
import omtteam.openmodularturrets.fixes.GeneratorTileEntityFix;
import omtteam.openmodularturrets.fixes.TENameFix;
import omtteam.openmodularturrets.fixes.TurretTileEntityFix;
import omtteam.openmodularturrets.handler.OMTGuiHandler;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.registry.ModularTurretsTab;
import omtteam.openmodularturrets.registry.TurretLoader;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.ACCEPTED_MINECRAFT_VERSION, dependencies = Reference.DEPENDENCIES)
public class OpenModularTurrets {
    /**
     * 1: 3.x tile entity ids; 2: one turret tile entity ({@code TurretTileEntityFix}); 3: power generator to generator
     * ({@code GeneratorTileEntityFix}).
     */
    public final static int DATA_VERSION = 3;
    @SuppressWarnings("unused")
    @Mod.Instance(Reference.MOD_ID)
    public static OpenModularTurrets instance;
    @SuppressWarnings({"CanBeFinal", "unused"})
    @SidedProxy(clientSide = "omtteam.openmodularturrets.client.ClientProxy", serverSide = "omtteam.openmodularturrets.CommonProxy")
    public static CommonProxy proxy;

    public OpenModularTurrets() {
        // before Forge loads the config into OMTConfig
        ConfigMigration.migrate();
        // created with the mod, so its place among the creative tabs stays as before
        ModularTurretsTab.getInstance();
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // turret definitions of OMT and other mods, before the blocks of the turrets are registered
        TurretLoader.load(event.getAsmData(), event.getModConfigurationDirectory());
        proxy.loadClientPlugins(event.getAsmData());
        proxy.preInit();
        ModCompatibility.checkForMods();
        ModCompatibility.preinit();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new OMTGuiHandler(proxy::getClientGuiElement));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModCompatibility.init();
        proxy.init();

        // Fixers
        CompoundDataFixer fixer = FMLCommonHandler.instance().getDataFixer();
        ModFixs modFixer = fixer.init(Reference.MOD_ID, DATA_VERSION);
        modFixer.registerFix(FixTypes.BLOCK_ENTITY, new TENameFix());
        modFixer.registerFix(FixTypes.BLOCK_ENTITY, new TurretTileEntityFix());
        modFixer.registerFix(FixTypes.BLOCK_ENTITY, new GeneratorTileEntityFix());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }
}