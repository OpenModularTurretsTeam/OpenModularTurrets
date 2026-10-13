package omtteam.openmodularturrets.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import omtteam.openmodularturrets.registry.ModDamageTypes;

/**
 * Datagen entrypoint. Generates the version-specific {@code data/} files (recipes, loot tables, tags, …)
 * into {@code src/main/generated}.
 */
public class OMTDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(OMTBlockLootProvider::new);
        pack.addProvider(OMTBlockTagProvider::new);
        pack.addProvider(OMTRecipeProvider::new);
        pack.addProvider(OMTDamageTypeProvider::new);
        pack.addProvider(OMTDamageTypeTagProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder builder) {
        builder.add(Registries.DAMAGE_TYPE, context -> ModDamageTypes.all()
                .forEach(key -> context.register(key, ModDamageTypes.create(key))));
    }
}
