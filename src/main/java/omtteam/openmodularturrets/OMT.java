package omtteam.openmodularturrets;

import net.minecraft.resources.ResourceLocation;
import omtteam.omlib.compat.VersionCompat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared constants of Open Modular Turrets. The start-up is {@code startup.OMTStartup}: this class depends on nothing
 * of the mod but {@link VersionCompat}, so every package may use it without joining a package cycle.
 */
public final class OMT {
    public static final String MOD_ID = "openmodularturrets";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private OMT() {
    }

    /**
     * Creates a {@link ResourceLocation} in the mod namespace.
     */
    public static ResourceLocation id(String path) {
        return VersionCompat.id(MOD_ID, path);
    }
}
