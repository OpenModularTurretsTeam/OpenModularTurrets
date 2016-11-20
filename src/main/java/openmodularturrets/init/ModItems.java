package openmodularturrets.init;


import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import openmodularturrets.items.*;
import openmodularturrets.reference.Names;

public class ModItems {
    public static Item bulletThrowable;
    public static Item grenadeThrowable;
    public static Item bulletCraftable;
    public static Item blazingClayCraftable;
    public static Item grenadeCraftable;
    public static Item rocketCraftable;
    public static Item ferroSlug;
    public static Item accuracyUpgradeItem;
    public static Item efficiencyUpgradeItem;
    public static Item fireRateUpgradeItem;
    public static Item rangeUpgradeItem;
    public static Item scattershotUpgradeItem;

    public static Item addonMetaItem;
    public static Item upgradeMetaItem;
    public static Item intermediateProductTiered;
    public static Item intermediateProductRegular;


    public static void init() {
        bulletThrowable = new BulletThrowableItem();
        GameRegistry.registerItem(bulletThrowable, Names.Items.bulletThrowableItem);

        grenadeThrowable = new GrenadeThrowableItem();
        GameRegistry.registerItem(grenadeThrowable, Names.Items.grenadeThrowableItem);

        /*bulletCraftable = new BulletAmmoItem();
        GameRegistry.registerItem(bulletCraftable, "bulletCraftable");

        blazingClayCraftable = new BlazingClayAmmoItem();
        GameRegistry.registerItem(blazingClayCraftable, "blazingClayCraftable");

        grenadeCraftable = new GrenadeAmmoItem();
        GameRegistry.registerItem(grenadeCraftable, "grenadeCraftable");

        rocketCraftable = new RocketAmmoItem();
        GameRegistry.registerItem(rocketCraftable, "rocketCraftable");

        ferroSlug = new FerroSlugItem();
        GameRegistry.registerItem(ferroSlug, "ferroSlug"); */

        intermediateProductTiered = new IntermediateProductTiered();
        GameRegistry.registerItem(intermediateProductTiered, Names.Items.intermediateTieredItem);

        intermediateProductRegular = new IntermediateProductRegular();
        GameRegistry.registerItem(intermediateProductRegular, Names.Items.intermediateRegularItem);

        addonMetaItem = new AddonMetaItem();
        GameRegistry.registerItem(addonMetaItem, Names.Items.addonMetaItem);

        upgradeMetaItem = new UpgradeMetaItem();
        GameRegistry.registerItem(upgradeMetaItem, Names.Items.upgradeMetaItem);

    }
}
