package omtteam.openmodularturrets.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.UpgradeType;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.item.AddonItem;
import omtteam.openmodularturrets.item.AmmoItem;
import omtteam.openmodularturrets.item.UpgradeItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * All items, including block items.
 */
public final class ModItems {
    private static final List<Item> ITEMS = new ArrayList<>();
    private static final String[] TIER_NAMES = {"one", "two", "three", "four", "five"};
    /** Every mod item in registration order (used for the creative tab). */
    public static final List<Item> ALL = Collections.unmodifiableList(ITEMS);

    public static final BlockItem TURRET_BASE_TIER_ONE = registerBlock(ModBlocks.TURRET_BASE_TIER_ONE);
    public static final BlockItem TURRET_BASE_TIER_TWO = registerBlock(ModBlocks.TURRET_BASE_TIER_TWO);
    public static final BlockItem TURRET_BASE_TIER_THREE = registerBlock(ModBlocks.TURRET_BASE_TIER_THREE);
    public static final BlockItem TURRET_BASE_TIER_FOUR = registerBlock(ModBlocks.TURRET_BASE_TIER_FOUR);
    public static final BlockItem TURRET_BASE_TIER_FIVE = registerBlock(ModBlocks.TURRET_BASE_TIER_FIVE);

    // Turrets
    public static final Map<TurretType, BlockItem> TURRETS = registerTurrets();

    // Ammunition (oldmod AmmoMetaItem meta 0–4)
    public static final AmmoItem AMMO_BLAZING_CLAY = registerAmmo("ammo_blazing_clay", AmmoType.BLAZING_CLAY);
    public static final AmmoItem AMMO_BULLET = registerAmmo("ammo_bullet", AmmoType.BULLET);
    public static final AmmoItem AMMO_FERRO_SLUG = registerAmmo("ammo_ferro_slug", AmmoType.FERRO_SLUG);
    public static final AmmoItem AMMO_GRENADE = registerAmmo("ammo_grenade", AmmoType.GRENADE);
    public static final AmmoItem AMMO_ROCKET = registerAmmo("ammo_rocket", AmmoType.ROCKET);

    // Intermediate products (oldmod IntermediateProductRegular)
    public static final Item IO_BUS = register("io_bus", new Item(new Item.Properties()));

    // Tiered intermediate products (oldmod IntermediateProductTiered meta 0–14), index = tier - 1
    public static final List<Item> SENSORS = registerTiered("sensor");
    public static final List<Item> CHAMBERS = registerTiered("chamber");
    public static final List<Item> BARRELS = registerTiered("barrel");

    // Upgrades (oldmod UpgradeMetaItem meta 0–4)
    public static final UpgradeItem UPGRADE_ACCURACY = registerUpgrade(UpgradeType.ACCURACY);
    public static final UpgradeItem UPGRADE_EFFICIENCY = registerUpgrade(UpgradeType.EFFICIENCY);
    public static final UpgradeItem UPGRADE_FIRE_RATE = registerUpgrade(UpgradeType.FIRE_RATE);
    public static final UpgradeItem UPGRADE_RANGE = registerUpgrade(UpgradeType.RANGE);
    public static final UpgradeItem UPGRADE_SCATTER_SHOT = registerUpgrade(UpgradeType.SCATTERSHOT);

    // Addons (oldmod AddonMetaItem; potentia and serial port are not ported, see AddonType)
    public static final AddonItem ADDON_CONCEALER = registerAddon(AddonType.CONCEALER);
    public static final AddonItem ADDON_DAMAGE_AMP = registerAddon(AddonType.DAMAGE_AMP);
    public static final AddonItem ADDON_RECYCLER = registerAddon(AddonType.RECYCLER);
    public static final AddonItem ADDON_REDSTONE_REACTOR = registerAddon(AddonType.REDSTONE_REACTOR);
    public static final AddonItem ADDON_SOLAR_PANEL = registerAddon(AddonType.SOLAR_PANEL);
    public static final AddonItem ADDON_FAKE_DROPS = registerAddon(AddonType.FAKE_DROPS);

    // Misc and passive defense blocks
    public static final BlockItem LEVER = registerBlock(ModBlocks.LEVER);
    public static final BlockItem GENERATOR = registerBlock(ModBlocks.GENERATOR);
    public static final List<BlockItem> HARDENED = ModBlocks.HARDENED.stream().map(ModItems::registerBlock).toList();
    public static final List<BlockItem> WALLS = ModBlocks.WALLS.stream().map(ModItems::registerBlock).toList();
    public static final List<BlockItem> FENCES = ModBlocks.FENCES.stream().map(ModItems::registerBlock).toList();
    public static final BlockItem CAMO_TRAP = registerBlock(ModBlocks.CAMO_TRAP);

    private ModItems() {
    }

    private static Map<TurretType, BlockItem> registerTurrets() {
        Map<TurretType, BlockItem> turrets = new EnumMap<>(TurretType.class);
        ModBlocks.TURRETS.forEach((type, block) -> turrets.put(type, registerBlock(block)));
        return Collections.unmodifiableMap(turrets);
    }

    private static List<Item> registerTiered(String kind) {
        List<Item> items = new ArrayList<>();
        for (String tier : TIER_NAMES) {
            items.add(register(kind + "_tier_" + tier, new Item(new Item.Properties())));
        }
        return List.copyOf(items);
    }

    private static UpgradeItem registerUpgrade(UpgradeType type) {
        return register("upgrade_" + type.getSerializedName(), new UpgradeItem(type, new Item.Properties()));
    }

    private static AddonItem registerAddon(AddonType type) {
        return register("addon_" + type.getSerializedName(), new AddonItem(type, new Item.Properties()));
    }

    private static AmmoItem registerAmmo(String name, AmmoType type) {
        return register(name, new AmmoItem(type, new Item.Properties()));
    }

    private static <T extends Item> T register(String name, T item) {
        ITEMS.add(item);
        return Registry.register(BuiltInRegistries.ITEM, OMT.id(name), item);
    }

    private static BlockItem registerBlock(Block block) {
        BlockItem item = new BlockItem(block, new Item.Properties());
        ITEMS.add(item);
        return Registry.register(BuiltInRegistries.ITEM, BuiltInRegistries.BLOCK.getKey(block), item);
    }

    /** Loads the class, registering all items. */
    public static void init() {
    }
}
