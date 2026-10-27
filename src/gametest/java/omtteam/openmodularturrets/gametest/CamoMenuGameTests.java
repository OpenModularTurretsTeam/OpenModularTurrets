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
//? if <1.21.11 {
import net.minecraft.gametest.framework.GameTest;
//?}
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import omtteam.omlib.camo.CamoHost;
import omtteam.omlib.core.security.AccessLevel;
import omtteam.omlib.core.security.TrustManager;
import omtteam.omlib.menu.CamoMenu;
import omtteam.omlib.network.CamoPacketHandler;
import omtteam.omlib.network.CamoPackets;
import omtteam.omlib.security.PlayerAdapter;
import omtteam.omlib.security.TrustHost;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.menu.TurretBaseMenu;
import omtteam.openmodularturrets.registry.ModBlocks;

/**
 * OMLib's camouflage menu on turret bases and the trap: the packets of its screens ({@code CamoPacketHandler}: open the
 * menu, "Back" to the base GUI), who gets the menu ({@code CamoMenus}), and the server menu itself ({@code CamoMenu}:
 * when it stays open, clicks of players without the right, the phantom slot). Disguises set through the menu by the
 * owner are tested in OpenModularLighting's {@code CamoGameTest}, the light in {@link CamoGameTests}. The players are
 * {@link GuiTestPlayer}s, which open menus without a client.
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class CamoMenuGameTests implements OMTGameTests {
    private static final BlockPos BLOCK = new BlockPos(1, 1, 1);

    /** The owner gets the menu of a tier 1 base, without light sliders (the base has no adjustable light). */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_menu")
    public void ownerOpensTheCamoMenu(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, base);

        open(helper, owner);

        CamoMenu menu = assertCamoMenu(helper, owner);
        helper.assertTrue(!menu.showsLight(), "A tier 1 base shows light sliders");
        helper.succeed();
    }

    /** An admin of a tier 4 base may change its light, so the menu opens for them too, with the light sliders. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_menu")
    public void adminsOfALightBaseOpenTheCamoMenu(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(3));
        ServerPlayer owner = owner(helper, base);
        ServerPlayer admin = trusted(helper, base, owner, "Admin", AccessLevel.ADMIN);

        open(helper, admin);

        helper.assertTrue(assertCamoMenu(helper, admin).showsLight(), "A tier 4 base shows no light sliders");
        helper.succeed();
    }

    /** Strangers and trusted players who may neither set the disguise nor change the light get no menu. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_menu")
    public void playersWithoutCamoRightsGetNoMenu(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(3));
        ServerPlayer owner = owner(helper, base);
        ServerPlayer settings = trusted(helper, base, owner, "Settings", AccessLevel.CHANGE_SETTINGS);
        ServerPlayer stranger = playerAt(helper, "Stranger");

        open(helper, settings);
        open(helper, stranger);

        assertNoMenu(helper, settings);
        assertNoMenu(helper, stranger);
        helper.succeed();
    }

    /** Requests from players more than the container distance away open nothing. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_menu")
    public void noMenuFromFarAway(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, base);
        moveAway(helper, owner);

        open(helper, owner);

        assertNoMenu(helper, owner);
        helper.succeed();
    }

    /** With {@code bases.allowBaseCamo} off, no camouflage menu opens (own batch: it changes the config). */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_menu_disabled")
    public void noMenuWithBaseCamouflageOff(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, base);
        ConfigData config = OMTConfig.get();
        boolean allowBaseCamo = config.bases.allowBaseCamo;
        config.bases.allowBaseCamo = false;
        try {
            open(helper, owner);
        } finally {
            config.bases.allowBaseCamo = allowBaseCamo;
        }
        assertNoMenu(helper, owner);
        helper.succeed();
    }

    /** "Back" in a base's camouflage menu opens the base GUI again, but not from far away. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_menu")
    public void backReturnsToTheBaseGui(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, base);
        open(helper, owner);
        BlockPos pos = helper.absolutePos(BLOCK);

        moveAway(helper, owner);
        CamoPacketHandler.handleBack(new CamoPackets.Back(pos), owner);
        assertCamoMenu(helper, owner);

        moveNear(helper, owner);
        CamoPacketHandler.handleBack(new CamoPackets.Back(pos), owner);
        helper.assertTrue(owner.containerMenu instanceof TurretBaseMenu menu && menu.getPos().equals(pos),
                "Back did not open the base GUI, the player has " + owner.containerMenu.getClass().getSimpleName());
        helper.succeed();
    }

    /** The trap's camouflage menu is opened on its own, so "Back" has nothing to return to and changes nothing. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_menu")
    public void theTrapHasNoMenuToGoBackTo(GameTestHelper helper) {
        CamoHost trap = place(helper, ModBlocks.CAMO_TRAP);
        ServerPlayer owner = owner(helper, trap);
        open(helper, owner);
        helper.assertTrue(assertCamoMenu(helper, owner).showsLight(), "The trap shows no light sliders");

        CamoPacketHandler.handleBack(new CamoPackets.Back(helper.absolutePos(BLOCK)), owner);

        assertCamoMenu(helper, owner);
        helper.succeed();
    }

    /** The menu stays open while the player is near and may use it, and closes when the block is disowned or removed. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_menu")
    public void theMenuIsValidOnlyWhileThePlayerMayUseIt(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, base);
        ServerPlayer stranger = playerAt(helper, "Stranger");
        CamoMenu menu = CamoMenu.server(1, owner.getInventory(), base);

        helper.assertTrue(menu.stillValid(owner), "The owner's menu is not valid");
        helper.assertTrue(!menu.stillValid(stranger), "The menu is valid for a stranger");
        moveAway(helper, owner);
        helper.assertTrue(!menu.stillValid(owner), "The menu is valid from far away");
        moveNear(helper, owner);
        ((TrustHost) base).clearOwner();
        helper.assertTrue(!menu.stillValid(owner), "The menu is valid after disowning");
        helper.setBlock(BLOCK, Blocks.AIR);
        helper.assertTrue(!menu.stillValid(owner), "The menu is valid after the block was removed");
        helper.succeed();
    }

    /** A player without the right changes no disguise through the menu (the server checks every click). */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_menu")
    public void strangersSetNoDisguiseThroughTheMenu(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(0));
        owner(helper, base);
        ServerPlayer stranger = playerAt(helper, "Stranger");
        CamoMenu menu = CamoMenu.server(1, stranger.getInventory(), base);

        menu.setCarried(new ItemStack(Items.STONE));
        menu.clicked(CamoMenu.PHANTOM_SLOT, 0, ClickType.PICKUP, stranger);
        stranger.getInventory().setItem(9, new ItemStack(Items.STONE_BRICKS));
        menu.quickMoveStack(stranger, 0);

        helper.assertTrue(base.getCamo() == null, "A stranger set the disguise " + base.getCamo());
        helper.succeed();
    }

    /** The phantom slot takes and gives no items, while the inventory slots of the menu work as usual. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_menu")
    public void thePhantomSlotHoldsNoItems(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, base);
        CamoMenu menu = CamoMenu.server(1, owner.getInventory(), base);
        Slot phantom = menu.getSlot(CamoMenu.PHANTOM_SLOT);
        helper.assertTrue(!phantom.mayPlace(new ItemStack(Items.STONE)), "The phantom slot takes items");
        helper.assertTrue(!phantom.mayPickup(owner), "Items can be taken from the phantom slot");

        // menu slot 0 is the first slot of the main inventory (inventory slot 9)
        owner.getInventory().setItem(9, new ItemStack(Items.STONE, 5));
        menu.clicked(0, 0, ClickType.PICKUP, owner);

        helper.assertTrue(menu.getCarried().is(Items.STONE) && menu.getCarried().getCount() == 5,
                "Clicking an inventory slot did not pick up its stack, the cursor holds " + menu.getCarried());
        helper.assertTrue(owner.getInventory().getItem(9).isEmpty(), "The stack is still in the inventory");
        helper.assertTrue(base.getCamo() == null, "Picking up a stack set the disguise " + base.getCamo());
        helper.succeed();
    }

    /** The client's menu finds the host at the menu's position and shows the light sliders if it has adjustable light. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_menu")
    public void theClientMenuShowsLightSlidersForLightHosts(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(BLOCK);
        ServerPlayer player = playerAt(helper, "Viewer");
        helper.setBlock(BLOCK, ModBlocks.TURRET_BASES.get(3));
        CamoMenu light = CamoMenu.client(1, player.getInventory(), pos);
        helper.setBlock(BLOCK, ModBlocks.TURRET_BASES.get(0));
        CamoMenu noLight = CamoMenu.client(2, player.getInventory(), pos);
        helper.setBlock(BLOCK, Blocks.STONE);
        CamoMenu noHost = CamoMenu.client(3, player.getInventory(), pos);

        helper.assertTrue(light.showsLight() && light.getPos().equals(pos), "A tier 4 base shows no light sliders");
        helper.assertTrue(!noLight.showsLight(), "A tier 1 base shows light sliders");
        helper.assertTrue(!noHost.showsLight(), "A block without camouflage shows light sliders");
        helper.succeed();
    }

    private static void open(GameTestHelper helper, ServerPlayer player) {
        CamoPacketHandler.handleOpenMenu(new CamoPackets.OpenMenu(helper.absolutePos(BLOCK)), player);
    }

    /** Places {@code block} at {@link #BLOCK} and returns its block entity. */
    private static CamoHost place(GameTestHelper helper, Block block) {
        helper.setBlock(BLOCK, block);
        return (CamoHost) helper.getLevel().getBlockEntity(helper.absolutePos(BLOCK));
    }

    /** A player next to the block who owns it. */
    private static ServerPlayer owner(GameTestHelper helper, CamoHost host) {
        ServerPlayer owner = playerAt(helper, "Owner");
        ((TrustHost) host).getTrustManager().setOwner(PlayerAdapter.identity(owner));
        return owner;
    }

    /** A player next to the block, on its trust list with {@code level}. */
    private static ServerPlayer trusted(GameTestHelper helper, CamoHost host, ServerPlayer owner, String name, AccessLevel level) {
        ServerPlayer player = playerAt(helper, name);
        TrustManager trust = ((TrustHost) host).getTrustManager();
        trust.addTrustedPlayer(PlayerAdapter.info(owner), PlayerAdapter.identity(player));
        trust.setAccessLevel(PlayerAdapter.info(owner), player.getUUID(), level);
        return player;
    }

    /** A {@link GuiTestPlayer} standing on top of the block, within reach. */
    private static ServerPlayer playerAt(GameTestHelper helper, String name) {
        ServerPlayer player = GuiTestPlayer.create(helper, name);
        moveNear(helper, player);
        return player;
    }

    private static void moveNear(GameTestHelper helper, ServerPlayer player) {
        BlockPos pos = helper.absolutePos(BLOCK);
        player.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
    }

    /** More than the container distance (8 blocks) away from the block. */
    private static void moveAway(GameTestHelper helper, ServerPlayer player) {
        BlockPos pos = helper.absolutePos(BLOCK);
        player.moveTo(pos.getX() + 9.5D, pos.getY(), pos.getZ() + 0.5D);
    }

    private static CamoMenu assertCamoMenu(GameTestHelper helper, ServerPlayer player) {
        helper.assertTrue(player.containerMenu instanceof CamoMenu menu && menu.getPos().equals(helper.absolutePos(BLOCK)),
                "The camouflage menu is not open, the player has " + player.containerMenu.getClass().getSimpleName());
        return (CamoMenu) player.containerMenu;
    }

    private static void assertNoMenu(GameTestHelper helper, ServerPlayer player) {
        helper.assertTrue(player.containerMenu == player.inventoryMenu,
                player.getGameProfile() + " got a menu: " + player.containerMenu.getClass().getSimpleName());
    }
}
