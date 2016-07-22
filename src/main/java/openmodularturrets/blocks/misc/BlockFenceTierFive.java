package openmodularturrets.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.ModBlocks;
import openmodularturrets.blocks.util.BlockAbstractMiscPane;
import openmodularturrets.reference.Names;

import java.util.Random;

public class BlockFenceTierFive extends BlockAbstractMiscPane {
    public BlockFenceTierFive() {
        super("stone", "cobblestone", Material.rock, false);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setResistance(80.0F);
        this.setHardness(80.0F);
        this.setHarvestLevel("pickaxe", 2);
        this.setStepSound(Block.soundTypeStone);
        this.setUnlocalizedName(Names.Blocks.fenceTierFive);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ModBlocks.fenceTierFive);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn) {
        if(!(entityIn instanceof EntityItem)) entityIn.attackEntityFrom(DamageSource.cactus, 5);
    }
}
