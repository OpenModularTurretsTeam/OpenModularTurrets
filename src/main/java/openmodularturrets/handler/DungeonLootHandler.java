package openmodularturrets.handler;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import openmodularturrets.blocks.Blocks;

/**
 * Created by Niel on 12/26/2015.
 */
public class DungeonLootHandler {

    public static void init() {

        if (ConfigHandler.isShouldSpawnDungeonLoot()) {
            ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR)
                    .addItem(new WeightedRandomChestContent(new ItemStack(Blocks.disposableItemTurret), 1, 2, 15));
            ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST)
                    .addItem(new WeightedRandomChestContent(new ItemStack(Blocks.disposableItemTurret), 1, 2, 15));
            ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR)
                    .addItem(new WeightedRandomChestContent(new ItemStack(Blocks.disposableItemTurret), 1, 2, 15));

            ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR)
                    .addItem(new WeightedRandomChestContent(new ItemStack(Blocks.turretBaseTierOne), 1, 1, 15));
            ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST)
                    .addItem(new WeightedRandomChestContent(new ItemStack(Blocks.turretBaseTierOne), 1, 1, 15));
            ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR)
                    .addItem(new WeightedRandomChestContent(new ItemStack(Blocks.turretBaseTierOne), 1, 1, 15));

            ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR)
                    .addItem(new WeightedRandomChestContent(new ItemStack(Blocks.leverBlock), 1, 1, 15));
            ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST)
                    .addItem(new WeightedRandomChestContent(new ItemStack(Blocks.leverBlock), 1, 1, 15));
            ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR)
                    .addItem(new WeightedRandomChestContent(new ItemStack(Blocks.leverBlock), 1, 1, 15));
        }

    }
}
