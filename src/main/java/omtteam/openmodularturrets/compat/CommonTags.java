package omtteam.openmodularturrets.compat;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Common ({@code c:}) item tags replacing the 1.12.2 ore dictionary entries, as plain tag keys (the same ids on Fabric
 * and NeoForge). The ids differ between versions ({@code c:iron_ingots} on 1.20.1, {@code c:ingots/iron} on 1.21).
 * <p>
 * 1.20.1 has no common tags for redstone/lapis blocks and wooden chests; recipes use the vanilla items there
 * ({@code Items.REDSTONE_BLOCK}, {@code Items.LAPIS_BLOCK}, {@code Items.CHEST}).
 */
public final class CommonTags {
    //? if >=1.21 {
    /** Ore dictionary {@code ingotIron}. */
    public static final TagKey<Item> IRON_INGOTS = tag("ingots/iron");
    /** Ore dictionary {@code dustRedstone}. */
    public static final TagKey<Item> REDSTONE_DUSTS = tag("dusts/redstone");
    /** Ore dictionary {@code ingotGold}. */
    public static final TagKey<Item> GOLD_INGOTS = tag("ingots/gold");
    /** Ore dictionary {@code gemQuartz}. */
    public static final TagKey<Item> QUARTZ_GEMS = tag("gems/quartz");
    /** Ore dictionary {@code gemDiamond}. */
    public static final TagKey<Item> DIAMOND_GEMS = tag("gems/diamond");
    //?} else {
    /*/^* Ore dictionary {@code ingotIron}. ^/
    public static final TagKey<Item> IRON_INGOTS = tag("iron_ingots");
    /^* Ore dictionary {@code dustRedstone}. ^/
    public static final TagKey<Item> REDSTONE_DUSTS = tag("redstone_dusts");
    /^* Ore dictionary {@code ingotGold}. ^/
    public static final TagKey<Item> GOLD_INGOTS = tag("gold_ingots");
    /^* Ore dictionary {@code gemQuartz}. ^/
    public static final TagKey<Item> QUARTZ_GEMS = tag("quartz");
    /^* Ore dictionary {@code gemDiamond}. ^/
    public static final TagKey<Item> DIAMOND_GEMS = tag("diamonds");
    *///?}
    /** Ore dictionary {@code paneGlass}. */
    public static final TagKey<Item> GLASS_PANES = tag("glass_panes");

    private CommonTags() {
    }

    private static TagKey<Item> tag(String path) {
        return TagKey.create(Registries.ITEM, VersionCompat.id("c", path));
    }
}
