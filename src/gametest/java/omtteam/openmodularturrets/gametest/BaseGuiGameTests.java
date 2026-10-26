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
//? if neoforge && <1.21.11 {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if forge {
/*import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
*///?}
//? if (neoforge && <1.21.11) || forge {
/*import omtteam.openmodularturrets.OMT;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if <1.21.11 {
import net.minecraft.gametest.framework.GameTest;
//?}
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.menu.TurretBaseMenu;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.omlib.security.PlayerAdapter;
import org.jetbrains.annotations.Nullable;
//? if neoforge {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import java.util.function.Consumer;
*///?}

import java.util.OptionalInt;
import java.util.UUID;

/**
 * Right-clicking a turret base opens its GUI for players with access, and only for them; only its admins attach
 * turrets to it.
 * <p>
 * The click goes through {@code ServerPlayerGameMode#useItemOn}, the vanilla entry point that the loader's block-use
 * event hooks into (Fabric {@code UseBlockCallback}, NeoForge {@code RightClickBlock}), so the whole path is covered:
 * the event, the base's {@code InteractiveBlock#onUse}, the permission check and the menu. Players are the loader's
 * fake players (the same class name and constructor on all loaders), one per test, so no state is shared between
 * tests. Forge opens block menus through {@code NetworkHooks.openScreen}, which builds the menu itself (no
 * {@code openMenu}).
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class BaseGuiGameTests implements OMTGameTests {
    private static final BlockPos BASE = TurretScene.BASE;

    /** The owner gets the base menu on a click with an empty hand and with a block in hand. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "base_gui_owner")
    public void ownerOpensTheBaseGui(GameTestHelper helper) {
        ServerPlayer owner = player(helper, "GuiTestOwner");
        TurretBaseBlockEntity base = placeBase(helper, owner);
        try {
            InteractionResult result = rightClick(helper, owner, InteractionHand.MAIN_HAND);
            helper.assertTrue(result.consumesAction(), "The click was not used: " + result);
            assertMenuOpen(helper, owner, base);

            owner.closeContainer();
            owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE, 8));
            rightClick(helper, owner, InteractionHand.MAIN_HAND);
            assertMenuOpen(helper, owner, base);
            helper.assertTrue(owner.getMainHandItem().getCount() == 8, "A block was placed instead of opening the GUI");
        } finally {
            TurretScene.clear(helper);
        }
        helper.succeed();
    }

    /** Strangers, a sneaking owner and the off hand do not open the menu, and the click is not used. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "base_gui_denied")
    public void othersDoNotOpenTheBaseGui(GameTestHelper helper) {
        ServerPlayer owner = player(helper, "GuiTestDeniedOwner");
        ServerPlayer stranger = player(helper, "GuiTestStranger");
        placeBase(helper, owner);
        try {
            rightClick(helper, stranger, InteractionHand.MAIN_HAND);
            helper.assertTrue(stranger.containerMenu == stranger.inventoryMenu, "A stranger opened the base GUI");

            owner.setShiftKeyDown(true);
            InteractionResult sneaking = rightClick(helper, owner, InteractionHand.MAIN_HAND);
            helper.assertTrue(owner.containerMenu == owner.inventoryMenu && !sneaking.consumesAction(),
                    "Sneaking opened the GUI or used the click (blocks could not be placed against the base)");
            owner.setShiftKeyDown(false);

            InteractionResult offHand = rightClick(helper, owner, InteractionHand.OFF_HAND);
            helper.assertTrue(owner.containerMenu == owner.inventoryMenu && !offHand.consumesAction(),
                    "The off hand opened the GUI or used the click");
        } finally {
            TurretScene.clear(helper);
        }
        helper.succeed();
    }

    /**
     * Only admins of a base attach turrets to it: a sneaking stranger's click with a turret places nothing, the
     * owner's places the turret on top (sneaking, so the click does not open the GUI).
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "base_turret_permission")
    public void onlyAdminsAttachTurrets(GameTestHelper helper) {
        ServerPlayer owner = player(helper, "TurretTestOwner");
        ServerPlayer stranger = player(helper, "TurretTestStranger");
        placeBase(helper, owner);
        Block turret = ModBlocks.TURRETS.get(TurretType.DISPOSABLE_TURRET);
        try {
            stranger.setShiftKeyDown(true);
            stranger.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(turret));
            rightClick(helper, stranger, InteractionHand.MAIN_HAND);
            helper.assertBlockNotPresent(turret, TurretScene.TURRET);

            owner.setShiftKeyDown(true);
            owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(turret));
            rightClick(helper, owner, InteractionHand.MAIN_HAND);
            helper.assertBlockPresent(turret, TurretScene.TURRET);
        } finally {
            TurretScene.clear(helper);
        }
        helper.succeed();
    }

    /**
     * A fake player with its own profile; every test uses new profiles, as fake players are cached by profile. The
     * loader's {@link FakePlayer} refuses to open menus; this one opens them like a real player but sends no packet to
     * a client (there is none): the menu factory is called and the menu becomes the player's current menu.
     */
    private static final class GuiTestPlayer extends FakePlayer {
        private int menuCounter;

        private GuiTestPlayer(ServerLevel level, GameProfile profile) {
            super(level, profile);
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

    /** A new player for every call: no state is shared between tests. */
    private static ServerPlayer player(GameTestHelper helper, String name) {
        return new GuiTestPlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), name));
    }

    /** A tier 1 base owned by {@code owner}. */
    @SuppressWarnings("DataFlowIssue")
    private static TurretBaseBlockEntity placeBase(GameTestHelper helper, ServerPlayer owner) {
        helper.setBlock(BASE, ModBlocks.TURRET_BASES.get(0));
        TurretBaseBlockEntity base = (TurretBaseBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(BASE));
        base.getTrustManager().setOwner(PlayerAdapter.identity(owner));
        return base;
    }

    /** A right click on the top of the base, as the server handles it. */
    private static InteractionResult rightClick(GameTestHelper helper, ServerPlayer player, InteractionHand hand) {
        BlockPos pos = helper.absolutePos(BASE);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos).add(0, 0.5, 0), Direction.UP, pos, false);
        return player.gameMode.useItemOn(player, helper.getLevel(), player.getItemInHand(hand), hand, hit);
    }

    private static void assertMenuOpen(GameTestHelper helper, ServerPlayer player, TurretBaseBlockEntity base) {
        helper.assertTrue(player.containerMenu instanceof TurretBaseMenu menu && menu.getPos().equals(base.getBlockPos()),
                "The base GUI is not open, the player has " + player.containerMenu.getClass().getSimpleName());
    }
}
