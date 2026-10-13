package omtteam.openmodularturrets.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import omtteam.openmodularturrets.registry.ModBlocks;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class OMTBlockLootProvider extends FabricBlockLootSubProvider {
    public OMTBlockLootProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generate() {
        // oldmod getDrops: the base drops itself (contents are dropped separately)
        ModBlocks.TURRET_BASES.forEach(this::dropSelf);
        // oldmod: turrets drop themselves when their base is removed or with "Drop turrets"
        ModBlocks.TURRETS.values().forEach(this::dropSelf);
        dropSelf(ModBlocks.LEVER);
        dropSelf(ModBlocks.GENERATOR);
        ModBlocks.HARDENED.forEach(this::dropSelf);
        ModBlocks.WALLS.forEach(this::dropSelf);
        ModBlocks.FENCES.forEach(this::dropSelf);
        dropSelf(ModBlocks.CAMO_TRAP);
    }
}
