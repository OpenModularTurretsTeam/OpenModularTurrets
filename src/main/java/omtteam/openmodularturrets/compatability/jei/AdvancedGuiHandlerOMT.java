package omtteam.openmodularturrets.compatability.jei;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import omtteam.openmodularturrets.client.gui.TurretBaseAbstractGui;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

/**
 * Created by Keridos on 03/12/16.
 * This Class
 */
@MethodsReturnNonnullByDefault
public class AdvancedGuiHandlerOMT implements IAdvancedGuiHandler<TurretBaseAbstractGui> {
    @Override
    public Class<TurretBaseAbstractGui> getGuiContainerClass() {
        return TurretBaseAbstractGui.class;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public java.util.List<Rectangle> getGuiExtraAreas(TurretBaseAbstractGui guiContainer) {
         return guiContainer.getBlockingAreas();
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public Object getIngredientUnderMouse(TurretBaseAbstractGui guiContainer, int mouseX, int mouseY) {
        return null;
    }
}
