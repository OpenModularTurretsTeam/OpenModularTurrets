package openmodularturrets.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.util.BlockAbstractContainer;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.init.ModBlocks;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.TurretBase;

import java.util.ArrayList;
import java.util.List;

public class BlockTurretBase extends BlockAbstractContainer {
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 1, 5);

    public BlockTurretBase() {
        super(Material.rock);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        if (!ConfigHandler.turretBreakable) {
            this.setBlockUnbreakable();
        }
        this.setStepSound(Block.soundTypeStone);
        this.setUnlocalizedName(Names.Blocks.turretBase);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        int MaxCharge;
        int MaxIO;
        switch (meta) {
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
    public boolean isOpaqueCube() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TIER, meta + 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TIER) - 1;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, TIER);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (!worldIn.isRemote && player.isSneaking() && ConfigHandler.isAllowBaseCamo() && player.getCurrentEquippedItem() == null) {
            TurretBase base = (TurretBase) worldIn.getTileEntity(pos);
            if (base != null) {
                if (player.getUniqueID().toString().equals(base.getOwner())) {
                    base.camoStack = null;
                } else {
                    player.addChatMessage(
                            new ChatComponentText(StatCollector.translateToLocal("status.ownership")));
                }
            }
        }

        if (!worldIn.isRemote && !player.isSneaking() && ConfigHandler.isAllowBaseCamo() && player.getCurrentEquippedItem() != null &&
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
                            new ChatComponentText(StatCollector.translateToLocal("status.ownership")));
                }
            }

        } else if (!worldIn.isRemote && !player.isSneaking()) {
            TurretBase base = (TurretBase) worldIn.getTileEntity(pos);
            if (base.getTrustedPlayer(player.getUniqueID()) != null) {
                if (base.getTrustedPlayer(player.getUniqueID()).canOpenGUI) {
                    player.openGui(ModularTurrets.instance, base.getBaseTier(), worldIn, pos.getX(), pos.getY(), pos.getZ());
                    return true;
                }
            }
            if (player.getUniqueID().toString().equals(base.getOwner())) {
                player.openGui(ModularTurrets.instance, base.getBaseTier(), worldIn, pos.getX(), pos.getY(), pos.getZ());
            } else {
                player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("status.ownership")));
            }
        }
        return true;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {

            if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TurretBase) {
                if (worldIn.isBlockIndirectlyGettingPowered(pos) > 0) {
                    ((TurretBase) worldIn.getTileEntity(pos)).setRedstone(true);
                } else if (worldIn.isBlockIndirectlyGettingPowered(pos) == 0) {
                    ((TurretBase) worldIn.getTileEntity(pos)).setRedstone(false);
                }
            }

    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TurretBase) {
            EntityPlayerMP player = (EntityPlayerMP) placer;
            TurretBase base = (TurretBase) worldIn.getTileEntity(pos);
            base.setOwner(player.getUniqueID().toString());
            if (worldIn.isBlockIndirectlyGettingPowered(pos) > 0) {
                base.setRedstone(true);
            } else if (worldIn.isBlockIndirectlyGettingPowered(pos) == 0) {
                base.setRedstone(false);
            }
            switch (state.getValue(TIER)) {
                case 1:
                    this.setResistance(ConfigHandler.getBaseTierOneBlastResistance());
                case 2:
                    this.setResistance(ConfigHandler.getBaseTierTwoBlastResistance());
                case 3:
                    this.setResistance(ConfigHandler.getBaseTierThreeBlastResistance());
                case 4:
                    this.setResistance(ConfigHandler.getBaseTierFourBlastResistance());
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
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(0, new ItemStack(ModBlocks.turretBase, 1, this.getMetaFromState(state)));
        return drops;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TIER)-1;
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
