package openmodularturrets.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import openmodularturrets.blocks.turretbases.*;
import openmodularturrets.blocks.turretheads.*;
import openmodularturrets.items.blocks.*;

public class Blocks {
    public static Block turretBaseTierWood;
    public static Block turretBaseTierOne;
    public static Block turretBaseTierTwo;
    public static Block turretBaseTierThree;
    public static Block turretBaseTierFour;
    public static Block disposableItemTurret;
    public static Block rocketTurret;
    public static Block machineGunTurret;
    public static Block grenadeLauncherTurret;
    public static Block laserTurret;
    public static Block railGunTurret;
    public static Block leverBlock;

    public static void init() {
        turretBaseTierWood = new BlockTurretBaseTierOne();
        GameRegistry.registerBlock(turretBaseTierWood, ItemBlockTurretBaseTierOne.class,
                                   BlockNames.unlocalisedTurretBaseTierOne);

        turretBaseTierOne = new BlockTurretBaseTierTwo();
        GameRegistry.registerBlock(turretBaseTierOne, ItemBlockTurretBaseTierTwo.class,
                                   BlockNames.unlocalisedTurretBaseTierTwo);

        turretBaseTierTwo = new BlockTurretBaseTierThree();
        GameRegistry.registerBlock(turretBaseTierTwo, ItemBlockTurretBaseTierThree.class,
                                   BlockNames.unlocalisedTurretBaseTierThree);

        turretBaseTierThree = new BlockTurretBaseTierFour();
        GameRegistry.registerBlock(turretBaseTierThree, ItemBlockTurretBaseTierFour.class,
                                   BlockNames.unlocalisedTurretBaseTierFour);

        turretBaseTierFour = new BlockTurretBaseTierFive();
        GameRegistry.registerBlock(turretBaseTierFour, ItemBlockTurretBaseTierFive.class,
                                   BlockNames.unlocalisedTurretBaseTierFive);

        disposableItemTurret = new BlockDisposableTurret();
        GameRegistry.registerBlock(disposableItemTurret, ItemBlockDisposableTurret.class,
                                   BlockNames.unlocalisedDisposableItemTurret);

        rocketTurret = new BlockRocketTurret();
        GameRegistry.registerBlock(rocketTurret, ItemBlockRocketTurret.class, BlockNames.unlocalisedRocketTurret);

        machineGunTurret = new BlockMachineGunTurret();
        GameRegistry.registerBlock(machineGunTurret, ItemBlockMachineGunTurret.class,
                                   BlockNames.unlocalisedMachineGunTurret);

        grenadeLauncherTurret = new BlockGrenadeLauncherTurret();
        GameRegistry.registerBlock(grenadeLauncherTurret, ItemBlockGrenadeLauncherTurret.class,
                                   BlockNames.unlocalisedGrenadeTurret);

        laserTurret = new BlockLaserTurret();
        GameRegistry.registerBlock(laserTurret, ItemBlockLaserTurret.class, BlockNames.unlocalisedLaserTurret);

        railGunTurret = new BlockRailGunTurret();
        GameRegistry.registerBlock(railGunTurret, ItemBlockRailGunTurret.class, BlockNames.unlocalisedRailGunTurret);

        leverBlock = new LeverBlock();
        GameRegistry.registerBlock(leverBlock, BlockNames.unlocalisedLever);
    }
}
