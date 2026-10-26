package omtteam.openmodularturrets.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
//? if >=1.21.11 {
/*import net.minecraft.client.input.MouseButtonEvent;
*///?}
import omtteam.openmodularturrets.core.security.AccessLevel;
import omtteam.openmodularturrets.core.security.TrustedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.Optional;
import java.util.UUID;

/**
 * Scrollable, selectable list of trusted players (replaces OMLib {@code StringListBox}).
 */
public class TrustedPlayerList extends AbstractWidget {
    private static final int ROW_HEIGHT = 12;
    private static final int PADDING = 3;

    private final List<TrustedPlayer> entries = new ArrayList<>();
    private UUID selected;
    private int scrollRows;

    private Function<AccessLevel, Component> levelNamer = TrustedPlayersScreen::levelName;

    public TrustedPlayerList(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    /** How the access levels are called in the list (a trap calls its lowest level "trusted"). */
    public void setLevelNamer(Function<AccessLevel, Component> levelNamer) {
        this.levelNamer = levelNamer;
    }

    /** Replaces the entries; the selection is kept by UUID. */
    public void setEntries(List<TrustedPlayer> players) {
        entries.clear();
        entries.addAll(players);
        if (selected != null && entries.stream().noneMatch(entry -> entry.getUuid().equals(selected))) {
            selected = null;
        }
        scrollRows = Math.max(0, Math.min(scrollRows, maxScroll()));
    }

    public Optional<TrustedPlayer> getSelected() {
        return entries.stream().filter(entry -> entry.getUuid().equals(selected)).findFirst();
    }

    private int visibleRows() {
        return (height - 2 * PADDING) / ROW_HEIGHT;
    }

    private int maxScroll() {
        return Math.max(0, entries.size() - visibleRows());
    }

    /** @param delta positive when scrolling up */
    public boolean scroll(double mouseX, double mouseY, double delta) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        scrollRows = Math.max(0, Math.min(maxScroll(), scrollRows - (int) Math.signum(delta)));
        return true;
    }

    //? if >=1.21.11 {
    /*@Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        select(event.y());
    }
    *///?} else {
    @Override
    public void onClick(double mouseX, double mouseY) {
        select(mouseY);
    }
    //?}

    private void select(double mouseY) {
        int row = (int) ((mouseY - getY() - PADDING) / ROW_HEIGHT) + scrollRows;
        selected = row >= 0 && row < entries.size() ? entries.get(row).getUuid() : null;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        graphics.enableScissor(getX(), getY(), getX() + width, getY() + height);
        int rows = Math.min(visibleRows(), entries.size() - scrollRows);
        for (int i = 0; i < rows; i++) {
            TrustedPlayer entry = entries.get(scrollRows + i);
            int rowY = getY() + PADDING + i * ROW_HEIGHT;
            if (entry.getUuid().equals(selected)) {
                graphics.fill(getX() + 1, rowY - 1, getX() + width - 1, rowY + ROW_HEIGHT - 1, 0x80FFFFFF);
            }
            Component text = Component.literal(entry.getName() + ", ")
                    .append(levelNamer.apply(entry.getAccessLevel()));
            graphics.drawString(font, text, getX() + PADDING + 2, rowY + 1, 0xFFFFFFFF, false);
        }
        graphics.disableScissor();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        getSelected().ifPresent(entry -> output.add(NarratedElementType.TITLE, Component.literal(entry.getName())));
    }
}
