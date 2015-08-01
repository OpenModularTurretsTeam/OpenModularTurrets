package openmodularturrets.compatability;

import igwmod.api.WikiRegistry;
import openmodularturrets.blocks.Blocks;

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

        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierOne, "block/turretBase");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierTwo, "block/turretBase");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierThree, "block/turretBase");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierFour, "block/turretBase");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierFive, "block/turretBase");
    }
}
