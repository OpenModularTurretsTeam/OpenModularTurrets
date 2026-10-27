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
//? if <1.21.11 {
import net.minecraft.gametest.framework.GameTest;
//?}
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;

/**
 * The solar panel addon charges its base in daylight under open sky (OMLib's {@code VersionCompat.isDay}), and not
 * with a block above it. The test server's weather is always clear and its time stands still in daylight.
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class SolarPanelGameTests implements OMTGameTests {
    private static final BlockPos BASE = new BlockPos(1, 1, 1);
    /** Long enough for several charges: the base sets its energy limits every 5 ticks. */
    private static final int WATCH_TICKS = 20;

    //? if fabric || neoforge {
    /** Not on Forge: its game test server places the tests underground. */
    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 40)
    public void solarPanelChargesUnderTheSky(GameTestHelper helper) {
        TurretBaseBlockEntity base = solarBase(helper);
        helper.succeedWhen(() -> helper.assertTrue(energy(base) > 0, "The solar panel did not charge the base"));
    }
    //?}

    @GameTest(template = EMPTY_STRUCTURE)
    public void solarPanelNeedsTheSky(GameTestHelper helper) {
        helper.setBlock(BASE.above(3), Blocks.STONE);
        TurretBaseBlockEntity base = solarBase(helper);
        helper.runAfterDelay(WATCH_TICKS, () -> {
            helper.assertTrue(energy(base) == 0, "The solar panel charged the base under a roof: " + energy(base));
            helper.succeed();
        });
    }

    /** An empty tier 2 base (the first with addon slots) with a solar panel addon. */
    private static TurretBaseBlockEntity solarBase(GameTestHelper helper) {
        helper.setBlock(BASE, ModBlocks.TURRET_BASES.get(1));
        TurretBaseBlockEntity base = (TurretBaseBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(BASE));
        base.getInventory().setItem(TurretBaseInventory.ADDON_START, new ItemStack(ModItems.ADDON_SOLAR_PANEL));
        return base;
    }

    private static long energy(TurretBaseBlockEntity base) {
        return base.getLogic().getEnergy().getAmount();
    }
}
