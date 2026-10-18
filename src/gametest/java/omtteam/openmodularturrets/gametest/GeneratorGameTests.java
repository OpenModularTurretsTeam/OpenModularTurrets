package omtteam.openmodularturrets.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import omtteam.openmodularturrets.blockentity.misc.GeneratorBlockEntity;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;

/**
 * The generator with energy multiplier upgrades: it burns faster, but a fuel item still yields the same energy. Every
 * test has its own batch and removes its generator when it passed (1.20.1 keeps a finished batch's blocks).
 */
public class GeneratorGameTests implements FabricGameTest {
    private static final BlockPos GENERATOR = new BlockPos(1, 1, 1);
    /** Furnace burn time of coal. */
    private static final int COAL_TICKS = 1600;
    private static final int UPGRADES = 2;
    private static final int TIMEOUT_TICKS = 600;

    /**
     * With 2 upgrades a coal burns 4 fuel ticks per tick: it is gone after 400 ticks instead of 1600 and has produced
     * {@code 1600 * generatorEnergyPerTick}, the same as without upgrades.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "generator_multiplier", timeoutTicks = TIMEOUT_TICKS)
    public void energyMultiplierBurnsFasterForTheSameEnergy(GameTestHelper helper) {
        helper.setBlock(GENERATOR, ModBlocks.GENERATOR);
        GeneratorBlockEntity generator = (GeneratorBlockEntity) helper.getBlockEntity(GENERATOR);
        generator.getFuelContainer().setItem(0, new ItemStack(Items.COAL));
        generator.getUpgradeContainer().setItem(0, new ItemStack(ModItems.UPGRADE_ENERGY_MULTIPLIER, UPGRADES));
        long expected = (long) COAL_TICKS * OMTConfig.get().miscellaneous.generatorEnergyPerTick;
        long start = helper.getTick();
        helper.succeedWhen(() -> {
            helper.assertTrue(generator.getFuelContainer().getItem(0).isEmpty() && generator.getBurnTime() == 0,
                    "The coal is still burning (" + generator.getBurnTime() + " fuel ticks left)");
            long ticks = helper.getTick() - start;
            helper.assertTrue(ticks <= COAL_TICKS / 4 + 5, "Burning took " + ticks + " ticks, not about " + COAL_TICKS / 4);
            helper.assertTrue(generator.getEnergy() == expected,
                    "The coal produced " + generator.getEnergy() + " RF instead of " + expected);
            helper.setBlock(GENERATOR, Blocks.AIR);
        });
    }

    /** Each machine's upgrade slots only accept its own upgrades. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "generator_upgrade_slots")
    public void upgradesOnlyFitTheirMachine(GameTestHelper helper) {
        helper.setBlock(GENERATOR, ModBlocks.GENERATOR);
        GeneratorBlockEntity generator = (GeneratorBlockEntity) helper.getBlockEntity(GENERATOR);
        helper.assertTrue(generator.getUpgradeContainer().canPlaceItem(0, new ItemStack(ModItems.UPGRADE_ENERGY_MULTIPLIER)),
                "The generator does not accept its energy multiplier upgrade");
        helper.assertFalse(generator.getUpgradeContainer().canPlaceItem(0, new ItemStack(ModItems.UPGRADE_RANGE)),
                "The generator accepts a turret base upgrade");
        helper.assertFalse(TurretBaseInventory.mayPlace(TurretBaseInventory.UPGRADE_START,
                new ItemStack(ModItems.UPGRADE_ENERGY_MULTIPLIER)), "A turret base accepts the generator upgrade");
        helper.assertTrue(TurretBaseInventory.mayPlace(TurretBaseInventory.UPGRADE_START, new ItemStack(ModItems.UPGRADE_RANGE)),
                "A turret base does not accept its range upgrade");
        helper.assertTrue(TurretBaseInventory.getSlotLimit(TurretBaseInventory.UPGRADE_START,
                new ItemStack(ModItems.UPGRADE_RANGE)) == 4, "Turret base upgrades do not stack to 4");
        helper.setBlock(GENERATOR, Blocks.AIR);
        helper.succeed();
    }
}
