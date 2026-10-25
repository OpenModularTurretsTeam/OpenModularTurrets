package omtteam.openmodularturrets.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.block.base.TurretBaseBlock;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.config.OMTConfig;

/**
 * Which OMT blocks other mods' contraptions may move although they are unbreakable: turret bases and turrets, while
 * {@code bases.allowContraptionMovement} is on. Asked by the loader hooks into those mods (Create's movement checks,
 * Simulated's Physics Assembler on NeoForge).
 */
public final class ContraptionMovement {
    private ContraptionMovement() {
    }

    public static boolean mayMove(BlockState state) {
        Block block = state.getBlock();
        return (block instanceof TurretBaseBlock || block instanceof TurretBlock)
                && OMTConfig.get().bases.allowContraptionMovement;
    }
}
