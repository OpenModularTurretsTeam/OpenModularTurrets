package openmodularturrets.blocks;

import net.minecraft.block.Block;

import openmodularturrets.blocks.expanders.*;
import openmodularturrets.blocks.misc.*;
import openmodularturrets.blocks.turretbases.*;
import openmodularturrets.blocks.turretheads.*;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.blocks.*;
import openmodularturrets.reference.Names;
import cpw.mods.fml.common.registry.GameRegistry;

public class Blocks {

    public static Block turretBaseTierOne;
    public static Block turretBaseTierTwo;
    public static Block turretBaseTierThree;
    public static Block turretBaseTierFour;
    public static Block turretBaseTierFive;
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
        turretBaseTierOne = new BlockTurretBaseTierOne();
        GameRegistry.registerBlock(
                turretBaseTierOne,
                ItemBlockTurretBaseTierOne.class,
                Names.Blocks.unlocalisedTurretBaseTierOne);

        turretBaseTierTwo = new BlockTurretBaseTierTwo();
        GameRegistry.registerBlock(
                turretBaseTierTwo,
                ItemBlockTurretBaseTierTwo.class,
                Names.Blocks.unlocalisedTurretBaseTierTwo);

        turretBaseTierThree = new BlockTurretBaseTierThree();
        GameRegistry.registerBlock(
                turretBaseTierThree,
                ItemBlockTurretBaseTierThree.class,
                Names.Blocks.unlocalisedTurretBaseTierThree);

        turretBaseTierFour = new BlockTurretBaseTierFour();
        GameRegistry.registerBlock(
                turretBaseTierFour,
                ItemBlockTurretBaseTierFour.class,
                Names.Blocks.unlocalisedTurretBaseTierFour);

        turretBaseTierFive = new BlockTurretBaseTierFive();
        GameRegistry.registerBlock(
                turretBaseTierFive,
                ItemBlockTurretBaseTierFive.class,
                Names.Blocks.unlocalisedTurretBaseTierFive);

        expanderPowerTierOne = new BlockExpanderPowerTierOne();
        GameRegistry.registerBlock(
                expanderPowerTierOne,
                ItemBlockExpanderPowerTierOne.class,
                Names.Blocks.unlocalisedExpanderPowerTierOne);

        expanderPowerTierTwo = new BlockExpanderPowerTierTwo();
        GameRegistry.registerBlock(
                expanderPowerTierTwo,
                ItemBlockExpanderPowerTierTwo.class,
                Names.Blocks.unlocalisedExpanderPowerTierTwo);

        expanderPowerTierThree = new BlockExpanderPowerTierThree();
        GameRegistry.registerBlock(
                expanderPowerTierThree,
                ItemBlockExpanderPowerTierThree.class,
                Names.Blocks.unlocalisedExpanderPowerTierThree);

        expanderPowerTierFour = new BlockExpanderPowerTierFour();
        GameRegistry.registerBlock(
                expanderPowerTierFour,
                ItemBlockExpanderPowerTierFour.class,
                Names.Blocks.unlocalisedExpanderPowerTierFour);

        expanderPowerTierFive = new BlockExpanderPowerTierFive();
        GameRegistry.registerBlock(
                expanderPowerTierFive,
                ItemBlockExpanderPowerTierFive.class,
                Names.Blocks.unlocalisedExpanderPowerTierFive);

        expanderInvTierOne = new BlockExpanderInvTierOne();
        GameRegistry.registerBlock(
                expanderInvTierOne,
                ItemBlockExpanderInvTierOne.class,
                Names.Blocks.unlocalisedExpanderInvTierOne);

        expanderInvTierTwo = new BlockExpanderInvTierTwo();
        GameRegistry.registerBlock(
                expanderInvTierTwo,
                ItemBlockExpanderInvTierTwo.class,
                Names.Blocks.unlocalisedExpanderInvTierTwo);

        expanderInvTierThree = new BlockExpanderInvTierThree();
        GameRegistry.registerBlock(
                expanderInvTierThree,
                ItemBlockExpanderInvTierThree.class,
                Names.Blocks.unlocalisedExpanderInvTierThree);

        expanderInvTierFour = new BlockExpanderInvTierFour();
        GameRegistry.registerBlock(
                expanderInvTierFour,
                ItemBlockExpanderInvTierFour.class,
                Names.Blocks.unlocalisedExpanderInvTierFour);

