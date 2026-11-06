package omtteam.openmodularturrets.gametest.platform.fabric;

import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.omlib.core.energy.EnergyStore;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.gametest.OMTGameTests;
import omtteam.openmodularturrets.registry.ModBlocks;

/**
 * NEEPMeat (Fabric 1.20.1 only): its vascular network charges turret bases at 1 eJ = 1 E (OMLib's
 * {@code FabricNeepMeatPower}), a base taking only what its energy still accepts. Only this node has NEEPMeat on the
 * game test server; the other Fabric nodes leave the class out of the test mod's entrypoints.
 */
@SuppressWarnings("unused")
public class NeepMeatGameTests implements OMTGameTests {
    private static final BlockPos EMITTER = new BlockPos(1, 1, 1);
    private static final BlockPos CONDUIT = EMITTER.east();
    private static final BlockPos BASE = CONDUIT.east();
    /** Power of the emitter in the network test, eJ per tick (below the tier three base's 1000 E/t intake). */
    private static final int EMITTER_POWER = 400;
    /** Tier one base: its energy accepts 50 E per tick and holds 5000 (config defaults). */
    private static final long TIER_ONE_INTAKE = 50;

    /**
     * Power emitter, vascular conduit, tier three base: the conduit finds the base as a sink when placed, and the base
     * is charged with the emitter's whole output, in whole ticks of it.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "neepmeat_network", timeoutTicks = 200)
    public void vascularNetworkChargesABase(GameTestHelper helper) {
        helper.setBlock(EMITTER, block("power_emitter"));
        // its saved power (the block entity's class extends one of a library nested in NEEPMeat's jar, not on the
        // compile classpath)
        CompoundTag power = new CompoundTag();
        power.putInt("power", EMITTER_POWER);
        helper.getBlockEntity(EMITTER).load(power);
        helper.setBlock(BASE, ModBlocks.TURRET_BASES.get(2));
        EnergyStore energy = ((TurretBaseBlockEntity) helper.getBlockEntity(BASE)).getLogic().getEnergy();
        placeAsPlayer(helper, CONDUIT, block("vascular_conduit"));
        helper.succeedWhen(() -> {
            long amount = energy.getAmount();
            helper.assertTrue(amount >= EMITTER_POWER * 20L, "The base holds only " + amount + " E");
            helper.assertTrue(amount % EMITTER_POWER == 0,
                    "The base holds " + amount + " E, not whole ticks of " + EMITTER_POWER + " eJ");
            clear(helper);
        });
    }

    /**
     * The base as a sink: a plain (not active) sink that claims only its energy's intake per tick, charges that much
     * each tick, asks the network for a new share when it is full and then claims nothing.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "neepmeat_sink", timeoutTicks = 300)
    public void baseClaimsOnlyWhatItsEnergyAccepts(GameTestHelper helper) {
        helper.setBlock(BASE, ModBlocks.TURRET_BASES.get(0));
        EnergyStore energy = ((TurretBaseBlockEntity) helper.getBlockEntity(BASE)).getLogic().getEnergy();
        BloodAcceptor sink = BloodAcceptor.SIDED.find(helper.getLevel(), helper.absolutePos(BASE), Direction.WEST);
        helper.assertTrue(sink != null, "The base is no sink of a vascular network");
        helper.assertTrue(sink.getMode() == BloodAcceptor.Mode.SINK, "The base is a " + sink.getMode() + " sink");
        int[] updates = {0};
        sink.setChangeListener(changed -> updates[0]++);
        helper.assertTrue(sink.getDemandHint() == TIER_ONE_INTAKE, "The base asks for " + sink.getDemandHint() + " eJ/t");
        // 1 per unit = 1000 eJ/t on offer: the base takes its 50
        float taken = sink.updateInflux(1f);
        helper.assertTrue(Math.round(taken * 1000) == TIER_ONE_INTAKE, "The base took " + taken * 1000 + " eJ/t of 1000");
        long start = helper.getTick();
        helper.succeedWhen(() -> {
            helper.assertTrue(energy.getFreeSpace() == 0, "The base is not full yet (" + energy.getAmount() + " E)");
            long ticks = helper.getTick() - start;
            long expected = energy.getCapacity() / TIER_ONE_INTAKE;
            helper.assertTrue(Math.abs(ticks - expected) <= 2, "Filling took " + ticks + " ticks, not " + expected);
            helper.assertTrue(updates[0] > 0, "The full base did not ask the network for a new share");
            helper.assertTrue(sink.getDemandHint() == 0, "The full base still asks for " + sink.getDemandHint() + " eJ/t");
            helper.assertTrue(sink.updateInflux(1f) == 0f, "The full base still takes power");
            sink.setChangeListener(null);
            clear(helper);
        });
    }

    private static Block block(String id) {
        return BuiltInRegistries.BLOCK.get(new ResourceLocation("neepmeat", id));
    }

    /** Places a block like a player does: its state from its neighbours, then its placement callback. */
    private static void placeAsPlayer(GameTestHelper helper, BlockPos pos, Block block) {
        BlockPos absolute = helper.absolutePos(pos);
        BlockState state = Block.updateFromNeighbourShapes(block.defaultBlockState(), helper.getLevel(), absolute);
        helper.getLevel().setBlockAndUpdate(absolute, state);
        block.setPlacedBy(helper.getLevel(), absolute, state, null, ItemStack.EMPTY);
    }

    private static void clear(GameTestHelper helper) {
        helper.setBlock(CONDUIT, Blocks.AIR);
        helper.setBlock(EMITTER, Blocks.AIR);
        helper.setBlock(BASE, Blocks.AIR);
    }
}
