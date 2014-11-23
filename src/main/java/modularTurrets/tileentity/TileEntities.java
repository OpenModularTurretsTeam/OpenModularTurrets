package modularTurrets.tileentity;

import modularTurrets.tileentity.turretBase.TurretBase;
import modularTurrets.tileentity.turretBase.TurretBaseTierFourTileEntity;
import modularTurrets.tileentity.turretBase.TurretBaseTierOneTileEntity;
import modularTurrets.tileentity.turretBase.TurretBaseTierThreeTileEntity;
import modularTurrets.tileentity.turretBase.TurretBaseTierTwoTileEntity;
import modularTurrets.tileentity.turretBase.TurretWoodBase;
import modularTurrets.tileentity.turrets.DisposableItemTurretTileEntity;
import modularTurrets.tileentity.turrets.GrenadeLauncherTurretTileEntity;
import modularTurrets.tileentity.turrets.LaserTurretTileEntity;
import modularTurrets.tileentity.turrets.MachineGunTurretTileEntity;
import modularTurrets.tileentity.turrets.RocketTurretTileEntity;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntities {
	
	public static void init()
	{
		GameRegistry.registerTileEntity(TurretBase.class, "turretBase");
		GameRegistry.registerTileEntity(TurretWoodBase.class, "turretWoodBase");
		GameRegistry.registerTileEntity(DisposableItemTurretTileEntity.class, "disposableItemTurret");
		GameRegistry.registerTileEntity(RocketTurretTileEntity.class, "rocketTurret");
		GameRegistry.registerTileEntity(MachineGunTurretTileEntity.class, "machineGunTurret");
		GameRegistry.registerTileEntity(GrenadeLauncherTurretTileEntity.class, "grenadeTurret");
		GameRegistry.registerTileEntity(LaserTurretTileEntity.class, "laserTurret");
		GameRegistry.registerTileEntity(LeverTileEntity.class, "leverTileEntity");
		GameRegistry.registerTileEntity(TurretBaseTierOneTileEntity.class, "turretBaseOne");
		GameRegistry.registerTileEntity(TurretBaseTierTwoTileEntity.class, "turretBaseTwo");
		GameRegistry.registerTileEntity(TurretBaseTierThreeTileEntity.class, "turretBaseThree");
		GameRegistry.registerTileEntity(TurretBaseTierFourTileEntity.class, "turretBaseFour");
	}
	
}
