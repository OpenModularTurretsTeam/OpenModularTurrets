package omtteam.openmodularturrets.platform.neoforge;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.block.ContraptionMovement;

/**
 * Lets Create's contraptions (bearings, pistons, pulleys, gantries, minecart and train contraptions) move turret bases
 * and turrets. Create refuses unbreakable blocks and blocks pistons cannot push, which both are; with
 * {@code bases.allowContraptionMovement} this check allows them before Create's own rules run. Only loaded when Create
 * is installed (Create is compile-only, never bundled). Simulated's Physics Assembler has its own rule, see
 * {@link omtteam.openmodularturrets.platform.neoforge.mixin.SimAssemblyContraptionMixin}.
 */
public final class NeoForgeCreateMovement {
    public static final String CREATE_MOD_ID = "create";

    private NeoForgeCreateMovement() {
    }

    /** Registers the check with Create; called once at common setup. */
    public static void register() {
        BlockMovementChecks.registerMovementAllowedCheck(NeoForgeCreateMovement::isMovementAllowed);
    }

    /** Allows bases and turrets while the config permits it ({@link ContraptionMovement}); everything else is left to Create. */
    static BlockMovementChecks.CheckResult isMovementAllowed(BlockState state, Level level, BlockPos pos) {
        return ContraptionMovement.mayMove(state) ? BlockMovementChecks.CheckResult.SUCCESS : BlockMovementChecks.CheckResult.PASS;
    }
}
