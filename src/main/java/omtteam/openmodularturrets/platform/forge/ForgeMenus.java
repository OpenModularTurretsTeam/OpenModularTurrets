package omtteam.openmodularturrets.platform.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.NetworkHooks;
import omtteam.openmodularturrets.compat.MenuCompat;
import omtteam.openmodularturrets.platform.Menus;

public final class ForgeMenus implements Menus {
    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createBlockMenuType(MenuCompat.ClientFactory<T> factory) {
        return IForgeMenuType.create((containerId, inventory, buf) -> factory.create(containerId, inventory, buf.readBlockPos()));
    }

    @Override
    public void openBlockMenu(ServerPlayer player, Component title, BlockPos pos, MenuCompat.ServerFactory factory) {
        NetworkHooks.openScreen(player, new SimpleMenuProvider(factory::create, title), pos);
    }
}
