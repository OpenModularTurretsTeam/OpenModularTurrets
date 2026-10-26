package omtteam.openmodularturrets.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import omtteam.openmodularturrets.OMT;
import omtteam.omlib.compat.VersionCompat;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;
import omtteam.openmodularturrets.core.item.TurretBaseUpgradeType;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.item.AddonItem;
import omtteam.openmodularturrets.item.AmmoItem;
import omtteam.openmodularturrets.item.MemoryCardItem;
import omtteam.omlib.item.OwnershipWandItem;
import omtteam.openmodularturrets.item.GeneratorUpgradeItem;
import omtteam.openmodularturrets.item.TurretBaseUpgradeItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

    // Ammunition (1.12.2 AmmoMetaItem meta 0–4)
    public static final AmmoItem AMMO_BLAZING_CLAY = registerAmmo("ammo_blazing_clay", AmmoType.BLAZING_CLAY);
    public static final AmmoItem AMMO_BOLT = registerAmmo("ammo_bolt", AmmoType.BOLT);
    public static final AmmoItem AMMO_BULLET = registerAmmo("ammo_bullet", AmmoType.BULLET);
    public static final AmmoItem AMMO_FERRO_SLUG = registerAmmo("ammo_ferro_slug", AmmoType.FERRO_SLUG);
    public static final AmmoItem AMMO_GRENADE = registerAmmo("ammo_grenade", AmmoType.GRENADE);
    public static final AmmoItem AMMO_ROCKET = registerAmmo("ammo_rocket", AmmoType.ROCKET);

    // Intermediate products (1.12.2 IntermediateProductRegular)
    public static final Item IO_BUS = register("io_bus", Item::new, new Item.Properties());

    // Memory card (1.12.2 UsableMetaItem meta 2)
    public static final MemoryCardItem MEMORY_CARD = register("memory_card", MemoryCardItem::new, new Item.Properties().stacksTo(1));

    // Map maker's ownership wand (creative only)
    public static final OwnershipWandItem OWNERSHIP_WAND = register("ownership_wand", OwnershipWandItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    // Tiered intermediate products (1.12.2 IntermediateProductTiered meta 0–14), index = tier - 1
    public static final List<Item> SENSORS = registerTiered("sensor");
    public static final List<Item> CHAMBERS = registerTiered("chamber");
    public static final List<Item> BARRELS = registerTiered("barrel");

    // Turret base upgrades (1.12.2 UpgradeMetaItem meta 0–4)
    public static final TurretBaseUpgradeItem UPGRADE_ACCURACY = registerUpgrade(TurretBaseUpgradeType.ACCURACY);
    public static final TurretBaseUpgradeItem UPGRADE_EFFICIENCY = registerUpgrade(TurretBaseUpgradeType.EFFICIENCY);
    public static final TurretBaseUpgradeItem UPGRADE_FIRE_RATE = registerUpgrade(TurretBaseUpgradeType.FIRE_RATE);
    public static final TurretBaseUpgradeItem UPGRADE_RANGE = registerUpgrade(TurretBaseUpgradeType.RANGE);
    public static final TurretBaseUpgradeItem UPGRADE_SCATTER_SHOT = registerUpgrade(TurretBaseUpgradeType.SCATTERSHOT);
    @SuppressWarnings("unused")
    public static final TurretBaseUpgradeItem UPGRADE_CREATIVE = registerUpgrade(TurretBaseUpgradeType.CREATIVE);

    // Generator upgrades
    public static final GeneratorUpgradeItem UPGRADE_ENERGY_MULTIPLIER = register(
            "upgrade_" + GeneratorUpgradeType.ENERGY_MULTIPLIER.getSerializedName(),
            properties -> new GeneratorUpgradeItem(GeneratorUpgradeType.ENERGY_MULTIPLIER, properties), new Item.Properties());

    // Addons (1.12.2 AddonMetaItem; potentia and serial port are not ported, see AddonType)
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
        Map<TurretType, BlockItem> turrets = new LinkedHashMap<>();
        ModBlocks.TURRETS.forEach((type, block) -> turrets.put(type, registerBlock(block)));
        return Collections.unmodifiableMap(turrets);
    }

    private static List<Item> registerTiered(String kind) {
        List<Item> items = new ArrayList<>();
        for (String tier : TIER_NAMES) {
            items.add(register(kind + "_tier_" + tier, Item::new, new Item.Properties()));
        }
        return List.copyOf(items);
    }

    private static TurretBaseUpgradeItem registerUpgrade(TurretBaseUpgradeType type) {
        return register("upgrade_" + type.getSerializedName(), properties -> new TurretBaseUpgradeItem(type, properties),
                new Item.Properties());
    }

    private static AddonItem registerAddon(AddonType type) {
        return register("addon_" + type.getSerializedName(), properties -> new AddonItem(type, properties), new Item.Properties());
    }

    private static AmmoItem registerAmmo(String name, AmmoType type) {
        return register(name, properties -> new AmmoItem(type, properties), new Item.Properties());
    }

    /** Creates the item from its properties (1.21.11 needs the registry id in them) and registers it as {@code name}. */
    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties properties) {
        ResourceLocation id = OMT.id(name);
        T item = factory.apply(VersionCompat.itemProperties(properties, id));
        ITEMS.add(item);
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    private static BlockItem registerBlock(Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        BlockItem item = new BlockItem(block, VersionCompat.blockItemProperties(new Item.Properties(), id));
        ITEMS.add(item);
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    /** Loads the class, registering all items. */
    public static void init() {
    }
}
