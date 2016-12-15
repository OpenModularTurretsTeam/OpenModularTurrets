package omtteam.openmodularturrets.compatability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import omtteam.openmodularturrets.reference.Reference;

import java.util.logging.Logger;

/**
 * Created by Keridos on 23/01/2015. This Class
 */
public class ModCompatibility {
    public static boolean IGWModLoaded = false;
    public static boolean ThermalExpansionLoaded = false;
    public static boolean EnderIOLoaded = false;
    public static boolean MekanismLoaded = false;
    public static boolean ThaumcraftLoaded = false;
    public static boolean OpenComputersLoaded = false;
    public static boolean ComputerCraftLoaded = false;
    @SuppressWarnings("unused")
    public static IGWHandler igwHandler = null;

    public static void checkForMods() {
        Logger logger = Logger.getLogger("OpenModularTurrets");

        ThermalExpansionLoaded = Loader.isModLoaded("ThermalExpansion");
        if (ThermalExpansionLoaded) {
            logger.info("Hi there, dV=V0B(t1-t0)! (Found ThermalExpansion)");
        }

        EnderIOLoaded = Loader.isModLoaded("EnderIO");
        if (EnderIOLoaded) {
            logger.info("Not sure if iron ingot, or electrical steel ingot... (Found EnderIO)");
        }

        MekanismLoaded = Loader.isModLoaded("Mekanism");
        if (MekanismLoaded) {
            logger.info("Mur omsimu, plz. (Found Mekanism)");
        }

        ThaumcraftLoaded = Loader.isModLoaded("Thaumcraft");
        if (ThaumcraftLoaded) {
            logger.info("Afrikaners is plesierig. (Found Thaumcraft)");
        }

        OpenComputersLoaded = Loader.isModLoaded("OpenComputers");
        ComputerCraftLoaded = Loader.isModLoaded("ComputerCraft");
        if (OpenComputersLoaded || ComputerCraftLoaded) {
            logger.info("Enabling LUA integration. (Found OpenComputers/ComputerCraft)");
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

    public static void performModCompat() {
        FMLInterModComms.sendMessage("Waila", "register",
                                     "omtteam.openmodularturrets.compatability.WailaTileHandler.callbackRegister");
        addVersionCheckerInfo();
        if (ComputerCraftLoaded) {
            registerCCCompat();
        }
    }

    @SuppressWarnings("EmptyMethod")
    @Optional.Method(modid = "ComputerCraft")
    private static void registerCCCompat() {
        //ComputerCraftAPI.registerPeripheralProvider(CCPeripheralProvider.getInstance());
    }
}
