package omtteam.openmodularturrets.compat;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Menus that receive the block position of their block entity when opened (Fabric extended menus).
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

    public static <T extends AbstractContainerMenu> MenuType<T> register(Identifier id, ClientFactory<T> factory) {
        MenuType<T> type = new ExtendedMenuType<>(factory::create, BlockPos.STREAM_CODEC);
        return Registry.register(BuiltInRegistries.MENU, id, type);
    }

    /** Opens a menu registered with {@link #register} for the block at {@code pos}. */
    public static void open(ServerPlayer player, Component title, BlockPos pos, ServerFactory factory) {
        player.openMenu(new ExtendedMenuProvider<BlockPos>() {
            @Override
            public BlockPos getScreenOpeningData(ServerPlayer opener) {
                return pos;
            }

            @Override
            public Component getDisplayName() {
                return title;
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player menuPlayer) {
                return factory.create(containerId, inventory, menuPlayer);
            }
        });
    }
}
