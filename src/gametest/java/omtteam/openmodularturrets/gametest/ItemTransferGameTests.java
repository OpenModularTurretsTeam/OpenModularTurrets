package omtteam.openmodularturrets.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.misc.GeneratorBlockEntity;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;

import java.util.UUID;

/**
 * What hoppers may move in and out of the base and the generator. A vanilla hopper goes through the loader's item API
 * (Fabric's {@code ItemStorage.SIDED}, NeoForge's item handler capability), so these tests cover the adapters of
 * {@code ItemAccess}. The unwanted item always sits in the hopper's or machine's first slot, so the filter has to skip
 * it. Every test has its own batch and empties and removes its blocks when it passed (1.20.1 keeps a finished batch's
 * blocks and entities).
 */
public class ItemTransferGameTests implements FabricGameTest {
    private static final BlockPos MACHINE = new BlockPos(1, 1, 1);
    private static final BlockPos ABOVE = MACHINE.above();
    private static final BlockPos BELOW = MACHINE.below();
    private static final int TIMEOUT_TICKS = 100;

    /** A hopper on top fills the ammo slots with ammo and leaves anything else in the hopper. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "items_base_ammo", timeoutTicks = TIMEOUT_TICKS)
    public void hopperFillsTheBaseWithAmmoOnly(GameTestHelper helper) {
        helper.setBlock(MACHINE, ModBlocks.TURRET_BASES.get(0));
        TurretBaseBlockEntity base = (TurretBaseBlockEntity) helper.getBlockEntity(MACHINE);
        // a base without owner drops itself on its next tick
        base.getTrustManager().setOwner(new PlayerIdentity(UUID.randomUUID(), "GameTestOwner"));
        HopperBlockEntity hopper = hopper(helper, ABOVE);
        hopper.setItem(0, new ItemStack(Items.DIRT));
        hopper.setItem(1, new ItemStack(ModItems.AMMO_BULLET, 2));
        helper.succeedWhen(() -> {
            helper.assertTrue(count(base.getInventory(), ModItems.AMMO_BULLET) == 2, "The ammo did not arrive in the base");
            helper.assertTrue(count(hopper, Items.DIRT) == 1, "The dirt left the hopper");
            clear(helper, hopper, base.getInventory());
        });
    }

    /** A hopper on top feeds fuel into the generator and leaves anything else in the hopper. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "items_generator_fuel", timeoutTicks = TIMEOUT_TICKS)
    public void hopperFeedsTheGeneratorFuelOnly(GameTestHelper helper) {
        GeneratorBlockEntity generator = generator(helper);
        HopperBlockEntity hopper = hopper(helper, ABOVE);
        hopper.setItem(0, new ItemStack(Items.DIRT));
        hopper.setItem(1, new ItemStack(Items.COAL, 2));
        helper.succeedWhen(() -> {
            helper.assertTrue(count(hopper, Items.COAL) == 0, "The coal is still in the hopper");
            helper.assertTrue(count(hopper, Items.DIRT) == 1, "The dirt left the hopper");
            clear(helper, hopper, generator.getFuelContainer());
        });
    }

    /** A hopper below takes leftovers (empty buckets) out of the generator, but never its fuel. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "items_generator_leftovers", timeoutTicks = TIMEOUT_TICKS)
    public void hopperTakesOnlyLeftoversFromTheGenerator(GameTestHelper helper) {
        GeneratorBlockEntity generator = generator(helper);
        generator.getFuelContainer().setItem(0, new ItemStack(Items.COAL, 4));
        generator.getFuelContainer().setItem(1, new ItemStack(Items.BUCKET));
        HopperBlockEntity hopper = hopper(helper, BELOW);
        helper.succeedWhen(() -> {
            helper.assertTrue(count(hopper, Items.BUCKET) == 1, "The bucket is still in the generator");
            helper.assertTrue(count(hopper, Items.COAL) == 0, "The hopper took coal out of the generator");
            clear(helper, hopper, generator.getFuelContainer());
        });
    }

    private static GeneratorBlockEntity generator(GameTestHelper helper) {
        helper.setBlock(MACHINE, ModBlocks.GENERATOR);
        return (GeneratorBlockEntity) helper.getBlockEntity(MACHINE);
    }

    /** A hopper pointing down (its default), so one above the machine pushes into it and one below pulls from it. */
    private static HopperBlockEntity hopper(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, Blocks.HOPPER);
        return (HopperBlockEntity) helper.getBlockEntity(pos);
    }

    private static int count(Container container, Item item) {
        int count = 0;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.is(item)) count += stack.getCount();
        }
        return count;
    }

    /** Empties both containers first, so that removing the blocks drops nothing into later tests. */
    private static void clear(GameTestHelper helper, Container hopper, Container machine) {
        hopper.clearContent();
        machine.clearContent();
        helper.setBlock(ABOVE, Blocks.AIR);
        helper.setBlock(BELOW, Blocks.AIR);
        helper.setBlock(MACHINE, Blocks.AIR);
    }
}
