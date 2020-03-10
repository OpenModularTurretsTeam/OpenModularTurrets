package omtteam.openmodularturrets.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import omtteam.omlib.OMLib;
import omtteam.omlib.api.gui.GuiParameters;
import omtteam.omlib.api.gui.IHasTooltips;
import omtteam.omlib.api.gui.ISupportsBackSystem;
import omtteam.omlib.handler.GUIBackSystem;
import omtteam.omlib.network.OMLibNetworkingHandler;
import omtteam.omlib.network.messages.MessageCloseGUITile;
import omtteam.omlib.network.messages.MessageOpenGUITile;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.OMTNetworkingHandler;
import omtteam.openmodularturrets.network.messages.*;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.TurretBase;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

import static omtteam.omlib.util.GeneralUtil.getColoredBooleanLocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.safeLocalize;
import static omtteam.omlib.util.player.PlayerUtil.addChatMessage;
import static omtteam.omlib.util.player.PlayerUtil.isPlayerOwner;

public class ConfigureGui extends GuiScreen implements IHasTooltips, GuiPageButtonList.GuiResponder, GuiSlider.FormatHelper, ISupportsBackSystem {
    protected final int xSize = 176;
    protected final int ySize = 205;
    private final TurretBase base;
    private final EntityPlayer player;
    protected int guiLeft;
    protected int guiTop;
    private int mouseX;
    private int mouseY;
    private GuiSlider sliderLightValue;
    private GuiSlider sliderLightOpacity;
    private int lightValue;
    private int lightOpacity;
    private boolean addedToSyncList;
    private int accessLevel = 0;

