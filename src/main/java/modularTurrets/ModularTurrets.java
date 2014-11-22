package modularTurrets;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import modularTurrets.blocks.Blocks;
import modularTurrets.gui.GuiHandler;
import modularTurrets.items.Items;
import modularTurrets.misc.ConfigHandler;
import modularTurrets.misc.ModularTurretsTab;
import modularTurrets.misc.Recipes;
import modularTurrets.projectiles.BulletProjectile;
import modularTurrets.projectiles.GrenadeProjectile;
import modularTurrets.projectiles.LaserProjectile;
import modularTurrets.projectiles.RocketProjectile;
import modularTurrets.proxies.CommonProxy;
import modularTurrets.tileentity.TileEntities;
import net.minecraft.creativetab.CreativeTabs;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, acceptedMinecraftVersions = "1.7.10")
public class ModularTurrets {

    @SidedProxy(clientSide = "modularTurrets.proxies.ClientProxy", serverSide = "modularTurrets.proxies.CommonProxy")
    public static CommonProxy proxy;

    @Instance("ModularTurrets")
    public static ModularTurrets instance;
    public GuiHandler gui = new GuiHandler();

    public static CreativeTabs modularTurretsTab = new ModularTurretsTab(ModInfo.NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(event.getSuggestedConfigurationFile());

        Items.init();
        Blocks.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, gui);

        TileEntities.init();

        proxy.initRenderers();
        Recipes.initRecipes();

        EntityRegistry.registerModEntity(RocketProjectile.class, "rocketProjectile", 1, this, 16, 1, true);
        EntityRegistry.registerModEntity(GrenadeProjectile.class, "grenadeProjectile", 2, this, 16, 1, true);
        EntityRegistry.registerModEntity(BulletProjectile.class, "bulletProjectile", 3, this, 16, 1, true);
        EntityRegistry.registerModEntity(LaserProjectile.class, "laserProjectile", 4, this, 16, 1, true);
    }
}