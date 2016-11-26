package openmodularturrets.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Created by Keridos on 20/07/16.
 * This Class
 */
public class WorldUtil {
    public static ArrayList<TileEntity> getTouchingTileEntities(World world, BlockPos pos){
        ArrayList<TileEntity> list = new ArrayList<>();
        for (EnumFacing facing: EnumFacing.VALUES) {
            list.add(world.getTileEntity(pos.offset(facing)));
        }
        return list;
    }
}
