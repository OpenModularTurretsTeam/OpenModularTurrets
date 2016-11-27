package omtteam.openmodularturrets.handler;

/**
 * Created by Niel on 12/26/2015.
 */
public class DungeonLootHandler {

    public static void init() {

        if (ConfigHandler.isShouldSpawnDungeonLoot()) {
            /*ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(
                    new WeightedRandomChestContent(new ItemStack(ModBlocks.disposableItemTurret), 1, 2, 15));
            ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
                    new WeightedRandomChestContent(new ItemStack(ModBlocks.disposableItemTurret), 1, 2, 15));
            ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR).addItem(
                    new WeightedRandomChestContent(new ItemStack(ModBlocks.disposableItemTurret), 1, 2, 15));

            ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(
                    new WeightedRandomChestContent(new ItemStack(ModBlocks.turretBase,1,1), 1, 1, 15));
            ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
                    new WeightedRandomChestContent(new ItemStack(ModBlocks.turretBase,1,1), 1, 1, 15));
            ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR).addItem(
                    new WeightedRandomChestContent(new ItemStack(ModBlocks.turretBase,1,1), 1, 1, 15));

            ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(
                    new WeightedRandomChestContent(new ItemStack(ModBlocks.leverBlock), 1, 1, 15));
            ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
                    new WeightedRandomChestContent(new ItemStack(ModBlocks.leverBlock), 1, 1, 15));
            ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR).addItem(
                    new WeightedRandomChestContent(new ItemStack(ModBlocks.leverBlock), 1, 1, 15));*/
        }
    }
}