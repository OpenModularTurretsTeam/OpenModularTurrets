package omtteam.openmodularturrets.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.defense.CamoTrapBlockEntity;
import omtteam.openmodularturrets.camo.CamoHost;
import omtteam.openmodularturrets.client.compat.ClientCompat;
import omtteam.openmodularturrets.client.compat.ClientNetworkCompat;
import omtteam.openmodularturrets.core.camo.CamoLight;
import omtteam.openmodularturrets.menu.CamoMenu;
import omtteam.openmodularturrets.network.BasePackets;
import omtteam.openmodularturrets.network.CamoPackets;
import omtteam.openmodularturrets.network.OMTPackets;
import omtteam.openmodularturrets.security.PlayerAdapter;

import java.util.Optional;

/**
 * Camouflage GUI of turret bases and camouflage traps: the disguise slot, and for hosts with adjustable light the
 * light value and light opacity sliders (1.12.2 {@code ConfigureGui}). Drawn without a texture.
 */
public class CamoScreen extends AbstractContainerScreen<CamoMenu> {
    private static final int WIDTH = 176;
    private static final int SLOT_SIZE = 18;
    /** Dark grey as in the turret base GUI textures, instead of the vanilla light grey. */
    private static final int PANEL = 0xFF585858;
    private static final int PANEL_LIGHT = 0xFF767676;
    private static final int PANEL_SHADOW = 0xFF323232;
    private static final int SLOT = 0xFF464646;
    private static final int SLOT_SHADOW = 0xFF232323;
    private static final int SLOT_LIGHT = 0xFF9A9A9A;
    private static final int LABEL_COLOR = 0x000000;
    private static final int SLIDER_WIDTH = 160;
    private static final int SLIDER_VALUE_Y = 48;
    private static final int SLIDER_OPACITY_Y = 72;

    private LightSlider valueSlider;
    private LightSlider opacitySlider;

    public CamoScreen(CamoMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = WIDTH;
        this.imageHeight = CamoMenu.imageHeight(menu.showsLight());
        this.inventoryLabelY = CamoMenu.inventoryTop(menu.showsLight()) - 11;
    }

    private Optional<CamoHost> host() {
        if (minecraft == null || minecraft.level == null) return Optional.empty();
        return minecraft.level.getBlockEntity(menu.getPos()) instanceof CamoHost found ? Optional.of(found) : Optional.empty();
    }

    @Override
    protected void init() {
        super.init();
        Optional<CamoHost> host = host();
        if (menu.showsLight()) {
            CamoLight light = host.map(CamoHost::getLight).orElse(CamoLight.DEFAULT);
            boolean editable = host.isPresent() && minecraft != null && minecraft.player != null
                    && host.get().canChangeLight(minecraft.player);
            valueSlider = addRenderableWidget(new LightSlider(leftPos + 8, topPos + SLIDER_VALUE_Y,
                    ScreenTexts.gui("light_value"), light.value(), this::sendLight));
            opacitySlider = addRenderableWidget(new LightSlider(leftPos + 8, topPos + SLIDER_OPACITY_Y,
                    ScreenTexts.gui("light_opacity"), light.opacity(), this::sendLight));
            valueSlider.active = editable;
            opacitySlider.active = editable;
        }
        if (host.orElse(null) instanceof TurretBaseBlockEntity) {
            addRenderableWidget(Button.builder(ScreenTexts.gui("back"), b -> back())
                    .bounds(leftPos + WIDTH - 8 - 50, topPos + CamoMenu.PHANTOM_Y - 1, 50, 20).build());
        } else if (host.orElse(null) instanceof CamoTrapBlockEntity trap) {
            // a trap has no other screen: its admins manage the trusted players from here
            Button trusted = addRenderableWidget(Button.builder(ScreenTexts.gui("trusted"), b -> openTrustedPlayers())
                    .bounds(leftPos + WIDTH - 8 - 64, topPos + CamoMenu.PHANTOM_Y - 1, 64, 20)
                    .tooltip(Tooltip.create(Component.translatable("tooltip.openmodularturrets.trusted_player_gui")))
                    .build());
            trusted.active = minecraft != null && minecraft.player != null
                    && trap.getTrustManager().isAdmin(PlayerAdapter.info(minecraft.player));
            if (minecraft != null && minecraft.player != null
                    && trap.getTrustManager().isOwner(PlayerAdapter.info(minecraft.player))) {
                // outside the panel, whose row is full; "No" asks the server for the camouflage menu again
                BlockPos pos = menu.getPos();
                addRenderableWidget(DisownConfirm.button(leftPos + WIDTH + 4, topPos + CamoMenu.PHANTOM_Y - 1, 80, pos,
                        () -> ClientNetworkCompat.sendToServer(OMTPackets.OPEN_CAMO_MENU, new CamoPackets.OpenMenu(pos))));
            }
        }
    }

