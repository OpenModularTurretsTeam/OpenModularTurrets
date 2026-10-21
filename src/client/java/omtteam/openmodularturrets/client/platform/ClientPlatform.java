package omtteam.openmodularturrets.client.platform;

/**
 * Holds the {@link ClientPlatformServices} of the running loader; the client counterpart of
 * {@link omtteam.openmodularturrets.platform.Platform}. The client entrypoint installs them once, as its first step.
 */
public final class ClientPlatform {
    private static ClientPlatformServices services;

    private ClientPlatform() {
    }

    /**
     * Called exactly once, first thing in the loader's client entrypoint.
     *
     * @throws IllegalStateException if services are already installed (two entrypoints ran)
     */
    public static void install(ClientPlatformServices clientServices) {
        if (services != null) {
            throw new IllegalStateException("Client platform services already installed");
        }
        services = clientServices;
    }

    public static ClientNetwork network() {
        return services().network();
    }

    private static ClientPlatformServices services() {
        if (services == null) {
            throw new IllegalStateException("Client platform services not installed yet");
        }
        return services;
    }
}
