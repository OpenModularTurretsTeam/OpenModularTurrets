package omtteam.openmodularturrets.platform.fabric;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.Identifier;
import omtteam.openmodularturrets.compat.ModuleEnabledCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import org.jetbrains.annotations.Nullable;
import net.minecraft.resources.RegistryOps.RegistryInfoLookup;

import java.util.List;

/**
 * Fabric registration of {@link ModuleEnabledCondition} as a resource condition ({@code ResourceCondition}).
 * <p>
 * Recipe JSON: {@code "fabric:load_conditions": [{"condition": "openmodularturrets:module_enabled", "items": [...]}]}.
 */
public final class FabricModuleEnabledCondition {
    private static final Identifier ID = ModuleEnabledCondition.ID;
    private static final String ITEMS = ModuleEnabledCondition.ITEMS;

    private FabricModuleEnabledCondition() {
    }

    private record Condition(List<String> items) implements ResourceCondition {
        static final MapCodec<Condition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.listOf().fieldOf(ITEMS).forGetter(Condition::items)).apply(instance, Condition::new));
        static final ResourceConditionType<Condition> TYPE = ResourceConditionType.create(ID, CODEC);

        @Override
        public ResourceConditionType<?> getType() {
            return TYPE;
        }

        @Override
        public boolean test(@Nullable RegistryInfoLookup registries) {
            return ModuleEnabledCondition.anyEnabled(items);
        }
    }

    /** Registers the condition type; called by the Fabric entrypoint. */
    public static void register() {
        ResourceConditions.register(Condition.TYPE);
    }

    /** The condition for datagen ({@code FabricRecipeProvider#withConditions}). */
    public static ResourceCondition of(String... items) {
        return new Condition(List.of(items));
    }
}
