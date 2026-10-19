package omtteam.openmodularturrets.platform.fabric;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import omtteam.openmodularturrets.compat.MenuCompat;
import omtteam.openmodularturrets.platform.Menus;
import org.jetbrains.annotations.NotNull;
//? if <1.21 {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}

/**
 * Fabric extended screen handlers, whose API differs between 1.20.1 and 1.21.
 */
public final class FabricMenus implements Menus {
    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createBlockMenuType(MenuCompat.ClientFactory<T> factory) {
        //? if >=1.21 {
        return new ExtendedScreenHandlerType<>(factory::create, BlockPos.STREAM_CODEC);
        //?} else {
        /*return new ExtendedScreenHandlerType<>(
                (containerId, inventory, buf) -> factory.create(containerId, inventory, buf.readBlockPos()));
        *///?}
    }

    @Override
    public void openBlockMenu(ServerPlayer player, Component title, BlockPos pos, MenuCompat.ServerFactory factory) {
        //? if >=1.21 {
        player.openMenu(new ExtendedScreenHandlerFactory<BlockPos>() {
            @Override
            public BlockPos getScreenOpeningData(ServerPlayer opener) {
                return pos;
            }

            @Override
            public @NotNull Component getDisplayName() {
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
