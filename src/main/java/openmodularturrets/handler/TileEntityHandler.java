package openmodularturrets.handler;

import cpw.mods.fml.common.registry.GameRegistry;
import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.turretbase.*;
import openmodularturrets.tileentity.turrets.*;

public class TileEntityHandler {

    public static void init() {
        GameRegistry.registerTileEntity(TurretBase.class, "turretbase");
        GameRegistry.registerTileEntity(TurretBaseTierOneTileEntity.class, "turretWoodBase");
        GameRegistry.registerTileEntity(DisposableItemTurretTileEntity.class, "disposableItemTurret");
        GameRegistry.registerTileEntity(RocketTurretTileEntity.class, "rocketTurret");
        GameRegistry.registerTileEntity(MachineGunTurretTileEntity.class, "machineGunTurret");
        GameRegistry.registerTileEntity(GrenadeLauncherTurretTileEntity.class, "grenadeTurret");
        GameRegistry.registerTileEntity(LaserTurretTileEntity.class, "laserTurret");
        GameRegistry.registerTileEntity(LeverTileEntity.class, "leverTileEntity");
        GameRegistry.registerTileEntity(TurretBaseTierTwoTileEntity.class, "turretBaseOne");
        GameRegistry.registerTileEntity(TurretBaseTierThreeTileEntity.class, "turretBaseTwo");
        GameRegistry.registerTileEntity(TurretBaseTierFourTileEntity.class, "turretBaseThree");
        GameRegistry.registerTileEntity(TurretBaseTierFiveTileEntity.class, "turretBaseFour");
        GameRegistry.registerTileEntity(RailGunTurretTileEntity.class, "railGunTurret");
    }
}
