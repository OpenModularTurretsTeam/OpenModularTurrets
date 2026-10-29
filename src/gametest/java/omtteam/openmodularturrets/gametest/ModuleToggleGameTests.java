package omtteam.openmodularturrets.gametest;

//? if neoforge && <1.21.11 {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if forge {
/*import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
*///?}
import net.minecraft.core.BlockPos;
//? if <1.21.11 {
import net.minecraft.gametest.framework.GameTest;
//?}
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
//? if >=1.21.11 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
*///?}
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.misc.GeneratorBlockEntity;
import omtteam.openmodularturrets.compat.ModuleEnabledCondition;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.TurretBaseUpgradeType;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.item.ModuleToggles;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;

import java.util.List;

/**
 * The config switches of upgrades and addons: their recipes load with the default config (the
 * {@code module_enabled} condition is registered and passes), the condition follows the config, and a disabled upgrade
 * or addon stays in its slot but does nothing in a turret base or generator. The config is changed and restored inside
 * one test call, so no other test sees it.
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings({"unused", "RedundantCast"})
public class ModuleToggleGameTests implements OMTGameTests {
    /** Every upgrade and addon with a recipe (the creative upgrade has none). */
    private static final List<String> RECIPES = List.of("upgrade_accuracy", "upgrade_efficiency", "upgrade_fire_rate",
            "upgrade_range", "upgrade_scatter_shot", "upgrade_energy_multiplier", "addon_concealer", "addon_damage_amp",
            "addon_recycler", "addon_redstone_reactor", "addon_solar_panel", "addon_fake_drops");
    private static final BlockPos BASE = new BlockPos(1, 1, 1);
    private static final BlockPos GENERATOR = new BlockPos(3, 1, 1);

    @GameTest(template = EMPTY_STRUCTURE)
    public void moduleRecipesLoadWithTheDefaultConfig(GameTestHelper helper) {
        RecipeManager recipes = helper.getLevel().getServer().getRecipeManager();
        for (String recipe : RECIPES) {
            //? if >=1.21.11 {
            /*boolean loaded = recipes.byKey(ResourceKey.create(Registries.RECIPE, OMT.id(recipe))).isPresent();
            *///?} else {
            boolean loaded = recipes.byKey(OMT.id(recipe)).isPresent();
            //?}
            helper.assertTrue(loaded, "recipe " + recipe + " loaded");
        }
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void conditionFollowsTheConfig(GameTestHelper helper) {
        ConfigData config = OMTConfig.get();
        boolean range = config.upgrades.range;
        boolean multiplier = config.upgrades.energyMultiplier;
        boolean amp = config.addons.damageAmp;
        try {
            for (String recipe : RECIPES) {
                helper.assertTrue(ModuleEnabledCondition.anyEnabled(List.of(recipe)), recipe + " enabled by default");
            }
            config.upgrades.range = false;
            config.upgrades.energyMultiplier = false;
            config.addons.damageAmp = false;
            helper.assertFalse(ModuleEnabledCondition.anyEnabled(List.of("upgrade_range")), "range upgrade disabled");
            helper.assertFalse(ModuleEnabledCondition.anyEnabled(List.of("upgrade_energy_multiplier")),
                    "generator upgrade disabled");
            helper.assertFalse(ModuleEnabledCondition.anyEnabled(List.of("addon_damage_amp")), "damage amp disabled");
            helper.assertTrue(ModuleEnabledCondition.anyEnabled(List.of("upgrade_accuracy")), "the others stay enabled");
            helper.assertTrue(ModuleToggles.isDisabled(new ItemStack(ModItems.UPGRADE_RANGE)), "the stack is disabled");
            helper.assertFalse(ModuleToggles.isDisabled(new ItemStack(ModItems.UPGRADE_ACCURACY)), "the stack is enabled");
        } finally {
            config.upgrades.range = range;
            config.upgrades.energyMultiplier = multiplier;
            config.addons.damageAmp = amp;
        }
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "module_toggles_machines")
    public void disabledModulesDoNothingInMachines(GameTestHelper helper) {
        helper.setBlock(BASE, ModBlocks.TURRET_BASES.get(4));
        TurretBaseBlockEntity base = (TurretBaseBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(BASE));
        base.getInventory().setItem(TurretBaseInventory.UPGRADE_START, new ItemStack(ModItems.UPGRADE_RANGE, 2));
        base.getInventory().setItem(TurretBaseInventory.ADDON_START, new ItemStack(ModItems.ADDON_DAMAGE_AMP, 3));
        helper.setBlock(GENERATOR, ModBlocks.GENERATOR);
        GeneratorBlockEntity generator = (GeneratorBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(GENERATOR));
        generator.getUpgradeContainer().setItem(0, new ItemStack(ModItems.UPGRADE_ENERGY_MULTIPLIER, 2));
        ConfigData config = OMTConfig.get();
        boolean range = config.upgrades.range;
        boolean multiplier = config.upgrades.energyMultiplier;
        boolean amp = config.addons.damageAmp;
        try {
            helper.assertTrue(base.getLogic().getUpgradeCount(TurretBaseUpgradeType.RANGE) == 2, "range upgrades count");
            helper.assertTrue(base.getLogic().getAddonCount(AddonType.DAMAGE_AMP) == 3, "damage amps count");
            helper.assertTrue(generator.getMultiplierUpgrades() == 2, "multiplier upgrades count");
            config.upgrades.range = false;
            config.upgrades.energyMultiplier = false;
            config.addons.damageAmp = false;
            helper.assertTrue(base.getLogic().getUpgradeCount(TurretBaseUpgradeType.RANGE) == 0,
                    "a disabled range upgrade still counts");
            helper.assertTrue(base.getLogic().getAddonCount(AddonType.DAMAGE_AMP) == 0, "a disabled damage amp still counts");
            helper.assertTrue(generator.getMultiplierUpgrades() == 0, "a disabled multiplier upgrade still counts");
            helper.assertTrue(base.getInventory().getItem(TurretBaseInventory.UPGRADE_START).getCount() == 2,
                    "the disabled upgrades stay in their slot");
        } finally {
            config.upgrades.range = range;
            config.upgrades.energyMultiplier = multiplier;
            config.addons.damageAmp = amp;
        }
        base.getInventory().clearContent();
        generator.getUpgradeContainer().clearContent();
        helper.setBlock(BASE, Blocks.AIR);
        helper.setBlock(GENERATOR, Blocks.AIR);
        helper.succeed();
    }
}
