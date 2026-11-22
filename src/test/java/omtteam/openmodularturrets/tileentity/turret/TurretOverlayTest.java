package omtteam.openmodularturrets.tileentity.turret;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.turret.TurretStatLines;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The lines of the Hwyla and The One Probe overlays of a turret, from the data {@link TurretOverlay#data} sends (the
 * keys below); the range falloff and turn rate lines come last, as in Jade on 1.20.1, 1.21.1 and 1.21.11.
 */
class TurretOverlayTest {
    /** Labels as their keys. */
    private static final Function<String, String> LABELS = key -> key;
    /** Values as their keys and arguments. */
    private static final TurretStatLines.Localizer VALUES = (key, args) -> key + Arrays.toString(args);

    /** The data of a turret for a player who may open its base. */
    private static NBTTagCompound fullData() {
        NBTTagCompound data = new NBTTagCompound();
        data.setBoolean("overlayActive", true);
        data.setString("overlayOwner", "Owner");
        data.setInteger("overlayAmmo", 12);
        data.setDouble("overlayDamageAmp", 0);
        data.setDouble("overlayInaccuracy", 10);
        data.setDouble("overlayRateOfFire", 2.5D);
        data.setInteger("overlayRange", 20);
        data.setDouble("overlayOptimalRange", 0.6D);
        data.setDouble("overlayFalloffDamage", 0.5D);
        data.setDouble("overlayTurnRate", 20);
        return data;
    }

    @Test
    void theRangeFalloffAndTurnRateComeAfterTheRateOfFire() {
        List<String> lines = TurretOverlay.lines(fullData(), false, LABELS, VALUES);

        assertEquals(8, lines.size(), lines.toString());
        assertTrue(lines.get(5).contains(OMTNames.Localizations.GUI.RATE_OF_FIRE), lines.get(5));
        assertEquals(TextFormatting.GOLD + OMTNames.Localizations.GUI.OPTIMAL_RANGE + ": " + TextFormatting.WHITE
                + "tooltip.openmodularturrets.turret.optimal_range.value[12, 50, 20]", lines.get(6));
        assertEquals(TextFormatting.GOLD + OMTNames.Localizations.GUI.TURN_RATE + ": " + TextFormatting.WHITE
                + "tooltip.openmodularturrets.turret.turn_rate.value[400]", lines.get(7));
    }

    @Test
    void noFalloffAndInstantTurning() {
        NBTTagCompound data = fullData();
        data.setDouble("overlayOptimalRange", 1);
        data.setDouble("overlayFalloffDamage", 1);
        data.setDouble("overlayTurnRate", 0);
        List<String> lines = TurretOverlay.lines(data, false, LABELS, VALUES);

        assertTrue(lines.get(6).endsWith("tooltip.openmodularturrets.turret.optimal_range.full[20]"), lines.get(6));
        assertTrue(lines.get(7).endsWith("tooltip.openmodularturrets.turret.turn_rate.instant[]"), lines.get(7));
    }

    /** Data from a server without the new keys (or for a player without access) shows no such lines. */
    @Test
    void theNewLinesNeedTheirData() {
        NBTTagCompound data = fullData();
        data.removeTag("overlayOptimalRange");
        data.removeTag("overlayTurnRate");
        assertEquals(6, TurretOverlay.lines(data, false, LABELS, VALUES).size());

        NBTTagCompound noAccess = new NBTTagCompound();
        noAccess.setBoolean("overlayActive", true);
        assertEquals(2, TurretOverlay.lines(noAccess, false, LABELS, VALUES).size());
    }

    @Test
    void aConcealedTurretShowsNothing() {
        assertTrue(TurretOverlay.lines(fullData(), true, LABELS, VALUES).isEmpty());
    }
}
