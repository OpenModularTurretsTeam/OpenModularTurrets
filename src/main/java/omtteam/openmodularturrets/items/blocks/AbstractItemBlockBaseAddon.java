package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.items.IDrawOutlineBase;
import omtteam.openmodularturrets.api.ITurretBaseAddonBlock;
import omtteam.openmodularturrets.turret.TurretHeadUtil;

import javax.annotation.Nullable;

/**
 * Created by Keridos on 17/05/17.
 * This Class
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractItemBlockBaseAddon extends ItemBlock implements IDrawOutlineBase {
    public AbstractItemBlockBaseAddon(Block block) {
        super(block);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderOutline(EnumFacing facing, World world, BlockPos pos) {
        return ((ITurretBaseAddonBlock) this.getBlock()).getBoundingBoxFromFacing(facing, world, pos);
    }

    @Nullable
    @Override
    public EnumFacing getBaseFacing(World world, BlockPos pos) {
        return TurretHeadUtil.getTurretBaseFacing(world, pos);
    }
}
