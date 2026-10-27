package omtteam.openmodularturrets.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
//? if >=1.21.11 {
/*import net.minecraft.advancements.criterion.StatePropertiesPredicate;
*///?} else {
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
//?}
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import omtteam.openmodularturrets.block.misc.InventoryExpanderBlock;
import omtteam.openmodularturrets.registry.ModBlocks;
//? if >=1.21 {
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;
//?}

public class OMTBlockLootProvider extends FabricBlockLootTableProvider {
    //? if >=1.21 {
    public OMTBlockLootProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
    //?} else {
    /*public OMTBlockLootProvider(FabricDataOutput output) {
        super(output);
    }
    *///?}

    /** Like a slab: the expander drops itself, twice for a double block. */
    private void dropExpander(InventoryExpanderBlock block) {
        add(block, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .add(applyExplosionDecay(block, LootItem.lootTableItem(block)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(InventoryExpanderBlock.DOUBLE, true))))))));
    }

    @Override
    public void generate() {
        // 1.12.2 getDrops: the base drops itself (contents are dropped separately)
        ModBlocks.TURRET_BASES.forEach(this::dropSelf);
        // turrets drop themselves when their base is removed or with "Drop turrets"
        ModBlocks.TURRETS.forEach((type, turret) -> {
            // turrets of other mods come with their own data
            if (type.isBuiltIn()) dropSelf(turret);
        });
        dropSelf(ModBlocks.LEVER);
        dropSelf(ModBlocks.GENERATOR);
        ModBlocks.INVENTORY_EXPANDERS.forEach(this::dropExpander);
        ModBlocks.HARDENED.forEach(this::dropSelf);
        ModBlocks.WALLS.forEach(this::dropSelf);
        ModBlocks.FENCES.forEach(this::dropSelf);
        dropSelf(ModBlocks.CAMO_TRAP);
    }
}
