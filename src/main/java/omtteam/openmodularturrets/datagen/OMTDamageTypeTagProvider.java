package omtteam.openmodularturrets.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import omtteam.openmodularturrets.registry.ModDamageTypes;

import java.util.concurrent.CompletableFuture;
//? if >=1.21.11 {
/*import net.minecraft.data.tags.TagAppender;
*///?}

/**
 * 1.12.2 {@code setProjectile()} → {@code minecraft:is_projectile}; {@code ArmorBypassDamageSource} →
 * {@code minecraft:bypasses_armor}.
 */
public class OMTDamageTypeTagProvider extends FabricTagProvider<DamageType> {
    public OMTDamageTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Registries.DAMAGE_TYPE, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        ModDamageTypes.PROJECTILE.forEach(tagBuilder(DamageTypeTags.IS_PROJECTILE)::add);
        ModDamageTypes.ARMOR_BYPASSING.forEach(tagBuilder(DamageTypeTags.BYPASSES_ARMOR)::add);
    }

    /** The builder that adds damage types by key (renamed in the Fabric API for 1.21.5). */
    //? if >=1.21.11 {
    /*private TagAppender<ResourceKey<DamageType>, DamageType> tagBuilder(TagKey<DamageType> tag) {
        return builder(tag);
    }
    *///?} else {
    private FabricTagBuilder tagBuilder(TagKey<DamageType> tag) {
        return getOrCreateTagBuilder(tag);
    }
    //?}
}
