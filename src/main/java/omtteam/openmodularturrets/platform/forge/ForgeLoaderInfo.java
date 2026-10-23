package omtteam.openmodularturrets.platform.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import omtteam.openmodularturrets.platform.LoaderInfo;

import java.nio.file.Path;

public final class ForgeLoaderInfo implements LoaderInfo {
    @Override
    public Path configDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
