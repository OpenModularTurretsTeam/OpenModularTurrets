package omtteam.openmodularturrets.registry;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import omtteam.omlib.registry.OMLibItems;
import omtteam.omlib.util.InitHelper;
import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.item.AddonMetaItem;
import omtteam.openmodularturrets.item.AmmoItem;
import omtteam.openmodularturrets.item.AmmoMetaItem;
import omtteam.openmodularturrets.item.IntermediateProductRegular;
import omtteam.openmodularturrets.item.IntermediateProductTiered;
import omtteam.openmodularturrets.item.ItemGeneratorUpgrade;
import omtteam.openmodularturrets.item.UpgradeMetaItem;
import omtteam.openmodularturrets.item.UsableMetaItem;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final List<Item> subblocks = new ArrayList<>();
    public static Item addonMetaItem;
    public static Item upgradeMetaItem;
    public static Item intermediateProductTiered;
    public static Item intermediateProductRegular;
    public static Item ammoMetaItem;
    public static Item ammoBolt;
    public static Item usableMetaItem;
    public static Item upgradeEnergyMultiplier;

    public static void init(IForgeRegistry<Item> registry) {
        intermediateProductTiered = InitHelper.registerItem(inTab(new IntermediateProductTiered()), registry);
        intermediateProductRegular = InitHelper.registerItem(inTab(new IntermediateProductRegular()), registry);
        addonMetaItem = InitHelper.registerItem(inTab(new AddonMetaItem()), registry);
        upgradeMetaItem = InitHelper.registerItem(inTab(new UpgradeMetaItem()), registry);
        ammoMetaItem = InitHelper.registerItem(inTab(new AmmoMetaItem()), registry);
        // the crossbow turret's ammo (new in 4.0.0, as on 1.20.1+), next to the other ammo in the creative tab
        ammoBolt = InitHelper.registerItem(inTab(new AmmoItem(AmmoType.BOLT)), registry);
        usableMetaItem = InitHelper.registerItem(inTab(new UsableMetaItem()), registry);
        upgradeEnergyMultiplier = InitHelper.registerItem(
                inTab(new ItemGeneratorUpgrade(GeneratorUpgradeType.ENERGY_MULTIPLIER)), registry);
        // OMLib's ownership wand moves from the Tools tab into ours, as on 1.20.1+ (one tab only); OMLib registers its
        // items first (its event handler is registered before ours, OMT loads after OMLib)
        if (OMLibItems.ownershipWand != null) {
            inTab(OMLibItems.ownershipWand);
        }

        for (Item item : subblocks) {
            // item blocks of other mods' turrets are registered as their mod
            ModContainerScope.registerAs(item.getRegistryName(), () -> registry.register(item));
        }
    }

    /** Puts an item into the mod's creative tab. */
    private static <T extends Item> T inTab(T item) {
        item.setCreativeTab(ModularTurretsTab.getInstance());
        return item;
    }
}
