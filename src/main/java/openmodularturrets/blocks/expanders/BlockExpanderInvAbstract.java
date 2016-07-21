package openmodularturrets.blocks.expanders;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.util.BlockAbstractContainer;
import openmodularturrets.tileentity.expander.AbstractInvExpander;
import openmodularturrets.tileentity.turretbase.TurretBase;

/**
 * Created by Keridos on 19/07/16.
 * This Class
 */
public abstract class BlockExpanderInvAbstract extends BlockAbstractContainer {
    public BlockExpanderInvAbstract(Material material) {
        super(material);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        AbstractInvExpander expander = (AbstractInvExpander) worldIn.getTileEntity(pos);
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
}
