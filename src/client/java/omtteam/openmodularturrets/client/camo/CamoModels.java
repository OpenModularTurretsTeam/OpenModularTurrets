package omtteam.openmodularturrets.client.camo;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.camo.CamoBlock;
import omtteam.openmodularturrets.compat.VersionCompat;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Which models show a disguise: those of the {@link CamoBlock}s. The loader client entrypoints wrap them in their
 * camouflage model ({@code FabricCamoModel}, {@code NeoForgeCamoModel}); the block entity hands the disguise to the
 * model through {@code CompatBlockEntity#renderData}. A block with a {@link CamoBlock#overlayTintIndex} also keeps
 * those quads of its own model over the disguise.
 */
public final class CamoModels {
    private CamoModels() {
    }

    /**
     * The tint index of the quads of this block's own model that stay visible over a disguise ({@link CamoBlock#overlayTintIndex}),
     * -1 if none (also for blocks that cannot be disguised, and for the item model, which has no state).
     */
    public static int overlayTintIndex(@Nullable BlockState state) {
        return state != null && state.getBlock() instanceof CamoBlock camo ? camo.overlayTintIndex() : -1;
    }

    /** All camouflage host blocks (after registration). */
    public static List<Block> camoBlocks() {
        return BuiltInRegistries.BLOCK.stream().filter(block -> block instanceof CamoBlock).toList();
    }

    /**
     * Whether the model with this id belongs to a camouflage host: block state and item models share the block's
     * registry path (only namespace and path of the id are compared).
     */
    public static boolean isCamoModel(ResourceLocation id) {
        if (!id.getNamespace().equals(OMT.MOD_ID)) return false;
        // a plain id: on 1.20.1 model ids can be ModelResourceLocations, which never equal a registry key
        return BuiltInRegistries.BLOCK.getOptional(VersionCompat.id(id.getNamespace(), id.getPath()))
                .map(block -> block instanceof CamoBlock).orElse(false);
    }
}
