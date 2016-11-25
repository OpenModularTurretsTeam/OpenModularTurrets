package openmodularturrets.init;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import openmodularturrets.blocks.BlockExpander;
import openmodularturrets.blocks.BlockTurretBase;
import openmodularturrets.blocks.LeverBlock;
import openmodularturrets.blocks.turretheads.*;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.blocks.*;
import openmodularturrets.reference.Names;
import openmodularturrets.reference.Reference;
import openmodularturrets.tileentity.Expander;
import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.TurretBase;
import openmodularturrets.tileentity.turrets.*;

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

    public static Block expander;



    public static void initBlocks() {
        turretBase = new BlockTurretBase();
        GameRegistry.registerBlock(turretBase, ItemBlockTurretBase.class, Names.Blocks.turretBase);

        expander = new BlockExpander();
        GameRegistry.registerBlock(expander, ItemBlockExpander.class,Names.Blocks.expander);


        /*hardWallTierOne = new BlockHardWallTierOne();
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
        GameRegistry.registerBlock(fenceTierFive, Names.Blocks.fenceTierFive);    */

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

    }

    public static void initTileEntities() {
        GameRegistry.registerTileEntity(TurretBase.class, Reference.MOD_ID.toLowerCase() + ":" + Names.Blocks.turretBase);
        GameRegistry.registerTileEntity(DisposableItemTurretTileEntity.class, "disposableItemTurret");
        GameRegistry.registerTileEntity(PotatoCannonTurretTileEntity.class, "potatoCannonTurret");
        GameRegistry.registerTileEntity(RocketTurretTileEntity.class, "rocketTurret");
        GameRegistry.registerTileEntity(GunTurretTileEntity.class, "machineGunTurret");
        GameRegistry.registerTileEntity(GrenadeLauncherTurretTileEntity.class, "grenadeTurret");
        GameRegistry.registerTileEntity(LaserTurretTileEntity.class, "laserTurret");
        GameRegistry.registerTileEntity(LeverTileEntity.class, "leverTileEntity");
        GameRegistry.registerTileEntity(RailGunTurretTileEntity.class, "railGunTurret");
        GameRegistry.registerTileEntity(IncendiaryTurretTileEntity.class, "incendiaryTurret");
        GameRegistry.registerTileEntity(RelativisticTurretTileEntity.class, "relativisticTurret");
        GameRegistry.registerTileEntity(TeleporterTurretTileEntity.class, "teleporterTurret");
        GameRegistry.registerTileEntity(Expander.class, Names.Blocks.expander);

    }
}
