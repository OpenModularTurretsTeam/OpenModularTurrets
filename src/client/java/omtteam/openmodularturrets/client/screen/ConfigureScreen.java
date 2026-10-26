package omtteam.openmodularturrets.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.client.compat.ClientCompat;
import omtteam.openmodularturrets.client.compat.ClientNetworkCompat;
import omtteam.openmodularturrets.client.compat.CompatScreen;
import omtteam.openmodularturrets.core.base.TargetingSettings;
import omtteam.openmodularturrets.core.security.AccessLevel;
import omtteam.openmodularturrets.network.BasePackets;
import omtteam.openmodularturrets.network.CamoPackets;
import omtteam.openmodularturrets.network.OMTPackets;
import omtteam.openmodularturrets.security.PlayerAdapter;

import java.util.Optional;

/**
 * Base configuration: target categories, the trusted player screen and the way to the camouflage menu (which has
 * the light sliders of 1.12.2 {@code ConfigureGui}). Ported from 1.12.2 {@code ConfigureGui}.
 */
public class ConfigureScreen extends CompatScreen {
    private static final ResourceLocation TEXTURE = OMT.id("textures/gui/configure.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 205;

    private final BlockPos pos;
    private int left;
    private int top;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<AccessLevel> shownAccess = Optional.empty();
    private Button mobsButton;
    private Button neutralsButton;
    private Button playersButton;

    public ConfigureScreen(BlockPos pos) {
        super(ScreenTexts.gui("configure"));
        this.pos = pos;
    }

    private Optional<TurretBaseBlockEntity> base() {
        if (minecraft == null || minecraft.level == null) return Optional.empty();
        return minecraft.level.getBlockEntity(pos) instanceof TurretBaseBlockEntity base
                ? Optional.of(base) : Optional.empty();
    }

    private Optional<AccessLevel> access() {
        if (minecraft == null || minecraft.player == null) return Optional.empty();
        return base().flatMap(base -> base.getTrustManager().getAccessLevel(PlayerAdapter.info(minecraft.player)));
    }

    private boolean isAdmin() {
        return shownAccess.map(level -> level.isAtLeast(AccessLevel.ADMIN)).orElse(false);
    }

    private boolean isOwner() {
        return minecraft != null && minecraft.player != null
                && base().map(base -> base.getTrustManager().isOwner(PlayerAdapter.info(minecraft.player))).orElse(false);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void init() {
        left = (width - WIDTH) / 2;
        top = (height - HEIGHT) / 2;
        shownAccess = access();
        mobsButton = addRenderableWidget(toggle(left + 10, top + 20, "target_mobs", BasePackets.TargetCategory.MOBS));
        neutralsButton = addRenderableWidget(toggle(left + 10, top + 40, "target_neutrals", BasePackets.TargetCategory.NEUTRALS));
        playersButton = addRenderableWidget(toggle(left + 10, top + 60, "target_players", BasePackets.TargetCategory.PLAYERS));
        if (isAdmin()) {
            addRenderableWidget(Button.builder(ScreenTexts.gui("trusted_players"), b -> openTrustedPlayers())
                    .bounds(left + 10, top + 95, 155, 20)
                    .tooltip(Tooltip.create(Component.translatable("tooltip.openmodularturrets.trusted_player_gui")))
                    .build());
        }
        if (isAdmin() && base().map(TurretBaseBlockEntity::isCamoEnabled).orElse(false)) {
            addRenderableWidget(Button.builder(ScreenTexts.gui("camouflage"), b -> openCamo())
                    .bounds(left + 10, top + 135, 155, 20)
                    .tooltip(Tooltip.create(Component.translatable("tooltip.openmodularturrets.camouflage")))
                    .build());
        }
        addRenderableWidget(Button.builder(ScreenTexts.gui("back"), b -> back()).bounds(left + 185, top + 20, 80, 20).build());
        if (isOwner()) {
            addRenderableWidget(DisownConfirm.button(left + 185, top + 45, 80, pos,
                    () -> minecraft.setScreen(this)));
        }
        updateLabels();
    }

    private Button toggle(int x, int y, String tooltipKey, BasePackets.TargetCategory category) {
        return Button.builder(Component.empty(), b -> toggleTargeting(category)).bounds(x, y, 155, 20)
                .tooltip(Tooltip.create(Component.translatable("tooltip.openmodularturrets." + tooltipKey)))
                .build();
    }

    private void toggleTargeting(BasePackets.TargetCategory category) {
        base().ifPresent(base -> {
            TargetingSettings targeting = base.getLogic().getTargetingSettings();
            boolean current = switch (category) {
                case MOBS -> targeting.isTargetMobs();
                case NEUTRALS -> targeting.isTargetPassive();
                case PLAYERS -> targeting.isTargetPlayers();
            };
            ClientNetworkCompat.sendToServer(OMTPackets.SET_TARGETING,
                    new BasePackets.SetTargeting(pos, category, !current));
        });
    }

    @SuppressWarnings("DataFlowIssue")
    private void openTrustedPlayers() {
        if (minecraft != null) minecraft.setScreen(new TrustedPlayersScreen(pos, () -> minecraft.setScreen(this)));
    }

    /** Opens the camouflage menu (a server-side menu, like the base GUI). */
    private void openCamo() {
        ClientNetworkCompat.sendToServer(OMTPackets.OPEN_CAMO_MENU, new CamoPackets.OpenMenu(pos));
    }

    /** "Back": reopen the base GUI (a server-side menu). */
    private void back() {
        ClientNetworkCompat.sendToServer(OMTPackets.BASE_ACTION,
                new BasePackets.SimpleAction(pos, BasePackets.Action.OPEN_MENU));
    }

    private void updateLabels() {
        base().ifPresent(base -> {
            TargetingSettings targeting = base.getLogic().getTargetingSettings();
            mobsButton.setMessage(ScreenTexts.labelled("attack_mobs", ScreenTexts.yesNo(targeting.isTargetMobs())));
            neutralsButton.setMessage(ScreenTexts.labelled("attack_neutrals", ScreenTexts.yesNo(targeting.isTargetPassive())));
            playersButton.setMessage(ScreenTexts.labelled("attack_players", ScreenTexts.yesNo(targeting.isTargetPlayers())));
        });
    }

    /** Players below "change settings" are sent back to the base GUI. */
    @Override
    public void tick() {
        super.tick();
        Optional<AccessLevel> access = access();
        if (access.map(level -> !level.isAtLeast(AccessLevel.CHANGE_SETTINGS)).orElse(true)) {
            onClose();
            if (access.isPresent()) back();
            return;
        }
        if (!access.equals(shownAccess)) {
            rebuildWidgets();
        }
        updateLabels();
    }

    @Override
    protected void renderPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ClientCompat.blit(graphics, TEXTURE, left, top, 0, 0, WIDTH, HEIGHT);
        graphics.drawString(font, ScreenTexts.gui("targeting_options").append(":"), left + 10, top + 9, 0xFF000000, false);
        if (isAdmin()) {
            graphics.drawString(font, ScreenTexts.gui("trusted_players").append(":"), left + 10, top + 84, 0xFF000000, false);
        }
    }
}
