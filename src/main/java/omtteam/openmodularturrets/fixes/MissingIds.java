package omtteam.openmodularturrets.fixes;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Block and item ids of 3.x that are gone in 4.0.0. Forge would otherwise ask the player before loading a 3.x world
 * that contains them: renamed ids are remapped to their new block and item, removed ids are ignored, which removes
 * the blocks and items silently (tile entities without a block are dropped by Minecraft).
 */
public final class MissingIds {
    /** Removed ids (block and item of the same name). */
    private static final Set<ResourceLocation> REMOVED;
    /** Renamed ids, old to new (block and item of the same name). */
    private static final Map<ResourceLocation, ResourceLocation> RENAMED;
    /** Renamed sound events, old to new (worlds save the sound ids too). */
    private static final Map<ResourceLocation, ResourceLocation> RENAMED_SOUNDS;

    static {
        Set<ResourceLocation> removed = new HashSet<>();
        removed.add(id(OMTNames.Blocks.expander));
        removed.add(id(OMTNames.Blocks.baseAddon));
        REMOVED = Collections.unmodifiableSet(removed);
        Map<ResourceLocation, ResourceLocation> renamed = new HashMap<>();
        renamed.put(id(OMTNames.Blocks.relativisticTurret), id(OMTNames.Blocks.slowingTurret));
        renamed.put(id(OMTNames.Blocks.powerGenerator), id(OMTNames.Blocks.generator));
        RENAMED = Collections.unmodifiableMap(renamed);
        Map<ResourceLocation, ResourceLocation> renamedSounds = new HashMap<>();
        renamedSounds.put(id("relativistic"), id("slowing"));
        RENAMED_SOUNDS = Collections.unmodifiableMap(renamedSounds);
    }

    private MissingIds() {
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(Reference.MOD_ID, path);
    }

    public static void handleBlocks(RegistryEvent.MissingMappings<Block> event) {
        for (RegistryEvent.MissingMappings.Mapping<Block> mapping : event.getAllMappings()) {
            ResourceLocation renamed = RENAMED.get(mapping.key);
            Block block = renamed == null ? null : ForgeRegistries.BLOCKS.getValue(renamed);
            if (block != null && renamed.equals(block.getRegistryName())) {
                OpenModularTurrets.getLogger().info("Renaming {} to {}", mapping.key, renamed);
                mapping.remap(block);
            } else {
                ignoreIfRemoved(mapping);
            }
        }
    }

    public static void handleItems(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getAllMappings()) {
            ResourceLocation renamed = RENAMED.get(mapping.key);
            Item item = renamed == null ? null : ForgeRegistries.ITEMS.getValue(renamed);
            if (item != null && renamed.equals(item.getRegistryName())) {
                OpenModularTurrets.getLogger().info("Renaming {} to {}", mapping.key, renamed);
                mapping.remap(item);
            } else {
                ignoreIfRemoved(mapping);
            }
        }
    }

    public static void handleSounds(RegistryEvent.MissingMappings<SoundEvent> event) {
        for (RegistryEvent.MissingMappings.Mapping<SoundEvent> mapping : event.getAllMappings()) {
            ResourceLocation renamed = RENAMED_SOUNDS.get(mapping.key);
            SoundEvent sound = renamed == null ? null : ForgeRegistries.SOUND_EVENTS.getValue(renamed);
            if (sound != null && renamed.equals(sound.getRegistryName())) {
                OpenModularTurrets.getLogger().info("Renaming sound {} to {}", mapping.key, renamed);
                mapping.remap(sound);
            }
        }
    }

    private static void ignoreIfRemoved(RegistryEvent.MissingMappings.Mapping<?> mapping) {
        if (REMOVED.contains(mapping.key)) {
            OpenModularTurrets.getLogger().info("Removing {} (no longer part of the mod)", mapping.key);
            mapping.ignore();
        }
    }
}
