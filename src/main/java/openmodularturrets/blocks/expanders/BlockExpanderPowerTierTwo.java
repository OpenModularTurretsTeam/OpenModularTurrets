package openmodularturrets.blocks.expanders;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.util.BlockAbstract;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.expander.ExpanderPowerTierTwoTileEntity;

public class BlockExpanderPowerTierTwo extends BlockAbstract implements ITileEntityProvider {
    public BlockExpanderPowerTierTwo() {
        super(Material.rock);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setResistance(3.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setUnlocalizedName(Names.Blocks.expanderPowerTierTwo);
        this.setBlockBounds(0.1F, 0.1F, 0.1F, 0.9F, 0.9F, 0.9F);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int par2) {
        return new ExpanderPowerTierTwoTileEntity();
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}

