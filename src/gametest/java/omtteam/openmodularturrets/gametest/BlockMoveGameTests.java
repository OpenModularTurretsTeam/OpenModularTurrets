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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import omtteam.openmodularturrets.block.misc.GeneratorBlock;
import omtteam.openmodularturrets.block.misc.LeverBlock;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.misc.GeneratorBlockEntity;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;

import java.util.List;

/**
 * Moving blocks the way {@code /setblock} and Sable contraptions do: they save the block entity's data, call
 * {@link Clearable#tryClear} and then remove the old block, which must not drop the items a second time. Moved blocks
 * are rotated and mirrored through their block state, which must turn their facing with them.
 */
//? if neoforge || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings({"unused", "RedundantCast", "deprecation"})
public class BlockMoveGameTests implements OMTGameTests {
    private static final BlockPos POS = new BlockPos(3, 1, 3);

    /** A cleared base drops nothing when it is removed (the block itself is not dropped by {@code setBlock}). */
    @GameTest(template = EMPTY_STRUCTURE, batch = "block_move_base_cleared")
    public void clearedBaseDropsNothing(GameTestHelper helper) {
        TurretBaseBlockEntity base = placeFilledBase(helper);
        Clearable.tryClear(base);
        helper.setBlock(POS, Blocks.AIR);
        List<ItemEntity> drops = drops(helper);
        drops.forEach(Entity::discard);
        helper.assertTrue(drops.isEmpty(), "The cleared base dropped " + drops.size() + " stacks");
        helper.succeed();
    }

    /** Without clearing, removing a base still drops its whole inventory (16 bullets and an upgrade). */
    @GameTest(template = EMPTY_STRUCTURE, batch = "block_move_base_removed")
    public void removedBaseDropsItsItems(GameTestHelper helper) {
        placeFilledBase(helper);
        helper.setBlock(POS, Blocks.AIR);
        List<ItemEntity> drops = drops(helper);
        drops.forEach(Entity::discard);
        // dropped stacks are split into random parts, so the items are counted
        int items = drops.stream().mapToInt(drop -> drop.getItem().getCount()).sum();
        helper.assertTrue(items == 17, "The removed base dropped " + items + " items instead of 17");
        helper.succeed();
    }

    /** A cleared generator drops neither its fuel nor its upgrades when it is removed. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "block_move_generator_cleared")
    public void clearedGeneratorDropsNothing(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.GENERATOR);
        GeneratorBlockEntity generator = (GeneratorBlockEntity) helper.getBlockEntity(POS);
        generator.getFuelContainer().setItem(0, new ItemStack(Items.COAL, 8));
        generator.getUpgradeContainer().setItem(0, new ItemStack(ModItems.UPGRADE_ENERGY_MULTIPLIER));
        Clearable.tryClear(generator);
        helper.setBlock(POS, Blocks.AIR);
        List<ItemEntity> drops = drops(helper);
        drops.forEach(Entity::discard);
        helper.assertTrue(drops.isEmpty(), "The cleared generator dropped " + drops.size() + " stacks");
        helper.succeed();
    }

    /** Turret, crank and generator turn and mirror their facing; a turret on top or bottom keeps it. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "block_move_rotation")
    public void rotatedBlocksTurnTheirFacing(GameTestHelper helper) {
        assertTurns(helper, ModBlocks.TURRETS.get(TurretType.DISPOSABLE_TURRET).defaultBlockState(), TurretBlock.FACING);
        assertTurns(helper, ModBlocks.LEVER.defaultBlockState(), LeverBlock.FACING);
        assertTurns(helper, ModBlocks.GENERATOR.defaultBlockState(), GeneratorBlock.FACING);
        BlockState onTop = ModBlocks.TURRETS.get(TurretType.DISPOSABLE_TURRET).defaultBlockState()
                .setValue(TurretBlock.FACING, Direction.UP);
        helper.assertTrue(onTop.rotate(Rotation.CLOCKWISE_90).getValue(TurretBlock.FACING) == Direction.UP,
                "A turret on top of its base does not keep facing up when rotated");
        helper.succeed();
    }

    private static void assertTurns(GameTestHelper helper, BlockState state, DirectionProperty facing) {
        BlockState north = state.setValue(facing, Direction.NORTH);
        String name = state.getBlock().getDescriptionId();
        helper.assertTrue(north.rotate(Rotation.CLOCKWISE_90).getValue(facing) == Direction.EAST,
                name + " facing north does not face east after a clockwise rotation");
        helper.assertTrue(north.mirror(Mirror.LEFT_RIGHT).getValue(facing) == Direction.SOUTH,
                name + " facing north does not face south when mirrored left to right");
    }

    /** A tier 1 base with a stack of ammo and an upgrade (no owner). */
    private static TurretBaseBlockEntity placeFilledBase(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.TURRET_BASES.get(0));
        TurretBaseBlockEntity base = (TurretBaseBlockEntity) helper.getBlockEntity(POS);
        base.getInventory().setItem(TurretBaseInventory.AMMO_START, new ItemStack(ModItems.AMMO_BULLET, 16));
        base.getInventory().setItem(TurretBaseInventory.UPGRADE_START, new ItemStack(ModItems.UPGRADE_RANGE));
        return base;
    }

    private static List<ItemEntity> drops(GameTestHelper helper) {
        return helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(POS)).inflate(2.0D));
    }
}