    public ConfigureGui(InventoryPlayer inventoryPlayer, TurretBase tileEntity) {
        super();
        this.base = tileEntity;
        this.lightValue = this.base.getCamoSettings().getLightValue();
        this.lightOpacity = this.base.getCamoSettings().getLightOpacity();
        player = inventoryPlayer.player;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void buttonInit() {

        if (PlayerUtil.canPlayerChangeSetting(player, base)) {
            String mobsButton = safeLocalize(OMLibNames.Localizations.GUI.ATTACK_MOBS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksMobs()));
            String neutralsButton = safeLocalize(OMLibNames.Localizations.GUI.ATTACK_NEUTRALS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksNeutrals()));
            String playersButton = safeLocalize(OMLibNames.Localizations.GUI.ATTACK_PLAYERS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksPlayers()));
            this.buttonList.add(new GuiButton(0, guiLeft + 10, guiTop + 20, 155, 20, mobsButton));
            this.buttonList.add(new GuiButton(1, guiLeft + 10, guiTop + 40, 155, 20, neutralsButton));
            this.buttonList.add(new GuiButton(2, guiLeft + 10, guiTop + 60, 155, 20, playersButton));
        }
        if (PlayerUtil.isPlayerAdmin(player, base)) {

            this.buttonList.add(new GuiButton(3, guiLeft + 10, guiTop + 95, 155, 20, safeLocalize(OMLibNames.Localizations.GUI.TRUSTED_PLAYERS)));
            this.sliderLightValue = new GuiSlider(this, 4, guiLeft + 10, guiTop + 157,
                                                  safeLocalize(OMTNames.Localizations.GUI.LIGHT_VALUE), 0, 15, lightValue, this);
            this.sliderLightOpacity = new GuiSlider(this, 5, guiLeft + 10, guiTop + 179,
                                                    safeLocalize(OMTNames.Localizations.GUI.LIGHT_OPACITY), 0, 15, lightOpacity, this);

            if (this.base.getTier() > 3) {
                this.buttonList.add(sliderLightValue);
                this.buttonList.add(sliderLightOpacity);
            }
        }
        this.buttonList.add(new GuiButton(6, guiLeft + 185, guiTop + 20, 80, 20, safeLocalize(OMLibNames.Localizations.GUI.BACK)));
    }

    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        if (!addedToSyncList) {
            OMLibNetworkingHandler.INSTANCE.sendToServer(new MessageOpenGUITile(base));
            addedToSyncList = true;
        }

        accessLevel = PlayerUtil.getPlayerAccessLevel(player, base).ordinal();
        if (accessLevel < 2) {
            player.closeScreen();
        }

        this.buttonList.clear();
        this.buttonInit();
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.mouseX = par1;
        this.mouseY = par2;

        this.drawDefaultBackground();

        if (accessLevel != PlayerUtil.getPlayerAccessLevel(player, base).ordinal() && !isPlayerOwner(player, base)) {
            accessLevel = PlayerUtil.getPlayerAccessLevel(player, base).ordinal();
            if (accessLevel != 0) {
                this.initGui();
            }
        } else if (!isPlayerOwner(player, base) && accessLevel < 2) {
            player.closeScreen();
            player.openGui(OpenModularTurrets.instance, 1, player.getEntityWorld(), base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
            return;
        }
        ResourceLocation texture = (new ResourceLocation(OMTNames.Textures.configureGUI));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);
        this.drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawString(safeLocalize(OMLibNames.Localizations.GUI.TARGETING_OPTIONS) + ": ", guiLeft + 10, guiTop + 9, 0);
        if (accessLevel > 2) {
            fontRenderer.drawString(safeLocalize(OMLibNames.Localizations.GUI.TRUSTED_PLAYERS) + ": ", guiLeft + 10, guiTop + 84, 0);
            fontRenderer.drawString(safeLocalize(OMLibNames.Localizations.GUI.LIGHT_VALUES) + ": ", guiLeft + 10, guiTop + 145, 0);
        }
        buttonList.get(0).displayString = safeLocalize(OMLibNames.Localizations.GUI.ATTACK_MOBS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksMobs()));
        buttonList.get(1).displayString = safeLocalize(OMLibNames.Localizations.GUI.ATTACK_NEUTRALS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksNeutrals()));
        buttonList.get(2).displayString = safeLocalize(OMLibNames.Localizations.GUI.ATTACK_PLAYERS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksPlayers()));

        super.drawScreen(par1, par2, par3);
        drawTooltips();
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 0) { //change Attack Mobs
            if (PlayerUtil.canPlayerChangeSetting(player, base)) {
                sendChangeToServerMobs(!base.isAttacksMobs());
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize(OMLibNames.Localizations.Text.STATUS_PERMISSION)));
            }
        }

        if (guibutton.id == 1) { //change Attack Neutrals
            if (PlayerUtil.canPlayerChangeSetting(player, base)) {
                sendChangeToServerNeutrals(!base.isAttacksNeutrals());
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize(OMLibNames.Localizations.Text.STATUS_PERMISSION)));
            }
        }

        if (guibutton.id == 2) { // change Attack Players
            if (PlayerUtil.canPlayerChangeSetting(player, base)) {
                sendChangeToServerPlayers(!base.isAttacksPlayers());
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize(OMLibNames.Localizations.Text.STATUS_PERMISSION)));
            }
        }
        if (guibutton.id == 3) { // Open TP GUI for this block
            if (PlayerUtil.isPlayerAdmin(player, base)) {
                player.openGui(OMLib.instance, 0, player.getEntityWorld(), base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize(OMLibNames.Localizations.Text.STATUS_PERMISSION)));
            }
        }
        if (guibutton.id == 6) { //back button
            GUIBackSystem.getInstance().openLastGui(player);
        }
    }

    @Override
    public void setEntryValue(int id, boolean value) {

    }

    @Override
    public void setEntryValue(int id, float value) {
        if (id == 4) {
            this.lightValue = Math.round(value);
            this.sliderLightValue.setSliderValue(Math.round(value), false);
            sendChangeToServerLightValue(lightValue);
        } else if (id == 5) {
            this.lightOpacity = Math.round(value);
            this.sliderLightOpacity.setSliderValue(Math.round(value), false);
            sendChangeToServerLightOpacity(lightOpacity);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setEntryValue(int id, String value) {

    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getText(int id, String name, float value) {
        return name + ": " + Math.round(value);
    }

    @Override
    public void drawTooltips() {
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        int tooltipToDraw = buttonList.stream().filter(GuiButton::isMouseOver).mapToInt(s -> s.id).sum();
        ArrayList<String> tooltip = new ArrayList<>();
        switch (tooltipToDraw) {
            case 0:
                if (buttonList.get(0).isMouseOver()) {
                    tooltip.add(safeLocalize(OMLibNames.Localizations.Tooltip.TARGET_MOBS));
                }
                break;
            case 1:
                tooltip.add(safeLocalize(OMLibNames.Localizations.Tooltip.TARGET_NEUTRALS));
                break;
            case 2:
                tooltip.add(safeLocalize(OMLibNames.Localizations.Tooltip.TARGET_PLAYERS));
                break;
            case 3:
                tooltip.add(safeLocalize(OMLibNames.Localizations.Tooltip.TRUSTED_PLAYER_GUI));
                break;
            default:
        }
        if (!tooltip.isEmpty())
            this.drawHoveringText(tooltip, guiLeft + mouseX - k, guiTop + mouseY - l, Minecraft.getMinecraft().fontRenderer);
    }

    private void sendChangeToServerMobs(boolean setTo) {
        MessageToggleAttackMobs message = new MessageToggleAttackMobs(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(), setTo);
        OMTNetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendChangeToServerNeutrals(boolean setTo) {
        MessageToggleAttackNeutralMobs message = new MessageToggleAttackNeutralMobs(base.getPos().getX(), base.getPos().getY(),
                                                                                    base.getPos().getZ(), setTo);
        OMTNetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendChangeToServerPlayers(boolean setTo) {
        MessageToggleAttackPlayers message = new MessageToggleAttackPlayers(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(),
                                                                            setTo);

        OMTNetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendChangeToServerLightValue(int value) {
        MessageAdjustLightValue message = new MessageAdjustLightValue(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(), value);
        OMTNetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendChangeToServerLightOpacity(int value) {
        MessageAdjustLightOpacity message = new MessageAdjustLightOpacity(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(), value);
        OMTNetworkingHandler.INSTANCE.sendToServer(message);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        OMLibNetworkingHandler.INSTANCE.sendToServer(new MessageCloseGUITile(base));
        super.onGuiClosed();
    }

    @Override
    @Nullable
    public GuiParameters getGuiParameters() {
        return new GuiParameters(OpenModularTurrets.instance, 20, player.getEntityWorld(),
                                 base.getPos().getX(),
                                 base.getPos().getY(),
                                 base.getPos().getZ());
    }
}