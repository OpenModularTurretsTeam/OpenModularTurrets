package openmodularturrets.items;


import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
    public static Item redstoneReactorAddon;
    public static Item damageAmpAddon;
    public static Item solarPanelAddon;
    public static Item potentiaAddon;
    public static Item serialPortAddon;
    public static Item recyclerAddon;
    public static Item concealerAddon;

    public static Item intermediateProductTiered;
    public static Item ioBus;

    public static void init() {
        bulletThrowable = new BulletThrowableItem();
        GameRegistry.registerItem(bulletThrowable, Names.Items.bulletThrowableItem);

        grenadeThrowable = new GrenadeThrowableItem();
        GameRegistry.registerItem(grenadeThrowable, Names.Items.grenadeThrowableItem);

        /*accuracyUpgradeItem = new AccuracyUpgradeItem();
        GameRegistry.registerItem(accuracyUpgradeItem, Names.Items.accuraccyUpgrade);

        efficiencyUpgradeItem = new EfficiencyUpgradeItem();
        GameRegistry.registerItem(efficiencyUpgradeItem, Names.Items.bulletThrowableItem);

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

        //Computer mods Only
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
        GameRegistry.registerItem(ferroSlug, "ferroSlug"); */

        intermediateProductTiered = new IntermediateProductTiered();
        GameRegistry.registerItem(intermediateProductTiered, Names.Items.intermediateOneItem);

        ioBus = new IOBusItem();
        GameRegistry.registerItem(ioBus, Names.Items.ioBus);
    }
}
