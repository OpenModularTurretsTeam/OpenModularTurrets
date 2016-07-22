package openmodularturrets.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import openmodularturrets.items.blocks.ItemBlockTurretBase;
import openmodularturrets.reference.Names;

public class ModBlocks {
    public static Block turretBase;
    public static Block disposableItemTurret;
    public static Block potatoCannonTurret;

    public static Block incendiaryTurret;

    public static Block machineGunTurret;
    public static Block grenadeLauncherTurret;

    public static Block relativisticTurret;

    public static Block teleporterTurret;

    public static Block rocketTurret;
    public static Block laserTurret;
    public static Block railGunTurret;
    public static Block leverBlock;

    public static Block expanderPowerTierOne;
    public static Block expanderPowerTierTwo;
    public static Block expanderPowerTierThree;
    public static Block expanderPowerTierFour;
    public static Block expanderPowerTierFive;

    public static Block expanderInvTierOne;
    public static Block expanderInvTierTwo;
    public static Block expanderInvTierThree;
    public static Block expanderInvTierFour;
    public static Block expanderInvTierFive;

    public static Block hardWallTierOne;
    public static Block hardWallTierTwo;
    public static Block hardWallTierThree;
    public static Block hardWallTierFour;
    public static Block hardWallTierFive;

    public static Block fenceTierOne;
    public static Block fenceTierTwo;
    public static Block fenceTierThree;
    public static Block fenceTierFour;
    public static Block fenceTierFive;

