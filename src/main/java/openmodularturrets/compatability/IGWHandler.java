package openmodularturrets.compatability;

import igwmod.api.WikiRegistry;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.handler.ConfigHandler;

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

        // this is a workaround until Minemaarten enables putting the wiki stuff in our own asset folder, for now its
        // in a subfolder in the igwmod assets
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.leverBlock, "openmodularturrets/block/leverBlock");

        if (ConfigHandler.getDisposableTurretSettings().isEnabled()) {
            WikiRegistry.registerBlockAndItemPageEntry(Blocks.disposableItemTurret,
                    "openmodularturrets/block/disposableItemTurret");
        }

        if (ConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            WikiRegistry.registerBlockAndItemPageEntry(Blocks.potatoCannonTurret,
                    "openmodularturrets/block/potatoCannonTurret");
        }

        if (ConfigHandler.getMachineGunTurretSettings().isEnabled()) {
            WikiRegistry.registerBlockAndItemPageEntry(Blocks.machineGunTurret,
                    "openmodularturrets/block/machineGunTurret");
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            WikiRegistry.registerBlockAndItemPageEntry(Blocks.incendiaryTurret,
                    "openmodularturrets/block/incendiaryTurret");
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            WikiRegistry.registerBlockAndItemPageEntry(Blocks.grenadeLauncherTurret,
                    "openmodularturrets/block/grenadeLauncherTurret");
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            WikiRegistry.registerBlockAndItemPageEntry(Blocks.relativisticTurret,
                    "openmodularturrets/block/relativisticTurret");
        }
        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            WikiRegistry.registerBlockAndItemPageEntry(Blocks.rocketTurret, "openmodularturrets/block/rocketTurret");
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            WikiRegistry.registerBlockAndItemPageEntry(Blocks.teleporterTurret, "openmodularturrets/block/teleporterTurret");
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            WikiRegistry.registerBlockAndItemPageEntry(Blocks.laserTurret, "openmodularturrets/block/laserTurret");
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            WikiRegistry.registerBlockAndItemPageEntry(Blocks.railGunTurret, "openmodularturrets/block/railGunTurret");
        }

        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierOne, "openmodularturrets/block/turretBase");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierTwo, "openmodularturrets/block/turretBase");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierThree, "openmodularturrets/block/turretBase");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierFour, "openmodularturrets/block/turretBase");
        WikiRegistry.registerBlockAndItemPageEntry(Blocks.turretBaseTierFive, "openmodularturrets/block/turretBase");
    }
}
