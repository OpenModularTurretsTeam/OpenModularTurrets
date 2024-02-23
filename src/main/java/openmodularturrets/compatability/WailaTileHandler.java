package openmodularturrets.compatability;

import static openmodularturrets.util.GeneralUtil.safeLocalize;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.Optional;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turretbase.TurretBase;

/**
 * Created by nico on 5/23/15.
 */
@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public class WailaTileHandler implements IWailaDataProvider {

    /**
     * Although this is likely not necessary, you can also use the Optional.Method interface to mark a method to be
     * stripped if a mod is not detected. In this case we're doing this for all methods which relate to Waila, so the
     * modid is Waila.
     * <p/>
     * The callbackRegister method is used by Waila to register this data provider. Note that inside this method we
     * initialize a new instance of this class, this instance is used for a lot of the IWailaRegistrar methods require
     * an instance of the data provider to work. This will also call the constructor of this class, which can be used to
     * help initialize other things. Alternatively you can initialize things within this method as well.
     */
    @Optional.Method(modid = "Waila")
    public static void callbackRegister(IWailaRegistrar register) {
        WailaTileHandler instance = new WailaTileHandler();

        register.registerNBTProvider(instance, TurretBase.class);
        register.registerBodyProvider(instance, TurretBase.class);
    }

    /**
     * This method allows you to change what ItemStack is used for the Waila tooltip. This is used by Waila to make
     * silverfish stone look like normal stone in the Waila hud. This is usually used as a way to prevent people from
     * cheating. It can also be used to correct block data. Note that there is a bug with this method in that it will
     * only affect the head of the tool tip. The body and tail method will ignore any changes made here.
     */
    @Override
    @Optional.Method(modid = "Waila")
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return accessor.getStack();
    }

    /**
     * The Waila hud is devided up into three sections, this is to allow for data to be aranged in a nice way. This
     * method adds data to the header of the waila tool tip. This is where the game displays the name of the block. The
     * accessor is an object wrapper which contains all relevant data while the config parameter allows you to take
     * advantage of the ingame config gui.
     */
    @Override
    @Optional.Method(modid = "Waila")
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    /**
     * This method adds data to the body of the waila tool tip. This is where you should place the majority of your
     * data. The accessor is an object wrapper which contains all relevant data while the config parameter allows you to
     * take advantage of the ingame config gui.
     */
    @Override
    @Optional.Method(modid = "Waila")
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        boolean invert = accessor.getNBTData().getBoolean("inverted");
        boolean active = accessor.getNBTData().getBoolean("inverted") ^ accessor.getNBTData().getBoolean("redstone");
        String inverted = (invert ? Names.Localizations.TRUE : Names.Localizations.FALSE);
        String isActive = (active ? Names.Localizations.TRUE : Names.Localizations.FALSE);
        currenttip.add(safeLocalize(Names.Localizations.INVERT) + ": " + safeLocalize(inverted));
        currenttip.add(safeLocalize(Names.Localizations.ACTIVE) + ": " + safeLocalize(isActive));
        String ownerName = accessor.getNBTData().getString("ownerName");
        currenttip.add(safeLocalize(Names.Localizations.OWNER) + ": " + ownerName);
        return currenttip;
    }

    /**
     * This method adds data to the tail of the waila tool tip. This is where the game displays the name of the mod
     * which adds this block to the game. The accessor is an object wrapper which contains all relevant data while the
     * config parameter allows you to take advantage of the ingame config gui.
     */
    @Override
    @Optional.Method(modid = "Waila")
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    /**
     * This method is used to sync data between server and client easily. The tag parameter is the nbt tag which is
     * provided when accessor.getNBTData() is called. Luckily for us, most of the time you can simply use
     * te.writeToNBT(tag) to take advantage of the built in nbt save function for tile entities.
     */

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
            int y, int z) {
        if (te != null) {
            te.writeToNBT(tag);
        }

        return tag;
    }
}
