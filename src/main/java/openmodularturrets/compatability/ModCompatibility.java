package openmodularturrets.compatability;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;
import openmodularturrets.reference.ModInfo;

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
	public static boolean ComputercraftLoaded = false;
	public static IGWHandler igwHandler = null;

	public static void checkForMods() {
		ThermalExpansionLoaded = Loader.isModLoaded("ThermalExpansion");
		if (ThermalExpansionLoaded) {
			Logger.getGlobal().info(
					"Hi there, dV=V0B(t1-t0)! (Found ThermalExpansion");
		}

		EnderIOLoaded = Loader.isModLoaded("EnderIO");
		if (EnderIOLoaded) {
			Logger.getGlobal()
					.info("Not sure if iron ingot, or electrical steel ingot... (Found EnderIO)");
		}

		MekanismLoaded = Loader.isModLoaded("Mekanism");
		if (MekanismLoaded) {
			Logger.getGlobal().info("Mur omsimu, plz. (Found Mekanism)");
		}

		ThaumcraftLoaded = Loader.isModLoaded("Thaumcraft");
		if (ThaumcraftLoaded) {
			Logger.getGlobal().info(
					"Afrikaners is plesierig. (Found Thaumcraft)");
		}
		
		OpenComputersLoaded = Loader.isModLoaded("OpenComputers");
		ComputercraftLoaded = Loader.isModLoaded("ComputerCraft");
		
		if(OpenComputersLoaded || ComputercraftLoaded)
		{
			
		}

		IGWModLoaded = Loader.isModLoaded("IGWMod");

	}

	/*public static String getBuildNumber() {
		Map<String, ModContainer> modList = Loader.instance()
				.getIndexedModList();
		if (modList.get(ModInfo.ID).getProcessedVersion().getVersionString()
				.split("-").length > 1) {
			return modList.get(ModInfo.ID).getProcessedVersion()
					.getVersionString().split("-")[1];
		} else {
			return "0";
		}
	}*/

	private static void addVersionCheckerInfo() {
		NBTTagCompound versionchecker = new NBTTagCompound();
		versionchecker.setString("curseProjectName",
				"224663-openmodularturrets");
		versionchecker.setString("curseFilenameParser",
				"OpenModularTurrets-1.7.10-[].jar");
		versionchecker.setString("modDisplayName", "OpenModularTurrets");
		versionchecker.setString("oldVersion", ModInfo.VERSION);
		FMLInterModComms.sendRuntimeMessage("openmodularturrets",
				"VersionChecker", "addCurseCheck", versionchecker);
	}

	public static void performModCompat() {
		FMLInterModComms
				.sendMessage("Waila", "register",
						"openmodularturrets.compatability.WailaTileHandler.callbackRegister");
		new IGWSupportNotifier();
		addVersionCheckerInfo();
	}

}
