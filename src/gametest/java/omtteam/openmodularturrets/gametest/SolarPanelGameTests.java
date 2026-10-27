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
 * with a block above it. The test server's weather is always clear, but its day passes (vanilla leaves the daylight
 * cycle on, and the server ticks as fast as it can, so a run reaches the night), so the tests set the time to noon.
 * {@code canSeeSky} reads the sky light, which the light engine computes later than the blocks change, so the tests
 * wait for it: a test far out in a new chunk may get its sky light late, and a roof darkens the base only after its
 * light update. The light engine runs on its own thread in real time, so while the test servers of all nodes run at
 * once it can be many ticks behind.
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
    /**
     * Room for a light engine that lags behind (a test that passes ends as soon as its condition holds, so only a
     * failing one waits this long); from {@link #NOON} it still ends in daylight.
     */
    private static final int TIMEOUT_TICKS = 2400;
    /** Day time of noon; the day ends at about 12000. */
    private static final long NOON = 6000L;

    //? if fabric || neoforge {
    /** Not on Forge: its game test server places the tests underground. */
    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = TIMEOUT_TICKS)
    public void solarPanelChargesUnderTheSky(GameTestHelper helper) {
        TurretBaseBlockEntity base = solarBase(helper);
        helper.succeedWhen(() -> {
            helper.assertTrue(helper.getLevel().canSeeSky(helper.absolutePos(BASE.above(2))),
                    "The light engine has not lit the base yet");
            helper.assertTrue(energy(base) > 0, "The solar panel did not charge the base");
        });
    }
    //?}

    /**
     * Once the light engine has seen the roof (until then the panel may still charge), the base is emptied and
     * watched for {@value #WATCH_TICKS} ticks.
     */
    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = TIMEOUT_TICKS)
    public void solarPanelNeedsTheSky(GameTestHelper helper) {
        helper.setBlock(BASE.above(3), Blocks.STONE);
        TurretBaseBlockEntity base = solarBase(helper);
        long[] roofSeenAt = {-1};
        long[] charged = {0};
        helper.succeedWhen(() -> {
            if (roofSeenAt[0] < 0) {
                helper.assertFalse(helper.getLevel().canSeeSky(helper.absolutePos(BASE.above(2))),
                        "The light engine has not seen the roof yet");
                base.getLogic().getEnergy().setAmountUnchecked(0);
                roofSeenAt[0] = helper.getTick();
            }
            charged[0] = Math.max(charged[0], energy(base));
            helper.assertTrue(charged[0] == 0, "The solar panel charged the base under a roof: " + charged[0]);
            helper.assertTrue(helper.getTick() >= roofSeenAt[0] + WATCH_TICKS, "Still watching the base");
        });
    }

    /** An empty tier 2 base (the first with addon slots) with a solar panel addon, at noon. */
    private static TurretBaseBlockEntity solarBase(GameTestHelper helper) {
        helper.getLevel().setDayTime(NOON);
        helper.setBlock(BASE, ModBlocks.TURRET_BASES.get(1));
        TurretBaseBlockEntity base = (TurretBaseBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(BASE));
        base.getInventory().setItem(TurretBaseInventory.ADDON_START, new ItemStack(ModItems.ADDON_SOLAR_PANEL));
        return base;
    }

    private static long energy(TurretBaseBlockEntity base) {
        return base.getLogic().getEnergy().getAmount();
    }
}
