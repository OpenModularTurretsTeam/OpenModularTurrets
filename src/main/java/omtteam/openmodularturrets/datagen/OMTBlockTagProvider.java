package omtteam.openmodularturrets.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import omtteam.openmodularturrets.registry.ModBlocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class OMTBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public OMTBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        // 1.12.2 Material.ROCK: needs a pickaxe (only relevant if bases are breakable)
        ModBlocks.TURRET_BASES.forEach(getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)::add);
        // turrets too (only relevant if turrets are breakable)
        ModBlocks.TURRETS.values().forEach(getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)::add);
        // OMPD: setHarvestLevel("pickaxe", 2) -> iron pickaxe
        Stream.of(ModBlocks.HARDENED, ModBlocks.WALLS, ModBlocks.FENCES).flatMap(List::stream).forEach(block -> {
            getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
            getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL).add(block);
        });
        // generator: like a furnace (pickaxe, any tier)
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.GENERATOR);
        // vanilla walls and panes connect to blocks in #minecraft:walls
        ModBlocks.WALLS.forEach(getOrCreateTagBuilder(BlockTags.WALLS)::add);
    }
}
