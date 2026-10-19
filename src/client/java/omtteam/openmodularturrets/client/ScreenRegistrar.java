package omtteam.openmodularturrets.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Registers a menu screen through the loader: Fabric passes {@code MenuScreens::register}, NeoForge
 * {@code RegisterMenuScreensEvent::register}. A generic method, so it takes method references, not lambdas.
 */
public interface ScreenRegistrar {
    <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void register(
            MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, S> factory);
}
