package omtteam.openmodularturrets.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.util.PlayerUtil;
import omtteam.omlib.util.TrustedPlayer;
import omtteam.openmodularturrets.ModularTurrets;
import omtteam.openmodularturrets.blocks.util.BlockAbstractTileEntity;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockTurretBase extends BlockAbstractTileEntity {
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 1, 5);

    public BlockTurretBase() {
        super(Material.ROCK);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        if (!ConfigHandler.turretBreakable) {
            this.setBlockUnbreakable();
        }
        setDefaultState(this.blockState.getBaseState().withProperty(TIER, 1));
        this.setSoundType(SoundType.STONE);
        this.setUnlocalizedName(Names.Blocks.turretBase);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        int MaxCharge;
        int MaxIO;
        switch (state.getValue(TIER) - 1) {
            case 0:
                MaxCharge = ConfigHandler.getBaseTierOneMaxCharge();
                MaxIO = ConfigHandler.getBaseTierOneMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 1);
            case 1:
                MaxCharge = ConfigHandler.getBaseTierTwoMaxCharge();
                MaxIO = ConfigHandler.getBaseTierTwoMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 2);
            case 2:
                MaxCharge = ConfigHandler.getBaseTierThreeMaxCharge();
                MaxIO = ConfigHandler.getBaseTierThreeMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 3);
            case 3:
                MaxCharge = ConfigHandler.getBaseTierFourMaxCharge();
                MaxIO = ConfigHandler.getBaseTierFourMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 4);
            case 4:
                MaxCharge = ConfigHandler.getBaseTierFiveMaxCharge();
                MaxIO = ConfigHandler.getBaseTierFiveMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 5);
        }
        return null;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return true;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TIER, meta + 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TIER) - 1;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TIER);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TurretBase base = (TurretBase) world.getTileEntity(pos);
        /*if (!world.isRemote && player.isSneaking() && ConfigHandler.isAllowBaseCamo() && player.getCurrentEquippedItem() == null) {
            TurretBase base = (TurretBase) world.getTileEntity(pos);
            if (base != null) {
                if (player.getUniqueID().toString().equals(base.getOwner())) {
                    base.camoStack = null;
                } else {
                    player.addChatMessage(
                            new TextComponentString(I18n.translateToLocal("status.ownership")));
                }
            }
        }

        if (!world.isRemote && !player.isSneaking() && ConfigHandler.isAllowBaseCamo() && player.getCurrentEquippedItem() != null &&
                player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemBlock &&
                Block.getBlockFromItem(player.getCurrentEquippedItem().getItem()).isNormalCube() && Block.getBlockFromItem(
                player.getCurrentEquippedItem().getItem()).isOpaqueCube() && !(Block.getBlockFromItem(
                player.getCurrentEquippedItem().getItem()) instanceof BlockTurretBase)) {
            TurretBase base = (TurretBase) worldIn.getTileEntity(pos);
            if (base != null) {
                if (player.getUniqueID().toString().equals(base.getOwner())) {
                    base.camoStack = player.getCurrentEquippedItem();
                } else {
                    player.addChatMessage(
                            new TextComponentString(I18n.translateToLocal("status.ownership")));
                }
            }

        } else  */
        if (!world.isRemote && !player.isSneaking()  && base != null) {
            TrustedPlayer trustedPlayer = PlayerUtil.getTrustedPlayer(player, base, ConfigHandler.offlineModeSupport);
            if (trustedPlayer != null && trustedPlayer.canOpenGUI) {
                player.openGui(ModularTurrets.instance, base.getTier(), world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        } else if (base != null && PlayerUtil.isPlayerOwner(player, base, ConfigHandler.offlineModeSupport)) {
            player.openGui(ModularTurrets.instance, base.getTier(), world, pos.getX(), pos.getY(), pos.getZ());
        } else {
            player.addChatMessage(new TextComponentString(I18n.translateToLocal("status.ownership")));
        }
        return true;
}

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        if (!worldIn.isRemote) {
            TurretBase base = (TurretBase) worldIn.getTileEntity(pos);
            if (base != null && worldIn.isBlockIndirectlyGettingPowered(pos) > 0) {
                base.setRedstone(true);
            } else if (base != null && worldIn.isBlockIndirectlyGettingPowered(pos) == 0) {
                base.setRedstone(false);
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TurretBase) {
            EntityPlayerMP player = (EntityPlayerMP) placer;
            TurretBase base = (TurretBase) worldIn.getTileEntity(pos);
            if (base == null) {
                return;
            }
            base.setOwner(player.getUniqueID().toString());
            if (worldIn.isBlockIndirectlyGettingPowered(pos) > 0) {
                base.setRedstone(true);
            } else if (worldIn.isBlockIndirectlyGettingPowered(pos) == 0) {
                base.setRedstone(false);
            }
            switch (state.getValue(TIER)) {
                case 1:
                    this.setResistance(ConfigHandler.getBaseTierOneBlastResistance());
                    break;
                case 2:
                    this.setResistance(ConfigHandler.getBaseTierTwoBlastResistance());
                    break;
                case 3:
                    this.setResistance(ConfigHandler.getBaseTierThreeBlastResistance());
                    break;
                case 4:
                    this.setResistance(ConfigHandler.getBaseTierFourBlastResistance());
                    break;
                case 5:
                    this.setResistance(ConfigHandler.getBaseTierFiveBlastResistance());
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            dropItems(worldIn, pos);
            super.breakBlock(worldIn, pos, state);
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(0, new ItemStack(ModBlocks.turretBase, 1, this.getMetaFromState(state)));
        return drops;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(ModBlocks.turretBase, 1, state.getValue(TIER) - 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
        for (int i = 0; i < 5; i++) {
            subItems.add(new ItemStack(ModBlocks.turretBase, 1, i));
        }
    }
}
