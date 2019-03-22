package omtteam.openmodularturrets.compatibility.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.omlib.compatibility.hwyla.IOMLibWailaDataProvider;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.EnumMachineMode;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.Nonnull;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.*;

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
        register.registerBodyProvider(instance, TurretBase.class);
    }

    /**
     * This method adds data to the body of the waila tool tip. This is where you should place the
     * majority of your data. The accessor is an object wrapper which contains all relevant data while
     * the config parameter allows you to take advantage of the ingame config gui.
     */
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        EnumMachineMode machineMode = EnumMachineMode.values()[accessor.getNBTData().getInteger("mode")];
        boolean active = accessor.getNBTData().getBoolean("active");
        currenttip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.MODE) + ": \u00A7A" + getMachineModeLocalization(machineMode));
        currenttip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.ACTIVE) + ": " + getColoredBooleanLocalizationYesNo(active));
        String ownerName = accessor.getNBTData().getString("ownerName");
        currenttip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.OWNER) + ": \u00A7F" + ownerName);
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
        if (te instanceof TurretBase) {
            TurretBase base = (TurretBase) te;
            // te.writeToNBT(tag);
            tag.setBoolean("active", base.isActive());
            tag.setInteger("mode", base.getMode().ordinal());
            tag.setString("ownerName", base.getOwner().getName());
        }

        return tag;
    }
}
