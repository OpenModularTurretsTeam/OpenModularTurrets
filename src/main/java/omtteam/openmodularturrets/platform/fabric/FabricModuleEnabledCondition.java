package omtteam.openmodularturrets.platform.fabric;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.compat.ModuleEnabledCondition;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

import java.util.List;

/**
 * Fabric registration of {@link ModuleEnabledCondition} as a resource condition ({@code ConditionJsonProvider}).
 * <p>
 * Recipe JSON: {@code "fabric:load_conditions": [{"condition": "openmodularturrets:module_enabled", "items": [...]}]}.
 */
public final class FabricModuleEnabledCondition {
    private static final ResourceLocation ID = ModuleEnabledCondition.ID;
    private static final String ITEMS = ModuleEnabledCondition.ITEMS;

    private FabricModuleEnabledCondition() {
    }

    /* Registers the condition type; called by the Fabric entrypoint. */
    public static void register() {
        ResourceConditions.register(ID, json -> {
            JsonArray array = GsonHelper.getAsJsonArray(json, ITEMS);
            List<String> items = new ArrayList<>();
            for (JsonElement element : array) {
                items.add(element.getAsString());
            }
            return ModuleEnabledCondition.anyEnabled(items);
        });
    }

    /* The condition for datagen ({@code FabricRecipeProvider#withConditions}). */
    public static ConditionJsonProvider of(String... items) {
        return new ConditionJsonProvider() {
            @Override
            public ResourceLocation getConditionId() {
                return ID;
            }

            @Override
            public void writeParameters(JsonObject object) {
                JsonArray array = new JsonArray();
                for (String item : items) {
                    array.add(item);
                }
                object.add(ITEMS, array);
            }
        };
    }
}
