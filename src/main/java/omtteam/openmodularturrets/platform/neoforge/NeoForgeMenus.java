package omtteam.openmodularturrets.platform.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import omtteam.openmodularturrets.compat.MenuCompat;
import omtteam.openmodularturrets.platform.Menus;

public final class NeoForgeMenus implements Menus {
    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createBlockMenuType(MenuCompat.ClientFactory<T> factory) {
        return IMenuTypeExtension.create((containerId, inventory, buf) -> factory.create(containerId, inventory, buf.readBlockPos()));
    }

    @Override
    public void openBlockMenu(ServerPlayer player, Component title, BlockPos pos, MenuCompat.ServerFactory factory) {
        player.openMenu(new SimpleMenuProvider(factory::create, title), pos);
    }
}
