package omtteam.openmodularturrets.compatibility.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.tileentity.EnumMachineMode;
import omtteam.openmodularturrets.tileentity.TurretBase;

import java.util.List;

import static omtteam.omlib.util.GeneralUtil.*;

/**
 * Created by nico on 5/23/15.
 * Waila/Hwyla Interface.
 */

@SuppressWarnings("unused")
@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public class WailaTurretBaseHandler implements IWailaDataProvider {
    /**
     * Although this is likely not necessary, you can also use the Optional.Method interface to mark a
     * method to be stripped if a mod is not detected. In this case we're doing this for all methods
     * which relate to Waila, so the modid is Waila.
     * <p/>
     * The callbackRegister method is used by Waila to register this data provider. Note that inside this
     * method we initialize a new instance of this class, this instance is used for a lot of the
     * IWailaRegistrar methods require an instance of the data provider to work. This will also call the
     * constructor of this class, which can be used to help initialize other things. Alternatively you
     * can initialize things within this method as well.
     */

    static void callbackRegister(IWailaRegistrar register) {
        WailaTurretBaseHandler instance = new WailaTurretBaseHandler();

        register.registerNBTProvider(instance, TurretBase.class);
        register.registerBodyProvider(instance, TurretBase.class);
    }

    /**
     * This method allows you to change what ItemStack is used for the Waila tooltip. This is used by
     * Waila to make silverfish stone look like normal stone in the Waila hud. This is usually used as a
     * way to prevent people from cheating. It can also be used to correct block data. Note that there is
     * a bug with this method in that it will only affect the head of the tool tip. The body and tail
     * method will ignore any changes made here.
     */
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return accessor.getStack();
    }

    /**
     * The Waila hud is devided up into three sections, this is to allow for data to be aranged in a nice
     * way. This method adds data to the header of the waila tool tip. This is where the game displays
     * the name of the block. The accessor is an object wrapper which contains all relevant data while
     * the config parameter allows you to take advantage of the ingame config gui.
     */
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    /**
     * This method adds data to the body of the waila tool tip. This is where you should place the
     * majority of your data. The accessor is an object wrapper which contains all relevant data while
     * the config parameter allows you to take advantage of the ingame config gui.
     */
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        EnumMachineMode machineMode = EnumMachineMode.values()[accessor.getNBTData().getInteger("mode")];
        boolean active = accessor.getNBTData().getBoolean("inverted") ^ accessor.getNBTData().getBoolean("redstone");
        currenttip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.MODE) + ": \u00A7A" + getMachineModeLocalization(machineMode));
        currenttip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.ACTIVE) + ": " + getColoredBooleanLocalizationYesNo(active));
        String ownerName = accessor.getNBTData().getString("ownerName");
        currenttip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.OWNER) + ": \u00A7F" + ownerName);
        return currenttip;
    }

    /**
     * This method adds data to the tail of the waila tool tip. This is where the game displays the name
     * of the mod which adds this block to the game. The accessor is an object wrapper which contains all
     * relevant data while the config parameter allows you to take advantage of the ingame config gui.
     */
    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    /**
     * This method is used to sync data between server and client easily. The tag parameter is the nbt
     * tag which is provided when accessor.getNBTData() is called. Luckily for us, most of the time you
     * can simply use te.writeToNBT(tag) to take advantage of the built in nbt save function for tile
     * entities.
     */

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te != null) {
            te.writeToNBT(tag);
        }

        return tag;
    }
}
