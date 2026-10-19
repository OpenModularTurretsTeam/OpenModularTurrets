package omtteam.openmodularturrets.client.platform;

import java.util.Objects;

/**
 * Every loader-specific service that shared client code calls; the client counterpart of
 * {@link omtteam.openmodularturrets.platform.PlatformServices}.
 */
public record ClientPlatformServices(ClientNetwork network) {
    public ClientPlatformServices {
        Objects.requireNonNull(network, "network");
    }
}
