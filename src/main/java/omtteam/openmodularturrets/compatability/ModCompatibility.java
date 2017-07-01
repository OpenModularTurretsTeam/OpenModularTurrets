package omtteam.openmodularturrets.compatability;

import li.cil.oc.api.Driver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.compatability.opencomputers.DriverTurretBase;
import omtteam.openmodularturrets.reference.Reference;

import static omtteam.omlib.compatability.ModCompatibility.*;

/**
 * Created by Keridos on 23/01/2015. This Class
 */
public class ModCompatibility {
    public static boolean IGWModLoaded = false;
    public static boolean ThermalExpansionLoaded = false;
    public static boolean EnderIOLoaded = false;
    public static boolean MekanismLoaded = false;
    public static boolean ThaumcraftLoaded = false;
    public static boolean ValkyrienWarfareLoaded = false;
    @SuppressWarnings("unused")
    public static IGWHandler igwHandler = null;

    public static void checkForMods() {
        ThermalExpansionLoaded = Loader.isModLoaded("ThermalExpansion");
        if (ThermalExpansionLoaded) {
            OpenModularTurrets.getLogger().info("Hi there, dV=V0B(t1-t0)! (Found ThermalExpansion)");
        }

        EnderIOLoaded = Loader.isModLoaded("EnderIO");
        if (EnderIOLoaded) {
            OpenModularTurrets.getLogger().info("Not sure if iron ingot, or electrical steel ingot... (Found EnderIO)");
        }

        MekanismLoaded = Loader.isModLoaded("Mekanism");
        if (MekanismLoaded) {
            OpenModularTurrets.getLogger().info("Mur omsimu, plz. (Found Mekanism)");
        }

        ThaumcraftLoaded = Loader.isModLoaded("Thaumcraft");
        if (ThaumcraftLoaded) {
            OpenModularTurrets.getLogger().info("Afrikaners is plesierig. (Found Thaumcraft)");
        }

        if (OpenComputersLoaded || ComputerCraftLoaded) {
            OpenModularTurrets.getLogger().info("Enabling LUA integration. (Found OpenComputers/ComputerCraft)");
        }

        ValkyrienWarfareLoaded = Loader.isModLoaded("valkyrienwarfare");
        if (ValkyrienWarfareLoaded) {
            OpenModularTurrets.getLogger().info("Valkyrien Warfare Found! You have a good taste in mods");
        }

        IGWModLoaded = Loader.isModLoaded("IGWMod");
    }

    private static void addVersionCheckerInfo() {
        NBTTagCompound versionchecker = new NBTTagCompound();
        versionchecker.setString("curseProjectName", "224663-omtteam.openmodularturrets");
        versionchecker.setString("curseFilenameParser", "OpenModularTurrets-1.10.2-[].jar");
        versionchecker.setString("modDisplayName", "OpenModularTurrets");
        versionchecker.setString("oldVersion", Reference.VERSION);
        FMLInterModComms.sendRuntimeMessage("omtteam/openmodularturrets", "VersionChecker", "addCurseCheck", versionchecker);
    }

    public static void init() {
        FMLInterModComms.sendMessage("Waila", "register",
                "omtteam.openmodularturrets.compatability.hwyla.WailaTurretBaseHandler.callbackRegister");
        FMLInterModComms.sendMessage("Waila", "register",
                "omtteam.openmodularturrets.compatability.hwyla.WailaTurretHandler.callbackRegister");
        addVersionCheckerInfo();
        if (ComputerCraftLoaded) {
            registerCCCompat();
        }
    }

    public static void preinit() {
        if (OpenComputersLoaded) {
            registerOCCompat();
        }
    }

    @SuppressWarnings("EmptyMethod")
    @Optional.Method(modid = OCModID)
    private static void registerOCCompat() {
        Driver.add(new DriverTurretBase());
    }

    @SuppressWarnings("EmptyMethod")
    @Optional.Method(modid = CCModID)
    private static void registerCCCompat() {
        //ComputerCraftAPI.registerPeripheralProvider(CCPeripheralProvider.getInstance());
    }
}
