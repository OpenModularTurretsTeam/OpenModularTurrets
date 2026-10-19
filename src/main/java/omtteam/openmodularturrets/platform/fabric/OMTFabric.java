package omtteam.openmodularturrets.platform.fabric;

import net.fabricmc.api.ModInitializer;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.platform.Platform;
import omtteam.openmodularturrets.platform.PlatformServices;

/**
 * Fabric entrypoint: installs the platform services, starts the mod and registers the Fabric API lookups.
 */
public final class OMTFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Platform.install(new PlatformServices(new FabricLoaderInfo(), new FabricFuel(), new FabricNetwork(),
                new FabricMenus(), new FabricEnergy()));
        OMT.init();
        FabricBlockLookups.register();
    }
}
