package omtteam.openmodularturrets.tileentity.base;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.EnumMachineMode;
import omtteam.omlib.util.player.PlayerUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static omtteam.omlib.util.GeneralUtil.getBooleanUnlocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.getMachineModeUnlocalization;

/**
 * The lines of the Hwyla and The One Probe overlays of a turret base (1.20.1+ {@code TurretBaseJadeProvider} and
 * {@code JadeLines}): machine mode, whether the base is active and its owner, or that it has none and can be claimed.
 * {@link #data} runs on the server, {@link #lines} turns its result into text. The caller passes the translation:
 * Hwyla translates on the client, The One Probe sends the keys to the client.
 */
public final class TurretBaseOverlay {
    // not keys the base saves: Hwyla falls back to the client's tile entity data until the server's arrives
    private static final String MODE = "overlayMode";
    private static final String ACTIVE = "overlayActive";
    private static final String OWNER = "overlayOwner";
    private static final String UNOWNED = "text.omlib.unowned";

    private TurretBaseOverlay() {
    }

    /** The data the overlay shows, read on the server. */
    public static NBTTagCompound data(TurretBase base) {
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger(MODE, base.getMode().ordinal());
        data.setBoolean(ACTIVE, base.isActive());
        if (base.getOwnerOrNull() != null) data.setString(OWNER, base.getOwner().getName());
        return data;
    }

    /** Whether {@code data} holds the data of {@link #data}. */
    public static boolean hasData(NBTTagCompound data) {
        return data.hasKey(MODE);
    }

    /** The overlay lines for the data of {@link #data}; none if it is missing. */
    public static List<String> lines(NBTTagCompound data, Function<String, String> localize) {
        List<String> lines = new ArrayList<>();
        if (!hasData(data)) return lines;
        EnumMachineMode[] modes = EnumMachineMode.values();
        EnumMachineMode mode = modes[Math.max(0, Math.min(modes.length - 1, data.getInteger(MODE)))];
        lines.add(line(localize, OMLibNames.Localizations.GUI.MODE,
                TextFormatting.GREEN + localize.apply(getMachineModeUnlocalization(mode))));
        lines.add(line(localize, OMLibNames.Localizations.GUI.ACTIVE, yesNo(localize, data.getBoolean(ACTIVE))));
        lines.add(ownerLine(localize, data.hasKey(OWNER) ? data.getString(OWNER) : null));
        return lines;
    }

    /**
     * Whether {@code player} sees the disguise instead of the base (1.20.1+ {@code canSeeThroughCamo} in the Jade
     * plugin): the base is camouflaged and the player may not open its GUI.
     */
    public static boolean isDisguisedFor(TurretBase base, IBlockState state, EntityPlayer player) {
        return base.isCamoEnabled() && base.getCamoState().getBlock() != state.getBlock()
                && !PlayerUtil.canPlayerAccessBlock(player, base);
    }

    /** "Label: value" in the colours of the overlays (gold label; the value brings its colour, see {@link #white}). */
    public static String line(Function<String, String> localize, String labelKey, String value) {
        return TextFormatting.GOLD + localize.apply(labelKey) + ": " + value;
    }

    /** A value in white, the colour of plain values. */
    public static String white(String value) {
        return TextFormatting.WHITE + value;
    }

    /** The owner line: the owner's name, or "nobody" with a hint to claim the block (green, it is up for grabs). */
    public static String ownerLine(Function<String, String> localize, @Nullable String owner) {
        return line(localize, OMLibNames.Localizations.GUI.OWNER,
                owner != null ? white(owner) : TextFormatting.GREEN + localize.apply(UNOWNED));
    }

    /** Green "yes" or red "no". */
    public static String yesNo(Function<String, String> localize, boolean value) {
        return (value ? TextFormatting.GREEN : TextFormatting.RED) + localize.apply(getBooleanUnlocalizationYesNo(value));
    }
}
