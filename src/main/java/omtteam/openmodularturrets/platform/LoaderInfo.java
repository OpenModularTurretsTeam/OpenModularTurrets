package omtteam.openmodularturrets.platform;

import java.nio.file.Path;

/**
 * Facts about the running mod loader that shared code needs.
 */
public interface LoaderInfo {
    /**
     * The folder for config files: {@code config/} in the game directory (client) or server directory.
     * Never null; the folder may not exist yet.
     */
    Path configDir();

    /**
     * Whether a mod with this mod ID is installed, e.g. to enable an optional integration.
     * Safe to call from the entrypoint on.
     */
    @SuppressWarnings("unused")
    boolean isModLoaded(String modId);
}
