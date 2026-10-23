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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omtteam.openmodularturrets.api.OMTApi;
import omtteam.openmodularturrets.api.TurretDefinition;
import omtteam.openmodularturrets.api.weapon.AreaWeapon;
import omtteam.openmodularturrets.api.weapon.EffectWeapon;
import omtteam.openmodularturrets.api.weapon.ProjectileWeapon;
import omtteam.openmodularturrets.api.weapon.RayWeapon;
import omtteam.openmodularturrets.api.weapon.SelfTargetingWeapon;
import omtteam.openmodularturrets.api.weapon.TurretWeapon;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.config.TurretSetting;
import omtteam.openmodularturrets.core.energy.EnergyStore;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.inventory.AmmoRules;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;

import java.util.Map;

/**
 * The turrets of {@link ApiTestTurrets}, one per weapon kind, added through the turret API like another mod would:
 * they are registered with their ids and config entries, and each runs its custom code at the right time, and only
 * then. Every test has its own batch, so no turret reaches the target of another test.
 */
//? if neoforge || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
public class ApiTurretGameTests implements OMTGameTests {
    private static final int TIMEOUT_TICKS = 400;
    /** Ticks a test waits to see that a turret does nothing (several cooldowns and target scans). */
    private static final int QUIET_TICKS = 100;

    /** Weapon kind and default stats of every API turret. */
    private static final Map<TurretType, Expected> EXPECTED = Map.of(
            ApiTestTurrets.API_RAY_TURRET, new Expected(RayWeapon.class, ApiTestTurrets.API_RAY_TURRET_STATS),
            ApiTestTurrets.API_PROJECTILE_TURRET, new Expected(ProjectileWeapon.class, ApiTestTurrets.CUSTOM_LOGIC_STATS),
            ApiTestTurrets.API_EFFECT_TURRET, new Expected(EffectWeapon.class, ApiTestTurrets.CUSTOM_LOGIC_STATS),
            ApiTestTurrets.API_AREA_TURRET, new Expected(AreaWeapon.class, ApiTestTurrets.CUSTOM_LOGIC_STATS),
            ApiTestTurrets.API_SELF_TARGETING_TURRET,
            new Expected(SelfTargetingWeapon.class, ApiTestTurrets.CUSTOM_LOGIC_STATS));

    private record Expected(Class<? extends TurretWeapon> weapon, TurretStats stats) {
    }

    // ---- Registration ----

