package omtteam.openmodularturrets.gametest;

//? if neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if neoforge {
/*import omtteam.openmodularturrets.OMT;
*///?}
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import omtteam.omlib.core.energy.EnergyAccess;
import omtteam.omlib.platform.Platform;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModItems;

/**
 * The effect turrets act on their target without a projectile or ray, so a "hit" is their effect. They cannot miss,
 * so a single attempt is enough. Every test has its own batch: batches run one after another, so a turret never
 * reaches the target of a test running next to it.
 */
//? if neoforge {
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

    /** Issue 324: the repelling turret pushes a target 5 blocks in front further away from it. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "repelling_turret", timeoutTicks = TIMEOUT_TICKS)
    public void repellingTurretPushesTargetAway(GameTestHelper helper) {
        TurretScene.build(helper, TurretType.REPELLING_TURRET, TIMEOUT_TICKS);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        double start = target.getX();

        TurretScene.succeedWhen(helper, () -> helper.assertTrue(target.getX() > start + 1.0D,
                "Target at x " + target.getX() + " was not pushed away from x " + start));
    }

    /** Issue 324: the tractor turret pulls a target 5 blocks in front towards it. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "tractor_turret", timeoutTicks = TIMEOUT_TICKS)
    public void tractorTurretPullsTargetIn(GameTestHelper helper) {
        TurretScene.build(helper, TurretType.TRACTOR_TURRET, TIMEOUT_TICKS);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        double start = target.getX();

        TurretScene.succeedWhen(helper, () -> helper.assertTrue(target.getX() < start - 1.0D,
                "Target at x " + target.getX() + " was not pulled in from x " + start));
    }

    /**
     * Issue 324: the energy drain turret takes energy from a player's armour first ({@link TestBattery} in the head
     * slot; another in the main hand), but not from the rest of the inventory.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "energy_drain_turret", timeoutTicks = TIMEOUT_TICKS)
    public void energyDrainTurretDrainsArmourAndHeldItems(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, TurretType.ENERGY_DRAIN_TURRET, TIMEOUT_TICKS);
        base.getLogic().setTargetPlayers(true);
        ServerPlayer player = playerWithBatteries(helper, HEAD_SLOT, 0, STORED_SLOT);
        long full = TestBattery.CAPACITY;

        TurretScene.succeedWhen(helper, () -> {
            // armour is drained first
            helper.assertTrue(energy(player, HEAD_SLOT) < full, "Nothing was drained from the helmet");
            helper.assertTrue(energy(player, STORED_SLOT) == full, "A battery in the inventory was drained");
            removePlayer(helper, player);
        });
    }

    /**
     * Issue 324: a player whose armour and held items hold no energy is not targeted, even with a charged battery
     * elsewhere in the inventory; the turret does not fire (its base keeps its energy).
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "energy_drain_turret_skip", timeoutTicks = TIMEOUT_TICKS)
    public void energyDrainTurretSkipsPlayersWithoutEnergy(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, TurretType.ENERGY_DRAIN_TURRET, TIMEOUT_TICKS);
        base.getLogic().setTargetPlayers(true);
        ServerPlayer player = playerWithBatteries(helper, STORED_SLOT);
        long baseEnergy = base.getLogic().getEnergy().getAmount();

        TurretScene.onEachTick(helper, () -> helper.assertTrue(!base.getLogic().getTargetIds().containsValue(player.getId()),
                "A player without energy in armour or hands was targeted"));
        helper.runAfterDelay(SKIP_CHECK_TICKS, () -> {
            helper.assertTrue(base.getLogic().getEnergy().getAmount() >= baseEnergy, "The turret fired");
            helper.assertTrue(energy(player, STORED_SLOT) == TestBattery.CAPACITY, "A battery in the inventory was drained");
            removePlayer(helper, player);
            helper.succeed();
        });
    }

    /**
     * Issue 324: the energy drain turret never targets mobs, even a hostile one holding a charged battery while the
     * base attacks mobs and players; the turret does not fire and the battery keeps its energy.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "energy_drain_turret_mob", timeoutTicks = TIMEOUT_TICKS)
    public void energyDrainTurretIgnoresMobs(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, TurretType.ENERGY_DRAIN_TURRET, TIMEOUT_TICKS);
        base.getLogic().setTargetMobs(true);
        base.getLogic().setTargetPlayers(true);
        LivingEntity mob = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        mob.setItemSlot(EquipmentSlot.MAINHAND, chargedBattery());
        long baseEnergy = base.getLogic().getEnergy().getAmount();

        TurretScene.onEachTick(helper, () -> helper.assertTrue(!base.getLogic().getTargetIds().containsValue(mob.getId()),
                "A mob was targeted"));
        helper.runAfterDelay(SKIP_CHECK_TICKS, () -> {
            helper.assertTrue(base.getLogic().getEnergy().getAmount() >= baseEnergy, "The turret fired");
            helper.assertTrue(energy(mob.getMainHandItem()) == TestBattery.CAPACITY, "The mob's battery was drained");
            TurretScene.succeed(helper);
        });
    }

    /**
     * Issue 324: a player holding the ownership wand is targeted and fired at, but the drain leaves their charged
     * helmet alone (the wand immunity of the effect weapons).
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "energy_drain_turret_wand", timeoutTicks = TIMEOUT_TICKS)
    public void energyDrainTurretSparesWandHolder(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, TurretType.ENERGY_DRAIN_TURRET, TIMEOUT_TICKS);
        base.getLogic().setTargetPlayers(true);
        ServerPlayer player = playerWithBatteries(helper, HEAD_SLOT);
        player.getInventory().setItem(0, new ItemStack(ModItems.OWNERSHIP_WAND));
        long baseEnergy = base.getLogic().getEnergy().getAmount();

        TurretScene.succeedWhen(helper, () -> {
            helper.assertTrue(base.getLogic().getTargetIds().containsValue(player.getId()), "The wand holder is not targeted");
            helper.assertTrue(base.getLogic().getEnergy().getAmount() < baseEnergy, "The turret has not fired yet");
            helper.assertTrue(energy(player, HEAD_SLOT) == TestBattery.CAPACITY, "The wand holder's helmet was drained");
            removePlayer(helper, player);
        });
    }

    /** Head armour slot of the player inventory (36 feet ... 39 head). */
    private static final int HEAD_SLOT = Inventory.INVENTORY_SIZE + 3;
    /** A main inventory slot that the drain turret leaves alone. */
    private static final int STORED_SLOT = 20;
    /** The skip test waits this long, several target searches and shots. */
    private static final int SKIP_CHECK_TICKS = 100;

    /** A player 5 blocks in front of the turret with a full {@link TestBattery} in each of the given slots. */
    private static ServerPlayer playerWithBatteries(GameTestHelper helper, int... slots) {
        ServerPlayer player = GuiTestPlayer.create(helper, "Drained");
        Inventory inventory = player.getInventory();
        inventory.selected = 0;
        for (int slot : slots) {
            inventory.setItem(slot, new ItemStack(TestBattery.ITEM));
            EnergyAccess battery = Platform.energy().itemEnergy(inventory, slot)
                    .orElseThrow(() -> new IllegalStateException("The test battery has no energy"));
            battery.insert(TestBattery.CAPACITY, false);
        }
        BlockPos pos = helper.absolutePos(TurretScene.TARGET_IN_FRONT);
        player.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        helper.getLevel().addNewPlayer(player);
        helper.runAfterDelay(TIMEOUT_TICKS - 1, () -> removePlayer(helper, player));
        return player;
    }

    /** A {@link TestBattery} filled up in a container of its own, so it can be handed to a mob. */
    private static ItemStack chargedBattery() {
        SimpleContainer container = new SimpleContainer(new ItemStack(TestBattery.ITEM));
        Platform.energy().itemEnergy(container, 0).orElseThrow(() -> new IllegalStateException("The test battery has no energy"))
                .insert(TestBattery.CAPACITY, false);
        return container.getItem(0);
    }

    private static long energy(ItemStack stack) {
        return Platform.energy().itemEnergy(new SimpleContainer(stack), 0).map(EnergyAccess::getAmount).orElse(0L);
    }

    private static long energy(ServerPlayer player, int slot) {
        return Platform.energy().itemEnergy(player.getInventory(), slot).map(EnergyAccess::getAmount).orElse(0L);
    }

    private static void removePlayer(GameTestHelper helper, ServerPlayer player) {
        if (!player.isRemoved()) helper.getLevel().removePlayerImmediately(player, Entity.RemovalReason.DISCARDED);
        TurretScene.clear(helper);
    }
}
