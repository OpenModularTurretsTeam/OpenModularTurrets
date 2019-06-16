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
import net.minecraftforge.energy.CapabilityEnergy;
import omtteam.omlib.api.block.IHasItemBlock;
import omtteam.omlib.blocks.BlockAbstractTileEntity;
import omtteam.omlib.power.OMEnergyStorage;
import omtteam.omlib.util.MathUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.api.ITurretBaseAddonBlock;
import omtteam.openmodularturrets.items.blocks.ItemBlockLever;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.turret.TurretHeadUtil;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.openmodularturrets.reference.OMTNames.Blocks.lever;
import static omtteam.openmodularturrets.turret.TurretHeadUtil.getTurretBase;
import static omtteam.openmodularturrets.turret.TurretHeadUtil.getTurretBaseFacing;

@SuppressWarnings("deprecation")
public class LeverBlock extends BlockAbstractTileEntity implements IHasItemBlock, ITurretBaseAddonBlock {
    public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 4);

    public LeverBlock() {
        super(Material.GLASS);
        this.setUnlocalizedName(lever);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setHardness(2F);
        this.setResistance(15F);
        this.setSoundType(SoundType.STONE);
        setDefaultState(this.blockState.getBaseState().withProperty(ROTATION, 0));
        this.setRegistryName(Reference.MOD_ID, lever);
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
    @Nonnull
    @ParametersAreNonnullByDefault
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
        float l = 0F;
        if (isBaseValid(worldIn.getTileEntity(pos.east()))) {
            l = 270F;
        }
        if (isBaseValid(worldIn.getTileEntity(pos.west()))) {
            l = 90F;
        }
        if (isBaseValid(worldIn.getTileEntity(pos.south()))) {
            l = 0F;
        }
        if (isBaseValid(worldIn.getTileEntity(pos.north()))) {
            l = 180F;
        }
        int shu = MathHelper.floor((double) (l * 4.0F / 360.0F) + 0.5D) & 3;
        worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(ROTATION, shu), 3);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (TurretHeadUtil.getTurretBaseFacing(worldIn, pos) == EnumFacing.DOWN || TurretHeadUtil.getTurretBaseFacing(worldIn, pos) == EnumFacing.UP) {
            return true;
        }
        TurretBase base = getTurretBase(worldIn, pos);
        LeverTileEntity lever = (LeverTileEntity) worldIn.getTileEntity(pos);
        OMEnergyStorage storage = (OMEnergyStorage) base.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN);
        if (storage == null) {
            return true;
        }
        if (lever != null && (worldIn.getBlockState(pos).getValue(ROTATION) * 90) == 0 &&
                isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)))) {
            base = (TurretBase) worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1));
            if (base != null) {
                lever.isTurning = true;

                if (lever.rotation == 0F) {
                    //worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), "omtteam.openmodularturrets:windup", 1.0F, 1.0F);
                    storage.receiveEnergy(50, false);
                }
            }
        }

        if (lever != null && (worldIn.getBlockState(pos).getValue(ROTATION) * 90) == 90 &&
                isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())))) {
            base = (TurretBase) worldIn.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    //worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "omtteam.openmodularturrets:windup", 1.0F, 1.0F);
                    storage.receiveEnergy(50, false);
                }
            }
        }

        if (lever != null && (worldIn.getBlockState(pos).getValue(ROTATION) * 90) == 180 &&
                isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)))) {
            base = (TurretBase) worldIn.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    //worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "omtteam.openmodularturrets:windup", 1.0F, 1.0F);
                    storage.receiveEnergy(50, false);
                }
            }
        }

        if (lever != null && (worldIn.getBlockState(pos).getValue(ROTATION) * 90) == 270 &&
                isBaseValid(worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())))) {
            base = (TurretBase) worldIn.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()));
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    //worldIn.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "omtteam.openmodularturrets:windup", 1.0F, 1.0F);
                    storage.receiveEnergy(50, false);
                }
            }
        }
        return true;
    }

    @Override
    @Nonnull
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

    @Override
    public AxisAlignedBB getBoundingBoxFromFacing(EnumFacing facing, World world, BlockPos pos) {
        facing = getTurretBaseFacing(world, pos);
        if (getTurretBase(world, pos) != null && getTurretBase(world, pos).getTier() == 1) {
            if (facing != null) {
                AxisAlignedBB axisAlignedBB = new AxisAlignedBB(-0.3F, -0.3F, -0.4F, 0.3F, 0.3F, 0.4F);
                axisAlignedBB = MathUtil.rotateAABB(axisAlignedBB, facing.getOpposite());
                double[] offset = new double[3];
                offset[0] = 0.5D + facing.getFrontOffsetX() * 0.1D;
                offset[1] = 0.5D + facing.getFrontOffsetY() * 0.1D;
                offset[2] = 0.5D + facing.getFrontOffsetZ() * 0.1D;
                return axisAlignedBB.offset(pos).offset(offset[0], offset[1], offset[2]);
            }
        }
        return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F).offset(pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos neighbor) {
        if (!(isBaseValid(worldIn.getTileEntity(pos.north())) ||
                isBaseValid(worldIn.getTileEntity(pos.east())) ||
                isBaseValid(worldIn.getTileEntity(pos.south())) ||
                isBaseValid(worldIn.getTileEntity(pos.west())))) {
            worldIn.destroyBlock(pos, true);
        }
    }
}