    /**
     * Every API turret has a block and item with its id, a definition with its weapon, and a config entry with its
     * default stats that its stats are read from. The plugin's projectile entity type is registered.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "api_registration")
    @SuppressWarnings("DataFlowIssue")
    public void apiTurretsAreRegisteredWithConfig(GameTestHelper helper) {
        helper.assertTrue(EXPECTED.keySet().containsAll(ApiTestTurrets.ALL), "A test turret has no expectations");
        for (TurretType type : ApiTestTurrets.ALL) {
            String id = type.getId();
            helper.assertTrue(BuiltInRegistries.BLOCK.getKey(OMTApi.block(type)).toString().equals(id),
                    "Turret block is not registered as " + id);
            helper.assertTrue(BuiltInRegistries.ITEM.getKey(OMTApi.item(type)).toString().equals(id),
                    "Turret item is not registered as " + id);
            TurretDefinition definition = OMTApi.turret(type).orElse(null);
            helper.assertTrue(definition != null, "No turret definition for " + id);
            Expected expected = EXPECTED.get(type);
            helper.assertTrue(expected.weapon().isInstance(definition.weapon()),
                    id + " has a " + definition.weapon().getClass().getSimpleName() + " instead of a "
                            + expected.weapon().getSimpleName());
            TurretSetting setting = OMTConfig.get().turrets.addonTurrets.get(id);
            helper.assertTrue(setting != null, "No config entry turrets.addon_turrets." + id);
            helper.assertTrue(setting.toStats().equals(expected.stats()),
                    "Config entry of " + id + " " + setting.toStats() + " differs from the default stats");
            helper.assertTrue(OMTConfig.get().turrets.get(type) == setting,
                    "Stats of " + id + " do not come from its config entry");
        }
        String projectileId = ApiTestTurrets.NAMESPACE + ":api_projectile";
        helper.assertTrue(BuiltInRegistries.ENTITY_TYPE.getKey(ApiTestEntities.PROJECTILE).toString().equals(projectileId),
                "The API projectile is not registered as " + projectileId);
        helper.succeed();
    }

    // ---- Ray ----

    /** The API ray turret traces its rays to a target in front and hits it. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "api_ray_turret", timeoutTicks = TIMEOUT_TICKS)
    public void apiRayTurretHitsTarget(GameTestHelper helper) {
        RayTurretGameTests.hitsTargetInFront(helper, ApiTestTurrets.API_RAY_TURRET);
    }

    /** The API ray turret is placed on its base tier with a block entity and uses its own ammo type. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "api_ray_turret_ammo", timeoutTicks = TIMEOUT_TICKS)
    public void apiRayTurretUsesItsOwnAmmo(GameTestHelper helper) {
        helper.assertTrue(AmmoRules.isValidAmmo(new ItemStack(Items.ARROW)), "Arrows are not accepted as ammo");
        TurretBaseBlockEntity base = TurretScene.build(helper, ApiTestTurrets.API_RAY_TURRET, TIMEOUT_TICKS);
        helper.assertTrue(helper.getBlockEntity(TurretScene.TURRET) instanceof TurretBlockEntity,
                "The API turret has no turret block entity");
        int ammo = base.getLogic().getAmmoLevel(ApiTestTurrets.API_RAY_TURRET);
        helper.assertTrue(ammo == 64, "The base counts " + ammo + " shots of arrow ammo instead of 64");
        TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        // the first shot uses one arrow
        TurretScene.succeedWhen(helper, () -> {
            ItemStack left = base.getInventory().getItem(TurretBaseInventory.AMMO_START);
            helper.assertTrue(left.is(Items.ARROW) && left.getCount() < 64, "No arrow was used");
        });
    }

    /** A {@code RayWeapon}'s entity hit handler runs when its ray hits the target. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "api_ray_turret_hit", timeoutTicks = TIMEOUT_TICKS)
    public void apiRayTurretCallsEntityHitHandler(GameTestHelper helper) {
        TurretScene.build(helper, ApiTestTurrets.API_RAY_TURRET, TIMEOUT_TICKS);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        TurretScene.succeedWhen(helper, () -> helper.assertTrue(target.hasEffect(MobEffects.LUCK),
                "The entity hit handler did not run for the hit target"));
    }

    // ---- Projectile ----

    /** The API projectile turret fires its own projectile entity, which hits a target in front. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "api_projectile_turret",
            timeoutTicks = ProjectileTurretGameTests.TIMEOUT_TICKS)
    public void apiProjectileTurretHitsTarget(GameTestHelper helper) {
        ProjectileTurretGameTests.hitsTargetInFront(helper, ApiTestTurrets.API_PROJECTILE_TURRET,
                ApiTestEntities.PROJECTILE);
    }

    /** A {@code ProjectileWeapon}'s hit handler runs when one of its projectiles hits the target. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "api_projectile_turret_hit", timeoutTicks = TIMEOUT_TICKS)
    public void apiProjectileTurretCallsHitHandler(GameTestHelper helper) {
        TurretScene.build(helper, ApiTestTurrets.API_PROJECTILE_TURRET, TIMEOUT_TICKS);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        TurretScene.succeedWhen(helper, () -> helper.assertTrue(target.hasEffect(MobEffects.HUNGER),
                "The hit handler did not run for the hit target"));
    }

    // ---- Effect ----

    /** An {@code EffectWeapon} gets the base's target and puts its effect on it. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "api_effect_turret", timeoutTicks = TIMEOUT_TICKS)
    public void apiEffectTurretActsOnBaseTarget(GameTestHelper helper) {
        TurretScene.build(helper, ApiTestTurrets.API_EFFECT_TURRET, TIMEOUT_TICKS);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        TurretScene.succeedWhen(helper, () -> helper.assertTrue(target.hasEffect(MobEffects.GLOWING),
                "The effect turret did not act on the base's target"));
    }

    /** An {@code EffectWeapon} only acts on the base's target: a base that targets no mobs leaves a mob alone. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "api_effect_turret_no_target", timeoutTicks = TIMEOUT_TICKS)
    public void apiEffectTurretNeedsBaseTarget(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, ApiTestTurrets.API_EFFECT_TURRET, TIMEOUT_TICKS);
        base.getLogic().setTargetMobs(false);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        TurretScene.onEachTick(helper, () -> {
            helper.assertTrue(!target.hasEffect(MobEffects.GLOWING),
                    "The effect turret acted on a mob its base does not target");
            if (helper.getTick() >= QUIET_TICKS) TurretScene.succeed(helper);
        });
    }

    // ---- Area ----

    /**
     * An {@code AreaWeapon} strikes what is in its area without a target from the base: it damages (through
     * {@code OMTApi.hurt}) and marks a mob next to the base, but not one 5 blocks away.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "api_area_turret", timeoutTicks = TIMEOUT_TICKS)
    public void apiAreaTurretStrikesOnlyItsArea(GameTestHelper helper) {
        TurretScene.build(helper, ApiTestTurrets.API_AREA_TURRET, TIMEOUT_TICKS);
        LivingEntity near = TurretScene.spawnTarget(helper, TurretScene.BASE.east());
        LivingEntity far = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        float maxHealth = near.getMaxHealth();
        TurretScene.succeedWhen(helper, () -> {
            helper.assertTrue(near.hasEffect(MobEffects.FIRE_RESISTANCE), "The area turret did not strike the mob in reach");
            helper.assertTrue(near.getHealth() < maxHealth, "OMTApi.hurt did not damage the mob in reach");
            helper.assertTrue(!far.hasEffect(MobEffects.FIRE_RESISTANCE), "The area turret struck a mob outside its area");
        });
    }

    // ---- Self-targeting ----

    /**
     * A {@code SelfTargetingWeapon} picks its own target from the entities around the base: it shoots a mob even
     * though the base targets no mobs (so the base has no target for it).
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "api_self_targeting_turret", timeoutTicks = TIMEOUT_TICKS)
    public void apiSelfTargetingTurretPicksItsOwnTarget(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, ApiTestTurrets.API_SELF_TARGETING_TURRET, TIMEOUT_TICKS);
        base.getLogic().setTargetMobs(false);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        TurretScene.succeedWhen(helper, () -> {
            helper.assertTrue(target.hasEffect(MobEffects.WEAKNESS), "The self-targeting turret did not shoot the mob");
            helper.assertTrue(base.getLogic().getTarget(Face.UP).isEmpty(),
                    "The base picked a target although it targets no mobs");
        });
    }

    /** While its picker finds nothing, a {@code SelfTargetingWeapon} keeps asking it but uses no energy. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "api_self_targeting_turret_idle", timeoutTicks = TIMEOUT_TICKS)
    public void apiSelfTargetingTurretIdlesWithoutTargets(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, ApiTestTurrets.API_SELF_TARGETING_TURRET, TIMEOUT_TICKS);
        EnergyStore energy = base.getLogic().getEnergy();
        int picksBefore = ApiTestTurrets.SELF_TARGETING_PICKS.get();
        TurretScene.onEachTick(helper, () -> {
            helper.assertTrue(energy.getAmount() == energy.getCapacity(),
                    "The idle self-targeting turret used energy (" + energy.getAmount() + " of " + energy.getCapacity() + ")");
            if (helper.getTick() >= QUIET_TICKS) {
                int picks = ApiTestTurrets.SELF_TARGETING_PICKS.get() - picksBefore;
                helper.assertTrue(picks > 1, "The picker ran " + picks + " times while idle");
                // retried every RETRY_TICKS, not every tick
                helper.assertTrue(picks <= QUIET_TICKS / SelfTargetingWeapon.RETRY_TICKS + 1,
                        "The picker ran " + picks + " times in " + QUIET_TICKS + " ticks");
                TurretScene.succeed(helper);
            }
        });
    }
}
