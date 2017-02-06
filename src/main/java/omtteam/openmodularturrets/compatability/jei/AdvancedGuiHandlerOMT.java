package omtteam.openmodularturrets.compatability.jei;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.client.gui.BlockingAbstractGui;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

/**
 * Created by Keridos on 03/12/16.
 * This Class
 */
@MethodsReturnNonnullByDefault
public class AdvancedGuiHandlerOMT implements IAdvancedGuiHandler<BlockingAbstractGui> {
    @Override
    public Class<BlockingAbstractGui> getGuiContainerClass() {
        return BlockingAbstractGui.class;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    @SideOnly(Side.CLIENT)
    public java.util.List<Rectangle> getGuiExtraAreas(BlockingAbstractGui guiContainer) {
        return guiContainer.getBlockingAreas();
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public Object getIngredientUnderMouse(BlockingAbstractGui guiContainer, int mouseX, int mouseY) {
        return null;
    }
}
