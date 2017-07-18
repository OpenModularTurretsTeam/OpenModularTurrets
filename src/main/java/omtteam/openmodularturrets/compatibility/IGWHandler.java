package omtteam.openmodularturrets.compatibility;

import igwmod.api.WikiRegistry;
import omtteam.openmodularturrets.init.ModBlocks;

/**
 * Created by Keridos on 23/01/2015.
 * This Class
 */
public class IGWHandler {
    private static IGWHandler instance = null;

    private IGWHandler() {
        initTab();
    }

    public static IGWHandler getInstance() {
        if (instance == null) {
            instance = new IGWHandler();
        }
        return instance;
    }

    private void initTab() {
        WikiRegistry.registerWikiTab(new OpenModularTurretsWikiTab());

        WikiRegistry.registerBlockAndItemPageEntry(ModBlocks.turretBase, "block/turret_base");
    }
}
