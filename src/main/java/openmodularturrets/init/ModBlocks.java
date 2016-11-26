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
        GameRegistry.registerTileEntity(TurretBase.class, Names.Blocks.turretBase);
        GameRegistry.registerTileEntity(DisposableItemTurretTileEntity.class, Names.Blocks.disposableItemTurret);
        GameRegistry.registerTileEntity(PotatoCannonTurretTileEntity.class, Names.Blocks.potatoCannonTurret);
        GameRegistry.registerTileEntity(RocketTurretTileEntity.class, Names.Blocks.rocketTurret);
        GameRegistry.registerTileEntity(GunTurretTileEntity.class, Names.Blocks.gunTurret);
        GameRegistry.registerTileEntity(GrenadeLauncherTurretTileEntity.class, Names.Blocks.grenadeTurret);
        GameRegistry.registerTileEntity(LaserTurretTileEntity.class, Names.Blocks.laserTurret);
        GameRegistry.registerTileEntity(LeverTileEntity.class, Names.Blocks.lever);
        GameRegistry.registerTileEntity(RailGunTurretTileEntity.class, Names.Blocks.railGunTurret);
        GameRegistry.registerTileEntity(IncendiaryTurretTileEntity.class, Names.Blocks.incendiaryTurret);
        GameRegistry.registerTileEntity(RelativisticTurretTileEntity.class, Names.Blocks.relativisticTurret);
        GameRegistry.registerTileEntity(TeleporterTurretTileEntity.class,  Names.Blocks.teleporterTurret);
        GameRegistry.registerTileEntity(Expander.class, Names.Blocks.expander);
    }
}
