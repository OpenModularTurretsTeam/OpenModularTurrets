package omtteam.openmodularturrets.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import omtteam.openmodularturrets.registry.ModBlocks;
//? if >=1.21 {
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;
//?}

public class OMTBlockLootProvider extends FabricBlockLootTableProvider {
    //? if >=1.21 {
    public OMTBlockLootProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
    //?} else {
    /*public OMTBlockLootProvider(FabricDataOutput output) {
        super(output);
    }
    *///?}

    @Override
    public void generate() {
        // 1.12.2 getDrops: the base drops itself (contents are dropped separately)
        ModBlocks.TURRET_BASES.forEach(this::dropSelf);
        // turrets drop themselves when their base is removed or with "Drop turrets"
        ModBlocks.TURRETS.forEach((type, turret) -> {
            // turrets of other mods come with their own data
            if (type.isBuiltIn()) dropSelf(turret);
        });
        dropSelf(ModBlocks.LEVER);
        dropSelf(ModBlocks.GENERATOR);
        ModBlocks.HARDENED.forEach(this::dropSelf);
        ModBlocks.WALLS.forEach(this::dropSelf);
        ModBlocks.FENCES.forEach(this::dropSelf);
        dropSelf(ModBlocks.CAMO_TRAP);
    }
}
