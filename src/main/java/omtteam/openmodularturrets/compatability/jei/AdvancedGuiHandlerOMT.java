package omtteam.openmodularturrets.compatability.jei;

import mezz.jei.api.gui.IAdvancedGuiHandler;
import omtteam.openmodularturrets.client.gui.TurretBaseAbstractGui;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by Keridos on 03/12/16.
 * This Class
 */
public class AdvancedGuiHandlerOMT implements IAdvancedGuiHandler<TurretBaseAbstractGui> {
    @Override
    public Class getGuiContainerClass() {
        return TurretBaseAbstractGui.class;
    }

    @Nullable
    @Override
    public java.util.List<Rectangle> getGuiExtraAreas(TurretBaseAbstractGui guiContainer) {
         return guiContainer.getBlockingAreas();
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(TurretBaseAbstractGui guiContainer, int mouseX, int mouseY) {
        return null;
    }
}
