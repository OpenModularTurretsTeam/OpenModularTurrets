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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.core.turret.TurretType;

/**
 * The effect turrets act on their target without a projectile or ray, so a "hit" is their effect. They cannot miss,
 * so a single attempt is enough. Every test has its own batch: batches run one after another, so a turret never
 * reaches the target of a test running next to it.
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class EffectTurretGameTests implements OMTGameTests {
    private static final int TIMEOUT_TICKS = 200;

    /** The slowing turret puts slowness on a target 5 blocks in front, which lowers its movement speed. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "slowing_turret", timeoutTicks = TIMEOUT_TICKS)
    public void slowingTurretSlowsTarget(GameTestHelper helper) {
        TurretScene.build(helper, TurretType.SLOWING_TURRET, TIMEOUT_TICKS);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        double normalSpeed = target.getAttributeValue(Attributes.MOVEMENT_SPEED);
        helper.assertTrue(!target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "Target is slowed before the turret fired");

        TurretScene.succeedWhen(helper, () -> {
            helper.assertTrue(target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "Target was not given slowness");
            double speed = target.getAttributeValue(Attributes.MOVEMENT_SPEED);
            helper.assertTrue(speed < normalSpeed, "Target speed " + speed + " is not below " + normalSpeed);
        });
    }

    /**
     * The teleporter turret pulls a target 5 blocks in front onto the block in front of the turret. Only the
     * horizontal position is checked: the target may sink into the turret block, which is not a full block.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "teleporter_turret", timeoutTicks = TIMEOUT_TICKS)
    public void teleporterTurretPullsTarget(GameTestHelper helper) {
        TurretScene.build(helper, TurretType.TELEPORTER_TURRET, TIMEOUT_TICKS);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        Vec3 destination = Vec3.atBottomCenterOf(helper.absolutePos(TurretScene.TURRET.above()));

        TurretScene.succeedWhen(helper, () -> {
            double distance = Math.hypot(target.getX() - destination.x, target.getZ() - destination.z);
            helper.assertTrue(distance < 0.5D, "Target at " + target.position() + " was not pulled to " + destination);
        });
    }

    /**
     * The melee turret only reaches the 3×2×3 area around itself and its base, so its target stands next to the
     * base instead of 5 blocks away.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "melee_turret", timeoutTicks = TIMEOUT_TICKS)
    public void meleeTurretDamagesTargetInReach(GameTestHelper helper) {
        TurretScene.build(helper, TurretType.MELEE_TURRET, TIMEOUT_TICKS);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.BASE.east());
        float maxHealth = target.getMaxHealth();

        TurretScene.succeedWhen(helper, () -> helper.assertTrue(target.getHealth() < maxHealth,
                "Target was not damaged (health " + target.getHealth() + " of " + maxHealth + ")"));
    }
}
