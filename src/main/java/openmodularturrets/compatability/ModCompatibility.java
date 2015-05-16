package openmodularturrets.compatability;

import cpw.mods.fml.common.Loader;

/**
 * Created by Keridos on 23/01/2015.
 * This Class
 */
public class ModCompatibility {
	
    public static boolean IGWModLoaded = false;
    public static IGWHandler igwHandler = null;
    
    public static void checkForMods() {
        IGWModLoaded = Loader.isModLoaded("IGWMod");
        new IGWSupportNotifier();
    }
}
