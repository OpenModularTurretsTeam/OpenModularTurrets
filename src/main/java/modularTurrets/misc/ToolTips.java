package modularTurrets.misc;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ToolTips {
	
	 ArrayList woodTurretToolTip;
	 ArrayList tierOneTurretToolTip;
	 ArrayList tierTwoTurretToolTip;
	 ArrayList tierThreeTurretToolTip;
	 ArrayList tierFourTurretToolTip;
	 
	 ArrayList disposableItemTurretTooltip;
	 ArrayList machineGunTurretTooltip;
	 ArrayList grenadeTurretTooltip;
	 ArrayList rocketTurretTooltip;
	 ArrayList laserTurretTooltip;
	 
	 ArrayList fireRateUpgradeTooltip;
	 ArrayList efficiencyUpgradeTooltip;
	 ArrayList rangeUpgradeTooltip;
	 ArrayList accuraccyUpgradeTooltip;
	 
	 ArrayList solarPanelAddon;
	 ArrayList damageAmpAddon;
	 ArrayList redstoneReactorAddon;
	
	@SuppressWarnings("unchecked")
    public ToolTips()
	{
		//woodTurretTooltip
		woodTurretToolTip = new ArrayList();
		woodTurretToolTip.add("");
		woodTurretToolTip.add("\u00A7b--Energy--");
		woodTurretToolTip.add("Max Capacity: "+ ConfigHandler.getBaseTierWoodMaxCharge());
		woodTurretToolTip.add("Max IO: "+ ConfigHandler.getBaseTierWoodMaxIo());
		woodTurretToolTip.add("");
		woodTurretToolTip.add("\u00A72--Extras--");
		woodTurretToolTip.add("None");
		woodTurretToolTip.add("");
		woodTurretToolTip.add("\u00A78The powder's dry, and I'm homeless.");
		
		//OneTurretTooltip
		tierOneTurretToolTip = new ArrayList();
		tierOneTurretToolTip.add("");
		tierOneTurretToolTip.add("\u00A7b--Energy--");
		tierOneTurretToolTip.add("Max Capacity: "+ConfigHandler.getBaseTierOneMaxCharge());
		tierOneTurretToolTip.add("Max IO: "+ ConfigHandler.getBaseTierOneMaxIo());
		tierOneTurretToolTip.add("");
		tierOneTurretToolTip.add("\u00A72--Extras--");
		tierOneTurretToolTip.add("2x Addon Slots");
		tierOneTurretToolTip.add("1x Upgrade Slot");
		tierOneTurretToolTip.add("");
		tierOneTurretToolTip.add("\u00A78I'll draw your blood.");
		
		//TwoTurretTooltip
		tierTwoTurretToolTip = new ArrayList();
		tierTwoTurretToolTip.add("");
		tierTwoTurretToolTip.add("\u00A7b--Energy--");
		tierTwoTurretToolTip.add("Max Capacity: "+ConfigHandler.getBaseTierTwoMaxCharge());
		tierTwoTurretToolTip.add("Max IO: "+ ConfigHandler.getBaseTierTwoMaxIo());
		tierTwoTurretToolTip.add("");
		tierTwoTurretToolTip.add("\u00A72--Extras--");
		tierTwoTurretToolTip.add("2x Addon Slots");
		tierTwoTurretToolTip.add("1x Upgrade Slot");
		tierTwoTurretToolTip.add("");
		tierTwoTurretToolTip.add("\u00A78I said good day sir!!");
		
		//ThreeTurretTooltip
		tierThreeTurretToolTip = new ArrayList();
		tierThreeTurretToolTip.add("");
		tierThreeTurretToolTip.add("\u00A7b--Energy--");
		tierThreeTurretToolTip.add("Max Capacity: "+ConfigHandler.getBaseTierThreeMaxCharge());
		tierThreeTurretToolTip.add("Max IO: "+ ConfigHandler.getBaseTierThreeMaxIo());
		tierThreeTurretToolTip.add("");
		tierThreeTurretToolTip.add("\u00A72--Extras--");
		tierThreeTurretToolTip.add("2x Addon Slots");
		tierThreeTurretToolTip.add("1x Upgrade Slot");
		tierThreeTurretToolTip.add("");
		tierThreeTurretToolTip.add("\u00A78Press the attack!");
		
		//FourTurretTooltip
		tierFourTurretToolTip = new ArrayList();
		tierFourTurretToolTip.add("");
		tierFourTurretToolTip.add("\u00A7b--Energy--");
		tierFourTurretToolTip.add("Max Capacity: "+ConfigHandler.getBaseTierFourMaxCharge());
		tierFourTurretToolTip.add("Max IO: "+ ConfigHandler.getBaseTierFourMaxIo());
		tierFourTurretToolTip.add("");
		tierFourTurretToolTip.add("\u00A72--Extras--");
		tierFourTurretToolTip.add("2x Addon Slots");
		tierFourTurretToolTip.add("2x Upgrade Slot");
		tierFourTurretToolTip.add("");
		tierFourTurretToolTip.add("\u00A78All the kings horses, and");
		tierFourTurretToolTip.add("\u00A78all the king's men...");
		
		DecimalFormat df = new DecimalFormat("#.0");
		
		disposableItemTurretTooltip = new ArrayList();
		disposableItemTurretTooltip.add("");
		disposableItemTurretTooltip.add("\u00A76-Info-");
		disposableItemTurretTooltip.add("Tier: \u00A7f0");
		disposableItemTurretTooltip.add("Range: \u00A7f"+Constants.disposableItemTurretRange+" blocks");
		disposableItemTurretTooltip.add("Accuracy: \u00A7fLow");
		disposableItemTurretTooltip.add("Ammo Type: \u00A7fAny Block/Item");
		disposableItemTurretTooltip.add("Base Minimum Tier: \u00A7fBasic");
		disposableItemTurretTooltip.add("");
		disposableItemTurretTooltip.add("\u00A75-Damage Output-");
		disposableItemTurretTooltip.add("Projectile Damage: \u00A7f" + (Constants.disposableItemTurretDamage/2) + " hearts");
		disposableItemTurretTooltip.add("AOE Radius: \u00A7f0");
		disposableItemTurretTooltip.add("Shots/s: \u00A7f"+df.format(20.0F/Constants.disposableItemTurretFireRate));
		disposableItemTurretTooltip.add("Energy Usage per shot: \u00A7f"+Constants.disposableItemTurretPowerUse+" RF");
		disposableItemTurretTooltip.add("");
		disposableItemTurretTooltip.add("\u00A78Poor man's sleeping aid.");
		
		machineGunTurretTooltip = new ArrayList();
		machineGunTurretTooltip.add("");
		machineGunTurretTooltip.add("\u00A76-Info-");
		machineGunTurretTooltip.add("Tier: \u00A7f1");
		machineGunTurretTooltip.add("Range: \u00A7f"+Constants.machineGunTurretRange+" blocks");
		machineGunTurretTooltip.add("Accuracy: \u00A7fMedium");
		machineGunTurretTooltip.add("Ammo Type: \u00A7fBullet");
		machineGunTurretTooltip.add("Base Minimum Tier: \u00A7fLeadstone");
		machineGunTurretTooltip.add("");
		machineGunTurretTooltip.add("\u00A75-Damage Output-");
		machineGunTurretTooltip.add("Projectile Damage: \u00A7f" + (Constants.machineGunTurretDamage/2) + " hearts");
		machineGunTurretTooltip.add("AOE Radius: \u00A7f0");
		machineGunTurretTooltip.add("Shots/s: \u00A7f"+df.format(20.0F/Constants.machineGunTurretFireRate));
		machineGunTurretTooltip.add("Energy Usage per shot: \u00A7f"+Constants.machineGunTurretPowerUse+" RF");
		machineGunTurretTooltip.add("");
		machineGunTurretTooltip.add("\u00A78TRRRAA!! TRRAATRRAA!!");
		
		grenadeTurretTooltip = new ArrayList();
		grenadeTurretTooltip.add("");
		grenadeTurretTooltip.add("\u00A76-Info-");
		grenadeTurretTooltip.add("Tier: \u00A7f2");
		grenadeTurretTooltip.add("Range: \u00A7f"+Constants.grenadeTurretRange+" blocks");
		grenadeTurretTooltip.add("Accuracy: \u00A7fMedium");
		grenadeTurretTooltip.add("Ammo Type: \u00A7fGrenade");
		grenadeTurretTooltip.add("Base Minimum Tier: \u00A7fReinforced");
		grenadeTurretTooltip.add("");
		grenadeTurretTooltip.add("\u00A75-Damage Output-");
		grenadeTurretTooltip.add("Projectile Damage: \u00A7f" + (Constants.grenadeTurretDamage/2) + " hearts");
		grenadeTurretTooltip.add("AOE Radius: 3");
		grenadeTurretTooltip.add("Shots/s: \u00A7f"+df.format(20.0F/Constants.grenadeTurretFireRate));
		grenadeTurretTooltip.add("Energy Usage per shot: \u00A7f"+Constants.grenadeTurretPowerUse+" RF");
		grenadeTurretTooltip.add("");
		grenadeTurretTooltip.add("\u00A78Another example is the inductive");
		grenadeTurretTooltip.add("\u00A78form of the horse paradox.");
		
		rocketTurretTooltip = new ArrayList();
		rocketTurretTooltip.add("");
		rocketTurretTooltip.add("\u00A76-Info-");
		rocketTurretTooltip.add("Tier: \u00A7f3");
		rocketTurretTooltip.add("Range: \u00A7f"+Constants.rocketTurretRange+" blocks");
		rocketTurretTooltip.add("Accuracy: \u00A7fPin Point");
		rocketTurretTooltip.add("Ammo Type: \u00A7fHoming Rocket");
		rocketTurretTooltip.add("Base Minimum Tier: \u00A7fHardened");
		rocketTurretTooltip.add("");
		rocketTurretTooltip.add("\u00A75-Damage Output-");
		rocketTurretTooltip.add("Projectile Damage: \u00A7f" + (Constants.rocketTurretDamage/2) + " hearts");
		rocketTurretTooltip.add("AOE Radius: \u00A7f5");
		rocketTurretTooltip.add("Shots/s: \u00A7f"+df.format(20.0F/Constants.rocketTurretFireRate));
		rocketTurretTooltip.add("Energy Usage per shot: \u00A7f"+Constants.rocketTurretPowerUse+" RF");
		rocketTurretTooltip.add("");
		rocketTurretTooltip.add("\u00A78Swoosh:boom");
		
		laserTurretTooltip = new ArrayList();
		laserTurretTooltip.add("");
		laserTurretTooltip.add("\u00A76-Info-");
		laserTurretTooltip.add("Tier: \u00A7f4");
		laserTurretTooltip.add("Range: \u00A7f"+Constants.laserTurretRange+" blocks");
		laserTurretTooltip.add("Accuracy: \u00A7fHigh");
		laserTurretTooltip.add("Ammo Type: \u00A7fNone");
		laserTurretTooltip.add("Base Minimum Tier: \u00A7fResonant");
		laserTurretTooltip.add("");
		laserTurretTooltip.add("\u00A75-Damage Output-");
		laserTurretTooltip.add("Projectile Damage: \u00A7f" + (Constants.laserTurretDamage/2) + " hearts");
		laserTurretTooltip.add("AOE Radius: \u00A7f0");
		laserTurretTooltip.add("Shots/s: \u00A7f"+df.format(20.0F/Constants.laserTurretFireRate));
		laserTurretTooltip.add("Energy Usage per shot: \u00A7f"+Constants.laserTurretPowerUse+" RF");
		laserTurretTooltip.add("");
		laserTurretTooltip.add("\u00A78Testla would be proud.");
		
		fireRateUpgradeTooltip = new ArrayList();
		fireRateUpgradeTooltip.add("");
		fireRateUpgradeTooltip.add("\u00A71Turret Upgrade");
		fireRateUpgradeTooltip.add("");
		fireRateUpgradeTooltip.add("+ "+Constants.fireRateUpgradeBoostPercentage*100+"% fire rate per upgrade.");
		fireRateUpgradeTooltip.add("Stacks up to 4 times.");
		fireRateUpgradeTooltip.add("");
		fireRateUpgradeTooltip.add("\u00A78Time consumer, time consuming.");
		
		efficiencyUpgradeTooltip = new ArrayList();
		efficiencyUpgradeTooltip.add("");
		efficiencyUpgradeTooltip.add("\u00A71Turret Upgrade");
		efficiencyUpgradeTooltip.add("");
		efficiencyUpgradeTooltip.add("- "+Constants.efficiencyUpgradeBoostPercentage*100+"% RF use per shot, per upgrade.");
		efficiencyUpgradeTooltip.add("Stacks up to 4 times.");
		efficiencyUpgradeTooltip.add("");
		efficiencyUpgradeTooltip.add("\u00A78Eureka!");		
		
		rangeUpgradeTooltip = new ArrayList();
		rangeUpgradeTooltip.add("");
		rangeUpgradeTooltip.add("\u00A71Turret Upgrade");
		rangeUpgradeTooltip.add("");
		rangeUpgradeTooltip.add("+ "+Constants.rangeUpgradeBoost+ " block range per upgrade.");
		rangeUpgradeTooltip.add("Stacks up to 4 times.");
		rangeUpgradeTooltip.add("");
		rangeUpgradeTooltip.add("\u00A78Will you reach me the fork?");
		
		accuraccyUpgradeTooltip = new ArrayList();
		accuraccyUpgradeTooltip.add("");
		accuraccyUpgradeTooltip.add("\u00A71Turret Upgrade");
		accuraccyUpgradeTooltip.add("");
		accuraccyUpgradeTooltip.add("+ "+Constants.accuraccyUpgradeBoost*100+ "% accuracy per upgrade.");
		accuraccyUpgradeTooltip.add("Stacks up to 4 times.");
		accuraccyUpgradeTooltip.add("");
		accuraccyUpgradeTooltip.add("\u00A78Good eye sniper,");
		accuraccyUpgradeTooltip.add("\u00A78I'll shoot, you run.");
		
		solarPanelAddon = new ArrayList();
		solarPanelAddon.add("");
		solarPanelAddon.add("\u00A74Turret Addon");
		solarPanelAddon.add("");
		solarPanelAddon.add("Generates "+Constants.solarPanelAddonGen+" RF/t in sunlight.");
		solarPanelAddon.add("");
		solarPanelAddon.add("\u00A78Good Helios!");
		
		redstoneReactorAddon = new ArrayList();
		redstoneReactorAddon.add("");
		redstoneReactorAddon.add("\u00A74Turret Addon");
		redstoneReactorAddon.add("");
		redstoneReactorAddon.add("Generates "+Constants.redstoneReactorAddonGen+" RF per");
		redstoneReactorAddon.add("redstone dust in Ammo inventory.");
		redstoneReactorAddon.add("");
		redstoneReactorAddon.add("\u00A78Sublimation, refined.");
		
		damageAmpAddon = new ArrayList();
		damageAmpAddon.add("");
		damageAmpAddon.add("\u00A74Turret Addon");
		damageAmpAddon.add("");
		damageAmpAddon.add("Adds "+Constants.damageAmpDmgBonus+" damage per projectile.");
		damageAmpAddon.add("");
		damageAmpAddon.add("\u00A78Here we are juggernaught.");
	}
}
