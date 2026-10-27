package omtteam.openmodularturrets.gametest;

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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.network.BasePacketHandler;
import omtteam.openmodularturrets.network.BasePackets;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.menu.TurretBaseMenu;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;
import omtteam.omlib.security.PlayerAdapter;

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
     * The sort button merges equal ammo and puts Minecraft's items first, then Open Modular Turrets' ammo, each by
     * registry name; a player without access to the GUI cannot sort.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "base_sort_ammo")
    public void sortButtonSortsTheAmmo(GameTestHelper helper) {
        ServerPlayer owner = player(helper, "SortTestOwner");
        ServerPlayer stranger = player(helper, "SortTestStranger");
        TurretBaseBlockEntity base = placeBase(helper, owner);
        Vec3 nearBase = Vec3.atCenterOf(helper.absolutePos(BASE.above()));
        owner.setPos(nearBase);
        stranger.setPos(nearBase);
        TurretBaseInventory inventory = base.getInventory();
        ItemStack[] filled = {new ItemStack(ModItems.AMMO_ROCKET, 3), new ItemStack(Items.ARROW, 40),
                ItemStack.EMPTY, new ItemStack(ModItems.AMMO_BULLET, 50), new ItemStack(Items.FIRE_CHARGE, 5),
                new ItemStack(ModItems.AMMO_BULLET, 30), new ItemStack(Items.ARROW, 40), ItemStack.EMPTY,
                new ItemStack(ModItems.AMMO_BOLT, 7)};
        try {
            for (int slot = 0; slot < filled.length; slot++) {
                inventory.setItem(TurretBaseInventory.AMMO_START + slot, filled[slot].copy());
            }
            BasePackets.SimpleAction sort = new BasePackets.SimpleAction(helper.absolutePos(BASE), BasePackets.Action.SORT_AMMO);
            BasePacketHandler.handleAction(sort, stranger);
            helper.assertTrue(inventory.getItem(TurretBaseInventory.AMMO_START).is(ModItems.AMMO_ROCKET),
                    "A stranger sorted the ammo");

            BasePacketHandler.handleAction(sort, owner);
            ItemStack[] expected = {new ItemStack(Items.ARROW, 64), new ItemStack(Items.ARROW, 16),
                    new ItemStack(Items.FIRE_CHARGE, 5), new ItemStack(ModItems.AMMO_BOLT, 7),
                    new ItemStack(ModItems.AMMO_BULLET, 64), new ItemStack(ModItems.AMMO_BULLET, 16),
                    new ItemStack(ModItems.AMMO_ROCKET, 3), ItemStack.EMPTY, ItemStack.EMPTY};
            for (int slot = 0; slot < expected.length; slot++) {
                ItemStack actual = inventory.getItem(TurretBaseInventory.AMMO_START + slot);
                helper.assertTrue(actual.getItem() == expected[slot].getItem() && actual.getCount() == expected[slot].getCount(),
                        "Slot " + slot + " holds " + actual + " instead of " + expected[slot]);
            }
        } finally {
            inventory.clearContent();
            TurretScene.clear(helper);
        }
        helper.succeed();
    }

    /** A new player for every call: no state is shared between tests. */
    private static ServerPlayer player(GameTestHelper helper, String name) {
        return GuiTestPlayer.create(helper, name);
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
