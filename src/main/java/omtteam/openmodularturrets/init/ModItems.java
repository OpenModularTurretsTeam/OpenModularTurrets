package omtteam.openmodularturrets.init;


import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import omtteam.openmodularturrets.items.*;
import omtteam.openmodularturrets.reference.Names;

public class ModItems {
    public static Item addonMetaItem;
    public static Item upgradeMetaItem;
    public static Item intermediateProductTiered;
    public static Item intermediateProductRegular;
    public static Item ammoMetaItem;
    public static Item throwableMetaItem;

    public static void init() {
        intermediateProductTiered = new IntermediateProductTiered();
        GameRegistry.registerItem(intermediateProductTiered, Names.Items.intermediateTieredItem);

        intermediateProductRegular = new IntermediateProductRegular();
        GameRegistry.registerItem(intermediateProductRegular, Names.Items.intermediateRegularItem);

        addonMetaItem = new AddonMetaItem();
        GameRegistry.registerItem(addonMetaItem, Names.Items.addonMetaItem);

        upgradeMetaItem = new UpgradeMetaItem();
        GameRegistry.registerItem(upgradeMetaItem, Names.Items.upgradeMetaItem);

        ammoMetaItem = new AmmoMetaItem();
        GameRegistry.registerItem(ammoMetaItem, Names.Items.ammoMetaItem);

        throwableMetaItem = new ThrowableMetaItem();
        GameRegistry.registerItem(throwableMetaItem, Names.Items.throwableMetaItem);

    }
}
