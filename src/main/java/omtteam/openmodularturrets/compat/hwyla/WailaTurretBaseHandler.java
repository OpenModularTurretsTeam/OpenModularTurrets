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
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.base.TurretBaseOverlay;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by nico on 5/23/15.
 * Waila/Hwyla Interface.
 */

@SuppressWarnings("unused")
public class WailaTurretBaseHandler implements IOMLibWailaDataProvider {
    /**
     * The callbackRegister method is used by Waila to register this data provider. Note that inside this
     * method we initialize a new instance of this class, this instance is used for a lot of the
     * IWailaRegistrar methods require an instance of the data provider to work. This will also call the
     * constructor of this class, which can be used to help initialize other things. Alternatively you
     * can initialize things within this method as well.
     */

    public void callbackRegister(IWailaRegistrar register) {
        WailaTurretBaseHandler instance = new WailaTurretBaseHandler();

        register.registerNBTProvider(instance, TurretBase.class);
        register.registerStackProvider(instance, TurretBase.class);
        register.registerBodyProvider(instance, TurretBase.class);
    }

    /** The disguise as the block players see when they may not see through it (1.20.1+ Jade). */
    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (!isDisguised(accessor)) return ItemStack.EMPTY;
        IBlockState camo = ((TurretBase) accessor.getTileEntity()).getCamoState();
        return camo.getBlock().getPickBlock(camo, accessor.getMOP(), accessor.getWorld(), accessor.getPosition(),
                accessor.getPlayer());
    }

    /** The lines of {@link TurretBaseOverlay}; none for a disguised base. */
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (!isDisguised(accessor)) {
            currenttip.addAll(TurretBaseOverlay.lines(accessor.getNBTData(), GeneralUtil::safeLocalize));
        }
        return currenttip;
    }

    /**
     * Whether the base shows its disguise: it has one and the server did not send its data, which it only does for
     * players who may see through the disguise (so also while no data has arrived yet).
     */
    private static boolean isDisguised(IWailaDataAccessor accessor) {
        TileEntity te = accessor.getTileEntity();
        return te instanceof TurretBase && ((TurretBase) te).getCamoState().getBlock() != accessor.getBlock()
                && !TurretBaseOverlay.hasData(accessor.getNBTData());
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
        if (te instanceof TurretBase) {
            TurretBase base = (TurretBase) te;
            // players who see the disguise get nothing that gives the base away
            if (!TurretBaseOverlay.isDisguisedFor(base, world.getBlockState(pos), player)) {
                tag.merge(TurretBaseOverlay.data(base));
            }
        }
        return tag;
    }
}
