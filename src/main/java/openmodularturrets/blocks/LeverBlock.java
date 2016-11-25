package openmodularturrets.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.util.BlockAbstract;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.TurretBase;

import javax.annotation.Nullable;

public class LeverBlock extends BlockAbstract implements ITileEntityProvider {
    public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 5);

    public LeverBlock() {
        super(Material.ROCK);
        this.setUnlocalizedName(Names.Blocks.lever);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setHardness(2F);
        //this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setResistance(15F);
        this.setSoundType(SoundType.STONE);
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ROTATION);
    }


    @Override
    public TileEntity createNewTileEntity(World worldIn, int par2) {
        return new LeverTileEntity();
    }

    private boolean isBaseValid(TileEntity base) {
        if (base instanceof TurretBase) {
            return ((TurretBase) base).getBaseTier() == 1;
        }
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return (isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()))) ||
                isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()))) ||
                isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1))) ||
                isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1))));
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        float l = 0;
        if (isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())))) {
            l = 270F;
        }
        if (isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())))) {
            l = 90F;
        }
        if (isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)))) {
            l = 0F;
        }
        if (isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)))) {
            l = 180;
        }
        int shu = MathHelper.floor_double((double) (l * 4.0F / 360.0F) + 0.5D) & 3;
        worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(ROTATION, shu), 2);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TurretBase base;
        LeverTileEntity lever = (LeverTileEntity) worldIn.getTileEntity(pos);
        if ((worldIn.getBlockState(pos).getValue(ROTATION) * 90) == 0 && isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(),
                pos.getZ() + 1)))) {
            base = (TurretBase) worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(),
                    pos.getZ() + 1));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    //worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), "openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(EnumFacing.DOWN, 50, false);
                }
            }
        }

        if ((worldIn.getBlockState(pos).getValue(ROTATION) * 90) == 90 && isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(),
                pos.getZ())))) {
            base = (TurretBase) worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    //worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(EnumFacing.DOWN, 50, false);
                }
            }
        }

        if ((worldIn.getBlockState(pos).getValue(ROTATION) * 90) == 180 && isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(),
                pos.getZ() - 1)))) {
            base = (TurretBase) worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(),
                    pos.getZ() - 1));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    //worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(EnumFacing.DOWN, 50, false);
                }
            }
        }

        if ((worldIn.getBlockState(pos).getValue(ROTATION) * 90) == 270 && isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(),
                pos.getZ())))) {
            base = (TurretBase) worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    //worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(EnumFacing.DOWN, 50, false);
                }
            }
        }
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }


    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
