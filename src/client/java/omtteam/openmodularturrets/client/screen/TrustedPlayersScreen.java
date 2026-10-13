package omtteam.openmodularturrets.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.client.compat.ClientNetworkCompat;
import omtteam.openmodularturrets.client.compat.CompatScreen;
import omtteam.openmodularturrets.core.security.AccessLevel;
import omtteam.openmodularturrets.core.security.TrustedPlayer;
import omtteam.openmodularturrets.network.OMTPackets;
import omtteam.openmodularturrets.network.TrustPackets;
import omtteam.openmodularturrets.security.PlayerAdapter;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

/**
 * Trusted player management of a base. Ported from OMLib {@code TrustedPlayersGUI} (layout, texture and buttons).
 * <p>
 * Shows the client copy of the trust list (synced with the block entity). Every button only sends a request; the
 * server checks the permission and the change comes back through the block entity sync.
 */
public class TrustedPlayersScreen extends CompatScreen {
    private static final ResourceLocation TEXTURE = OMT.id("textures/gui/trusted_players.png");
    private static final int WIDTH = 256;
    private static final int HEIGHT = 256;

    private final BlockPos pos;
    @Nullable
    private final Screen parent;
    private int left;
    private int top;
    private EditBox nameField;
    private TrustedPlayerList list;

    /**
     * @param parent the screen the "Back" button returns to (OMLib {@code GUIBackSystem}); {@code null} closes the
     *               screen instead
     */
    public TrustedPlayersScreen(BlockPos pos, @Nullable Screen parent) {
        super(Component.translatable("gui.openmodularturrets.trusted_players"));
        this.pos = pos;
        this.parent = parent;
    }

    static Component levelName(AccessLevel level) {
        return Component.translatable("gui.openmodularturrets.access_level." + level.getSerializedName());
    }

    private Optional<TurretBaseBlockEntity> base() {
        if (minecraft == null || minecraft.level == null) return Optional.empty();
        return minecraft.level.getBlockEntity(pos) instanceof TurretBaseBlockEntity base
                ? Optional.of(base) : Optional.empty();
    }

    /** Client-side check for the UI only; the server checks again. */
    private boolean isAdmin() {
        return minecraft != null && minecraft.player != null
                && base().map(base -> base.getTrustManager().isAdmin(PlayerAdapter.info(minecraft.player))).orElse(false);
    }

    @Override
    protected void init() {
        left = (width - WIDTH) / 2;
        top = (height - HEIGHT) / 2;

        list = addRenderableWidget(new TrustedPlayerList(left + 5, top + 5, 246, 212));
        nameField = addRenderableWidget(new EditBox(font, left + 11, top + 231, 100, 18,
                Component.translatable("gui.openmodularturrets.add_trusted_player")));
        nameField.setMaxLength(TrustPackets.MAX_NAME_LENGTH);
        nameField.setTooltip(tooltip("tp_text_field"));

        addRenderableWidget(Button.builder(Component.literal("+"), button -> addPlayer())
                .bounds(left + 114, top + 230, 33, 20).tooltip(tooltip("add_trusted_player")).build());
        addRenderableWidget(Button.builder(Component.literal("-"), button -> removeSelected())
                .bounds(left + 148, top + 230, 33, 20).tooltip(tooltip("remove_trusted_player")).build());
        addRenderableWidget(Button.builder(Component.literal("-"), button -> changeSelected(-1))
                .bounds(left + 185, top + 230, 23, 20).tooltip(tooltip("tp_decrease_access")).build());
        addRenderableWidget(Button.builder(Component.literal("+"), button -> changeSelected(1))
                .bounds(left + 226, top + 230, 23, 20).tooltip(tooltip("tp_increase_access")).build());
        // positioned to the right of the panel
        addRenderableWidget(Button.builder(Component.translatable("gui.openmodularturrets.back"), button -> back())
                .bounds(left + 261, top + 10, 80, 20).build());
        refreshList();
    }

    private static Tooltip tooltip(String key) {
        return Tooltip.create(Component.translatable("tooltip.openmodularturrets." + key));
    }

    private void refreshList() {
        base().ifPresent(base -> list.setEntries(base.getTrustManager().getTrustedPlayers()));
    }

    private void back() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    private void addPlayer() {
        String name = nameField.getValue().trim();
        if (name.isEmpty()) return;
        ClientNetworkCompat.sendToServer(OMTPackets.ADD_TRUSTED_PLAYER, new TrustPackets.Add(pos, name));
        nameField.setValue("");
    }

    private void removeSelected() {
        list.getSelected().ifPresent(entry -> ClientNetworkCompat.sendToServer(OMTPackets.REMOVE_TRUSTED_PLAYER,
                new TrustPackets.Remove(pos, entry.getUuid())));
    }

    private void changeSelected(int delta) {
        list.getSelected().ifPresent(entry -> ClientNetworkCompat.sendToServer(OMTPackets.CHANGE_ACCESS_LEVEL,
                new TrustPackets.ChangeAccess(pos, entry.getUuid(), delta)));
    }

    /** As in 1.12.2, the screen closes as soon as the player is no longer an admin. */
    @Override
    public void tick() {
        super.tick();
        if (!isAdmin()) {
            onClose();
            return;
        }
        refreshList();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (nameField.isFocused() && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            addPlayer();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean onScroll(double mouseX, double mouseY, double delta) {
        return list.scroll(mouseX, mouseY, delta);
    }

    @Override
    protected void renderPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(TEXTURE, left, top, 0, 0, WIDTH, HEIGHT);
        graphics.drawString(font, Component.translatable("gui.openmodularturrets.add_trusted_player").append(":"),
                left + 10, top + 220, 0x000000, false);
        graphics.drawString(font, Component.translatable("gui.openmodularturrets.permissions"),
                left + 186, top + 220, 0x000000, false);
    }

    /** Hovering the "Permissions" label explains the selected player's level. */
    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int labelWidth = font.width(Component.translatable("gui.openmodularturrets.permissions"));
        boolean overLabel = mouseX >= left + 186 && mouseX < left + 186 + labelWidth
                && mouseY >= top + 218 && mouseY < top + 229;
        if (!overLabel) return;
        Component text = list.getSelected()
                .map(entry -> Component.translatable("tooltip.openmodularturrets." + describe(entry)))
                .orElse(Component.translatable("tooltip.openmodularturrets.info_access_level"));
        graphics.renderTooltip(font, text.copy().withStyle(ChatFormatting.WHITE), mouseX, mouseY);
    }

    private static String describe(TrustedPlayer entry) {
        return switch (entry.getAccessLevel()) {
            case NONE -> "tp_none";
            case OPEN_GUI -> "tp_can_open_gui";
            case CHANGE_SETTINGS -> "tp_can_change_settings";
            case ADMIN -> "tp_can_administer";
        };
    }
}
