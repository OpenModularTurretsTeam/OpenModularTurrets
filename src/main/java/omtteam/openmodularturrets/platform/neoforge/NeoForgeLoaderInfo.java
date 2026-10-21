package omtteam.openmodularturrets.platform.neoforge;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import omtteam.openmodularturrets.platform.LoaderInfo;

import java.nio.file.Path;

public final class NeoForgeLoaderInfo implements LoaderInfo {
    @Override
    public Path configDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
