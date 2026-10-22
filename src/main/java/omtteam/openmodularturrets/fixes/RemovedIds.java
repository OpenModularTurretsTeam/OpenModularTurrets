package omtteam.openmodularturrets.fixes;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Block and item ids removed in 4.0.0. Forge would otherwise ask the player before loading a 3.x world that contains
 * them; ignoring the mapping removes the blocks and items silently (tile entities without a block are dropped by
 * Minecraft).
 */
public final class RemovedIds {
    /** Removed ids (block and item of the same name). */
    private static final Set<ResourceLocation> REMOVED;

    static {
        Set<ResourceLocation> removed = new HashSet<>();
        removed.add(new ResourceLocation(Reference.MOD_ID, OMTNames.Blocks.expander));
        REMOVED = Collections.unmodifiableSet(removed);
    }

    private RemovedIds() {
    }

    /** Ignores the missing mappings of removed ids. */
    public static <T extends IForgeRegistryEntry<T>> void ignoreRemoved(RegistryEvent.MissingMappings<T> event) {
        for (RegistryEvent.MissingMappings.Mapping<T> mapping : event.getAllMappings()) {
            if (REMOVED.contains(mapping.key)) {
                OpenModularTurrets.getLogger().info("Removing {} (no longer part of the mod)", mapping.key);
                mapping.ignore();
            }
        }
    }
}
