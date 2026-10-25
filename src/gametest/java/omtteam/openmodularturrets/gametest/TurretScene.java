package omtteam.openmodularturrets.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import omtteam.openmodularturrets.api.AmmoDefinition;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.core.base.MachineMode;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.TurretRegistry;

import java.util.UUID;

/**
 * Builds the shared test scene in the 8×8×8 empty structure: a stone floor, a turret base of the turret's tier at
 * {@link #BASE} and the turret on top of it at {@link #TURRET}, ready to fire (owner set, always on, full energy,
 * maximum range, a stack of the turret's ammo).
 * <p>
 * 1.20.1 places the batches next to each other and keeps their blocks and entities, so a finished test's turret
 * would still shoot at the next test's target, and its target would be picked by the next test's turret. Tests
 * therefore end with {@link #succeedWhen} or {@link #succeed}, which clear the scene once the test passed. A failing
 * test clears it too: {@link #onEachTick} clears it when a check throws, and {@link #build} clears it one tick before
 * the test times out.
 */
@SuppressWarnings("RedundantCast")
public final class TurretScene {
    public static final BlockPos BASE = new BlockPos(1, 1, 1);
    public static final BlockPos TURRET = BASE.above();
    /** 5 blocks in front of the turret, standing on a block so the eyes are above the turret and in sight. */
    public static final BlockPos TARGET_IN_FRONT = TURRET.east(5);

    private static final PlayerIdentity OWNER = new PlayerIdentity(UUID.randomUUID(), "GameTestOwner");
    /**
     * Chunks around the scene that are force-loaded while a test runs. The test server does not tick entities in the
     * chunks next to a test, and projectiles remove themselves before flying into such a chunk, so a grenade that
     * lands just past the target would vanish instead of exploding next to it.
     */
    private static final int FORCED_CHUNK_RADIUS = 2;

    private TurretScene() {
    }

    /** Builds the scene and clears it one tick before {@code timeoutTicks} (the test's timeout). */
    public static TurretBaseBlockEntity build(GameTestHelper helper, TurretType type, int timeoutTicks) {
        removeEntities(helper);
        forceChunks(helper, true);
        helper.runAfterDelay(timeoutTicks - 1, () -> clear(helper));
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
            }
        }
        helper.setBlock(BASE, ModBlocks.TURRET_BASES.get(type.getRequiredBaseTier() - 1));
        helper.setBlock(TURRET, ModBlocks.TURRETS.get(type));
        TurretBaseBlockEntity base = (TurretBaseBlockEntity) helper.getBlockEntity(BASE);
        // a base without owner does not fire
        base.getTrustManager().setOwner(OWNER);
        base.onNeighbourChanged();
        TurretBaseLogic logic = base.getLogic();
        logic.setMode(MachineMode.ALWAYS_ON);
        logic.getEnergy().generate(logic.getEnergy().getCapacity());
        logic.updateMaxRange();
        logic.setRange(logic.getTargetingSettings().getMaxRange());
        type.getAmmo().ifPresent(ammo -> base.getInventory().setItem(TurretBaseInventory.AMMO_START, ammoStack(ammo)));
        return base;
    }

    /** A full stack of the ammo's {@linkplain AmmoDefinition#sample() sample}. */
    private static ItemStack ammoStack(AmmoType ammo) {
        ItemStack stack = TurretRegistry.ammo(ammo).orElseThrow(() -> new IllegalStateException("No ammo definition for " + ammo))
                .sample().get();
        stack.setCount(stack.getMaxStackSize());
        return stack;
    }

    /** {@link GameTestHelper#succeedWhen}, then the scene is {@linkplain #clear cleared}. */
    public static void succeedWhen(GameTestHelper helper, Runnable check) {
        helper.succeedWhen(() -> {
            check.run();
            clear(helper);
        });
    }

    /** {@link GameTestHelper#onEachTick}, clearing the scene when {@code check} fails the test. */
    public static void onEachTick(GameTestHelper helper, Runnable check) {
        helper.onEachTick(() -> {
            try {
                check.run();
            } catch (RuntimeException e) {
                clear(helper);
                throw e;
            }
        });
    }

    /** Clears the scene and lets the test pass right away. */
    public static void succeed(GameTestHelper helper) {
        clear(helper);
        helper.succeed();
    }

    /** Removes the turret, its base and all entities of the scene, and releases the forced chunks. */
    public static void clear(GameTestHelper helper) {
        helper.setBlock(TURRET, Blocks.AIR);
        helper.setBlock(BASE, Blocks.AIR);
        removeEntities(helper);
        forceChunks(helper, false);
    }

    /** Force-loads (or releases) the chunks within {@link #FORCED_CHUNK_RADIUS} of the turret, so their entities tick. */
    private static void forceChunks(GameTestHelper helper, boolean forced) {
        BlockPos turret = helper.absolutePos(TURRET);
        int centreX = turret.getX() >> 4;
        int centreZ = turret.getZ() >> 4;
        for (int x = centreX - FORCED_CHUNK_RADIUS; x <= centreX + FORCED_CHUNK_RADIUS; x++) {
            for (int z = centreZ - FORCED_CHUNK_RADIUS; z <= centreZ + FORCED_CHUNK_RADIUS; z++) {
                helper.getLevel().setChunkForced(x, z, forced);
            }
        }
    }

    /** Removes all entities but players in the scene (without death animation or drops). */
    private static void removeEntities(GameTestHelper helper) {
        AABB area = new AABB(helper.absolutePos(BlockPos.ZERO)).minmax(new AABB(helper.absolutePos(new BlockPos(7, 7, 7))))
                .inflate(1.0D);
        for (Entity entity : helper.getLevel().getEntitiesOfClass(Entity.class, area, entity -> !(entity instanceof Player))) {
            entity.discard();
        }
    }

    /**
     * A husk (hostile, does not burn in daylight) without AI at {@code pos}, on a stone block if {@code pos} is above
     * the floor.
     */
    public static LivingEntity spawnTarget(GameTestHelper helper, BlockPos pos) {
        if (pos.getY() > 1) {
            helper.setBlock(pos.below(), Blocks.STONE);
        }
        return helper.spawnWithNoFreeWill(EntityType.HUSK, pos);
    }
}
