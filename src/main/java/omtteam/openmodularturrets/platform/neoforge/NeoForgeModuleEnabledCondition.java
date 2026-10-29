package omtteam.openmodularturrets.platform.neoforge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;
import omtteam.openmodularturrets.compat.ModuleEnabledCondition;

import java.util.List;

/**
 * NeoForge registration of {@link ModuleEnabledCondition} ({@code ICondition}, registered under
 * {@link ModuleEnabledCondition#ID} in {@code NeoForgeRegistries.CONDITION_SERIALIZERS}).
 * <p>
 * Recipe JSON: {@code "neoforge:conditions": [{"type": "openmodularturrets:module_enabled", "items": [...]}]}.
 */
public record NeoForgeModuleEnabledCondition(List<String> items) implements ICondition {
    public static final MapCodec<NeoForgeModuleEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.listOf().fieldOf(ModuleEnabledCondition.ITEMS).forGetter(NeoForgeModuleEnabledCondition::items))
            .apply(instance, NeoForgeModuleEnabledCondition::new));

    @Override
    public boolean test(IContext context) {
        return ModuleEnabledCondition.anyEnabled(items);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
