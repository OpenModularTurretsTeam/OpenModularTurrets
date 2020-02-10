package omtteam.openmodularturrets.compatibility;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import omtteam.omlib.compatibility.OMLibModCompatibility;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.compatibility.computercraft.CCPeripheralProvider;
import omtteam.openmodularturrets.compatibility.hwyla.OMTWailaWrapper;
import omtteam.openmodularturrets.compatibility.opencomputers.DriverTurretBase;
import omtteam.openmodularturrets.reference.Reference;

/**
 * Created by Keridos on 23/01/2015. This Class
 */
public class ModCompatibility {
    public static boolean ValkyrienWarfareLoaded = false;

    public static void checkForMods() {
        if (OMLibModCompatibility.ThermalExpansionLoaded) {
            OpenModularTurrets.getLogger().info("Hi there, dV=V0B(t1-t0)! (Found ThermalExpansion)");
        }
        if (OMLibModCompatibility.EnderIOLoaded) {
            OpenModularTurrets.getLogger().info("Not sure if iron ingot, or electrical steel ingot... (Found EnderIO)");
        }
        if (OMLibModCompatibility.MekanismLoaded) {
            OpenModularTurrets.getLogger().info("Mur omsimu, plz. (Found Mekanism)");
        }
        if (OMLibModCompatibility.OpenComputersLoaded || OMLibModCompatibility.ComputerCraftLoaded) {
            OpenModularTurrets.getLogger().info("Enabling LUA integration. (Found OpenComputers/ComputerCraft)");
        }

        ValkyrienWarfareLoaded = Loader.isModLoaded("valkyrienskies");
        if (ValkyrienWarfareLoaded) {
            OpenModularTurrets.getLogger().info("ValkyrienSkies Found! You have a good taste in mods");
        }
    }

    private static void addVersionCheckerInfo() {
        NBTTagCompound versionchecker = new NBTTagCompound();
        versionchecker.setString("curseProjectName", "openmodularturrets");
        versionchecker.setString("curseFilenameParser", "OpenModularTurrets-1.12.2-[].jar");
        versionchecker.setString("modDisplayName", "OpenModularTurrets");
        versionchecker.setString("oldVersion", Reference.VERSION);
        FMLInterModComms.sendRuntimeMessage("openmodularturrets", "versionChecker", "addCurseCheck", versionchecker);
    }

    public static void init() {
        addVersionCheckerInfo();
        if (OMLibModCompatibility.ComputerCraftLoaded) {
            registerCCCompat();
        }
        if (OMLibModCompatibility.OpenComputersLoaded) {
            registerOCCompat();
        }
        if (OMLibModCompatibility.WailaLoaded) {
            addWailaPlugins();
        }
    }

    @SuppressWarnings("EmptyMethod")
    public static void preinit() {

    }

    private static void addWailaPlugins() {
        OMTWailaWrapper.getInstance().register();
    }

    private static void registerOCCompat() {
        DriverTurretBase.getInstance().registerWrapper();
    }

    private static void registerCCCompat() {
        CCPeripheralProvider.getInstance().registerWrapper();
    }
}
