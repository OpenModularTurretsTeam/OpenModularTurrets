package omtteam.openmodularturrets.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import omtteam.openmodularturrets.registry.ModBlocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class OMTBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public OMTBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        // oldmod Material.ROCK: needs a pickaxe (only relevant if bases are breakable)
        ModBlocks.TURRET_BASES.forEach(block -> builder(BlockTags.MINEABLE_WITH_PICKAXE).add(key(block)));
        // OMPD: setHarvestLevel("pickaxe", 2) -> iron pickaxe
        Stream.of(ModBlocks.HARDENED, ModBlocks.WALLS, ModBlocks.FENCES).flatMap(List::stream).forEach(block -> {
            builder(BlockTags.MINEABLE_WITH_PICKAXE).add(key(block));
            builder(BlockTags.NEEDS_IRON_TOOL).add(key(block));
        });
        // generator: like a furnace (pickaxe, any tier)
        builder(BlockTags.MINEABLE_WITH_PICKAXE).add(key(ModBlocks.GENERATOR));
        // vanilla walls and panes connect to blocks in #minecraft:walls
        ModBlocks.WALLS.forEach(block -> builder(BlockTags.WALLS).add(key(block)));
    }

    private static ResourceKey<Block> key(Block block) {
        return block.builtInRegistryHolder().key();
    }
}