        expanderInvTierFive = new BlockExpanderInvTierFive();
        GameRegistry.registerBlock(
                expanderInvTierFive,
                ItemBlockExpanderInvTierFive.class,
                Names.Blocks.unlocalisedExpanderInvTierFive);

        hardWallTierOne = new BlockHardWallTierOne();
        GameRegistry.registerBlock(hardWallTierOne, Names.Blocks.unlocalisedHardWallTierOne);

        hardWallTierTwo = new BlockHardWallTierTwo();
        GameRegistry.registerBlock(hardWallTierTwo, Names.Blocks.unlocalisedHardWallTierTwo);

        hardWallTierThree = new BlockHardWallTierThree();
        GameRegistry.registerBlock(hardWallTierThree, Names.Blocks.unlocalisedHardWallTierThree);

        hardWallTierFour = new BlockHardWallTierFour();
        GameRegistry.registerBlock(hardWallTierFour, Names.Blocks.unlocalisedHardWallTierFour);

        hardWallTierFive = new BlockHardWallTierFive();
        GameRegistry.registerBlock(hardWallTierFive, Names.Blocks.unlocalisedHardWallTierFive);

        fenceTierOne = new BlockFenceTierOne();
        GameRegistry.registerBlock(fenceTierOne, Names.Blocks.unlocalisedFenceTierOne);

        fenceTierTwo = new BlockFenceTierTwo();
        GameRegistry.registerBlock(fenceTierTwo, Names.Blocks.unlocalisedFenceTierTwo);

        fenceTierThree = new BlockFenceTierThree();
        GameRegistry.registerBlock(fenceTierThree, Names.Blocks.unlocalisedFenceTierThree);

        fenceTierFour = new BlockFenceTierFour();
        GameRegistry.registerBlock(fenceTierFour, Names.Blocks.unlocalisedFenceTierFour);

        fenceTierFive = new BlockFenceTierFive();
        GameRegistry.registerBlock(fenceTierFive, Names.Blocks.unlocalisedFenceTierFive);

        if (ConfigHandler.getDisposableTurretSettings().isEnabled()) {
            disposableItemTurret = new BlockDisposableTurret();
            GameRegistry.registerBlock(
                    disposableItemTurret,
                    ItemBlockDisposableTurret.class,
                    Names.Blocks.unlocalisedDisposableItemTurret);
        }

        if (ConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            potatoCannonTurret = new BlockPotatoCannonTurret();
            GameRegistry.registerBlock(
                    potatoCannonTurret,
                    ItemBlockPotatoCannonTurret.class,
                    Names.Blocks.unlocalisedPotatoCannonTurret);
        }

        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            machineGunTurret = new BlockGunTurret();
            GameRegistry.registerBlock(
                    machineGunTurret,
                    ItemBlockMachineGunTurret.class,
                    Names.Blocks.unlocalisedGunTurret);
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            incendiaryTurret = new BlockIncendiaryTurret();
            GameRegistry.registerBlock(
                    incendiaryTurret,
                    ItemBlockIncendiaryTurret.class,
                    Names.Blocks.unlocalisedIncendiaryTurret);
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            grenadeLauncherTurret = new BlockGrenadeTurret();
            GameRegistry.registerBlock(
                    grenadeLauncherTurret,
                    ItemBlockGrenadeLauncherTurret.class,
                    Names.Blocks.unlocalisedGrenadeTurret);
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            relativisticTurret = new BlockRelativisticTurret();
            GameRegistry.registerBlock(
                    relativisticTurret,
                    ItemBlockRelativisticTurret.class,
                    Names.Blocks.unlocalisedRelativisticTurret);
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            rocketTurret = new BlockRocketTurret();
            GameRegistry.registerBlock(rocketTurret, ItemBlockRocketTurret.class, Names.Blocks.unlocalisedRocketTurret);
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            teleporterTurret = new BlockTeleporterTurret();
            GameRegistry.registerBlock(
                    teleporterTurret,
                    ItemBlockTeleporterTurret.class,
                    Names.Blocks.unlocalisedTeleporterTurret);
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            laserTurret = new BlockLaserTurret();
            GameRegistry.registerBlock(laserTurret, ItemBlockLaserTurret.class, Names.Blocks.unlocalisedLaserTurret);
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            railGunTurret = new BlockRailGunTurret();
            GameRegistry
                    .registerBlock(railGunTurret, ItemBlockRailGunTurret.class, Names.Blocks.unlocalisedRailGunTurret);
        }

        leverBlock = new LeverBlock();
        GameRegistry.registerBlock(leverBlock, Names.Blocks.unlocalisedLever);
    }
}
