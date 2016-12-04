package omtteam.openmodularturrets.init;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import omtteam.openmodularturrets.blocks.BlockExpander;
import omtteam.openmodularturrets.blocks.BlockTurretBase;
import omtteam.openmodularturrets.blocks.LeverBlock;
import omtteam.openmodularturrets.blocks.turretheads.*;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.items.blocks.*;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.tileentity.Expander;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.*;

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
        GameRegistry.register(turretBase);
        GameRegistry.register(new ItemBlockTurretBase(turretBase));

        expander = new BlockExpander();
        GameRegistry.register(expander);
        GameRegistry.register(new ItemBlockExpander(expander));

        if (ConfigHandler.getDisposableTurretSettings().isEnabled()) {
            disposableItemTurret = new BlockDisposableTurret();
            GameRegistry.register(disposableItemTurret);
            GameRegistry.register(new ItemBlockDisposableTurret(disposableItemTurret));
        }

        if (ConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            potatoCannonTurret = new BlockPotatoCannonTurret();
            GameRegistry.register(potatoCannonTurret);
            GameRegistry.register(new ItemBlockPotatoCannonTurret(potatoCannonTurret));
        }

        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            machineGunTurret = new BlockGunTurret();
            GameRegistry.register(machineGunTurret);
            GameRegistry.register(new ItemBlockMachineGunTurret(machineGunTurret));
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            incendiaryTurret = new BlockIncendiaryTurret();
            GameRegistry.register(incendiaryTurret);
            GameRegistry.register(new ItemBlockIncendiaryTurret(incendiaryTurret));
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            grenadeLauncherTurret = new BlockGrenadeTurret();
            GameRegistry.register(grenadeLauncherTurret);
            GameRegistry.register(new ItemBlockGrenadeLauncherTurret(grenadeLauncherTurret));
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            relativisticTurret = new BlockRelativisticTurret();
            GameRegistry.register(relativisticTurret);
            GameRegistry.register(new ItemBlockRelativisticTurret(relativisticTurret));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            rocketTurret = new BlockRocketTurret();
            GameRegistry.register(rocketTurret);
            GameRegistry.register(new ItemBlockRocketTurret(rocketTurret));
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            teleporterTurret = new BlockTeleporterTurret();
            GameRegistry.register(teleporterTurret);
            GameRegistry.register(new ItemBlockTeleporterTurret(teleporterTurret));
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            laserTurret = new BlockLaserTurret();
            GameRegistry.register(laserTurret);
            GameRegistry.register(new ItemBlockLaserTurret(laserTurret));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            railGunTurret = new BlockRailGunTurret();
            GameRegistry.register(railGunTurret);
            GameRegistry.register(new ItemBlockRailGunTurret(railGunTurret));
        }

        leverBlock = new LeverBlock();
        GameRegistry.register(leverBlock);
        GameRegistry.register(new ItemBlockLever(leverBlock));
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
        GameRegistry.registerTileEntity(TeleporterTurretTileEntity.class, Names.Blocks.teleporterTurret);
        GameRegistry.registerTileEntity(Expander.class, Names.Blocks.expander);
    }
}
