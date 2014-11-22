package modularTurrets.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

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
	public static Block leverBlock;

	public static void init() {
		turretBaseTierWood = new TurretBaseTierWood();
		GameRegistry.registerBlock(turretBaseTierWood, BlockNames.turretBaseTierWood);
		
		turretBaseTierOne = new TurretBaseTierOne();
		GameRegistry.registerBlock(turretBaseTierOne, BlockNames.turretBaseTierOne);
		
		turretBaseTierTwo = new TurretBaseTierTwo();
		GameRegistry.registerBlock(turretBaseTierTwo, BlockNames.turretBaseTierTwo);
		
		turretBaseTierThree = new TurretBaseTierThree();
		GameRegistry.registerBlock(turretBaseTierThree, BlockNames.turretBaseTierThree);
		
		turretBaseTierFour = new TurretBaseTierFour();
		GameRegistry.registerBlock(turretBaseTierFour, BlockNames.turretBaseTierFour);
		
		disposableItemTurret = new DisposableTurretBlock();
		GameRegistry.registerBlock(disposableItemTurret, BlockNames.disposableItemTurret);
		
		rocketTurret = new RocketTurretBlock();
		GameRegistry.registerBlock(rocketTurret, BlockNames.rocketTurret);
		
		machineGunTurret = new MachineGunTurretBlock();
		GameRegistry.registerBlock(machineGunTurret, BlockNames.machineGunTurret);
		
		grenadeLauncherTurret = new GrenadeLauncherTurretBlock();
		GameRegistry.registerBlock(grenadeLauncherTurret, BlockNames.grenadeTurret);
		
		laserTurret = new LaserTurretBlock();
		GameRegistry.registerBlock(laserTurret, BlockNames.laserTurret);
		
		leverBlock = new LeverBlock();
		GameRegistry.registerBlock(leverBlock, BlockNames.lever);
	}
}
