package omtteam.openmodularturrets.platform.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import omtteam.openmodularturrets.platform.Platform;
import omtteam.openmodularturrets.platform.PlatformServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NeoForge entrypoint (the Fabric one is {@code OMTFabric}).
 * Porting in progress: installs the platform services; nothing is registered yet (see OpenModularLighting's
 * {@code OMLNeoForge} for the target: OML.init, registration steps per RegisterEvent, capabilities, events).
 */
@Mod("openmodularturrets")
public final class OMTNeoForge {
    private static final Logger LOGGER = LoggerFactory.getLogger("openmodularturrets");

    public OMTNeoForge(IEventBus modBus) {
        Platform.install(new PlatformServices(new NeoForgeLoaderInfo(), new NeoForgeFuel(), new NeoForgeNetwork(modBus),
                new NeoForgeMenus()));
        LOGGER.info("Open Modular Turrets on NeoForge: port in progress, no content registered yet");
    }
}
