package omtteam.openmodularturrets.gametest.platform.neoforge;

import net.minecraft.core.BlockPos;
//? if <1.21.11 {
import net.minecraft.gametest.framework.GameTest;
//?}
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.gametest.OMTGameTests;
import omtteam.openmodularturrets.gametest.TurretScene;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Simulated's Physics Assembler (Create Aeronautics) may move turret bases and turrets while
 * {@code bases.allowContraptionMovement} is on ({@code SimAssemblyContraptionMixin}). Asks the assembler's own
 * {@code movementAllowed} by reflection, as OMT does not compile against Simulated; the test fails if Simulated renames
 * the class or method the mixin hooks (NeoForge only: Create Aeronautics is on the NeoForge GameTest server).
 */
@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
@SuppressWarnings("unused")
public class SimulatedAssemblyGameTests implements OMTGameTests {
    private static final String ASSEMBLY = "dev.simulated_team.simulated.util.assembly.SimAssemblyContraption";

    @GameTest(template = EMPTY_STRUCTURE, batch = "simulated_assembly")
    public void physicsAssemblerMayMoveBasesAndTurretsWhenAllowed(GameTestHelper helper) throws ReflectiveOperationException {
        TurretScene.build(helper, TurretType.MACHINE_GUN_TURRET, 100);
        Class<?> type = Class.forName(ASSEMBLY);
        Constructor<?> constructor = type.getDeclaredConstructor(BlockPos.class, boolean.class);
        Object assembly = constructor.newInstance(helper.absolutePos(BlockPos.ZERO), false);
        Method movementAllowed = type.getDeclaredMethod("movementAllowed", BlockState.class, Level.class, BlockPos.class);
        movementAllowed.setAccessible(true);
        ConfigData.Bases bases = OMTConfig.get().bases;
        boolean configured = bases.allowContraptionMovement;
        try {
            bases.allowContraptionMovement = true;
            helper.assertTrue(allowed(helper, movementAllowed, assembly, TurretScene.BASE), "The Physics Assembler may not move the turret base");
            helper.assertTrue(allowed(helper, movementAllowed, assembly, TurretScene.TURRET), "The Physics Assembler may not move the turret");
            bases.allowContraptionMovement = false;
            helper.assertFalse(allowed(helper, movementAllowed, assembly, TurretScene.BASE),
                    "The Physics Assembler may move the turret base although the config forbids it");
            helper.assertTrue((boolean) movementAllowed.invoke(assembly, Blocks.STONE.defaultBlockState(), helper.getLevel(),
                    helper.absolutePos(TurretScene.BASE)), "The mixin changed the rule for other blocks");
        } finally {
            bases.allowContraptionMovement = configured;
        }
        TurretScene.succeed(helper);
    }

    private static boolean allowed(GameTestHelper helper, Method movementAllowed, Object assembly, BlockPos pos)
            throws ReflectiveOperationException {
        BlockPos absolute = helper.absolutePos(pos);
        return (boolean) movementAllowed.invoke(assembly, helper.getLevel().getBlockState(absolute), helper.getLevel(), absolute);
    }
}
