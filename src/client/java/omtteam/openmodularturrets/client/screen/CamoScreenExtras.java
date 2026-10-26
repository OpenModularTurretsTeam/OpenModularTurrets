package omtteam.openmodularturrets.client.screen;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.BlockPos;
import omtteam.openmodularturrets.camo.CamoHost;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Buttons that only some camouflage hosts have, added to the {@link CamoScreen} (a mod's trap, say, has buttons for its
 * trusted players). The mod registers its providers once while the client starts.
 */
public final class CamoScreenExtras {
    private static final List<Provider> PROVIDERS = new ArrayList<>();

    private CamoScreenExtras() {
    }

    /** Adds widgets for the hosts it knows; ignores the others. */
    @FunctionalInterface
    public interface Provider {
        /**
         * @param host  the host whose screen is shown
         * @param pos   its position
         * @param left  left edge of the screen (the first row, with the disguise slot, is at {@code top + CamoMenu.PHANTOM_Y})
         * @param top   top edge of the screen
         * @param width width of the screen
         * @param adder adds a widget to the screen
         */
        void addWidgets(CamoHost host, BlockPos pos, int left, int top, int width, Consumer<AbstractWidget> adder);
    }

    public static void register(Provider provider) {
        PROVIDERS.add(provider);
    }

    static void addWidgets(CamoHost host, BlockPos pos, int left, int top, int width, Consumer<AbstractWidget> adder) {
        for (Provider provider : PROVIDERS) {
            provider.addWidgets(host, pos, left, top, width, adder);
        }
    }
}
