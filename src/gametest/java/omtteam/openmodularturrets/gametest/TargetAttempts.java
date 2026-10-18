package omtteam.openmodularturrets.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.LivingEntity;
import omtteam.openmodularturrets.core.turret.TurretType;

/**
 * Up to {@value #MAX_ATTEMPTS} attempts to hit a target, for turrets that scatter: each attempt has a fresh target
 * at the same place, a {@link #miss()} starts the next one, a {@link #hit()} passes the test and the test fails
 * once all attempts missed.
 */
final class TargetAttempts {
    static final int MAX_ATTEMPTS = 10;

    private final GameTestHelper helper;
    private final TurretType type;
    private final BlockPos targetPos;
    private LivingEntity target;
    private int attempt;

    TargetAttempts(GameTestHelper helper, TurretType type, BlockPos targetPos) {
        this.helper = helper;
        this.type = type;
        this.targetPos = targetPos;
        next();
    }

    /** The target of the current attempt. */
    LivingEntity target() {
        return target;
    }

    /** The number of the current attempt, starting at 1. */
    int attempt() {
        return attempt;
    }

    /** Whether the current target took damage. */
    boolean targetDamaged() {
        return target.getHealth() < target.getMaxHealth();
    }

    void hit() {
        TurretScene.succeed(helper);
    }

    void miss() {
        if (attempt >= MAX_ATTEMPTS) {
            helper.fail(type + " missed the target " + MAX_ATTEMPTS + " times");
        }
        next();
    }

    private void next() {
        if (target != null) target.discard();
        attempt++;
        target = TurretScene.spawnTarget(helper, targetPos);
    }
}
