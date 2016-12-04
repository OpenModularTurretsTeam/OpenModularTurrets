package omtteam.openmodularturrets.init;


import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import omtteam.openmodularturrets.items.*;

public class ModItems {
    public static Item addonMetaItem;
    public static Item upgradeMetaItem;
    public static Item intermediateProductTiered;
    public static Item intermediateProductRegular;
    public static Item ammoMetaItem;
    public static Item throwableMetaItem;

    public static void init() {
        intermediateProductTiered = new IntermediateProductTiered();
        GameRegistry.register(intermediateProductTiered);

        intermediateProductRegular = new IntermediateProductRegular();
        GameRegistry.register(intermediateProductRegular);

        addonMetaItem = new AddonMetaItem();
        GameRegistry.register(addonMetaItem);

        upgradeMetaItem = new UpgradeMetaItem();
        GameRegistry.register(upgradeMetaItem);

        ammoMetaItem = new AmmoMetaItem();
        GameRegistry.register(ammoMetaItem);

        throwableMetaItem = new ThrowableMetaItem();
        GameRegistry.register(throwableMetaItem);
    }
}