    private void sendLight() {
        ClientNetworkCompat.sendToServer(OMTPackets.CAMO_LIGHT,
                new CamoPackets.SetLight(menu.getPos(), valueSlider.level(), opacitySlider.level()));
    }

    /** Replaces this menu screen; "Back" asks the server for the camouflage menu again. */
    private void openTrustedPlayers() {
        if (minecraft == null) return;
        BlockPos pos = menu.getPos();
        minecraft.setScreen(new TrustedPlayersScreen(pos,
                () -> ClientNetworkCompat.sendToServer(OMTPackets.OPEN_CAMO_MENU, new CamoPackets.OpenMenu(pos))));
    }

    /** Bases return to their GUI (a server-side menu). */
    private void back() {
        ClientNetworkCompat.sendToServer(OMTPackets.BASE_ACTION,
                new BasePackets.SimpleAction(menu.getPos(), BasePackets.Action.OPEN_MENU));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ClientCompat.renderContainerBackground(this, graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        Slot hovered = hoveredSlot;
        if (hovered != null && hovered.index == CamoMenu.PHANTOM_SLOT && !hovered.hasItem()) {
            graphics.renderTooltip(font, Component.translatable("tooltip.openmodularturrets.camouflage_slot"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, LABEL_COLOR, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOR, false);
        graphics.drawString(font, ScreenTexts.gui("disguise").append(":"), 8, CamoMenu.PHANTOM_Y + 5, LABEL_COLOR, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int right = leftPos + imageWidth;
        int bottom = topPos + imageHeight;
        graphics.fill(leftPos, topPos, right, bottom, 0xFF000000);
        graphics.fill(leftPos + 1, topPos + 1, right - 1, bottom - 1, PANEL);
        graphics.fill(leftPos + 1, topPos + 1, right - 1, topPos + 2, PANEL_LIGHT);
        graphics.fill(leftPos + 1, topPos + 1, leftPos + 2, bottom - 1, PANEL_LIGHT);
        graphics.fill(right - 2, topPos + 2, right - 1, bottom - 1, PANEL_SHADOW);
        graphics.fill(leftPos + 2, bottom - 2, right - 1, bottom - 1, PANEL_SHADOW);
        for (Slot slot : menu.slots) {
            slotBackground(graphics, leftPos + slot.x - 1, topPos + slot.y - 1);
        }
    }

    private static void slotBackground(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, SLOT);
        graphics.fill(x, y, x + SLOT_SIZE - 1, y + 1, SLOT_SHADOW);
        graphics.fill(x, y, x + 1, y + SLOT_SIZE - 1, SLOT_SHADOW);
        graphics.fill(x + 1, y + SLOT_SIZE - 1, x + SLOT_SIZE, y + SLOT_SIZE, SLOT_LIGHT);
        graphics.fill(x + SLOT_SIZE - 1, y + 1, x + SLOT_SIZE, y + SLOT_SIZE, SLOT_LIGHT);
    }

    /** A 0–15 slider that sends its values to the server when released. */
    private static final class LightSlider extends AbstractSliderButton {
        private final Component label;
        private final Runnable onChange;

        private LightSlider(int x, int y, Component label, int initial, Runnable onChange) {
            super(x, y, SLIDER_WIDTH, 20, CommonComponents.EMPTY, initial / (double) CamoLight.MAX);
            this.label = label;
            this.onChange = onChange;
            updateMessage();
        }

        int level() {
            return CamoLight.clamp((int) Math.round(value * CamoLight.MAX));
        }

        @Override
        protected void updateMessage() {
            setMessage(label.copy().append(": " + level()));
        }

        @Override
        protected void applyValue() {
            value = level() / (double) CamoLight.MAX;
        }

        @Override
        public void onRelease(double mouseX, double mouseY) {
            super.onRelease(mouseX, mouseY);
            onChange.run();
        }
    }
}
