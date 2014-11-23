package modularTurrets.items;

import cpw.mods.fml.common.registry.GameRegistry;
import modularTurrets.items.addons.DamageAmpAddonItem;
import modularTurrets.items.addons.RedstoneReactorAddonItem;
import modularTurrets.items.addons.SolarPanelAddonItem;
import modularTurrets.items.ammo.BulletAmmoItem;
import modularTurrets.items.ammo.GrenadeAmmoItem;
import modularTurrets.items.ammo.RocketAmmoItem;
import modularTurrets.items.upgrades.AccuraccyUpgradeItem;
import modularTurrets.items.upgrades.EfficiencyUpgradeItem;
import modularTurrets.items.upgrades.FireRateUpgradeItem;
import modularTurrets.items.upgrades.RangeUpgradeItem;
import net.minecraft.item.Item;

public class Items {
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
