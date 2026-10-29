package omtteam.openmodularturrets.tileentity.turret;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.base.TurretBaseOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import static omtteam.openmodularturrets.tileentity.base.TurretBaseOverlay.line;
import static omtteam.openmodularturrets.tileentity.base.TurretBaseOverlay.white;

/**
 * The lines of the Hwyla and The One Probe overlays of a turret (1.20.1+ {@code TurretJadeProvider}): whether it is
 * active, its owner and, for players who may open the base's GUI, ammo, damage amp, inaccuracy and rate of fire. The
 * stats show what the base contains, so {@link #data} (on the server) only adds them for players with that
 * permission; a turret without a base has no data, a concealed turret shows nothing.
 */
public final class TurretOverlay {
    // not keys the turret saves: Hwyla falls back to the client's tile entity data until the server's arrives
    private static final String ACTIVE = "overlayActive";
    private static final String OWNER = "overlayOwner";
    private static final String AMMO = "overlayAmmo";
    private static final String DAMAGE_AMP = "overlayDamageAmp";
    private static final String INACCURACY = "overlayInaccuracy";
    private static final String RATE_OF_FIRE = "overlayRateOfFire";

    private TurretOverlay() {
    }

    /** The data the overlay shows {@code player}, read on the server; empty without a base. */
    public static NBTTagCompound data(TurretTileEntity turret, EntityPlayer player) {
        NBTTagCompound data = new NBTTagCompound();
        TurretBase base = turret.getBase();
        TurretType type = turret.getCoreType();
        if (base == null || type == null) return data;
        TurretBaseLogic logic = base.getLogic();
        data.setBoolean(ACTIVE, base.isActive());
        if (base.getOwnerOrNull() != null) data.setString(OWNER, base.getOwner().getName());
        if (!PlayerUtil.canPlayerAccessBlock(player, base)) return data;
        int ammo = logic.getAmmoLevel(type);
        // -1: unlimited
        data.setInteger(AMMO, ammo == Integer.MAX_VALUE ? -1 : ammo);
        data.setDouble(DAMAGE_AMP, logic.getDamageAmpBonus(type) * 100);
        data.setDouble(INACCURACY, Math.min(100.0D, logic.getAccuracyDeviation(type)));
        double cooldown = logic.getFireCooldownTicks(type);
        data.setDouble(RATE_OF_FIRE, cooldown > 0 ? 20.0D / cooldown : 0);
        return data;
    }

    /** The overlay lines for the data of {@link #data}; a concealed turret shows nothing. */
    public static List<String> lines(NBTTagCompound data, boolean concealed, Function<String, String> localize) {
        List<String> lines = new ArrayList<>();
        if (concealed || !data.hasKey(ACTIVE)) return lines;
        lines.add(line(localize, OMLibNames.Localizations.GUI.ACTIVE,
                TurretBaseOverlay.yesNo(localize, data.getBoolean(ACTIVE))));
        lines.add(TurretBaseOverlay.ownerLine(localize, data.hasKey(OWNER) ? data.getString(OWNER) : null));
        if (!data.hasKey(AMMO)) return lines;
        int ammo = data.getInteger(AMMO);
        lines.add(line(localize, OMTNames.Localizations.GUI.AMMO,
                white(ammo < 0 ? localize.apply(OMTNames.Localizations.GUI.UNLIMITED) : String.valueOf(ammo))));
        lines.add(line(localize, OMTNames.Localizations.GUI.DAMAGE_AMP, white(format(data.getDouble(DAMAGE_AMP)) + "%")));
        lines.add(line(localize, OMTNames.Localizations.GUI.INACCURACY, white(format(data.getDouble(INACCURACY)) + "%")));
        lines.add(line(localize, OMTNames.Localizations.GUI.RATE_OF_FIRE,
                white(format(data.getDouble(RATE_OF_FIRE)) + "/s")));
        return lines;
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
