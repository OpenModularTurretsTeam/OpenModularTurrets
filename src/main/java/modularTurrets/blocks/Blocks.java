package modularTurrets.blocks;

import net.minecraft.block.Block;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Blocks {
	
	public static Block genericBlock;
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
		
	
		turretBaseTierWood = new TurretBaseTierWood(BlockIDs.turretBaseTierWood);
		GameRegistry.registerBlock(turretBaseTierWood, BlockNames.turretBaseTierWood);
		
		turretBaseTierOne = new TurretBaseTierOne(BlockIDs.turretBaseTierOne);
		GameRegistry.registerBlock(turretBaseTierOne, BlockNames.turretBaseTierOne);
		
		turretBaseTierTwo = new TurretBaseTierTwo(BlockIDs.turretBaseTierTwo);
		GameRegistry.registerBlock(turretBaseTierTwo, BlockNames.turretBaseTierTwo);
		
		turretBaseTierThree = new TurretBaseTierThree(BlockIDs.turretBaseTierThree);
		GameRegistry.registerBlock(turretBaseTierThree, BlockNames.turretBaseTierThree);
		
		turretBaseTierFour = new TurretBaseTierFour(BlockIDs.turretBaseTierFour);
		GameRegistry.registerBlock(turretBaseTierFour, BlockNames.turretBaseTierFour);
		
		disposableItemTurret = new DisposableTurretBlock(BlockIDs.disposableItemTurret);
		GameRegistry.registerBlock(disposableItemTurret, BlockNames.disposableItemTurret);
		
		rocketTurret = new RocketTurretBlock(BlockIDs.rocketTurret);
		GameRegistry.registerBlock(rocketTurret, BlockNames.rocketTurret);
		
		machineGunTurret = new MachineGunTurretBlock(BlockIDs.machineGunTurret);
		GameRegistry.registerBlock(machineGunTurret, BlockNames.machineGunTurret);
		
		grenadeLauncherTurret = new GrenadeLauncherTurretBlock(BlockIDs.grenadeTurret);
		GameRegistry.registerBlock(grenadeLauncherTurret, BlockNames.grenadeTurret);
		
		laserTurret = new LaserTurretBlock(BlockIDs.laserTurret);
		GameRegistry.registerBlock(laserTurret, BlockNames.laserTurret);
		
		leverBlock = new LeverBlock(BlockIDs.lever);
		GameRegistry.registerBlock(leverBlock, BlockNames.lever);
		

	}
	
	public static void addNames() {
				
		LanguageRegistry.addName(turretBaseTierWood, BlockNames.turretBaseTierWood);
		LanguageRegistry.addName(turretBaseTierOne, BlockNames.turretBaseTierOne);
		LanguageRegistry.addName(turretBaseTierTwo, BlockNames.turretBaseTierTwo);
		LanguageRegistry.addName(turretBaseTierThree, BlockNames.turretBaseTierThree);
		LanguageRegistry.addName(turretBaseTierFour, BlockNames.turretBaseTierFour);
		LanguageRegistry.addName(disposableItemTurret, BlockNames.disposableItemTurret);
		LanguageRegistry.addName(rocketTurret, BlockNames.rocketTurret);
		LanguageRegistry.addName(machineGunTurret, BlockNames.machineGunTurret);
		LanguageRegistry.addName(grenadeLauncherTurret, BlockNames.grenadeTurret);
		LanguageRegistry.addName(laserTurret, BlockNames.laserTurret);
		LanguageRegistry.addName(leverBlock, BlockNames.lever);

	}
	
	

}
