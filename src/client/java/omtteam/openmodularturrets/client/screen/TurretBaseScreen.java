package omtteam.openmodularturrets.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.client.compat.ClientNetworkCompat;
import omtteam.openmodularturrets.core.base.TargetingSettings;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.security.AccessLevel;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.TrustedPlayer;
import omtteam.openmodularturrets.menu.TurretBaseMenu;
import omtteam.openmodularturrets.network.BasePackets;
import omtteam.openmodularturrets.network.OMTPackets;
import omtteam.openmodularturrets.security.PlayerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Turret base GUI. Ported from oldmod {@code TurretBaseGui}: inventory, energy bar, range, info panel and the
 * admin/settings buttons. Buttons only send requests; the server checks permissions.
 */
public class TurretBaseScreen extends AbstractContainerScreen<TurretBaseMenu> {
    private static final String[] TIER_NAMES = {"one", "two", "three", "four", "five"};
    /** The energy bar flickers between three fill textures (oldmod {@code drawEnergyBar}). */
    private static final int[] ENERGY_FILL_U = {196, 215, 234};

    private final Identifier texture;
    private final Random random = new Random();
    private Optional<AccessLevel> shownAccess = Optional.empty();
    private Button targetButton;

    public TurretBaseScreen(TurretBaseMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.texture = OMT.id("textures/gui/turret_base_tier_" + TIER_NAMES[menu.getTier() - 1] + ".png");
        this.inventoryLabelY = imageHeight - 94;
    }

    private Optional<TurretBaseBlockEntity> base() {
        if (minecraft == null || minecraft.level == null) return Optional.empty();
        return minecraft.level.getBlockEntity(menu.getPos()) instanceof TurretBaseBlockEntity base
                ? Optional.of(base) : Optional.empty();
    }

    private Optional<AccessLevel> access() {
        if (minecraft == null || minecraft.player == null) return Optional.empty();
        return base().flatMap(base -> base.getTrustManager().getAccessLevel(PlayerAdapter.info(minecraft.player)));
    }

    @Override
    protected void init() {
        super.init();
        shownAccess = access();
        int x = leftPos;
        int y = topPos;
        boolean admin = shownAccess.map(level -> level.isAtLeast(AccessLevel.ADMIN)).orElse(false);
        boolean settings = shownAccess.map(level -> level.isAtLeast(AccessLevel.CHANGE_SETTINGS)).orElse(false);
        targetButton = null;
        if (admin) {
            addRenderableWidget(button(ScreenTexts.gui("mode"), x + 180, y, "toggle_mode",
                    () -> sendAction(BasePackets.Action.TOGGLE_MODE)));
            addRenderableWidget(button(ScreenTexts.gui("drop_base"), x + 180, y + 75, null, this::dropBase));
            addRenderableWidget(button(ScreenTexts.gui("drop_turrets"), x + 180, y + 100, null,
                    () -> sendAction(BasePackets.Action.DROP_TURRETS)));
        }
        if (settings) {
            addRenderableWidget(Button.builder(Component.literal("+"), b -> changeRange(1))
                    .bounds(x + 120, y + 15, 20, 20).tooltip(tooltip("plus_range")).build());
            addRenderableWidget(Button.builder(Component.literal("-"), b -> changeRange(-1))
                    .bounds(x + 120, y + 50, 20, 20).tooltip(tooltip("minus_range")).build());
            addRenderableWidget(button(ScreenTexts.gui("configure"), x + 180, y + 25, "configure_base", this::openConfigure));
            targetButton = addRenderableWidget(button(targetLabel(), x + 180, y + 50, "multi_targeting",
                    () -> sendAction(BasePackets.Action.TOGGLE_MULTI_TARGETING)));
        }
    }

    private Button button(Component label, int x, int y, String tooltipKey, Runnable action) {
        Button.Builder builder = Button.builder(label, b -> action.run()).bounds(x, y, 80, 20);
        if (tooltipKey != null) builder.tooltip(tooltip(tooltipKey));
        return builder.build();
    }

    private static Tooltip tooltip(String key) {
        return Tooltip.create(Component.translatable("tooltip.openmodularturrets." + key));
    }

    private Component targetLabel() {
        boolean multi = base().map(base -> base.getLogic().isMultiTargeting()).orElse(false);
        return ScreenTexts.labelled("target", ScreenTexts.gui(multi ? "multi" : "single"));
    }

    private void sendAction(BasePackets.Action action) {
        ClientNetworkCompat.sendToServer(OMTPackets.BASE_ACTION, new BasePackets.SimpleAction(menu.getPos(), action));
    }

    private void dropBase() {
        onClose();
        sendAction(BasePackets.Action.DROP_BASE);
    }

    private void changeRange(int delta) {
        base().ifPresent(base -> ClientNetworkCompat.sendToServer(OMTPackets.SET_RANGE,
                new BasePackets.SetRange(menu.getPos(), base.getLogic().getTargetingSettings().getRange() + delta)));
    }

