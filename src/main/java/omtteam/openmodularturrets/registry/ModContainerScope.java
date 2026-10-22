package omtteam.openmodularturrets.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

/**
 * Registers objects of other mods' turrets as their mod: Forge expects registry names in the namespace of the mod
 * that registers them and warns about "dangerous alternative prefixes" otherwise.
 */
final class ModContainerScope {
    private ModContainerScope() {
    }

    /** Runs the registration with the mod of the id's namespace as the active mod (if it is a loaded mod). */
    static void registerAs(ResourceLocation id, Runnable registration) {
        ModContainer owner = Loader.instance().getIndexedModList().get(id.getResourceDomain());
        ModContainer previous = Loader.instance().activeModContainer();
        if (owner == null || owner == previous) {
            registration.run();
            return;
        }
        Loader.instance().setActiveModContainer(owner);
        try {
            registration.run();
        } finally {
            Loader.instance().setActiveModContainer(previous);
        }
    }
}
