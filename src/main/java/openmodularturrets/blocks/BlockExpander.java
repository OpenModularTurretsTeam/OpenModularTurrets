package openmodularturrets.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
import openmodularturrets.init.ModBlocks;
import openmodularturrets.tileentity.Expander;
import openmodularturrets.tileentity.TurretBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keridos on 19/07/16.
 * This Class
 */
public class BlockExpander extends BlockAbstractContainer {
    public static final PropertyInteger META = PropertyInteger.create("meta", 0, 9);
    public BlockExpander() {
        super(Material.rock);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setResistance(3.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockBounds(0.1F, 0.1F, 0.1F, 0.9F, 0.9F, 0.9F);
    }

    @Override
    @SuppressWarnings("unchecked")
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(META, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(META) ;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, META);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta < 5) {
            return new Expander(meta, false);
        }else {
            return new Expander(meta, true);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (state.getValue(META) > 4) return true;
        Expander expander = (Expander) worldIn.getTileEntity(pos);
        TurretBase base = expander.getBase();
        if (base != null && base.getTrustedPlayer(player.getUniqueID()) != null) {
            if (base.getTrustedPlayer(player.getUniqueID()).canOpenGUI) {
                player.openGui(ModularTurrets.instance, 7, worldIn, pos.getX(), pos.getX(), pos.getZ());
                return true;
            }
        }
        if (base != null && player.getUniqueID().toString().equals(base.getOwner())) {
            player.openGui(ModularTurrets.instance, 7, worldIn, pos.getX(), pos.getX(), pos.getZ());
        } else {
            player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("status.ownership")));
        }
        return true;
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
        return state.getValue(META);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
        for (int i = 0; i < 10; i++) {
            subItems.add(new ItemStack(ModBlocks.expander, 1, i));
        }
    }
}
