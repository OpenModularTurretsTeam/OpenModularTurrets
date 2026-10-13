package omtteam.openmodularturrets.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
import omtteam.openmodularturrets.block.misc.LeverBlock;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.core.turret.TurretType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
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
     * One block per turret type, registry id = {@link TurretType#getId()}. Disabled turrets are registered too (the
     * registry must match between client and server); they just cannot be placed.
     */
    public static final Map<TurretType, TurretBlock> TURRETS = registerTurrets();

    // Misc (oldmod)
    public static final LeverBlock LEVER = register("lever_block", LeverBlock::new, LeverBlock.createProperties());
    /** New block (not in oldmod). */
    public static final GeneratorBlock GENERATOR = register("generator", GeneratorBlock::new, GeneratorBlock.createProperties());

    // Passive defense (OMPD), one block per tier
    public static final List<HardenedBlock> HARDENED = registerTiers("hardened", PassiveDefense::properties, HardenedBlock::new);
    public static final List<DefenseWallBlock> WALLS = registerTiers("wall",
            tier -> PassiveDefense.properties(tier).forceSolidOn(), DefenseWallBlock::new);
    public static final List<DefenseFenceBlock> FENCES = registerTiers("fence",
            tier -> PassiveDefense.properties(tier).noOcclusion(), DefenseFenceBlock::new);
    public static final CamoTrapBlock CAMO_TRAP = register("camo_trap", CamoTrapBlock::new, CamoTrapBlock.createProperties());

    private ModBlocks() {
    }

    private static TurretBaseBlock registerBase(String name, int tier) {
        return register(name, properties -> new TurretBaseBlock(tier, properties), TurretBaseBlock.createProperties(tier));
    }

    private static Map<TurretType, TurretBlock> registerTurrets() {
        Map<TurretType, TurretBlock> turrets = new EnumMap<>(TurretType.class);
        for (TurretType type : TurretType.values()) {
            turrets.put(type, register(type.getId(), properties -> new TurretBlock(type, properties),
                    TurretBlock.createProperties()));
        }
        return Collections.unmodifiableMap(turrets);
    }

    /** {@code <name>_tier_one} … {@code <name>_tier_five}, ordered by tier. */
    private static <T extends Block> List<T> registerTiers(String name,
                                                           Function<DefenseTier, BlockBehaviour.Properties> properties,
                                                           BiFunction<DefenseTier, BlockBehaviour.Properties, T> factory) {
        List<T> blocks = new ArrayList<>();
        for (DefenseTier tier : DefenseTier.values()) {
            blocks.add(register(name + "_tier_" + tier.getName(), props -> factory.apply(tier, props),
                    properties.apply(tier)));
        }
        return List.copyOf(blocks);
    }

    private static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> factory,
                                                BlockBehaviour.Properties properties) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, OMT.id(name));
        return Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
    }

    /** Loads the class, registering all blocks. */
    public static void init() {
    }
}
