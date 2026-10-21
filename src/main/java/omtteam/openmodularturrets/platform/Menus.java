package omtteam.openmodularturrets.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import omtteam.openmodularturrets.compat.MenuCompat;

/**
 * Menus whose client side receives the block position of their block entity. Gameplay code uses
 * {@link MenuCompat}, which forwards here.
 */
public interface Menus {
    /**
     * Creates (does not register) a menu type; the client builds its menu from the position sent on opening.
     */
    <T extends AbstractContainerMenu> MenuType<T> createBlockMenuType(MenuCompat.ClientFactory<T> factory);

    /**
     * Opens a menu of such a type for the block at {@code pos} (server side).
     */
    void openBlockMenu(ServerPlayer player, Component title, BlockPos pos, MenuCompat.ServerFactory factory);
}
