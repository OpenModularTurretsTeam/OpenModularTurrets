package omtteam.openmodularturrets.gametest.platform.neoforge;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.gametest.OMTGameTests;
import omtteam.openmodularturrets.gametest.TurretScene;

/**
 * Create's contraptions (and Simulated's, which Create Aeronautics uses) may move turret bases and turrets while
 * {@code bases.allowContraptionMovement} is on, although Create refuses unbreakable blocks that pistons cannot push
 * (NeoForge only: Create is on the NeoForge GameTest server).
 */
@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
@SuppressWarnings("unused")
public class CreateMovementGameTests implements OMTGameTests {

    @GameTest(template = EMPTY_STRUCTURE, batch = "create_movement")
    public void contraptionsMayMoveBasesAndTurretsWhenAllowed(GameTestHelper helper) {
        TurretScene.build(helper, TurretType.MACHINE_GUN_TURRET, 100);
        ConfigData.Bases bases = OMTConfig.get().bases;
        boolean configured = bases.allowContraptionMovement;
        try {
            bases.allowContraptionMovement = true;
            helper.assertTrue(movable(helper, TurretScene.BASE), "Create may not move the turret base");
            helper.assertTrue(movable(helper, TurretScene.TURRET), "Create may not move the turret");
            bases.allowContraptionMovement = false;
            helper.assertFalse(movable(helper, TurretScene.BASE), "Create may move the turret base although the config forbids it");
            helper.assertFalse(movable(helper, TurretScene.TURRET), "Create may move the turret although the config forbids it");
        } finally {
            bases.allowContraptionMovement = configured;
        }
        TurretScene.succeed(helper);
    }

    private static boolean movable(GameTestHelper helper, BlockPos pos) {
        BlockPos absolute = helper.absolutePos(pos);
        return BlockMovementChecks.isMovementAllowed(helper.getLevel().getBlockState(absolute), helper.getLevel(), absolute);
    }
}
