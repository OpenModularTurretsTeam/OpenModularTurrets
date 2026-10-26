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
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import omtteam.openmodularturrets.camo.CamoRules;
import omtteam.openmodularturrets.registry.ModBlocks;

/**
 * Which blocks may be used as a disguise: full blocks without a block entity, and hardened blocks, whose block entity
 * only holds the owner.
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class CamoGameTests implements OMTGameTests {
    @GameTest(template = EMPTY_STRUCTURE)
    public void hardenedBlocksAreValidDisguises(GameTestHelper helper) {
        assertDisguise(helper, Blocks.STONE, true);
        assertDisguise(helper, Blocks.GLASS, true);
        ModBlocks.HARDENED.forEach(block -> assertDisguise(helper, block, true));
        assertDisguise(helper, Blocks.FURNACE, false);
        assertDisguise(helper, ModBlocks.TURRET_BASES.get(0), false);
        assertDisguise(helper, ModBlocks.CAMO_TRAP, false);
        helper.succeed();
    }

    private static void assertDisguise(GameTestHelper helper, Block block, boolean valid) {
        helper.assertTrue(CamoRules.isValidDisguise(block.defaultBlockState()) == valid,
                block + (valid ? " is not a valid disguise" : " is a valid disguise"));
    }
}
