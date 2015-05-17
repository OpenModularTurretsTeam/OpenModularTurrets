package openmodularturrets.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import openmodularturrets.blocks.turretbases.*;
import openmodularturrets.blocks.turretheads.*;
import openmodularturrets.items.blocks.*;

public class Blocks {
    public static Block turretBaseTierOne;
    public static Block turretBaseTierTwo;
    public static Block turretBaseTierThree;
    public static Block turretBaseTierFour;
    public static Block turretBaseTierFive;
    public static Block disposableItemTurret;
    public static Block rocketTurret;
    public static Block machineGunTurret;
    public static Block grenadeLauncherTurret;
    public static Block laserTurret;
    public static Block railGunTurret;
    public static Block leverBlock;

    public static void init() {
        turretBaseTierOne = new BlockTurretBaseTierOne();
        GameRegistry.registerBlock(turretBaseTierOne, ItemBlockTurretBaseTierOne.class,
                                   BlockNames.unlocalisedTurretBaseTierOne);

        turretBaseTierTwo = new BlockTurretBaseTierTwo();
        GameRegistry.registerBlock(turretBaseTierTwo, ItemBlockTurretBaseTierTwo.class,
                                   BlockNames.unlocalisedTurretBaseTierTwo);

        turretBaseTierThree = new BlockTurretBaseTierThree();
        GameRegistry.registerBlock(turretBaseTierThree, ItemBlockTurretBaseTierThree.class,
                                   BlockNames.unlocalisedTurretBaseTierThree);

        turretBaseTierFour = new BlockTurretBaseTierFour();
        GameRegistry.registerBlock(turretBaseTierFour, ItemBlockTurretBaseTierFour.class,
                                   BlockNames.unlocalisedTurretBaseTierFour);

        turretBaseTierFive = new BlockTurretBaseTierFive();
        GameRegistry.registerBlock(turretBaseTierFive, ItemBlockTurretBaseTierFive.class,
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
