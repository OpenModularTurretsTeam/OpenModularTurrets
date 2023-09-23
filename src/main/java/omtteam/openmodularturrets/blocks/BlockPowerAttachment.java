package omtteam.openmodularturrets.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.PowerGenerator;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.util.player.PlayerUtil.addChatMessage;

public class BlockPowerAttachment extends AbstractBaseAttachment {
    public BlockPowerAttachment() {
        super(Material.GLASS);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        if (!OMTConfig.TURRETS.turretBreakable) {
            this.setBlockUnbreakable();
        }
        this.setResistance(3.0F);
        this.setHardness(3.0F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState());
        this.setUnlocalizedName(OMTNames.Blocks.powerGenerator);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.powerGenerator);
    }


    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState();
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState_OM() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te = (worldIn.getTileEntity(pos));
        if (te instanceof PowerGenerator) {
            return state.withProperty(FACING, ((PowerGenerator) te).getOrientation());
        } else return state.withProperty(FACING, EnumFacing.NORTH);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity_OM(World world, IBlockState state) {
        return new PowerGenerator();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isFullBlock_OM(IBlockState state) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isOpaqueCube_OM(IBlockState state) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated_OM(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hand.equals(EnumHand.OFF_HAND)) return true;
        PowerGenerator powerGenerator = (PowerGenerator) worldIn.getTileEntity(pos);
        if (powerGenerator == null) {
            return true;
        }
        TurretBase base = powerGenerator.getBase();
        if (base == null) {
            worldIn.destroyBlock(pos, true);
            return true;
        }

        if (PlayerUtil.canPlayerAccessBlock(playerIn, base)) {
            playerIn.openGui(OpenModularTurrets.instance, 2, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        if (PlayerUtil.isPlayerOwner(playerIn, base)) {
            if (playerIn.isSneaking() && playerIn.getHeldItemMainhand().isEmpty()) {
                worldIn.destroyBlock(pos, true);
            } else {
                playerIn.openGui(OpenModularTurrets.instance, 3, worldIn, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        } else {
            addChatMessage(playerIn, new TextComponentTranslation(OMLibNames.Localizations.Text.STATUS_PERMISSION));
        }
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            dropItems(worldIn, pos);
            super.breakBlock(worldIn, pos, state);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockPlacedBy_OM(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        PowerGenerator powerGenerator = (PowerGenerator) worldIn.getTileEntity(pos);
        if (powerGenerator != null) {
            powerGenerator.setOwner(powerGenerator.getBase().getOwner());
            powerGenerator.setSide();
        }
    }
}
