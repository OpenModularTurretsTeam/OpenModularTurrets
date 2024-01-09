package openmodularturrets.items;

import net.minecraft.item.Item;

import cpw.mods.fml.common.registry.GameRegistry;
import openmodularturrets.compatability.ModCompatibility;
import openmodularturrets.items.addons.ConcealerAddonItem;
import openmodularturrets.items.addons.DamageAmpAddonItem;
import openmodularturrets.items.addons.PotentiaAddonItem;
import openmodularturrets.items.addons.RecyclerAddonItem;
import openmodularturrets.items.addons.RedstoneReactorAddonItem;
import openmodularturrets.items.addons.SerialPortAddonItem;
import openmodularturrets.items.addons.SolarPanelAddonItem;
import openmodularturrets.items.ammo.BlazingClayAmmoItem;
import openmodularturrets.items.ammo.BulletAmmoItem;
import openmodularturrets.items.ammo.FerroSlugItem;
import openmodularturrets.items.ammo.GrenadeAmmoItem;
import openmodularturrets.items.ammo.RocketAmmoItem;
import openmodularturrets.items.upgrades.AccuracyUpgradeItem;
import openmodularturrets.items.upgrades.EfficiencyUpgradeItem;
import openmodularturrets.items.upgrades.FireRateUpgradeItem;
import openmodularturrets.items.upgrades.RangeUpgradeItem;
import openmodularturrets.items.upgrades.ScattershotUpgradeItem;

public class Items {

    public static Item bulletThrowable;
    public static Item grenadeThrowable;
    public static Item bulletCraftable;
    public static Item blazingClayCraftable;
    public static Item grenadeCraftable;
    public static Item rocketCraftable;
    public static Item ferroSlug;
    public static Item accuraccyUpgradeItem;
    public static Item efficiencyUpgradeItem;
    public static Item fireRateUpgradeItem;
    public static Item rangeUpgradeItem;
    public static Item scattershotUpgradeItem;
    public static Item redstoneReactorAddon;
    public static Item damageAmpAddon;
    public static Item solarPanelAddon;
    public static Item potentiaAddon;
    public static Item serialPortAddon;
    public static Item recyclerAddon;
    public static Item concealerAddon;

    public static Item sensorTierOneItem;
    public static Item sensorTierTwoItem;
    public static Item sensorTierThreeItem;
    public static Item sensorTierFourItem;
    public static Item sensorTierFiveItem;

    public static Item barrelTierOneItem;
    public static Item barrelTierTwoItem;
    public static Item barrelTierThreeItem;
    public static Item barrelTierFourItem;
    public static Item barrelTierFiveItem;

    public static Item chamberTierOneItem;
    public static Item chamberTierTwoItem;
    public static Item chamberTierThreeItem;
    public static Item chamberTierFourItem;
    public static Item chamberTierFiveItem;

    public static Item ioBus;

    public static void init() {
        bulletThrowable = new BulletThrowableItem();
        GameRegistry.registerItem(bulletThrowable, "bulletThrowable");

        grenadeThrowable = new GrenadeThrowableItem();
        GameRegistry.registerItem(grenadeThrowable, "grenadeThrowable");

        accuraccyUpgradeItem = new AccuracyUpgradeItem();
        GameRegistry.registerItem(accuraccyUpgradeItem, "accuraccyUpgradeItem");

        efficiencyUpgradeItem = new EfficiencyUpgradeItem();
        GameRegistry.registerItem(efficiencyUpgradeItem, "efficiencyUpgradeItem");

        fireRateUpgradeItem = new FireRateUpgradeItem();
        GameRegistry.registerItem(fireRateUpgradeItem, "fireRateUpgradeItem");

        rangeUpgradeItem = new RangeUpgradeItem();
        GameRegistry.registerItem(rangeUpgradeItem, "rangeUpgradeItem");

        scattershotUpgradeItem = new ScattershotUpgradeItem();
        GameRegistry.registerItem(scattershotUpgradeItem, "scattershotUpgradeItem");

        redstoneReactorAddon = new RedstoneReactorAddonItem();
        GameRegistry.registerItem(redstoneReactorAddon, "redstoneReactorAddon");

        damageAmpAddon = new DamageAmpAddonItem();
        GameRegistry.registerItem(damageAmpAddon, "damageAmpAddon");

        solarPanelAddon = new SolarPanelAddonItem();
        GameRegistry.registerItem(solarPanelAddon, "solarPanelAddon");

        recyclerAddon = new RecyclerAddonItem();
        GameRegistry.registerItem(recyclerAddon, "recyclerAddon");

        concealerAddon = new ConcealerAddonItem();
        GameRegistry.registerItem(concealerAddon, "concealerAddon");

        // Thaumcraft Only
        if (ModCompatibility.ThaumcraftLoaded) {
            potentiaAddon = new PotentiaAddonItem();
            GameRegistry.registerItem(potentiaAddon, "potentiaAddon");
        }

        // Computer mods Only
        if (ModCompatibility.ComputercraftLoaded || ModCompatibility.OpenComputersLoaded) {
            serialPortAddon = new SerialPortAddonItem();
            GameRegistry.registerItem(serialPortAddon, "serialPortAddon");
        }

        bulletCraftable = new BulletAmmoItem();
        GameRegistry.registerItem(bulletCraftable, "bulletCraftable");

        blazingClayCraftable = new BlazingClayAmmoItem();
        GameRegistry.registerItem(blazingClayCraftable, "blazingClayCraftable");

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

        sensorTierFiveItem = new SensorTierFiveItem();
        GameRegistry.registerItem(sensorTierFiveItem, "sensorTierFiveItem");

        barrelTierOneItem = new BarrelTierOneItem();
        GameRegistry.registerItem(barrelTierOneItem, "barrelTierOne");

        barrelTierTwoItem = new BarrelTierTwoItem();
        GameRegistry.registerItem(barrelTierTwoItem, "barrelTierTwo");

        barrelTierThreeItem = new BarrelTierThreeItem();
        GameRegistry.registerItem(barrelTierThreeItem, "barrelTierThree");

        barrelTierFourItem = new BarrelTierFourItem();
        GameRegistry.registerItem(barrelTierFourItem, "barrelTierFour");

        barrelTierFiveItem = new BarrelTierFiveItem();
        GameRegistry.registerItem(barrelTierFiveItem, "barrelTierFive");

        chamberTierOneItem = new ChamberTierOneItem();
        GameRegistry.registerItem(chamberTierOneItem, "chamberTierOne");

        chamberTierTwoItem = new ChamberTierTwoItem();
        GameRegistry.registerItem(chamberTierTwoItem, "chamberTierTwo");

        chamberTierThreeItem = new ChamberTierThreeItem();
        GameRegistry.registerItem(chamberTierThreeItem, "chamberTierThree");

        chamberTierFourItem = new ChamberTierFourItem();
        GameRegistry.registerItem(chamberTierFourItem, "chamberTierFour");

        chamberTierFiveItem = new ChamberTierFiveItem();
        GameRegistry.registerItem(chamberTierFiveItem, "chamberTierFive");

        ioBus = new IOBusItem();
        GameRegistry.registerItem(ioBus, "ioBus");
    }
}
