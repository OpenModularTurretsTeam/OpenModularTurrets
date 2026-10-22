package omtteam.openmodularturrets.registry;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import omtteam.omlib.util.InitHelper;
import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;
import omtteam.openmodularturrets.item.AddonMetaItem;
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
    public static Item usableMetaItem;
    public static Item upgradeEnergyMultiplier;

    public static void init(IForgeRegistry<Item> registry) {
        intermediateProductTiered = InitHelper.registerItem(new IntermediateProductTiered(), registry);
        intermediateProductRegular = InitHelper.registerItem(new IntermediateProductRegular(), registry);
        addonMetaItem = InitHelper.registerItem(new AddonMetaItem(), registry);
        upgradeMetaItem = InitHelper.registerItem(new UpgradeMetaItem(), registry);
        ammoMetaItem = InitHelper.registerItem(new AmmoMetaItem(), registry);
        usableMetaItem = InitHelper.registerItem(new UsableMetaItem(), registry);
        upgradeEnergyMultiplier = InitHelper.registerItem(
                new ItemGeneratorUpgrade(GeneratorUpgradeType.ENERGY_MULTIPLIER), registry);

        for (Item item : subblocks) {
            // item blocks of other mods' turrets are registered as their mod
            ModContainerScope.registerAs(item.getRegistryName(), () -> registry.register(item));
        }
    }
}
