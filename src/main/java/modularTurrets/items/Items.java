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

        bulletThrowable = new BulletThrowableItem();
        grenadeThrowable = new GrenadeThrowableItem();
        accuraccyUpgradeItem = new AccuraccyUpgradeItem();
        efficiencyUpgradeItem = new EfficiencyUpgradeItem();
        fireRateUpgradeItem = new FireRateUpgradeItem();
        rangeUpgradeItem = new RangeUpgradeItem();
        redstoneReactorAddon = new RedstoneReactorAddonItem();
        damageAmpAddon = new DamageAmpAddonItem();
        solarPanelAddon = new SolarPanelAddonItem();
        configTab = new ConfigTabItem();
        bulletCraftable = new BulletCraftableItem();
        grenadeCraftable = new GrenadeCraftableItem();
        rocketCraftable = new RocketCraftableItem();
        sensorTierOneItem = new SensorTierOneItem();
        sensorTierTwoItem = new SensorTierTwoItem();
        sensorTierThreeItem = new SensorTierThreeItem();
        sensorTierFourItem = new SensorTierFourItem();
        barrel = new BarrelItem();
        chamber = new ChamberItem();
        ioBus = new IOBusItem();
        energeticBarrel = new EnergeticBarrelItem();
        containmentChamber = new ContainmentChamberItem();
    }
}
