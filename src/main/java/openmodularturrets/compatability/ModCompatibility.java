package openmodularturrets.compatability;

import cpw.mods.fml.common.Loader;

import java.util.logging.Logger;

/**
 * Created by Keridos on 23/01/2015.
 * This Class
 */
public class ModCompatibility {
	
    public static boolean IGWModLoaded = false;
    public static boolean ThermalExpansionLoaded = false;
    public static boolean EnderIOLoaded = false;
    public static boolean MekanismLoaded = false;
    public static boolean ThaumcraftLoaded = false;
    public static IGWHandler igwHandler = null;
    
    public static void checkForMods() {
        ThermalExpansionLoaded = Loader.isModLoaded("ThermalExpansion");
        if(ThermalExpansionLoaded){
            Logger.getGlobal().info("Hi there, ?? = ??0?T (Found ThermalExpansion");
        }
        EnderIOLoaded = Loader.isModLoaded("EnderIO");
        if(EnderIOLoaded){
            Logger.getGlobal().info("Not sure if iron ingot, or electrical steel ingot... (Found EnderIO)");
        }
        MekanismLoaded = Loader.isModLoaded("Mekanism");
        if(MekanismLoaded){
            Logger.getGlobal().info("Mur omsimu, plz. (Found Mekanism)");
        }
        ThaumcraftLoaded = Loader.isModLoaded("Thaumcraft");
        if(ThaumcraftLoaded){
            Logger.getGlobal().info("Ek het geweet ek kon nie die enigste modding saffer wees nie. (Found Thaumcraft)");
        }
        IGWModLoaded = Loader.isModLoaded("IGWMod");
        new IGWSupportNotifier();
    }
}
