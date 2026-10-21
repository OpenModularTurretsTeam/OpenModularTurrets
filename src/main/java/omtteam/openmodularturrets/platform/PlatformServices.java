package omtteam.openmodularturrets.platform;

import java.util.Objects;

/**
 * Every loader-specific service that shared code calls. Each loader entrypoint creates one and passes it to
 * {@link Platform#install}; a new component makes every entrypoint fail to compile until it supplies it.
 */
public record PlatformServices(LoaderInfo loaderInfo, Fuel fuel, Network network, Menus menus,
                               Energy energy) {
    public PlatformServices {
        Objects.requireNonNull(loaderInfo, "loaderInfo");
        Objects.requireNonNull(fuel, "fuel");
        Objects.requireNonNull(network, "network");
        Objects.requireNonNull(menus, "menus");
        Objects.requireNonNull(energy, "energy");
    }
}
