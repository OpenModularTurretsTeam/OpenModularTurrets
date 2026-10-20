package omtteam.openmodularturrets.gametest;

//? if neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import omtteam.openmodularturrets.OMT;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.misc.GeneratorBlockEntity;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;

import java.util.UUID;

/**
 * The generator with energy multiplier upgrades: it burns faster, but a fuel item still yields the same energy; and it
 * pushes its energy into neighbours. Every test has its own batch and removes its blocks when it passed (1.20.1 keeps a
 * finished batch's blocks).
 */
//? if neoforge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings({"unused", "RedundantCast"})
public class GeneratorGameTests implements OMTGameTests {
    private static final BlockPos GENERATOR = new BlockPos(1, 1, 1);
    private static final BlockPos BASE = GENERATOR.east();
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

    /**
     * The generator pushes into a neighbouring base (through the loader's energy API on both sides) without losing
     * energy: once the coal is burnt, base and generator together hold what it produced.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "generator_push", timeoutTicks = TIMEOUT_TICKS)
    public void pushesEnergyIntoANeighbouringBase(GameTestHelper helper) {
        helper.setBlock(BASE, ModBlocks.TURRET_BASES.get(0));
        TurretBaseBlockEntity base = (TurretBaseBlockEntity) helper.getBlockEntity(BASE);
        // a base without owner drops itself on its next tick
        base.getTrustManager().setOwner(new PlayerIdentity(UUID.randomUUID(), "GameTestOwner"));
        helper.setBlock(GENERATOR, ModBlocks.GENERATOR);
        GeneratorBlockEntity generator = (GeneratorBlockEntity) helper.getBlockEntity(GENERATOR);
        generator.getFuelContainer().setItem(0, new ItemStack(Items.COAL));
        generator.getUpgradeContainer().setItem(0, new ItemStack(ModItems.UPGRADE_ENERGY_MULTIPLIER, 4));
        long produced = (long) COAL_TICKS * OMTConfig.get().miscellaneous.generatorEnergyPerTick;
        helper.succeedWhen(() -> {
            helper.assertTrue(generator.getBurnTime() == 0 && generator.getFuelContainer().getItem(0).isEmpty(),
                    "The coal is still burning");
            long inBase = base.getLogic().getEnergy().getAmount();
            helper.assertTrue(inBase > 0, "The base received no energy");
            helper.assertTrue(inBase + generator.getEnergy() == produced,
                    "Base " + inBase + " + generator " + generator.getEnergy() + " RF instead of " + produced);
            helper.setBlock(GENERATOR, Blocks.AIR);
            helper.setBlock(BASE, Blocks.AIR);
        });
    }

    /** Burning a lava bucket leaves the empty bucket in its slot, like a furnace. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "generator_remainder", timeoutTicks = TIMEOUT_TICKS)
    public void burntLavaBucketLeavesTheBucket(GameTestHelper helper) {
        helper.setBlock(GENERATOR, ModBlocks.GENERATOR);
        GeneratorBlockEntity generator = (GeneratorBlockEntity) helper.getBlockEntity(GENERATOR);
        generator.getFuelContainer().setItem(0, new ItemStack(Items.LAVA_BUCKET));
        helper.succeedWhen(() -> {
            helper.assertTrue(generator.getBurnTime() > 0, "The lava bucket is not burning");
            helper.assertTrue(generator.getFuelContainer().getItem(0).is(Items.BUCKET),
                    "The slot holds " + generator.getFuelContainer().getItem(0) + " instead of the empty bucket");
            generator.getFuelContainer().clearContent();
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
