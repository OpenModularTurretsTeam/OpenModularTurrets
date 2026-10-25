package omtteam.openmodularturrets.gametest;

//? if neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if forge {
/*import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
*///?}
//? if neoforge || forge {
/*import omtteam.openmodularturrets.OMT;
*///?}
import com.mojang.authlib.GameProfile;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.core.base.TurretPlacement;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.security.PlayerAdapter;

import java.util.UUID;

/**
 * Bases without an owner: they stay in the world, do not fire (unless {@code bases.unownedBasesActive} is set) and
 * take no turrets from players who did not claim them.
 */
//? if neoforge || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class OwnershipGameTests implements OMTGameTests {
    private static final int TIMEOUT_TICKS = 100;
    /** Long enough for the laser turret to find the target and fire several times. */
    private static final int WATCH_TICKS = 60;
    private static final TurretType TYPE = TurretType.LASER_TURRET;

    /** A base without owner, ready to fire, neither drops itself nor shoots a target in front of its turret. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_unowned_inert", timeoutTicks = TIMEOUT_TICKS)
    public void unownedBaseStaysAndDoesNotFire(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, TYPE, TIMEOUT_TICKS);
        base.getTrustManager().clearOwner();
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);

        helper.runAfterDelay(WATCH_TICKS, () -> {
            try {
                helper.assertBlockPresent(ModBlocks.TURRET_BASES.get(TYPE.getRequiredBaseTier() - 1), TurretScene.BASE);
                helper.assertTrue(target.getHealth() == target.getMaxHealth(), "An unowned base hurt the target");
            } finally {
                TurretScene.clear(helper);
            }
            helper.succeed();
        });
    }

    /** Only players who may use game master blocks attach turrets to an unowned base; others claim it first. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_unowned_attach", timeoutTicks = TIMEOUT_TICKS)
    public void unownedBaseTakesNoTurretsFromStrangers(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, TYPE, TIMEOUT_TICKS);
        base.getTrustManager().clearOwner();
        ServerPlayer player = VersionCompat.fakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "Claimer"));
        TurretBlock turret = ModBlocks.TURRETS.get(TYPE);
        Direction facing = Direction.EAST;

        TurretPlacement.Result unowned = turret.attachResult(helper.getLevel(),
                helper.absolutePos(TurretScene.BASE.relative(facing)), facing, player);
        helper.assertTrue(unowned == TurretPlacement.Result.NO_PERMISSION, "Attaching to an unowned base gave " + unowned);

        base.getTrustManager().claim(PlayerAdapter.identity(player));
        TurretPlacement.Result claimed = turret.attachResult(helper.getLevel(),
                helper.absolutePos(TurretScene.BASE.relative(facing)), facing, player);
        helper.assertTrue(claimed != null && claimed.allowed(), "Attaching after claiming gave " + claimed);
        TurretScene.succeed(helper);
    }

}
