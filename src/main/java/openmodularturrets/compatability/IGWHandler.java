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

        // this is a workaround until Minemaarten enables putting the wiki stuff in our own asset folder, for now its in a subfolder in the igwmod assets
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.disposableItemTurret, "openmodularturrets/block/disposableItemTurret");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.leverBlock, "openmodularturrets/block/leverBlock");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.grenadeLauncherTurret, "openmodularturrets/block/grenadeLauncherTurret");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.laserTurret, "openmodularturrets/block/laserTurret");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.machineGunTurret, "openmodularturrets/block/machineGunTurret");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.rocketTurret, "openmodularturrets/block/rocketTurret");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierWood, "openmodularturrets/block/turretBase");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierOne, "openmodularturrets/block/turretBase");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierTwo, "openmodularturrets/block/turretBase");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierThree, "openmodularturrets/block/turretBase");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierFour, "openmodularturrets/block/turretBase");
    }
}
