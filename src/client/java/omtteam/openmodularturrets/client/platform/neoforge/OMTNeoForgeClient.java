package omtteam.openmodularturrets.client.platform.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import omtteam.openmodularturrets.client.platform.ClientPlatform;
import omtteam.openmodularturrets.client.platform.ClientPlatformServices;
import omtteam.openmodularturrets.platform.Platform;
import omtteam.openmodularturrets.platform.neoforge.NeoForgeNetwork;

/**
 * NeoForge client entrypoint. FML constructs it after the common {@code OMTNeoForge} (entrypoints for more sides
 * first), so {@link Platform} is already installed. Porting in progress: nothing client-side is wired yet.
 */
@Mod(value = "openmodularturrets", dist = Dist.CLIENT)
public final class OMTNeoForgeClient {
    public OMTNeoForgeClient() {
        ClientPlatform.install(new ClientPlatformServices(new NeoForgeClientNetwork((NeoForgeNetwork) Platform.network())));
    }
}