    /** oldmod opened the configure GUI through the server; a plain screen needs no server menu. */
    private void openConfigure() {
        if (minecraft == null || minecraft.player == null) return;
        minecraft.player.closeContainer();
        minecraft.gui.setScreen(new ConfigureScreen(menu.getPos()));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (!access().equals(shownAccess)) {
            rebuildWidgets();
        } else if (targetButton != null) {
            targetButton.setMessage(targetLabel());
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        renderInfoPanel(graphics);
        renderAreaTooltips(graphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        long capacity = menu.getCapacity();
        int filled = (int) (menu.getEnergy() * 51 / (capacity == 0 ? 1 : capacity));
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos + 153, topPos + 17, 178, 17, 14, 51, 256, 256);
        int u = ENERGY_FILL_U[random.nextInt(ENERGY_FILL_U.length)];
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos + 153, topPos + 17 + 51 - filled, u, 68 - filled, 14, filled, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (menu.getTier() > 1) {
            graphics.text(font, ScreenTexts.gui("addons"), 71, 6, 0xFF000000, false);
            graphics.text(font, ScreenTexts.gui("upgrades"), 71, 39, 0xFF000000, false);
        }
        graphics.text(font, ScreenTexts.gui("ammo"), 8, 6, 0xFF000000, false);
        graphics.text(font, ScreenTexts.gui("inventory"), 8, inventoryLabelY, 0xFF000000, false);
        graphics.text(font, ScreenTexts.gui("range"), 116, 6, 0xFF000000, false);
        base().ifPresent(base -> {
            TargetingSettings targeting = base.getLogic().getTargetingSettings();
            String range = String.valueOf(targeting.getRange());
            int color = targeting.getRange() == targeting.getMaxRange() ? 0xFFFF3232 : 0xFF009C40;
            graphics.text(font, range, range.length() == 1 ? 127 : 124, 39, color, true);
        });
    }

    /** oldmod info box to the left of the GUI: owner, mode, kills, trusted players, targeting. */
    private void renderInfoPanel(GuiGraphicsExtractor graphics) {
        Optional<TurretBaseBlockEntity> found = base();
        if (found.isEmpty()) return;
        TurretBaseLogic logic = found.get().getLogic();
        TargetingSettings targeting = logic.getTargetingSettings();
        PlayerIdentity owner = found.get().getOwner();
        List<Component> lines = new ArrayList<>();
        lines.add(ScreenTexts.gui("owner").append(": ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(owner == null ? "?" : owner.name()).withStyle(ChatFormatting.WHITE)));
        lines.add(ScreenTexts.gui("mode").append(": ").withStyle(ChatFormatting.GOLD)
                .append(Component.translatable(logic.getMode().getTranslationKey()).withStyle(ChatFormatting.WHITE)));
        lines.add(ScreenTexts.gui("active").append(": ").withStyle(ChatFormatting.GOLD).append(ScreenTexts.yesNo(logic.isActive())));
        lines.add(Component.empty());
        lines.add(ScreenTexts.gui("kills").append(": " + logic.getKills()).withStyle(ChatFormatting.GOLD));
        lines.add(ScreenTexts.gui("player_kills").append(": " + logic.getPlayerKills()).withStyle(ChatFormatting.GOLD));
        lines.add(Component.empty());
        List<TrustedPlayer> trusted = found.get().getTrustManager().getTrustedPlayers();
        if (trusted.isEmpty()) {
            lines.add(ScreenTexts.gui("trusted_players").append(": ").withStyle(ChatFormatting.DARK_PURPLE)
                    .append(ScreenTexts.yesNo(false)));
        } else {
            lines.add(ScreenTexts.gui("trusted_players").append(":").withStyle(ChatFormatting.DARK_PURPLE));
            for (TrustedPlayer player : trusted) {
                lines.add(Component.literal(player.getName()).withStyle(ChatFormatting.AQUA));
            }
        }
        lines.add(Component.empty());
        lines.add(ScreenTexts.gui("attack_mobs").append(": ").withStyle(ChatFormatting.GRAY).append(ScreenTexts.yesNo(targeting.isTargetMobs())));
        lines.add(ScreenTexts.gui("attack_neutrals").append(": ").withStyle(ChatFormatting.GRAY).append(ScreenTexts.yesNo(targeting.isTargetPassive())));
        lines.add(ScreenTexts.gui("attack_players").append(": ").withStyle(ChatFormatting.GRAY).append(ScreenTexts.yesNo(targeting.isTargetPlayers())));
        // drawn right away (not as the hover tooltip), like oldmod's always-visible info box
        graphics.tooltip(font, lines.stream().map(line -> ClientTooltipComponent.create(line.getVisualOrderText())).toList(),
                leftPos - 128 - 12, topPos + 17 + 12, DefaultTooltipPositioner.INSTANCE, null, false);
    }

    /** oldmod {@code drawTooltips}: energy, slot group labels and the range number. */
    private void renderAreaTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = mouseX - leftPos;
        int y = mouseY - topPos;
        Component tooltip = null;
        if (x > 153 && x < 167 && y > 17 && y < 68) {
            tooltip = Component.literal(menu.getEnergy() + "/" + menu.getCapacity() + " RF");
        } else if (menu.getTier() > 1 && x > 71 && x < 111 && y > 6 && y < 20) {
            tooltip = Component.translatable("tooltip.openmodularturrets.addon_slot");
        } else if (menu.getTier() > 1 && x > 71 && x < 111 && y > 39 && y < 53) {
            tooltip = Component.translatable("tooltip.openmodularturrets.upgrade_slot");
        } else if (x > 10 && x < 48 && y > 6 && y < 20) {
            tooltip = Component.translatable("tooltip.openmodularturrets.ammo_slot");
        } else if (x > 123 && x < 134 && y > 35 && y < 48) {
            tooltip = Component.translatable("tooltip.openmodularturrets.base_max_range");
        }
        if (tooltip != null) graphics.setTooltipForNextFrame(font, tooltip, mouseX, mouseY);
    }
}
