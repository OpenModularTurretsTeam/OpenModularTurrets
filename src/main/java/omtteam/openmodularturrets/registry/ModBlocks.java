package omtteam.openmodularturrets.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.block.base.TurretBaseBlock;
import omtteam.openmodularturrets.block.defense.CamoTrapBlock;
import omtteam.openmodularturrets.block.defense.DefenseFenceBlock;
import omtteam.openmodularturrets.block.defense.DefenseTier;
import omtteam.openmodularturrets.block.defense.DefenseWallBlock;
import omtteam.openmodularturrets.block.defense.HardenedBlock;
import omtteam.openmodularturrets.block.defense.PassiveDefense;
import omtteam.openmodularturrets.block.misc.GeneratorBlock;
import omtteam.openmodularturrets.block.misc.InventoryExpanderBlock;
import omtteam.openmodularturrets.block.misc.LeverBlock;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.omlib.compat.VersionCompat;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.turret.TurretTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * All blocks. Must be initialized after the config is loaded (block properties come from it).
 */
public final class ModBlocks {
    public static final TurretBaseBlock TURRET_BASE_TIER_ONE = registerBase("turret_base_tier_one", 1);
    public static final TurretBaseBlock TURRET_BASE_TIER_TWO = registerBase("turret_base_tier_two", 2);
    public static final TurretBaseBlock TURRET_BASE_TIER_THREE = registerBase("turret_base_tier_three", 3);
    public static final TurretBaseBlock TURRET_BASE_TIER_FOUR = registerBase("turret_base_tier_four", 4);
    public static final TurretBaseBlock TURRET_BASE_TIER_FIVE = registerBase("turret_base_tier_five", 5);

    /** Turret bases ordered by tier. */
    public static final List<TurretBaseBlock> TURRET_BASES = List.of(TURRET_BASE_TIER_ONE, TURRET_BASE_TIER_TWO,
            TURRET_BASE_TIER_THREE, TURRET_BASE_TIER_FOUR, TURRET_BASE_TIER_FIVE);

    /**
     * One block per turret type (built-in and added by other mods), registry id = {@link TurretType#getId()}.
     * Disabled turrets are registered too (the registry must match between client and server); they just cannot be
     * placed.
     */
    public static final Map<TurretType, TurretBlock> TURRETS = registerTurrets();

    // Misc
    public static final LeverBlock LEVER = register("lever_block", LeverBlock::new, LeverBlock.createProperties());
    /** New block (not in the 1.12.2 version). */
    public static final GeneratorBlock GENERATOR = register("generator", GeneratorBlock::new, GeneratorBlock.createProperties());
    /** Inventory expanders ordered by tier ({@code inventory_expander_tier_one} … {@code _tier_three}). */
    public static final List<InventoryExpanderBlock> INVENTORY_EXPANDERS = List.of(registerExpander("one", 1),
            registerExpander("two", 2), registerExpander("three", 3));

    // Passive defense (OMPD), one block per tier
    public static final List<HardenedBlock> HARDENED = registerTiers("hardened", HardenedBlock::new,
            PassiveDefense::properties);
    public static final List<DefenseWallBlock> WALLS = registerTiers("wall", DefenseWallBlock::new,
            tier -> PassiveDefense.properties(tier).forceSolidOn());
    public static final List<DefenseFenceBlock> FENCES = registerTiers("fence", DefenseFenceBlock::new,
            tier -> PassiveDefense.properties(tier).noOcclusion());
    public static final CamoTrapBlock CAMO_TRAP = register("camo_trap", CamoTrapBlock::new, CamoTrapBlock.createProperties());

    private ModBlocks() {
    }

    private static TurretBaseBlock registerBase(String name, int tier) {
        return register(name, properties -> new TurretBaseBlock(tier, properties), TurretBaseBlock.createProperties(tier));
    }

    private static InventoryExpanderBlock registerExpander(String tierName, int tier) {
        return register("inventory_expander_tier_" + tierName, properties -> new InventoryExpanderBlock(tier, properties),
                InventoryExpanderBlock.createProperties(tier));
    }

    private static Map<TurretType, TurretBlock> registerTurrets() {
        if (!TurretTypes.isFrozen()) throw new IllegalStateException("Turret blocks registered before the turret types");
        Map<TurretType, TurretBlock> turrets = new LinkedHashMap<>();
        for (TurretType type : TurretTypes.all()) {
            ResourceLocation id = VersionCompat.id(type.getNamespace(), type.getPath());
            turrets.put(type, Registry.register(BuiltInRegistries.BLOCK, id,
                    new TurretBlock(type, VersionCompat.blockProperties(TurretBlock.createProperties(), id))));
        }
        return Collections.unmodifiableMap(turrets);
    }

    /** {@code <name>_tier_one} … {@code <name>_tier_five}, ordered by tier. */
    private static <T extends Block> List<T> registerTiers(String name, BiFunction<DefenseTier, BlockBehaviour.Properties, T> factory,
                                                           Function<DefenseTier, BlockBehaviour.Properties> properties) {
        List<T> blocks = new ArrayList<>();
        for (DefenseTier tier : DefenseTier.values()) {
            blocks.add(register(name + "_tier_" + tier.getName(), props -> factory.apply(tier, props), properties.apply(tier)));
        }
        return List.copyOf(blocks);
    }

    /** Creates the block from its properties (1.21.11 needs the registry id in them) and registers it as {@code name}. */
    private static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> factory,
                                                BlockBehaviour.Properties properties) {
        ResourceLocation id = OMT.id(name);
        return Registry.register(BuiltInRegistries.BLOCK, id, factory.apply(VersionCompat.blockProperties(properties, id)));
    }

    /** Loads the class, registering all blocks. */
    public static void init() {
    }
}
