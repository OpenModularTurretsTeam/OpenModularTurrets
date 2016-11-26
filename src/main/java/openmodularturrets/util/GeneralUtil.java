package openmodularturrets.util;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
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
        if (!I18n.translateToLocal(text).equals(text)) {
            return I18n.translateToLocal(text);
        } else {
            return I18n.translateToFallback(text);
        }
    }
}