package omtteam.openmodularturrets.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import omtteam.openmodularturrets.registry.ModDamageTypes;

import java.util.concurrent.CompletableFuture;

/**
 * oldmod {@code setProjectile()} → {@code minecraft:is_projectile}; {@code ArmorBypassDamageSource} →
 * {@code minecraft:bypasses_armor}.
 */
public class OMTDamageTypeTagProvider extends FabricTagsProvider<DamageType> {
    public OMTDamageTypeTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Registries.DAMAGE_TYPE, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        ModDamageTypes.PROJECTILE.forEach(builder(DamageTypeTags.IS_PROJECTILE)::add);
        ModDamageTypes.ARMOR_BYPASSING.forEach(builder(DamageTypeTags.BYPASSES_ARMOR)::add);
    }
}
