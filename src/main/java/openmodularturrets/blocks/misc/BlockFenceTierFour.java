package openmodularturrets.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import openmodularturrets.ModularTurrets;
import openmodularturrets.init.ModBlocks;
import openmodularturrets.blocks.util.BlockAbstractMiscPane;
import openmodularturrets.reference.Names;

import java.util.Random;

public class BlockFenceTierFour extends BlockAbstractMiscPane {
    public BlockFenceTierFour() {
        super("stone", "cobblestone", Material.ROCK, false);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setResistance(50.0F);
        this.setHardness(50.0F);
        this.setHarvestLevel("pickaxe", 2);
        this.setSoundType(SoundType.STONE);
        this.setUnlocalizedName(Names.Blocks.fenceTierFour);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ModBlocks.fenceTierFive);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if(!(entityIn instanceof EntityItem)) entityIn.attackEntityFrom(DamageSource.cactus, 5);
    }
}
