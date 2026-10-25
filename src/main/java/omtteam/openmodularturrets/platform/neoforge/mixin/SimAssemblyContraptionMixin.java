package omtteam.openmodularturrets.platform.neoforge.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.block.ContraptionMovement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Simulated's Physics Assembler (Create Aeronautics) only moves blocks that are not unbreakable and ignores Create's
 * movement checks, so it refuses turret bases and turrets. Until Simulated offers a hook, this lets it move them while
 * {@code bases.allowContraptionMovement} is on ({@link ContraptionMovement}).
 * <p>
 * {@link Pseudo}: the target is named by string only, so OMT needs no Simulated at compile time, and without Simulated
 * (or if the method is renamed) the mixin is skipped ({@code require = 0}; its config is not required).
 */
@Pseudo
@Mixin(targets = "dev.simulated_team.simulated.util.assembly.SimAssemblyContraption")
public abstract class SimAssemblyContraptionMixin {
    @Inject(method = "movementAllowed", at = @At("HEAD"), cancellable = true, require = 0)
    private void openmodularturrets$allowTurrets(BlockState state, Level level, BlockPos pos,
                                                 CallbackInfoReturnable<Boolean> cir) {
        if (ContraptionMovement.mayMove(state)) {
            cir.setReturnValue(true);
        }
    }
}
