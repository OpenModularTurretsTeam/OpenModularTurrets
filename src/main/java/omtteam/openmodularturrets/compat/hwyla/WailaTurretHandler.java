package omtteam.openmodularturrets.compat.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.omlib.compat.hwyla.IOMLibWailaDataProvider;
import omtteam.omlib.reference.OMLibNames;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;

import javax.annotation.Nonnull;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.getColoredBooleanLocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.safeLocalize;

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

    /**
     * This method adds data to the body of the waila tool tip. This is where you should place the
     * majority of your data. The accessor is an object wrapper which contains all relevant data while
     * the config parameter allows you to take advantage of the ingame config gui.
     */
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity te = accessor.getTileEntity();
        if (te instanceof TurretTileEntity) {
            NBTTagCompound data = accessor.getNBTData();
            boolean active = data.getBoolean("active");

            currenttip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.ACTIVE) + ": " + getColoredBooleanLocalizationYesNo(active));
            currenttip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.OWNER) + ": \u00A7F" + (data.hasKey("ownerName")
                    ? data.getString("ownerName") : safeLocalize("text.omlib.unowned")));
            if (!data.hasKey("ammoLevel")) {
                // no base: the stats come from the base
                return currenttip;
            }
            currenttip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.AMMO) + ": \u00A7F" + data.getInteger("ammoLevel"));
            currenttip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.DAMAGE_AMP) + ": \u00A7F" + data.getString("ampLevel") + "%");
            currenttip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.ACCURACY) + ": \u00A7F" + data.getString("accuracy") + "%");
            currenttip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.RATE_OF_FIRE) + ": \u00A7F" + data.getString("rof") + "s/sec");
        }
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
            TurretTileEntity turret = (TurretTileEntity) te;
            // te.writeToNBT(tag);
            TurretBase base = turret.getBase();
            tag.setBoolean("active", base != null && base.isActive());
            // no owner name: the base has no owner (shown as unowned)
            if (base == null || base.getOwnerOrNull() != null) tag.setString("ownerName", turret.getOwner().getName());
            if (base == null) {
                // the base's chunk is not loaded or the base was just removed; the stats below need it
                return tag;
            }
            tag.setInteger("ammoLevel", turret.getAmmoLevel());
            tag.setString("ampLevel", String.format("%.2f", turret.getTurretDamageAmpBonus() * 100 * turret.getDamageAmpLevel()));
            tag.setString("accuracy", String.format("%.2f", 100F - turret.getActualTurretAccuracyDeviation()));
            tag.setString("rof", String.format("%.2f", 20F / turret.getFireCooldownTicks()));
        }

        return tag;
    }
}
