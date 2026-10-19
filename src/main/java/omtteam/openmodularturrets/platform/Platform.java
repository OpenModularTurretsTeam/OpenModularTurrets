package omtteam.openmodularturrets.platform;

/**
 * Holds the {@link PlatformServices} of the running loader. The loader entrypoint installs them once, as its first
 * step; shared code then reaches each service through the static getters.
 */
public final class Platform {
    private static PlatformServices services;

    private Platform() {
    }

    /**
     * Called exactly once, first thing in the loader entrypoint.
     *
     * @throws IllegalStateException if services are already installed (two entrypoints ran)
     */
    public static void install(PlatformServices platformServices) {
        if (services != null) {
            throw new IllegalStateException("Platform services already installed");
        }
        services = platformServices;
    }

    public static LoaderInfo loaderInfo() {
        return services().loaderInfo();
    }

    public static Fuel fuel() {
        return services().fuel();
    }

    public static Network network() {
        return services().network();
    }

    public static Menus menus() {
        return services().menus();
    }

    private static PlatformServices services() {
        if (services == null) {
            throw new IllegalStateException("Platform services not installed yet");
        }
        return services;
    }
}
