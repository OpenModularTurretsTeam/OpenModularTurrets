package omtteam.openmodularturrets.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.blocks.BlockAbstractTileEntity;
import omtteam.omlib.util.IHasItemBlock;
import omtteam.omlib.util.MathUtil;
import omtteam.omlib.util.PlayerUtil;
import omtteam.omlib.util.TrustedPlayer;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.items.blocks.ItemBlockExpander;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.Expander;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.ITurretBaseAddonBlock;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;
import static omtteam.omlib.util.WorldUtil.getTouchingTileEntities;
import static omtteam.omlib.util.compat.ChatTools.addChatMessage;

/**
 * Created by Keridos on 19/07/16.
 * This Class
 */
@SuppressWarnings("deprecation")
public class BlockExpander extends BlockAbstractTileEntity implements IHasItemBlock, ITurretBaseAddonBlock {
    private static final PropertyInteger META = PropertyInteger.create("meta", 0, 9);
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public BlockExpander() {
        super(Material.GLASS);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        if (!ConfigHandler.turretBreakable) {
            this.setBlockUnbreakable();
        }
        this.setResistance(3.0F);
        this.setHardness(3.0F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(META, 0));
        this.setUnlocalizedName(OMTNames.Blocks.expander);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.expander);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockExpander(block);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(META, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(META);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, META, FACING);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Expander te = ((Expander) worldIn.getTileEntity(pos));
        if (te != null) {
            return state.withProperty(FACING, te.getOrientation());
        } else return state.withProperty(FACING, EnumFacing.NORTH);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (state.getValue(META) < 5) {
            return new Expander(state.getValue(META), false);
        } else {
            return new Expander(state.getValue(META), true);
        }
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        IBlockState blockState = this.getActualState(state, source, pos);
        return MathUtil.rotateAABB(new AxisAlignedBB(1 / 8F, 1 / 8F, 0F, 7 / 8F, 7 / 8F, 3 / 8F), blockState.getValue(FACING).getOpposite());
    }

    @Override
    public AxisAlignedBB getBoundingBoxFromState(IBlockState blockState, World world, BlockPos pos) {
        return MathUtil.rotateAABB(new AxisAlignedBB(1 / 8F, 1 / 8F, 0F, 7 / 8F, 7 / 8F, 3 / 8F), blockState.getValue(FACING).getOpposite()).offset(pos);
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    protected boolean clOnBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hand.equals(EnumHand.OFF_HAND)) return true;
        Expander expander = (Expander) worldIn.getTileEntity(pos);
        if (expander == null) {
            return true;
        }
        TurretBase base = expander.getBase();
        if (base == null) {
            worldIn.destroyBlock(pos, true);
            return true;
        }
        TrustedPlayer trustedPlayer = PlayerUtil.getTrustedPlayer(playerIn, base);
        if (trustedPlayer != null) {
            if (base.getTrustedPlayer(playerIn.getUniqueID()).canOpenGUI && state.getValue(META) < 5) {
                playerIn.openGui(OpenModularTurrets.instance, 7, worldIn, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }
        if (PlayerUtil.isPlayerOwner(playerIn, base)) {
            if (playerIn.isSneaking() && playerIn.getHeldItemMainhand() == null) {
                worldIn.destroyBlock(pos, true);
            } else if (state.getValue(META) < 5) {
                playerIn.openGui(OpenModularTurrets.instance, 7, worldIn, pos.getX(), pos.getY(), pos.getZ());
            } else {
                return true;
            }
        } else {
            addChatMessage(playerIn, new TextComponentString(safeLocalize("status.ownership")));
        }
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (TileEntity tileEntity : getTouchingTileEntities(worldIn, pos)) {
            if (tileEntity instanceof TurretBase) return true;
        }
        return false;
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
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        Expander expander = (Expander) worldIn.getTileEntity(pos);
        if (expander != null) {
            expander.setOwnerName(expander.getBase().getOwnerName());
            expander.setOwner(expander.getBase().getOwner());
            expander.setSide();
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(META);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void clGetSubBlocks(Item item, CreativeTabs tab, List subItems) {
        for (int i = 0; i < 10; i++) {
            subItems.add(new ItemStack(ModBlocks.expander, 1, i));
        }
    }
}
