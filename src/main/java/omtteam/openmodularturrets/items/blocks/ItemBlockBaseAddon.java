package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.compatability.minecraft.CompatItemBlock;
import omtteam.openmodularturrets.util.ITurretBaseAddonBlock;

/**
 * Created by Keridos on 17/05/17.
 * This Class
 */
public abstract class ItemBlockBaseAddon extends CompatItemBlock {
    public ItemBlockBaseAddon(Block block) {
        super(block);
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderOutline(IBlockState state, World world, BlockPos pos) {
        return ((ITurretBaseAddonBlock) this.getBlock()).getBoundingBoxFromState(state, world, pos);
    }
}