    public static void init() {
        turretBase = new BlockTurretBase();
        GameRegistry.registerBlock(turretBase, ItemBlockTurretBase.class, Names.Blocks.turretBase);

        /*expanderPowerTierOne = new BlockExpanderPowerTierOne();
        GameRegistry.registerBlock(expanderPowerTierOne, ItemBlockExpanderPowerTierOne.class,
                                   Names.Blocks.expanderPowerTierOne);

        expanderPowerTierTwo = new BlockExpanderPowerTierTwo();
        GameRegistry.registerBlock(expanderPowerTierTwo, ItemBlockExpanderPowerTierTwo.class,
                                   Names.Blocks.expanderPowerTierTwo);

        expanderPowerTierThree = new BlockExpanderPowerTierThree();
        GameRegistry.registerBlock(expanderPowerTierThree, ItemBlockExpanderPowerTierThree.class,
                                   Names.Blocks.expanderPowerTierThree);

        expanderPowerTierFour = new BlockExpanderPowerTierFour();
        GameRegistry.registerBlock(expanderPowerTierFour, ItemBlockExpanderPowerTierFour.class,
                                   Names.Blocks.expanderPowerTierFour);

        expanderPowerTierFive = new BlockExpanderPowerTierFive();
        GameRegistry.registerBlock(expanderPowerTierFive, ItemBlockExpanderPowerTierFive.class,
                                   Names.Blocks.expanderPowerTierFive);

        expanderInvTierOne = new BlockExpanderInvTierOne();
        GameRegistry.registerBlock(expanderInvTierOne, ItemBlockExpanderInvTierOne.class,
                                   Names.Blocks.expanderInvTierOne);

        expanderInvTierTwo = new BlockExpanderInvTierTwo();
        GameRegistry.registerBlock(expanderInvTierTwo, ItemBlockExpanderInvTierTwo.class,
                                   Names.Blocks.expanderInvTierTwo);

        expanderInvTierThree = new BlockExpanderInvTierThree();
        GameRegistry.registerBlock(expanderInvTierThree, ItemBlockExpanderInvTierThree.class,
                                   Names.Blocks.expanderInvTierThree);

        expanderInvTierFour = new BlockExpanderInvTierFour();
        GameRegistry.registerBlock(expanderInvTierFour, ItemBlockExpanderInvTierFour.class,
                                   Names.Blocks.expanderInvTierFour);

        expanderInvTierFive = new BlockExpanderInvTierFive();
        GameRegistry.registerBlock(expanderInvTierFive, ItemBlockExpanderInvTierFive.class,
                                   Names.Blocks.expanderInvTierFive);

        hardWallTierOne = new BlockHardWallTierOne();
        GameRegistry.registerBlock(hardWallTierOne, Names.Blocks.hardWallTierOne);

        hardWallTierTwo = new BlockHardWallTierTwo();
        GameRegistry.registerBlock(hardWallTierTwo, Names.Blocks.hardWallTierTwo);

        hardWallTierThree = new BlockHardWallTierThree();
        GameRegistry.registerBlock(hardWallTierThree, Names.Blocks.hardWallTierThree);

        hardWallTierFour = new BlockHardWallTierFour();
        GameRegistry.registerBlock(hardWallTierFour, Names.Blocks.hardWallTierFour);

        hardWallTierFive = new BlockHardWallTierFive();
        GameRegistry.registerBlock(hardWallTierFive, Names.Blocks.hardWallTierFive);

        fenceTierOne = new BlockFenceTierOne();
        GameRegistry.registerBlock(fenceTierOne, Names.Blocks.fenceTierOne);

        fenceTierTwo = new BlockFenceTierTwo();
        GameRegistry.registerBlock(fenceTierTwo, Names.Blocks.fenceTierTwo);

        fenceTierThree = new BlockFenceTierThree();
        GameRegistry.registerBlock(fenceTierThree, Names.Blocks.fenceTierThree);

        fenceTierFour = new BlockFenceTierFour();
        GameRegistry.registerBlock(fenceTierFour, Names.Blocks.fenceTierFour);

        fenceTierFive = new BlockFenceTierFive();
        GameRegistry.registerBlock(fenceTierFive, Names.Blocks.fenceTierFive);

        if (ConfigHandler.getDisposableTurretSettings().isEnabled()) {
            disposableItemTurret = new BlockDisposableTurret();
            GameRegistry.registerBlock(disposableItemTurret, ItemBlockDisposableTurret.class,
                                       Names.Blocks.disposableItemTurret);
        }

        if (ConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            potatoCannonTurret = new BlockPotatoCannonTurret();
            GameRegistry.registerBlock(potatoCannonTurret, ItemBlockPotatoCannonTurret.class,
                                       Names.Blocks.potatoCannonTurret);
        }

        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            machineGunTurret = new BlockGunTurret();
            GameRegistry.registerBlock(machineGunTurret, ItemBlockMachineGunTurret.class,
                                       Names.Blocks.gunTurret);
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            incendiaryTurret = new BlockIncendiaryTurret();
            GameRegistry.registerBlock(incendiaryTurret, ItemBlockIncendiaryTurret.class,
                                       Names.Blocks.incendiaryTurret);
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            grenadeLauncherTurret = new BlockGrenadeTurret();
            GameRegistry.registerBlock(grenadeLauncherTurret, ItemBlockGrenadeLauncherTurret.class,
                                       Names.Blocks.grenadeTurret);
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            relativisticTurret = new BlockRelativisticTurret();
            GameRegistry.registerBlock(relativisticTurret, ItemBlockRelativisticTurret.class,
                                       Names.Blocks.relativisticTurret);
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            rocketTurret = new BlockRocketTurret();
            GameRegistry.registerBlock(rocketTurret, ItemBlockRocketTurret.class, Names.Blocks.rocketTurret);
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            teleporterTurret = new BlockTeleporterTurret();
            GameRegistry.registerBlock(teleporterTurret, ItemBlockTeleporterTurret.class,
                                       Names.Blocks.teleporterTurret);
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            laserTurret = new BlockLaserTurret();
            GameRegistry.registerBlock(laserTurret, ItemBlockLaserTurret.class, Names.Blocks.laserTurret);
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            railGunTurret = new BlockRailGunTurret();
            GameRegistry.registerBlock(railGunTurret, ItemBlockRailGunTurret.class,
                                       Names.Blocks.railGunTurret);
        }

        leverBlock = new LeverBlock();
        GameRegistry.registerBlock(leverBlock, Names.Blocks.lever);
        */
    }
}
