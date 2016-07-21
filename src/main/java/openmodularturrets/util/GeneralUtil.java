package openmodularturrets.util;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.registry.GameData;

/**
 * Created by Keridos on 17.05.2015.
 * This Class
 */
public class GeneralUtil {
    public static Item getMinecraftItem(String name) {
        Item item;
        item = GameData.getItemRegistry().getObject(new ResourceLocation("minecraft:" + name));
        return item;
    }

    public static String safeLocalize(String text) {
        if (StatCollector.translateToLocal(text) != null) {
            return StatCollector.translateToLocal(text);
        } else {
            return StatCollector.translateToFallback(text);
        }
    }
}