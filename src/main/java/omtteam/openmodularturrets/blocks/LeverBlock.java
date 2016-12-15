package omtteam.openmodularturrets.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import omtteam.omlib.blocks.BlockAbstractTileEntity;
import omtteam.omlib.util.IHasItemBlock;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.items.blocks.ItemBlockLever;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
public class LeverBlock extends BlockAbstractTileEntity implements IHasItemBlock {
    public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 4);

    public LeverBlock() {
        super(Material.GLASS);
        this.setUnlocalizedName(OMTNames.Blocks.lever);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setHardness(2F);
        this.setResistance(15F);
        this.setSoundType(SoundType.STONE);
        setDefaultState(this.blockState.getBaseState().withProperty(ROTATION, 0));
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.lever);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockLever(block);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ROTATION, meta);

    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ROTATION);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ROTATION);
    }


    @Override
    public TileEntity createTileEntity(World worldIn, IBlockState blockState) {
        return new LeverTileEntity();
    }

    private boolean isBaseValid(TileEntity base) {
        return base instanceof TurretBase && ((TurretBase) base).getTier() == 1;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return (isBaseValid(worldIn.getTileEntity(pos.north())) ||
                isBaseValid(worldIn.getTileEntity(pos.east())) ||
                isBaseValid(worldIn.getTileEntity(pos.south())) ||
                isBaseValid(worldIn.getTileEntity(pos.west())));
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        float l = 0;
        if (isBaseValid(worldIn.getTileEntity(pos.north()))) {
            l = 270F;
        }
        if (isBaseValid(worldIn.getTileEntity(pos.east()))) {
            l = 90F;
        }
        if (isBaseValid(worldIn.getTileEntity(pos.south()))) {
            l = 0F;
        }
        if (isBaseValid(worldIn.getTileEntity(pos.west()))) {
            l = 180;
        }
        int shu = MathHelper.floor_double((double) (l * 4.0F / 360.0F) + 0.5D) & 3;
        worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(ROTATION, shu), 2);

    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TurretBase base;
        LeverTileEntity lever = (LeverTileEntity) worldIn.getTileEntity(pos);
        if (lever != null && (worldIn.getBlockState(pos).getValue(ROTATION) * 90) == 0 && isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(),
                pos.getZ() + 1)))) {
            base = (TurretBase) worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(),
                    pos.getZ() + 1));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    //worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), "omtteam.openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(EnumFacing.DOWN, 50, false);
                }
            }
        }

        if (lever != null && (worldIn.getBlockState(pos).getValue(ROTATION) * 90) == 90 && isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(),
                pos.getZ())))) {
            base = (TurretBase) worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    //worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "omtteam.openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(EnumFacing.DOWN, 50, false);
                }
            }
        }

        if (lever != null && (worldIn.getBlockState(pos).getValue(ROTATION) * 90) == 180 && isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(),
                pos.getZ() - 1)))) {
            base = (TurretBase) worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(),
                    pos.getZ() - 1));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    //worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "omtteam.openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(EnumFacing.DOWN, 50, false);
                }
            }
        }

        if (lever != null && (worldIn.getBlockState(pos).getValue(ROTATION) * 90) == 270 && isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(),
                pos.getZ())))) {
            base = (TurretBase) worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    //worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "omtteam.openmodularturrets:windup", 1.0F, 1.0F);
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

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.1F, 0.1F, 0.1F, 0.9F, 0.9F, 0.9F);
    }
}
