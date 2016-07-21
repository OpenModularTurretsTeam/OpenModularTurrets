package openmodularturrets.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.util.BlockAbstract;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.turretbase.TurretBaseTierOneTileEntity;

public class LeverBlock extends BlockAbstract implements ITileEntityProvider {
    public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 16);
    public LeverBlock() {
        super(Material.rock);
        this.setUnlocalizedName(Names.Blocks.unlocalisedLever);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setHardness(2F);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setResistance(15F);
        this.setStepSound(Block.soundTypeStone);
        setDefaultState(this.blockState.getBaseState().withProperty(ROTATION, 0));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
            return this.getDefaultState().withProperty(ROTATION, meta);

    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ROTATION);
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, ROTATION);
    }



    @Override
    public TileEntity createNewTileEntity(World worldIn, int par2) {
        return new LeverTileEntity();
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return (worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())) instanceof TurretBaseTierOneTileEntity ||
                worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())) instanceof TurretBaseTierOneTileEntity ||
                worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)) instanceof TurretBaseTierOneTileEntity ||
                worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)) instanceof TurretBaseTierOneTileEntity);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        float l = 0;
        if (worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())) instanceof TurretBaseTierOneTileEntity) {
            l = 270F;
        }
        if (worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())) instanceof TurretBaseTierOneTileEntity) {
            l = 90F;
        }
        if (worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)) instanceof TurretBaseTierOneTileEntity) {
            l = 0F;
        }
        if (worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)) instanceof TurretBaseTierOneTileEntity) {
            l = 180;
        }
        int shu = MathHelper.floor_double((double) (l * 4.0F / 360.0F) + 0.5D) & 3;
        worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(ROTATION, shu), 2);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        TurretBaseTierOneTileEntity base;
        LeverTileEntity lever = (LeverTileEntity) worldIn.getTileEntity(pos);
        if ((worldIn.getBlockState(pos).getValue(ROTATION)* 90) == 0 && worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(),
                pos.getZ() + 1)) instanceof TurretBaseTierOneTileEntity) {
            base = (TurretBaseTierOneTileEntity) worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(),
                    pos.getZ() + 1));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(EnumFacing.DOWN, 50, false);
                }
            }
        }

        if ((worldIn.getBlockState(pos).getValue(ROTATION)* 90) == 90 && worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(),
                pos.getZ())) instanceof TurretBaseTierOneTileEntity) {
            base = (TurretBaseTierOneTileEntity) worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(EnumFacing.DOWN, 50, false);
                }
            }
        }

        if ((worldIn.getBlockState(pos).getValue(ROTATION)* 90) == 180 && worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(),
                pos.getZ() - 1)) instanceof TurretBaseTierOneTileEntity) {
            base = (TurretBaseTierOneTileEntity) worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(),
                    pos.getZ() - 1));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(EnumFacing.DOWN, 50, false);
                }
            }
        }

        if ((worldIn.getBlockState(pos).getValue(ROTATION)* 90) == 270 && worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(),
                pos.getZ())) instanceof TurretBaseTierOneTileEntity) {
            base = (TurretBaseTierOneTileEntity) worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(EnumFacing.DOWN, 50, false);
                }
            }
        }
        return true;
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
