package modularTurrets;

import java.util.logging.Level;

import modularTurrets.blocks.Blocks;
import modularTurrets.gui.GuiHandler;
import modularTurrets.items.Items;
import modularTurrets.misc.ConfigHandler;
import modularTurrets.misc.LogHelper;
import modularTurrets.misc.ModularTurretsTab;
import modularTurrets.misc.PacketHandler;
import modularTurrets.misc.Recipes;
import modularTurrets.misc.RegisterSounds;
import modularTurrets.misc.ToolTips;
import modularTurrets.projectiles.BulletProjectile;
import modularTurrets.projectiles.GrenadeProjectile;
import modularTurrets.projectiles.LaserProjectile;
import modularTurrets.projectiles.RocketProjectile;
import modularTurrets.proxies.CommonProxy;
import modularTurrets.tileEntities.TileEntities;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, acceptedMinecraftVersions = "1.6.4")
@NetworkMod(channels = { ModInfo.CHANNEL }, clientSideRequired = true, serverSideRequired = true, packetHandler = PacketHandler.class)
public class ModularTurrets {

    @SidedProxy(clientSide = ModInfo.PROXY_LOCATION + ".ClientProxy", serverSide = ModInfo.PROXY_LOCATION
	    + ".CommonProxy")
    public static CommonProxy proxy;

    @Instance("ModularTurrets")
    public static ModularTurrets instance;
    public GuiHandler gui = new GuiHandler();

    public static CreativeTabs modularTurretsTab = new ModularTurretsTab(
	    CreativeTabs.getNextID(), ModInfo.NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

	LogHelper.init();
	ConfigHandler.init(event.getSuggestedConfigurationFile());
	LogHelper.log(Level.INFO, "");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
	NetworkRegistry.instance().registerGuiHandler(this, gui);

	LanguageRegistry.instance().addStringLocalization(
		"itemGroup." + ModInfo.NAME, "en_US", ModInfo.NAME);

	LogHelper.log(Level.INFO, "'Roiding up items...");
	Items.init();
	Items.addNames();

	LogHelper.log(Level.INFO, "Manning up blocks...");
	Blocks.init();
	Blocks.addNames();
	
	LogHelper.log(Level.INFO, "Shaking up TileEntities...");
	TileEntities.init();

	proxy.initRenderers();
	proxy.initSounds();
	Recipes.initRecipes();
	
	LogHelper.log(Level.INFO, "Making everything else explosive...");

	EntityRegistry.registerModEntity(RocketProjectile.class,
		"rocketProjectile", 1, this, 16, 1, true);
	EntityRegistry.registerModEntity(GrenadeProjectile.class,
		"grenadeProjectile", 2, this, 16, 1, true);
	EntityRegistry.registerModEntity(BulletProjectile.class,
		"bulletProjectile", 3, this, 16, 1, true);
	EntityRegistry.registerModEntity(LaserProjectile.class,
		"laserProjectile", 4, this, 16, 1, true);

    }

    @EventHandler
    public void postInit(FMLPreInitializationEvent event) {
	
    }

}