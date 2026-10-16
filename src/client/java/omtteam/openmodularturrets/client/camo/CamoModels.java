package omtteam.openmodularturrets.client.camo;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.camo.CamoBlock;
import omtteam.openmodularturrets.client.compat.ClientCompat;

import java.util.HashSet;
import java.util.Set;

/**
 * Wraps the models of all {@link CamoBlock}s in a {@link CamoBakedModel}.
 */
public final class CamoModels {
    private CamoModels() {
    }

    public static void register() {
        ModelLoadingPlugin.register(plugin -> {
            Set<String> paths = camoBlockPaths();
            ClientCompat.modifyModelsAfterBake(plugin, (model, id) ->
                    id.getNamespace().equals(OMT.MOD_ID) && paths.contains(id.getPath()) ? new CamoBakedModel(model) : model);
        });
    }

    /** Registry paths of the camouflage blocks; their block state and item models share these paths. */
    private static Set<String> camoBlockPaths() {
        Set<String> paths = new HashSet<>();
        for (ResourceLocation key : BuiltInRegistries.BLOCK.keySet()) {
            if (key.getNamespace().equals(OMT.MOD_ID) && BuiltInRegistries.BLOCK.get(key) instanceof CamoBlock) {
                paths.add(key.getPath());
            }
        }
        return paths;
    }
}
