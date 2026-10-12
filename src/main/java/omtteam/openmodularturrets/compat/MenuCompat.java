package omtteam.openmodularturrets.compat;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
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
//? if <1.21 {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}

/**
 * Menus that receive the block position of their block entity when opened (Fabric extended screen handlers, whose
 * API differs between 1.20.1 and 1.21).
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
        //? if >=1.21 {
        MenuType<T> type = new ExtendedScreenHandlerType<>(factory::create, BlockPos.STREAM_CODEC);
        //?} else {
        /*MenuType<T> type = new ExtendedScreenHandlerType<>(
                (containerId, inventory, buf) -> factory.create(containerId, inventory, buf.readBlockPos()));
        *///?}
        return Registry.register(BuiltInRegistries.MENU, id, type);
    }

    /** Opens a menu registered with {@link #register} for the block at {@code pos}. */
    public static void open(ServerPlayer player, Component title, BlockPos pos, ServerFactory factory) {
        //? if >=1.21 {
        player.openMenu(new ExtendedScreenHandlerFactory<BlockPos>() {
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
        //?} else {
        /*player.openMenu(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayer opener, FriendlyByteBuf buf) {
                buf.writeBlockPos(pos);
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
        *///?}
    }
}
