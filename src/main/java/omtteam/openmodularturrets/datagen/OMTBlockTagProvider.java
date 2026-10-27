package omtteam.openmodularturrets.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import omtteam.openmodularturrets.registry.ModBlocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
//? if >=1.21.11 {
/*import net.minecraft.data.tags.TagAppender;
*///?}

public class OMTBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public OMTBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        // 1.12.2 Material.ROCK: needs a pickaxe (only relevant if bases are breakable)
        ModBlocks.TURRET_BASES.forEach(tagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)::add);
        // turrets too (only relevant if turrets are breakable)
        ModBlocks.TURRETS.forEach((type, turret) -> {
            // turrets of other mods come with their own data
            if (type.isBuiltIn()) tagBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(turret);
        });
        // OMPD: setHarvestLevel("pickaxe", 2) -> iron pickaxe
        Stream.of(ModBlocks.HARDENED, ModBlocks.WALLS, ModBlocks.FENCES).flatMap(List::stream).forEach(block -> {
            tagBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
            tagBuilder(BlockTags.NEEDS_IRON_TOOL).add(block);
        });
        // generator: like a furnace (pickaxe, any tier)
        tagBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.GENERATOR);
        // inventory expanders: stone like the bases (pickaxe, any tier)
        ModBlocks.INVENTORY_EXPANDERS.forEach(tagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)::add);
        // vanilla walls and panes connect to blocks in #minecraft:walls
        ModBlocks.WALLS.forEach(tagBuilder(BlockTags.WALLS)::add);
    }

    /** The builder that adds blocks by value (renamed in the Fabric API for 1.21.5). */
    //? if >=1.21.11 {
    /*private TagAppender<Block, Block> tagBuilder(TagKey<Block> tag) {
        return valueLookupBuilder(tag);
    }
    *///?} else {
    private FabricTagBuilder tagBuilder(TagKey<Block> tag) {
        return getOrCreateTagBuilder(tag);
    }
    //?}
}
