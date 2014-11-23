package modularTurrets.items;

import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Items {

    public static Item bulletThrowable;
    public static Item grenadeThrowable;
    public static Item bulletCraftable;
    public static Item grenadeCraftable;
    public static Item rocketCraftable;
    public static Item accuraccyUpgradeItem;
    public static Item efficiencyUpgradeItem;
    public static Item fireRateUpgradeItem;
    public static Item rangeUpgradeItem;
    public static Item redstoneReactorAddon;
    public static Item damageAmpAddon;
    public static Item solarPanelAddon;
    public static Item configTab;
    public static Item sensorTierOneItem;
    public static Item sensorTierTwoItem;
    public static Item sensorTierThreeItem;
    public static Item sensorTierFourItem;
    public static Item barrel;
    public static Item chamber;
    public static Item ioBus;
    public static Item energeticBarrel;
    public static Item containmentChamber;

    public static void init() {

	bulletThrowable = new BulletThrowableItem(ItemIDs.bulletThrowableItem);
	grenadeThrowable = new GrenadeThrowableItem(
		ItemIDs.grenadeThrowableItem);
	accuraccyUpgradeItem = new AccuraccyUpgradeItem(
		ItemIDs.accuraccyUpgrade);
	efficiencyUpgradeItem = new EfficiencyUpgradeItem(
		ItemIDs.efficiencyUpgrade);
	fireRateUpgradeItem = new FireRateUpgradeItem(ItemIDs.fireRateUpgrade);
	rangeUpgradeItem = new RangeUpgradeItem(ItemIDs.rangeUpgrade);
	redstoneReactorAddon = new RedstoneReactorAddonItem(
		ItemIDs.redstoneReactorAddon);
	damageAmpAddon = new DamageAmpAddonItem(ItemIDs.damageAmpAddon);
	solarPanelAddon = new SolarPanelAddonItem(ItemIDs.solarPanelAddon);
	configTab = new ConfigTabItem(ItemIDs.configTab);
	bulletCraftable = new BulletCraftableItem(ItemIDs.bulletCraftItem);
	grenadeCraftable = new GrenadeCraftableItem(ItemIDs.grenadeCraftItem);
	rocketCraftable = new RocketCraftableItem(ItemIDs.rocketCraftItem);
	sensorTierOneItem = new SensorTierOneItem(ItemIDs.sensorTierOne);
	sensorTierTwoItem = new SensorTierTwoItem(ItemIDs.sensorTierTwo);
	sensorTierThreeItem = new SensorTierThreeItem(ItemIDs.sensorTierThree);
	sensorTierFourItem = new SensorTierFourItem(ItemIDs.sensorTierFour);
	barrel = new BarrelItem(ItemIDs.barrel);
	chamber = new ChamberItem(ItemIDs.chamber);
	ioBus = new IOBusItem(ItemIDs.ioBus);
	energeticBarrel = new EnergeticBarrelItem(ItemIDs.energeticBarrel);
	containmentChamber = new ContainmentChamberItem(
		ItemIDs.containmentChamber);
    }

    public static void addNames() {

	LanguageRegistry
		.addName(bulletThrowable, ItemNames.bulletThrowableItem);
	LanguageRegistry.addName(grenadeThrowable,
		ItemNames.grenadeThrowableItem);
	LanguageRegistry
		.addName(bulletCraftable, ItemNames.bulletCraftableItem);
	LanguageRegistry.addName(grenadeCraftable,
		ItemNames.grenadeCraftableItem);
	LanguageRegistry
		.addName(rocketCraftable, ItemNames.rocketCraftableItem);
	LanguageRegistry.addName(accuraccyUpgradeItem,
		ItemNames.accuraccyUpgrade);
	LanguageRegistry.addName(efficiencyUpgradeItem,
		ItemNames.efficiencyUpgrade);
	LanguageRegistry
		.addName(fireRateUpgradeItem, ItemNames.fireRateUpgrade);
	LanguageRegistry.addName(rangeUpgradeItem, ItemNames.rangeUpgrade);
	LanguageRegistry.addName(redstoneReactorAddon,
		ItemNames.redReactorAddon);
	LanguageRegistry.addName(damageAmpAddon, ItemNames.damageAmpAddon);
	LanguageRegistry.addName(solarPanelAddon, ItemNames.solarPanelAddon);
	LanguageRegistry.addName(configTab, ItemNames.configTablet);
	LanguageRegistry.addName(sensorTierOneItem, ItemNames.sensorTierOne);
	LanguageRegistry.addName(sensorTierTwoItem, ItemNames.sensorTierTwo);
	LanguageRegistry
		.addName(sensorTierThreeItem, ItemNames.sensorTierThree);
	LanguageRegistry.addName(sensorTierFourItem, ItemNames.sensorTierFour);
	LanguageRegistry.addName(barrel, ItemNames.barrel);
	LanguageRegistry.addName(chamber, ItemNames.chamber);
	LanguageRegistry.addName(ioBus, ItemNames.ioBus);
	LanguageRegistry.addName(energeticBarrel, ItemNames.energeticBarrel);
	LanguageRegistry.addName(containmentChamber,
		ItemNames.containmentChamber);

    }

}
