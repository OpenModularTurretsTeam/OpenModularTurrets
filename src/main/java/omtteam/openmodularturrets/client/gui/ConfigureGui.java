package omtteam.openmodularturrets.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import omtteam.omlib.client.gui.IHasTooltips;
import omtteam.omlib.handler.OMLibNetworkingHandler;
import omtteam.omlib.network.messages.MessageCloseGUI;
import omtteam.omlib.network.messages.MessageOpenGUI;
import omtteam.omlib.util.PlayerUtil;
import omtteam.omlib.util.TrustedPlayer;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.OMTNetworkingHandler;
import omtteam.openmodularturrets.network.messages.*;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.TurretBase;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

import static omtteam.omlib.util.GeneralUtil.getColoredBooleanLocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.safeLocalize;
import static omtteam.omlib.util.PlayerUtil.addChatMessage;

public class ConfigureGui extends GuiScreen implements IHasTooltips {
    protected int xSize = 176;
    protected int ySize = 166;
    protected int guiLeft;
    protected int guiTop;
    private final TurretBase base;
    private GuiTextField textFieldAddTrustedPlayer;
    private final EntityPlayer player;
    private int mouseX;
    private int mouseY;
    private int waitForServerTrustedPlayers = -1;

    public ConfigureGui(InventoryPlayer inventoryPlayer, TurretBase tileEntity) {
        super();
        this.base = tileEntity;
        player = inventoryPlayer.player;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        //super.initGui();
        OMLibNetworkingHandler.INSTANCE.sendToServer(new MessageOpenGUI(base));
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.buttonList.clear();

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        String mobsButton = safeLocalize(OMTNames.Localizations.GUI.ATTACK_MOBS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksMobs()));
        String neutralsButton = safeLocalize(OMTNames.Localizations.GUI.ATTACK_NEUTRALS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksNeutrals()));
        String playersButton = safeLocalize(OMTNames.Localizations.GUI.ATTACK_PLAYERS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksPlayers()));


        textFieldAddTrustedPlayer = new GuiTextField(0, fontRenderer, guiLeft + 11, guiTop + 99, 100, 18);
        textFieldAddTrustedPlayer.setMaxStringLength(50);
        textFieldAddTrustedPlayer.setFocused(true);
        fontRenderer.drawString("?", guiLeft + 93, guiTop + 135, 0);

        this.buttonList.add(new GuiButton(1, guiLeft + 10, guiTop + 20, 155, 20, mobsButton));
        this.buttonList.add(new GuiButton(2, guiLeft + 10, guiTop + 40, 155, 20, neutralsButton));
        this.buttonList.add(new GuiButton(3, guiLeft + 10, guiTop + 60, 155, 20, playersButton));
        this.buttonList.add(new GuiButton(4, guiLeft + 114, guiTop + 98, 51, 20, "+"));
        this.buttonList.add(new GuiButton(5, guiLeft + 35, guiTop + 135, 30, 20, "-"));
        this.buttonList.add(new GuiButton(6, guiLeft + 10, guiTop + 135, 20, 20, "<<"));
        this.buttonList.add(new GuiButton(7, guiLeft + 145, guiTop + 135, 20, 20, ">>"));

        if (this.base.getTrustedPlayers().size() > 0) {
            this.buttonList.add(new GuiButton(8, guiLeft + 70, guiTop + 135, 23, 20, "-"));
            this.buttonList.add(new GuiButton(9, guiLeft + 116, guiTop + 135, 23, 20, "+"));
        } else {
            this.buttonList.add(new GuiButton(8, guiLeft + 70, guiTop + 135, 23, 20, "-"));
            this.buttonList.add(new GuiButton(9, guiLeft + 116, guiTop + 135, 23, 20, "+"));
        }
    }

    private void drawMode() {
        if (base.getTrustedPlayers().size() > base.trustedPlayerIndex) {
            TrustedPlayer trustedPlayer = base.getTrustedPlayers().get(base.trustedPlayerIndex);
            if (trustedPlayer != null) {
                fontRenderer.drawString(trustedPlayer.getAccessMode().ordinal() + "", guiLeft + 102, guiTop + 141, 0xFFFF00);
            }
        } else {
            fontRenderer.drawString("?", guiLeft + 102, guiTop + 140, 0);
        }
    }

    @Override
    public void drawTooltips() {
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        int tooltipToDraw = buttonList.stream().filter(GuiButton::isMouseOver).mapToInt(s -> s.id).sum();
        if (tooltipToDraw == 0) {
            tooltipToDraw = isMouseOverTextField(textFieldAddTrustedPlayer, mouseX - this.guiLeft, mouseY - this.guiTop) ? 11 : 0;
        }
        if (tooltipToDraw == 0 && mouseX > guiLeft + 95 && mouseX < guiLeft + 115 && mouseY > guiTop + 135 && mouseY < guiTop + 155) {
            tooltipToDraw = 10;
        }
        ArrayList<String> tooltip = new ArrayList<>();
        switch (tooltipToDraw) {
            case 1:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TARGET_MOBS));
                break;
            case 2:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TARGET_NEUTRALS));
                break;
            case 3:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TARGET_PLAYERS));
                break;
            case 4:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.ADD_TRUSTED_PLAYER));
                break;
            case 5:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.REMOVE_TRUSTED_PLAYER));
                break;
            case 6:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.VIEW_NEXT_TRUSTED_PLAYER));
                break;
            case 7:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.VIEW_PREVIOUS_TRUSTED_PLAYER));
                break;
            case 8:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TP_DECREASE_ACCESS));
                break;
            case 9:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TP_INCREASE_ACCESS));
                break;
            case 10:
                if (base.getTrustedPlayers().size() > base.trustedPlayerIndex) {
                    TrustedPlayer trustedPlayer = base.getTrustedPlayers().get(base.trustedPlayerIndex);
                    if (trustedPlayer != null) {
                        switch (trustedPlayer.getAccessMode()) {
                            case ADMIN:
                                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TP_CAN_ADMINISTER));
                                break;
                            case CHANGE_SETTINGS:
                                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TP_CAN_CHANGE_SETTINGS));
                                break;
                            case OPEN_GUI:
                                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TP_CAN_OPEN_GUI));
                                break;
                            case NONE:
                                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TP_NONE));
                                break;
                        }
                    }
                }
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.INFO_ACCESS_LEVEL));
                break;
            case 11:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TEXT_TRUSTED_PLAYER));
                break;
        }
        if (!tooltip.isEmpty())
            this.drawHoveringText(tooltip, guiLeft + mouseX - k, guiTop + mouseY - l, Minecraft.getMinecraft().fontRenderer);
    }

    @SuppressWarnings("EmptyCatchBlock")
    @Override
    public void mouseClicked(int i, int j, int k) {
        try {
            super.mouseClicked(i, j, k);
        } catch (IOException e) {

        }
        textFieldAddTrustedPlayer.mouseClicked(i - guiLeft, j - guiTop, k);
    }

    @SuppressWarnings("EmptyCatchBlock")
    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (!textFieldAddTrustedPlayer.isFocused()) {
            try {
                super.keyTyped(typedChar, keyCode);
            } catch (IOException e) {

            }
        } else {
            textFieldAddTrustedPlayer.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 1) { //change Attack Mobs
            if (PlayerUtil.canPlayerChangeSetting(player, base)) {
                sendChangeToServerMobs(!base.isAttacksMobs());
                guibutton.displayString = safeLocalize(OMTNames.Localizations.GUI.ATTACK_MOBS) + ": " + (getColoredBooleanLocalizationYesNo(!base.isAttacksMobs()));
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
            }
        }

        if (guibutton.id == 2) { //change Attack Neutrals
            if (PlayerUtil.canPlayerChangeSetting(player, base)) {
                sendChangeToServerNeutrals(!base.isAttacksNeutrals());
                guibutton.displayString = safeLocalize(OMTNames.Localizations.GUI.ATTACK_NEUTRALS) + ": " + (getColoredBooleanLocalizationYesNo(!base.isAttacksNeutrals()));
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
            }
        }

        if (guibutton.id == 3) { // change Attack Players
            if (PlayerUtil.canPlayerChangeSetting(player, base)) {
                sendChangeToServerPlayers(!base.isAttacksPlayers());
                guibutton.displayString = safeLocalize(OMTNames.Localizations.GUI.ATTACK_PLAYERS) + ": " + (getColoredBooleanLocalizationYesNo(!base.isAttacksPlayers()));
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
            }
        }

        if (guibutton.id == 4) { //add trusted player
            if (PlayerUtil.isPlayerAdmin(player, base)) {
                if (!textFieldAddTrustedPlayer.getText().equals("") || !textFieldAddTrustedPlayer.getText().isEmpty()) {
                    sendChangeToServerAddTrusted();
                    textFieldAddTrustedPlayer.setText("");
                    waitForServerTrustedPlayers = 20;
                }
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
            }
        }

        if (guibutton.id == 5) { //remove trusted player

            if (this.base.getTrustedPlayers().size() <= base.trustedPlayerIndex) {
                return;
            }

            if (base.getTrustedPlayers().size() > 0) {
                if (this.base.getTrustedPlayers().get(
                        base.trustedPlayerIndex) != null && PlayerUtil.isPlayerAdmin(player, base)) {
                    sendChangeToServerRemoveTrusted();
                    base.removeTrustedPlayer(base.getTrustedPlayers().get(base.trustedPlayerIndex).getName());
                    textFieldAddTrustedPlayer.setText("");
                    this.base.trustedPlayerIndex = 0;
                    if (!player.getUniqueID().toString().equals(base.getOwner()) && (base.getTrustedPlayers().size() == 0 || PlayerUtil.isPlayerAdmin(player, base))) {
                        mc.displayGuiScreen(null);
                        return;
                    }
                    player.openGui(OpenModularTurrets.instance, 6, player.getEntityWorld(), base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
                } else {
                    addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
                }
            }
        }

        if (guibutton.id == 6) { //decrease index of trusted player list
            if ((this.base.trustedPlayerIndex - 1 >= 0)) {
                this.base.trustedPlayerIndex--;
                player.openGui(OpenModularTurrets.instance, 6, player.getEntityWorld(), base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
            }
        }

        if (guibutton.id == 7) { //increase index of trusted player list
            if (!((this.base.trustedPlayerIndex + 1) > (base.getTrustedPlayers().size() - 1))) {
                this.base.trustedPlayerIndex++;
                player.openGui(OpenModularTurrets.instance, 6, player.getEntityWorld(), base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
            }
        }

        if (this.base.getTrustedPlayers().size() <= base.trustedPlayerIndex) {
            return;
        }

        if (guibutton.id == 8) { // decrease permission level by 1
            if (PlayerUtil.isPlayerAdmin(player, base) && this.base.getTrustedPlayers().get(
                    base.trustedPlayerIndex) != null) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), -1);

            } else {
                addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
            }
        }

        if (guibutton.id == 9) { //increase permission level by 1
            if (PlayerUtil.isPlayerAdmin(player, base) && this.base.getTrustedPlayers().get(
                    base.trustedPlayerIndex) != null) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), 1);

            } else {
                addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
            }
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.mouseX = par1;
        this.mouseY = par2;

        ResourceLocation texture = (new ResourceLocation(OMTNames.Textures.configureGUI));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawString(safeLocalize(OMTNames.Localizations.GUI.TARGETING_OPTIONS) + ": ", guiLeft + 10, guiTop + 8, 0);
        fontRenderer.drawString(safeLocalize(OMTNames.Localizations.GUI.ADD_TRUSTED_PLAYER) + ": ", guiLeft + 10, guiTop + 87, 0);

        if (this.base.getTrustedPlayers().size() == 0) {
            fontRenderer.drawString("\u00A7f" + safeLocalize(OMTNames.Localizations.GUI.NO_TRUSTED_PLAYERS), guiLeft + 10, guiTop + 124, 0);
        } else {
            fontRenderer.drawString(base.getTrustedPlayers().get(base.trustedPlayerIndex).getName() + "'s " + safeLocalize(OMTNames.Localizations.GUI.PERMISSIONS),
                    guiLeft + 10, 124, 0);
        }
        drawMode();
        textFieldAddTrustedPlayer.drawTextBox();
        super.drawScreen(par1, par2, par3);
        drawTooltips();
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

    private void sendChangeToServerAddTrusted() {
        MessageAddTrustedPlayer message = new MessageAddTrustedPlayer(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(),
                textFieldAddTrustedPlayer.getText());

        OMTNetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendChangeToServerRemoveTrusted() {
        MessageRemoveTrustedPlayer message = new MessageRemoveTrustedPlayer(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(),
                base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).getName());

        OMTNetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendChangeToServerModifyPermissions(String player, Integer change) {
        MessageModifyPermissions message = new MessageModifyPermissions(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(), player,
                change);

        OMTNetworkingHandler.INSTANCE.sendToServer(message);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.base.getTrustedPlayers().size() == 0 && this.buttonList.size() > 9 && !this.buttonList.get(9).displayString.equals("?")) {
            this.initGui();
        } else if (waitForServerTrustedPlayers >= 0 && this.base.getTrustedPlayers().size() > 0) {
            waitForServerTrustedPlayers = -1;
            this.base.trustedPlayerIndex = 0;
            this.initGui();
        } else if (waitForServerTrustedPlayers >= 0) {
            waitForServerTrustedPlayers--;
        }
    }

    @Override
    public void onGuiClosed() {
        OMLibNetworkingHandler.INSTANCE.sendToServer(new MessageCloseGUI(base));
        super.onGuiClosed();
    }
}