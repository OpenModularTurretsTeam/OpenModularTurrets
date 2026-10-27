package omtteam.openmodularturrets.gametest;

import com.mojang.authlib.GameProfile;
//? if fabric {
import net.fabricmc.fabric.api.entity.FakePlayer;
//?}
//? if neoforge {
/*import net.neoforged.neoforge.common.util.FakePlayer;
*///?}
//? if forge {
/*import net.minecraftforge.common.util.FakePlayer;
*///?}
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;
//? if neoforge {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import java.util.function.Consumer;
*///?}

import java.util.OptionalInt;
import java.util.UUID;

/**
 * A fake player with its own profile; every test uses new profiles, as fake players are cached by profile. The
 * loader's {@link FakePlayer} refuses to open menus; this one opens them like a real player but sends no packet to
 * a client (there is none): the menu factory is called and the menu becomes the player's current menu.
 */
final class GuiTestPlayer extends FakePlayer {
    private int menuCounter;

    private GuiTestPlayer(ServerLevel level, GameProfile profile) {
        super(level, profile);
    }

    /** A new player for every call: no state is shared between tests. */
    static ServerPlayer create(GameTestHelper helper, String name) {
        return new GuiTestPlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), name));
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public OptionalInt openMenu(@Nullable MenuProvider provider) {
        if (provider == null) return OptionalInt.empty();
        if (containerMenu != inventoryMenu) closeContainer();
        AbstractContainerMenu menu = provider.createMenu(++menuCounter, getInventory(), this);
        if (menu == null) return OptionalInt.empty();
        containerMenu = menu;
        return OptionalInt.of(menu.containerId);
    }

    //? if neoforge {
    /*/^* NeoForge opens block menus with extra data for the client (none here), through this variant. ^/
    @Override
    public OptionalInt openMenu(@Nullable MenuProvider provider, @Nullable Consumer<RegistryFriendlyByteBuf> extraDataWriter) {
        return openMenu(provider);
    }
    *///?}
}
