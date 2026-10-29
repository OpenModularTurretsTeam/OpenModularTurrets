package omtteam.openmodularturrets.compat.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.omlib.compat.hwyla.IOMLibWailaDataProvider;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.tileentity.turret.TurretOverlay;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by nico on 5/23/15.
 * Waila/Hwyla Interface.
 */

@SuppressWarnings("unused")
public class WailaTurretHandler implements IOMLibWailaDataProvider {
    /**
     * The callbackRegister method is used by Waila to register this data provider. Note that inside this
     * method we initialize a new instance of this class, this instance is used for a lot of the
     * IWailaRegistrar methods require an instance of the data provider to work. This will also call the
     * constructor of this class, which can be used to help initialize other things. Alternatively you
     * can initialize things within this method as well.
     */
    public void callbackRegister(IWailaRegistrar register) {
        WailaTurretHandler instance = new WailaTurretHandler();

        register.registerNBTProvider(instance, TurretTileEntity.class);
        register.registerBodyProvider(instance, TurretTileEntity.class);
    }

    /** The lines of {@link TurretOverlay}; none for a concealed turret. */
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        IBlockState state = accessor.getBlockState();
        boolean concealed = state.getBlock() instanceof BlockTurret && state.getValue(BlockTurret.CONCEALED);
        currenttip.addAll(TurretOverlay.lines(accessor.getNBTData(), concealed, GeneralUtil::safeLocalize));
        return currenttip;
    }

    /**
     * This method is used to sync data between server and client easily. The tag parameter is the nbt
     * tag which is provided when accessor.getNBTData() is called. Luckily for us, most of the time you
     * can simply use te.writeToNBT(tag) to take advantage of the built in nbt save function for tile
     * entities.
     */

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te instanceof TurretTileEntity) {
            tag.merge(TurretOverlay.data((TurretTileEntity) te, player));
        }
        return tag;
    }
}
