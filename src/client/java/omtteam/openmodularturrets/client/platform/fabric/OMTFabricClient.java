package omtteam.openmodularturrets.client.platform.fabric;

import net.fabricmc.api.ClientModInitializer;
import omtteam.openmodularturrets.client.OMTClient;
import omtteam.openmodularturrets.client.platform.ClientPlatform;
import omtteam.openmodularturrets.client.platform.ClientPlatformServices;

/**
 * Fabric client entrypoint: installs the client platform services and starts the client.
 */
public final class OMTFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlatform.install(new ClientPlatformServices(new FabricClientNetwork()));
        OMTClient.init();
    }
}
