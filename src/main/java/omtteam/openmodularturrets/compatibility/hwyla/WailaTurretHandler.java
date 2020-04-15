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
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.turret.TurretHeadUtil;

import javax.annotation.Nonnull;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.getColoredBooleanLocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.safeLocalize;
import static omtteam.openmodularturrets.turret.TurretHeadUtil.getAmmoLevel;
import static omtteam.openmodularturrets.turret.TurretHeadUtil.getAmpLevel;

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

        register.registerNBTProvider(instance, TurretHead.class);
        register.registerBodyProvider(instance, TurretHead.class);
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
        if (te instanceof TurretHead) {
            TurretHead turret = (TurretHead) te;
            boolean active = accessor.getNBTData().getBoolean("active");

            currenttip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.ACTIVE) + ": " + getColoredBooleanLocalizationYesNo(active));
            currenttip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.OWNER) + ": \u00A7F" + accessor.getNBTData().getString("ownerName"));
            currenttip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.AMMO) + ": \u00A7F" + getAmmoLevel(turret, turret.getBase()));
            currenttip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.DAMAGE_AMP) + ": \u00A7F" + accessor.getNBTData().getString("ampLevel") + "%");
            currenttip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.ACCURACY) + ": \u00A7F" + accessor.getNBTData().getString("accuracy") + "%");
            currenttip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.RATE_OF_FIRE) + ": \u00A7F" + accessor.getNBTData().getString("rof") + "s/sec");
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
        if (te instanceof TurretHead) {
            TurretHead turret = (TurretHead) te;
            // te.writeToNBT(tag);
            tag.setBoolean("active", turret.getBase().isActive());
            tag.setString("ownerName", turret.getOwner().getName());
            tag.setInteger("ammoLevel", getAmmoLevel(turret, turret.getBase()));
            tag.setString("ampLevel", String.format("%.2f", turret.getTurretDamageAmpBonus() * 100 * getAmpLevel(turret.getBase())));
            tag.setString("accuracy", String.format("%.2f", Math.min(100F, turret.getActualTurretAccuracyDeviation())));
            tag.setString("rof", String.format("%.2f", 20F / (turret.getTurretBaseFireRate() /
                    (1 + TurretHeadUtil.getFireRateUpgrades(turret.getBase(), turret)))));
        }

        return tag;
    }
}
