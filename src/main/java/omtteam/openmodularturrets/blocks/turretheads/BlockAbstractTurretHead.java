package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import omtteam.openmodularturrets.ModularTurrets;
import omtteam.openmodularturrets.tileentity.TurretBase;

abstract class BlockAbstractTurretHead extends Block implements ITileEntityProvider {
    BlockAbstractTurretHead() {
        super(Material.GLASS);

        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setSoundType(SoundType.STONE);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.2F, 0.0F, 0.2F, 0.8F, 1F, 0.8F);
    }


    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())) instanceof TurretBase ||
                worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())) instanceof TurretBase ||
                worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())) instanceof TurretBase ||
                worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())) instanceof TurretBase ||
                worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)) instanceof TurretBase ||
                worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)) instanceof TurretBase;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }
}
