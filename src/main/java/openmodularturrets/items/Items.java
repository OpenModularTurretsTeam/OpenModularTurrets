package openmodularturrets.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import openmodularturrets.items.addons.DamageAmpAddonItem;
import openmodularturrets.items.addons.RedstoneReactorAddonItem;
import openmodularturrets.items.addons.SolarPanelAddonItem;
import openmodularturrets.items.ammo.BulletAmmoItem;
import openmodularturrets.items.ammo.FerroSlugItem;
import openmodularturrets.items.ammo.GrenadeAmmoItem;
import openmodularturrets.items.ammo.RocketAmmoItem;
import openmodularturrets.items.upgrades.AccuraccyUpgradeItem;
import openmodularturrets.items.upgrades.EfficiencyUpgradeItem;
import openmodularturrets.items.upgrades.FireRateUpgradeItem;
import openmodularturrets.items.upgrades.RangeUpgradeItem;

public class Items {
    public static Item bulletThrowable;
    public static Item grenadeThrowable;
    public static Item bulletCraftable;
    public static Item grenadeCraftable;
    public static Item rocketCraftable;
    public static Item ferroSlug;
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
        bulletThrowable = new BulletThrowableItem();
        GameRegistry.registerItem(bulletThrowable, "bulletThrowable");

        grenadeThrowable = new GrenadeThrowableItem();
        GameRegistry.registerItem(grenadeThrowable, "grenadeThrowable");

        accuraccyUpgradeItem = new AccuraccyUpgradeItem();
        GameRegistry.registerItem(accuraccyUpgradeItem, "accuraccyUpgradeItem");

        efficiencyUpgradeItem = new EfficiencyUpgradeItem();
        GameRegistry.registerItem(efficiencyUpgradeItem, "efficiencyUpgradeItem");

        fireRateUpgradeItem = new FireRateUpgradeItem();
        GameRegistry.registerItem(fireRateUpgradeItem, "fireRateUpgradeItem");

        rangeUpgradeItem = new RangeUpgradeItem();
        GameRegistry.registerItem(rangeUpgradeItem, "rangeUpgradeItem");

        redstoneReactorAddon = new RedstoneReactorAddonItem();
        GameRegistry.registerItem(redstoneReactorAddon, "redstoneReactorAddon");

        damageAmpAddon = new DamageAmpAddonItem();
        GameRegistry.registerItem(damageAmpAddon, "damageAmpAddon");

        solarPanelAddon = new SolarPanelAddonItem();
        GameRegistry.registerItem(solarPanelAddon, "solarPanelAddon");

        configTab = new ConfigTabItem();
        GameRegistry.registerItem(configTab, "configTab");

        bulletCraftable = new BulletAmmoItem();
        GameRegistry.registerItem(bulletCraftable, "bulletCraftable");

        grenadeCraftable = new GrenadeAmmoItem();
        GameRegistry.registerItem(grenadeCraftable, "grenadeCraftable");

        rocketCraftable = new RocketAmmoItem();
        GameRegistry.registerItem(rocketCraftable, "rocketCraftable");
        
        ferroSlug = new FerroSlugItem();
        GameRegistry.registerItem(ferroSlug, "ferroSlug");

        sensorTierOneItem = new SensorTierOneItem();
        GameRegistry.registerItem(sensorTierOneItem, "sensorTierOneItem");

        sensorTierTwoItem = new SensorTierTwoItem();
        GameRegistry.registerItem(sensorTierTwoItem, "sensorTierTwoItem");

        sensorTierThreeItem = new SensorTierThreeItem();
        GameRegistry.registerItem(sensorTierThreeItem, "sensorTierThreeItem");

        sensorTierFourItem = new SensorTierFourItem();
        GameRegistry.registerItem(sensorTierFourItem, "sensorTierFourItem");

        barrel = new BarrelItem();
        GameRegistry.registerItem(barrel, "barrel");

        chamber = new ChamberItem();
        GameRegistry.registerItem(chamber, "chamber");

        ioBus = new IOBusItem();
        GameRegistry.registerItem(ioBus, "ioBus");

        energeticBarrel = new EnergeticBarrelItem();
        GameRegistry.registerItem(energeticBarrel, "energeticBarrel");

        containmentChamber = new ContainmentChamberItem();
        GameRegistry.registerItem(containmentChamber, "containmentChamber");
    }
}
