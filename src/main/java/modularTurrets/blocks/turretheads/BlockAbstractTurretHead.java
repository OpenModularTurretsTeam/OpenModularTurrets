package modularTurrets.blocks.turretheads;

import modularTurrets.ModularTurrets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;

public abstract class BlockAbstractTurretHead extends BlockContainer implements ITileEntityProvider {
    public BlockAbstractTurretHead() {
        super(Material.rock);

        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setHardness(-1F);
        this.setBlockBounds(1F, 1F, 1F, 1F, 1F, 1F);
        this.setResistance(20F);
        this.setStepSound(Block.soundTypeStone);
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
