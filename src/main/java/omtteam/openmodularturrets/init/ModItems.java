package omtteam.openmodularturrets.init;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import omtteam.omlib.util.InitHelper;
import omtteam.openmodularturrets.items.*;

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

    public static void init(IForgeRegistry<Item> registry) {
        intermediateProductTiered = InitHelper.registerItem(new IntermediateProductTiered(), registry);
        intermediateProductRegular = InitHelper.registerItem(new IntermediateProductRegular(), registry);
        addonMetaItem = InitHelper.registerItem(new AddonMetaItem(), registry);
        upgradeMetaItem = InitHelper.registerItem(new UpgradeMetaItem(), registry);
        ammoMetaItem = InitHelper.registerItem(new AmmoMetaItem(), registry);
        usableMetaItem = InitHelper.registerItem(new UsableMetaItem(), registry);

        for (Item item : subblocks) {
            registry.register(item);
        }
    }
}
