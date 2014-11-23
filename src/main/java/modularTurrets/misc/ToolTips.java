package modularTurrets.misc;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ToolTips {
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
