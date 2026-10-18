package omtteam.openmodularturrets.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omtteam.openmodularturrets.api.OMTApi;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.config.TurretSetting;
import omtteam.openmodularturrets.inventory.AmmoRules;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;

/**
 * The turret of {@link ApiTestTurrets}, added through the turret API, works like a built-in one: it is registered
 * with its id, gets a config entry with its default stats, accepts its own ammo and hits a target in front of it.
 */
public class ApiTurretGameTests implements FabricGameTest {
    private static final int TIMEOUT_TICKS = 400;

    @GameTest(template = EMPTY_STRUCTURE, batch = "api_ray_turret", timeoutTicks = TIMEOUT_TICKS)
    public void apiTurretHitsTarget(GameTestHelper helper) {
        RayTurretGameTests.hitsTargetInFront(helper, ApiTestTurrets.API_RAY_TURRET);
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "api_ray_turret_setup", timeoutTicks = TIMEOUT_TICKS)
    public void apiTurretIsRegisteredWithConfigAndAmmo(GameTestHelper helper) {
        String id = ApiTestTurrets.API_RAY_TURRET.getId();
        helper.assertTrue(BuiltInRegistries.BLOCK.getKey(OMTApi.block(ApiTestTurrets.API_RAY_TURRET)).toString().equals(id),
                "Turret block is not registered as " + id);
        helper.assertTrue(BuiltInRegistries.ITEM.getKey(OMTApi.item(ApiTestTurrets.API_RAY_TURRET)).toString().equals(id),
                "Turret item is not registered as " + id);
        TurretSetting setting = OMTConfig.get().turrets.addonTurrets.get(id);
        helper.assertTrue(setting != null, "No config entry turrets.addon_turrets." + id);
        helper.assertTrue(setting.toStats().equals(ApiTestTurrets.API_RAY_TURRET_STATS),
                "Config entry " + setting.toStats() + " differs from the default stats");
        helper.assertTrue(OMTConfig.get().turrets.get(ApiTestTurrets.API_RAY_TURRET) == setting,
                "Turret stats do not come from its config entry");
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
}
