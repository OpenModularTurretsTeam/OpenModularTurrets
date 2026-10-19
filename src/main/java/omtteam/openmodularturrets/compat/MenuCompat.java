package omtteam.openmodularturrets.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import omtteam.openmodularturrets.platform.Platform;

/**
 * Menus that receive the block position of their block entity when opened; the loader-specific part is
 * {@link omtteam.openmodularturrets.platform.Menus}.
 */
public final class MenuCompat {
    private MenuCompat() {
    }

    /** Creates the client-side menu from the position sent by the server. */
    @FunctionalInterface
    public interface ClientFactory<T extends AbstractContainerMenu> {
        T create(int containerId, Inventory inventory, BlockPos pos);
    }

    /** Creates the server-side menu. */
    @FunctionalInterface
    public interface ServerFactory {
        AbstractContainerMenu create(int containerId, Inventory inventory, Player player);
    }

    public static <T extends AbstractContainerMenu> MenuType<T> register(ResourceLocation id, ClientFactory<T> factory) {
        return Registry.register(BuiltInRegistries.MENU, id, Platform.menus().createBlockMenuType(factory));
    }

    /** Opens a menu registered with {@link #register} for the block at {@code pos}. */
    public static void open(ServerPlayer player, Component title, BlockPos pos, ServerFactory factory) {
        Platform.menus().openBlockMenu(player, title, pos, factory);
    }
}
