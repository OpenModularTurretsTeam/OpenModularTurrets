package omtteam.openmodularturrets.compat;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
//? if >=1.21 {
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
//?} else {
/*import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
*///?}

/**
 * Common ({@code c:}) item tags replacing the 1.12.2 ore dictionary entries. The tag ids differ between versions
 * ({@code c:iron_ingots} on 1.20.1, {@code c:ingots/iron} on 1.21), Fabric API provides both.
 * <p>
 * 1.20.1 has no common tags for redstone/lapis blocks and wooden chests; recipes use the vanilla items there
 * ({@code Items.REDSTONE_BLOCK}, {@code Items.LAPIS_BLOCK}, {@code Items.CHEST}).
 */
public final class CommonTags {
    /** Ore dictionary {@code ingotIron}. */
    public static final TagKey<Item> IRON_INGOTS = ConventionalItemTags.IRON_INGOTS;
    /** Ore dictionary {@code dustRedstone}. */
    public static final TagKey<Item> REDSTONE_DUSTS = ConventionalItemTags.REDSTONE_DUSTS;
    /** Ore dictionary {@code ingotGold}. */
    public static final TagKey<Item> GOLD_INGOTS = ConventionalItemTags.GOLD_INGOTS;
    /** Ore dictionary {@code paneGlass}. */
    public static final TagKey<Item> GLASS_PANES = ConventionalItemTags.GLASS_PANES;
    //? if >=1.21 {
    /** Ore dictionary {@code gemQuartz}. */
    public static final TagKey<Item> QUARTZ_GEMS = ConventionalItemTags.QUARTZ_GEMS;
    /** Ore dictionary {@code gemDiamond}. */
    public static final TagKey<Item> DIAMOND_GEMS = ConventionalItemTags.DIAMOND_GEMS;
    //?} else {
    /*/^* Ore dictionary {@code gemQuartz}. ^/
    public static final TagKey<Item> QUARTZ_GEMS = ConventionalItemTags.QUARTZ;
    /^* Ore dictionary {@code gemDiamond}. ^/
    public static final TagKey<Item> DIAMOND_GEMS = ConventionalItemTags.DIAMONDS;
    *///?}

    private CommonTags() {
    }
}
