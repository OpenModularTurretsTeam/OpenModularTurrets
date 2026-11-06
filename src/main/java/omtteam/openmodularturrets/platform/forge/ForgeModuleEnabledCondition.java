package omtteam.openmodularturrets.platform.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import omtteam.openmodularturrets.compat.ModuleEnabledCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Forge registration of {@link ModuleEnabledCondition} ({@code ICondition}, its {@link #SERIALIZER} registered under
 * {@link ModuleEnabledCondition#ID} with {@code CraftingHelper.register}). Forge 1.20.1 reads it on recipes and
 * advancements.
 * <p>
 * Recipe JSON: {@code "conditions": [{"type": "openmodularturrets:module_enabled", "items": [...]}]}.
 */
public record ForgeModuleEnabledCondition(List<String> items) implements ICondition {
    public static final IConditionSerializer<ForgeModuleEnabledCondition> SERIALIZER = new IConditionSerializer<>() {
        @Override
        public void write(JsonObject json, ForgeModuleEnabledCondition condition) {
            JsonArray array = new JsonArray();
            condition.items().forEach(array::add);
            json.add(ModuleEnabledCondition.ITEMS, array);
        }

        @Override
        public ForgeModuleEnabledCondition read(JsonObject json) {
            List<String> items = new ArrayList<>();
            for (JsonElement element : GsonHelper.getAsJsonArray(json, ModuleEnabledCondition.ITEMS)) {
                items.add(element.getAsString());
            }
            return new ForgeModuleEnabledCondition(items);
        }

        @Override
        public ResourceLocation getID() {
            return ModuleEnabledCondition.ID;
        }
    };

    @Override
    public ResourceLocation getID() {
        return ModuleEnabledCondition.ID;
    }

    @Override
    public boolean test(IContext context) {
        return ModuleEnabledCondition.anyEnabled(items);
    }
}
