package openmodularturrets.util;

import net.minecraft.tileentity.TileEntity;
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
        list.add(world.getTileEntity(pos.up()));
        list.add(world.getTileEntity(pos.down()));
        list.add(world.getTileEntity(pos.east()));
        list.add(world.getTileEntity(pos.south()));
        list.add(world.getTileEntity(pos.west()));
        list.add(world.getTileEntity(pos.north()));
        return list;

    }
}
