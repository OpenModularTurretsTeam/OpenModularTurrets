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
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import javax.annotation.Nonnull;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.getColoredBooleanLocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.safeLocalize;
import static omtteam.openmodularturrets.util.TurretHeadUtil.*;

/**
 * Created by nico on 5/23/15.
 * Waila/Hwyla Interface.
 */

@SuppressWarnings("unused")
public class WailaTurretHandler implements IWailaDataProvider {
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
        WailaTurretHandler instance = new WailaTurretHandler();

        register.registerNBTProvider(instance, TurretHead.class);
        register.registerBodyProvider(instance, TurretHead.class);
    }

    /**
     * This method allows you to change what ItemStack is used for the Waila tooltip. This is used by
     * Waila to make silverfish stone look like normal stone in the Waila hud. This is usually used as a
     * way to prevent people from cheating. It can also be used to correct block data. Note that there is
     * a bug with this method in that it will only affect the head of the tool tip. The body and tail
     * method will ignore any changes made here.
     */
    @Nonnull
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
    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    /**
     * This method adds data to the body of the waila tool tip. This is where you should place the
     * majority of your data. The accessor is an object wrapper which contains all relevant data while
     * the config parameter allows you to take advantage of the ingame config gui.
     */
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getWorld().isRemote) {
            TileEntity te = accessor.getTileEntity();
            if (te instanceof TurretHead && currenttip.size() == 0) {
                TurretHead turret = (TurretHead) te;
                boolean active = turret.getBase().isActive();

                currenttip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.ACTIVE) + ": " + getColoredBooleanLocalizationYesNo(active));
                String ownerName = turret.getBase().getOwnerName();
                currenttip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.OWNER) + ": \u00A7F" + ownerName);
                currenttip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.AMMO) + ": \u00A7F" + getAmmoLevel(turret, turret.getBase()));
                currenttip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.DAMAGE_AMP) + ": \u00A7F" + String.format("%.2f", turret.getTurretDamageAmpBonus() * 100 * getAmpLevel(turret.getBase())) + "%");
                currenttip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.ACCURACY) + ": \u00A7F" + String.format("%.2f", Math.min(100F, (100 - turret.getTurretAccuracy() * 10) * (1.0 + getAccuraccyUpgrades(turret.getBase())))) + "%");
                currenttip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.RATE_OF_FIRE) + ": \u00A7F" + String.format("%.2f", 20F / (turret.getTurretFireRate() * (1 - TurretHeadUtil.getFireRateUpgrades(turret.getBase())))) + "s/sec");
            }
        }
        return currenttip;
    }

    /**
     * This method adds data to the tail of the waila tool tip. This is where the game displays the name
     * of the mod which adds this block to the game. The accessor is an object wrapper which contains all
     * relevant data while the config parameter allows you to take advantage of the ingame config gui.
     */
    @Nonnull
    @Override
    @Optional.Method(modid = "waila")
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
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
        if (te != null) {
            te.writeToNBT(tag);
        }

        return tag;
    }
}
