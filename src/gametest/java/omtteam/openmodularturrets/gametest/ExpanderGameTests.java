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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
//? if <1.21.11 {
import net.minecraft.gametest.framework.GameTest;
//?}
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.block.misc.InventoryExpanderBlock;
import omtteam.omlib.compat.VersionCompat;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.misc.GeneratorBlockEntity;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.menu.TurretBaseMenu;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;

/**
 * Inventory expanders: which ones a machine counts, the larger slots through hoppers, saving and the menu, which hands
 * out at most one stack at a time. The config defaults apply (tier 1–3 add 1, 2 and 3 stacks). Every test has its own
 * batch and empties and removes its blocks when it passed (1.20.1 keeps a finished batch's blocks).
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings({"unused", "RedundantCast", "DefaultAnnotationParam"})
public class ExpanderGameTests implements OMTGameTests {
    private static final BlockPos MACHINE = new BlockPos(1, 1, 1);
    private static final int TIMEOUT_TICKS = 100;
    /** Menu index of the first ammo slot (after the 36 player inventory slots). */
    private static final int FIRST_AMMO_SLOT = 36;

    /**
     * Only the expander next to a face counts, not one behind it or one with a gap before it; every face counts on its
     * own: tier 1 to the east and tier 2 on top make 3 extra stacks.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "expander_scan", timeoutTicks = TIMEOUT_TICKS)
    public void baseCountsOnlyTheExpanderNextToIt(GameTestHelper helper) {
        TurretBaseBlockEntity base = base(helper);
        BlockPos[] expanders = {MACHINE.east(), MACHINE.east(2), MACHINE.above(), MACHINE.south(2)};
        int[] tiers = {1, 3, 2, 3};
        for (int i = 0; i < expanders.length; i++) {
            helper.setBlock(expanders[i], ModBlocks.INVENTORY_EXPANDERS.get(tiers[i] - 1));
        }
        helper.succeedWhen(() -> {
            helper.assertTrue(base.getSlotCapacity().bonusStacks() == 3,
                    "Bonus stacks " + base.getSlotCapacity().bonusStacks() + " instead of 3");
            helper.assertTrue(base.getInventory().getSlotLimit(0, new ItemStack(ModItems.AMMO_BULLET)) == 4 * 64,
                    "An ammo slot does not hold 4 stacks");
            helper.assertTrue(base.getInventory().getSlotLimit(TurretBaseInventory.UPGRADE_START,
                    new ItemStack(ModItems.UPGRADE_RANGE)) == 4, "The expanders changed the upgrade slot");
            removeAll(helper, expanders);
        });
    }

    /** A double expander that loses a plate, then the expander's removal, shrink the slots. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "expander_remove", timeoutTicks = TIMEOUT_TICKS)
    public void removingAPlateShrinksTheSlots(GameTestHelper helper) {
        TurretBaseBlockEntity base = base(helper);
        BlockState single = ModBlocks.INVENTORY_EXPANDERS.get(0).defaultBlockState()
                .setValue(InventoryExpanderBlock.FACING, Direction.WEST);
        BlockPos expander = MACHINE.east();
        helper.setBlock(expander, single.setValue(InventoryExpanderBlock.DOUBLE, true));
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(base.getSlotCapacity().bonusStacks() == 2, "The double expander did not count twice");
            helper.setBlock(expander, single);
            helper.runAfterDelay(2, () -> {
                helper.assertTrue(base.getSlotCapacity().bonusStacks() == 1, "The removed plate still counts");
                helper.setBlock(expander, Blocks.AIR);
                helper.succeedWhen(() -> {
                    helper.assertTrue(base.getSlotCapacity().bonusStacks() == 0, "The removed expander still counts");
                    removeAll(helper);
                });
            });
        });
    }

    /** A double block counts as two expanders (the block behind it does not count); it drops two items. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "expander_double", timeoutTicks = TIMEOUT_TICKS)
    public void doubleExpanderCountsTwice(GameTestHelper helper) {
        TurretBaseBlockEntity base = base(helper);
        InventoryExpanderBlock tierThree = ModBlocks.INVENTORY_EXPANDERS.get(2);
        BlockState doubleState = tierThree.defaultBlockState()
                .setValue(InventoryExpanderBlock.FACING, Direction.WEST).setValue(InventoryExpanderBlock.DOUBLE, true);
        BlockPos[] expanders = {MACHINE.east(), MACHINE.east(2)};
        helper.setBlock(expanders[0], doubleState);
        helper.setBlock(expanders[1], tierThree);
        int drops = Block.getDrops(doubleState, helper.getLevel(), helper.absolutePos(expanders[0]), null).stream()
                .filter(stack -> stack.is(tierThree.asItem())).mapToInt(ItemStack::getCount).sum();
        helper.assertTrue(drops == 2, "A double expander drops " + drops + " items");
        helper.succeedWhen(() -> {
            helper.assertTrue(base.getSlotCapacity().bonusStacks() == 6,
                    "Bonus stacks " + base.getSlotCapacity().bonusStacks() + " instead of 6");
            removeAll(helper, expanders);
        });
    }

    /**
     * An expander goes onto a machine's face, not onto another block: against stone or the open face of another
     * tier's expander, nothing is placed.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "expander_place_machine", timeoutTicks = TIMEOUT_TICKS)
    public void expandersOnlyGoOntoMachines(GameTestHelper helper) {
        TurretBaseBlockEntity base = base(helper);
        InventoryExpanderBlock tierOne = ModBlocks.INVENTORY_EXPANDERS.get(0);
        BlockPos[] blocks = {MACHINE.east(), MACHINE.east(2), MACHINE.west(), MACHINE.west(2)};
        helper.setBlock(blocks[3], Blocks.STONE);
        ServerPlayer player = GuiTestPlayer.create(helper, "expander_machine");
        // sneaking, so the base does not open its menu
        player.setShiftKeyDown(true);

        click(helper, player, MACHINE, new Vec3(1.0, 0.5, 0.5), tierOne);
        BlockState placed = helper.getBlockState(blocks[0]);
        helper.assertTrue(placed.is(tierOne) && placed.getValue(InventoryExpanderBlock.FACING) == Direction.WEST,
                "The expander was not placed against the base: " + placed);

        click(helper, player, blocks[0], new Vec3(6.0 / 16.0, 0.5, 0.5), ModBlocks.INVENTORY_EXPANDERS.get(1));
        helper.assertTrue(helper.getBlockState(blocks[1]).isAir(), "An expander was placed against another expander");
        helper.assertFalse(helper.getBlockState(blocks[0]).getValue(InventoryExpanderBlock.DOUBLE),
                "An expander of another tier became the second plate");

        click(helper, player, blocks[3], new Vec3(1.0, 0.5, 0.5), tierOne);
        helper.assertTrue(helper.getBlockState(blocks[2]).isAir(), "An expander was placed against stone");
        helper.succeedWhen(() -> {
            helper.assertTrue(base.getSlotCapacity().bonusStacks() == 1,
                    "Bonus stacks " + base.getSlotCapacity().bonusStacks() + " instead of 1");
            removeAll(helper, blocks);
        });
    }

    /**
     * Clicking the open side of a single expander with one of the same tier stacks the second plate onto the first,
     * from the plate's open face or from the base's face around the plate; clicking the block across the free side
     * places nothing.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "expander_place_double", timeoutTicks = TIMEOUT_TICKS)
    public void secondPlateStacksOntoTheFirst(GameTestHelper helper) {
        TurretBaseBlockEntity base = base(helper);
        InventoryExpanderBlock tierOne = ModBlocks.INVENTORY_EXPANDERS.get(0);
        BlockState single = tierOne.defaultBlockState().setValue(InventoryExpanderBlock.FACING, Direction.WEST);
        BlockPos[] expanders = {MACHINE.east(), MACHINE.east(2)};
        helper.setBlock(expanders[0], single);
        ServerPlayer player = GuiTestPlayer.create(helper, "expander_place");
        // sneaking, so the base does not open its menu
        player.setShiftKeyDown(true);

        helper.setBlock(expanders[1], Blocks.STONE);
        click(helper, player, expanders[1], new Vec3(0.0, 0.5, 0.5), tierOne);
        helper.assertTrue(helper.getBlockState(expanders[0]).equals(single),
                "Clicking the block across the free side changed the expander");
        helper.setBlock(expanders[1], Blocks.AIR);

        click(helper, player, expanders[0], new Vec3(6.0 / 16.0, 0.5, 0.5), tierOne);
        BlockState stacked = helper.getBlockState(expanders[0]);
        helper.assertTrue(stacked.getValue(InventoryExpanderBlock.DOUBLE),
                "Clicking the plate's open face did not add the second plate");
        double depth = stacked.getShape(helper.getLevel(), helper.absolutePos(expanders[0])).max(Direction.Axis.X);
        helper.assertTrue(depth == 12.0 / 16.0, "The two plates reach " + depth + " into the block, not 12 / 16");

        helper.setBlock(expanders[0], single);
        click(helper, player, MACHINE, new Vec3(1.0, 1.0 / 16.0, 0.5), tierOne);
        helper.assertTrue(helper.getBlockState(expanders[0]).getValue(InventoryExpanderBlock.DOUBLE),
                "Clicking the base's face around the plate did not add the second plate");
        helper.succeedWhen(() -> {
            helper.assertTrue(base.getSlotCapacity().bonusStacks() == 2,
                    "Bonus stacks " + base.getSlotCapacity().bonusStacks() + " instead of 2");
            removeAll(helper, expanders);
        });
    }

    /** Right-clicks {@code block} at {@code at} (relative to its corner; x 0 is the west face, otherwise the east face). */
    private static void click(GameTestHelper helper, ServerPlayer player, BlockPos block, Vec3 at, Block held) {
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(held));
        BlockPos pos = helper.absolutePos(block);
        Direction face = at.x == 0.0 ? Direction.WEST : Direction.EAST;
        BlockHitResult hit = new BlockHitResult(Vec3.atLowerCornerOf(pos).add(at), face, pos, false);
        player.gameMode.useItemOn(player, helper.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
    }

    /** With every ammo slot holding one stack, a hopper on top still fills the first slot beyond it. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "expander_hopper", timeoutTicks = TIMEOUT_TICKS)
    public void hopperFillsAnAmmoSlotBeyondOneStack(GameTestHelper helper) {
        TurretBaseBlockEntity base = base(helper);
        helper.setBlock(MACHINE.east(), ModBlocks.INVENTORY_EXPANDERS.get(0));
        for (int slot = 0; slot < TurretBaseInventory.AMMO_COUNT; slot++) {
            base.getInventory().setItem(slot, new ItemStack(ModItems.AMMO_BULLET, 64));
        }
        helper.setBlock(MACHINE.above(), Blocks.HOPPER);
        HopperBlockEntity hopper = (HopperBlockEntity) helper.getLevel()
                .getBlockEntity(helper.absolutePos(MACHINE.above()));
        hopper.setItem(0, new ItemStack(ModItems.AMMO_BULLET, 2));
        helper.succeedWhen(() -> {
            helper.assertTrue(base.getInventory().getItem(0).getCount() == 66,
                    "The first ammo slot holds " + base.getInventory().getItem(0).getCount() + " instead of 66");
            hopper.clearContent();
            removeAll(helper, MACHINE.east(), MACHINE.above());
        });
    }

    /** Counts above one stack (and above what vanilla can save) survive saving and loading. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "expander_save")
    public void largeCountsSurviveSavingAndLoading(GameTestHelper helper) {
        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
        items.set(0, new ItemStack(ModItems.AMMO_BULLET, 300));
        items.set(2, new ItemStack(Items.COAL, 5));
        CompoundTag tag = new CompoundTag();
        VersionCompat.saveItems(tag, items, registries);
        NonNullList<ItemStack> loaded = NonNullList.withSize(3, ItemStack.EMPTY);
        VersionCompat.loadItems(tag, loaded, registries);
        helper.assertTrue(loaded.get(0).is(ModItems.AMMO_BULLET) && loaded.get(0).getCount() == 300,
                "Loaded " + loaded.get(0) + " instead of 300 bullets");
        helper.assertTrue(loaded.get(1).isEmpty(), "The empty slot loaded " + loaded.get(1));
        helper.assertTrue(loaded.get(2).is(Items.COAL) && loaded.get(2).getCount() == 5,
                "Loaded " + loaded.get(2) + " instead of 5 coal");
        helper.succeed();
    }

    /**
     * The menu hands out one stack at a time (pickup and hotbar swap), takes it back and lets shift-click fill a slot
     * beyond one stack.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "expander_menu", timeoutTicks = TIMEOUT_TICKS)
    public void menuHandsOutOneStackAtATime(GameTestHelper helper) {
        TurretBaseBlockEntity base = base(helper);
        helper.setBlock(MACHINE.east(), ModBlocks.INVENTORY_EXPANDERS.get(2));
        base.getInventory().setItem(0, new ItemStack(ModItems.AMMO_BULLET, 200));
        helper.runAfterDelay(2, () -> {
            ServerPlayer player = GuiTestPlayer.create(helper, "expander_menu");
            TurretBaseMenu menu = TurretBaseMenu.server(1, player.getInventory(), base);
            // the max stack size of the GUI's info panel, from the data the client gets
            helper.assertTrue(menu.getSlotCapacity().limit(64) == 4 * 64,
                    "The menu reports " + menu.getSlotCapacity().limit(64) + " items per slot instead of 256");

            menu.clicked(FIRST_AMMO_SLOT, 0, ClickType.PICKUP, player);
            helper.assertTrue(menu.getCarried().getCount() == 64, "Picked up " + menu.getCarried().getCount());
            helper.assertTrue(base.getInventory().getItem(0).getCount() == 136, "The slot did not keep the rest");
            menu.clicked(FIRST_AMMO_SLOT, 0, ClickType.PICKUP, player);
            helper.assertTrue(menu.getCarried().isEmpty(), "The slot did not take the stack back");
            helper.assertTrue(base.getInventory().getItem(0).getCount() == 200, "The slot holds no 200 again");

            menu.clicked(FIRST_AMMO_SLOT, 0, ClickType.SWAP, player);
            helper.assertTrue(player.getInventory().getItem(0).getCount() == 64,
                    "The hotbar swap moved " + player.getInventory().getItem(0).getCount());
            helper.assertTrue(base.getInventory().getItem(0).getCount() == 136, "The swap took more than one stack");

            menu.clicked(0, 0, ClickType.QUICK_MOVE, player);
            helper.assertTrue(player.getInventory().getItem(0).isEmpty(), "Shift-click left bullets in the hotbar");
            helper.assertTrue(base.getInventory().getItem(0).getCount() == 200,
                    "Shift-click filled the slot to " + base.getInventory().getItem(0).getCount());
            removeAll(helper, MACHINE.east());
            helper.succeed();
        });
    }

    /** Fuel slots grow too, but not for fuel that leaves a remainder (the slot would lose the buckets). */
    @GameTest(template = EMPTY_STRUCTURE, batch = "expander_generator", timeoutTicks = TIMEOUT_TICKS)
    public void generatorFuelSlotsGrowExceptForRemainders(GameTestHelper helper) {
        helper.setBlock(MACHINE, ModBlocks.GENERATOR);
        GeneratorBlockEntity generator = (GeneratorBlockEntity) helper.getLevel()
                .getBlockEntity(helper.absolutePos(MACHINE));
        helper.setBlock(MACHINE.east(), ModBlocks.INVENTORY_EXPANDERS.get(0));
        helper.succeedWhen(() -> {
            helper.assertTrue(generator.getFuelContainer().getSlotLimit(0, new ItemStack(Items.COAL)) == 128,
                    "A fuel slot does not hold 2 stacks of coal");
            helper.assertTrue(generator.getFuelContainer().getSlotLimit(0, new ItemStack(Items.LAVA_BUCKET)) == 1,
                    "Lava buckets stack in a fuel slot");
            removeAll(helper, MACHINE.east());
        });
    }

    private static TurretBaseBlockEntity base(GameTestHelper helper) {
        helper.setBlock(MACHINE, ModBlocks.TURRET_BASES.get(0));
        return (TurretBaseBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(MACHINE));
    }

    /** Empties the machine first, so that removing it drops nothing into later tests. */
    private static void removeAll(GameTestHelper helper, BlockPos... others) {
        if (helper.getLevel().getBlockEntity(helper.absolutePos(MACHINE)) instanceof Clearable machine) {
            machine.clearContent();
        }
        for (BlockPos pos : others) {
            helper.setBlock(pos, Blocks.AIR);
        }
        helper.setBlock(MACHINE, Blocks.AIR);
    }
}
