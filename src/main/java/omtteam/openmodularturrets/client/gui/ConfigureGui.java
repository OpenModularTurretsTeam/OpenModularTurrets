package omtteam.openmodularturrets.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import omtteam.omlib.client.gui.IHasTooltips;
import omtteam.omlib.util.PlayerUtil;
import omtteam.omlib.util.TrustedPlayer;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.client.gui.containers.ConfigContainer;
import omtteam.openmodularturrets.handler.NetworkingHandler;
import omtteam.openmodularturrets.network.messages.*;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.TurretBase;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

import static omtteam.omlib.util.GeneralUtil.getColoredBooleanLocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.safeLocalize;
import static omtteam.omlib.util.compat.ChatTools.addChatMessage;

public class ConfigureGui extends GuiContainer implements IHasTooltips {
    private final TurretBase base;
    private GuiTextField textFieldAddTrustedPlayer;
    private final EntityPlayer player;
    private int mouseX;
    private int mouseY;
    private int waitForServerTrustedPlayers = -1;

    public ConfigureGui(InventoryPlayer inventoryPlayer, TurretBase tileEntity) {
        super(new ConfigContainer(tileEntity, inventoryPlayer.player));
        this.base = tileEntity;
        player = inventoryPlayer.player;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        String mobsButton = safeLocalize(OMTNames.Localizations.GUI.ATTACK_MOBS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksMobs()));
        String neutralsButton = safeLocalize(OMTNames.Localizations.GUI.ATTACK_NEUTRALS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksNeutrals()));
        String playersButton = safeLocalize(OMTNames.Localizations.GUI.ATTACK_PLAYERS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksPlayers()));

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        textFieldAddTrustedPlayer = new GuiTextField(0, fontRenderer, 11, 99, 100, 18);
        textFieldAddTrustedPlayer.setMaxStringLength(50);
        textFieldAddTrustedPlayer.setFocused(true);

        this.buttonList.add(new GuiButton(1, x + 10, y + 20, 155, 20, mobsButton));
        this.buttonList.add(new GuiButton(2, x + 10, y + 40, 155, 20, neutralsButton));
        this.buttonList.add(new GuiButton(3, x + 10, y + 60, 155, 20, playersButton));
        this.buttonList.add(new GuiButton(4, x + 114, y + 98, 51, 20, "+"));
        this.buttonList.add(new GuiButton(5, x + 35, y + 135, 30, 20, "-"));
        this.buttonList.add(new GuiButton(6, x + 10, y + 135, 20, 20, "<<"));
        this.buttonList.add(new GuiButton(7, x + 145, y + 135, 20, 20, ">>"));

        if (this.base.getTrustedPlayers().size() > 0) {
            this.buttonList.add(new GuiButton(8, x + 70, y + 135, 23, 20, this.base.getTrustedPlayers().get(
                    base.trustedPlayerIndex).canOpenGUI ? "\u00A72Y" : "\u00A7cN"));
            this.buttonList.add(new GuiButton(9, x + 93, y + 135, 23, 20, this.base.getTrustedPlayers().get(
                    base.trustedPlayerIndex).canChangeTargeting ? "\u00A72Y" : "\u00A7cN"));
            this.buttonList.add(new GuiButton(10, x + 116, y + 135, 23, 20, this.base.getTrustedPlayers().get(
                    base.trustedPlayerIndex).admin ? "\u00A72Y" : "\u00A7cN"));
        } else {
            this.buttonList.add(new GuiButton(8, x + 70, y + 135, 23, 20, "?"));
            this.buttonList.add(new GuiButton(9, x + 93, y + 135, 23, 20, "?"));
            this.buttonList.add(new GuiButton(10, x + 116, y + 135, 23, 20, "?"));
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
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TP_CAN_OPEN_GUI));
                break;
            case 9:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TP_CAN_CHANGE_TARGETING));
                break;
            case 10:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TP_CAN_ADMINISTER));
                break;
            case 11:
                tooltip.add(safeLocalize(OMTNames.Localizations.Tooltip.TEXT_TRUSTED_PLAYER));
                break;
        }
        if (!tooltip.isEmpty())
            this.drawHoveringText(tooltip, mouseX - k, mouseY - l, Minecraft.getMinecraft().fontRendererObj);
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
        TrustedPlayer trustedPlayer = PlayerUtil.getTrustedPlayer(player, base);
        if (guibutton.id == 1) { //change Attack Mobs
            if (PlayerUtil.isPlayerOwner(player, base)) {
                sendChangeToServerMobs(!base.isAttacksMobs());
                guibutton.displayString = safeLocalize(OMTNames.Localizations.GUI.ATTACK_MOBS) + ": " + (getColoredBooleanLocalizationYesNo(!base.isAttacksMobs()));
            } else if (trustedPlayer != null && trustedPlayer.canChangeTargeting) {
                sendChangeToServerMobs(!base.isAttacksMobs());
                guibutton.displayString = safeLocalize(OMTNames.Localizations.GUI.ATTACK_MOBS) + ": " + (getColoredBooleanLocalizationYesNo(!base.isAttacksMobs()));
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
            }
        }

        if (guibutton.id == 2) { //change Attack Neutrals
            if (PlayerUtil.isPlayerOwner(player, base)) {
                sendChangeToServerNeutrals(!base.isAttacksNeutrals());
                guibutton.displayString = safeLocalize(OMTNames.Localizations.GUI.ATTACK_NEUTRALS) + ": " + (getColoredBooleanLocalizationYesNo(!base.isAttacksNeutrals()));
            } else if (trustedPlayer != null && trustedPlayer.canChangeTargeting) {
                sendChangeToServerNeutrals(!base.isAttacksNeutrals());
                guibutton.displayString = safeLocalize(OMTNames.Localizations.GUI.ATTACK_NEUTRALS) + ": " + (getColoredBooleanLocalizationYesNo(!base.isAttacksNeutrals()));
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
            }
        }

        if (guibutton.id == 3) { // change Attack Players
            if (PlayerUtil.isPlayerOwner(player, base)) {
                sendChangeToServerPlayers(!base.isAttacksPlayers());
                guibutton.displayString = safeLocalize(OMTNames.Localizations.GUI.ATTACK_PLAYERS) + ": " + (getColoredBooleanLocalizationYesNo(!base.isAttacksPlayers()));
            } else if (trustedPlayer != null && trustedPlayer.canChangeTargeting) {
                sendChangeToServerPlayers(!base.isAttacksPlayers());
                guibutton.displayString = safeLocalize(OMTNames.Localizations.GUI.ATTACK_PLAYERS) + ": " + (getColoredBooleanLocalizationYesNo(!base.isAttacksPlayers()));
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
            }
        }

        if (guibutton.id == 4) { //add trusted player
            if (PlayerUtil.isPlayerOwner(player, base)) {
                if (!textFieldAddTrustedPlayer.getText().equals("") || !textFieldAddTrustedPlayer.getText().isEmpty()) {
                    sendChangeToServerAddTrusted();
                    textFieldAddTrustedPlayer.setText("");
                    waitForServerTrustedPlayers = 20;
                }
            } else if (trustedPlayer != null && trustedPlayer.admin) {
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
                        base.trustedPlayerIndex) != null && player.getUniqueID().toString().equals(base.getOwner())) {
                    sendChangeToServerRemoveTrusted();
                    base.removeTrustedPlayer(base.getTrustedPlayers().get(base.trustedPlayerIndex).getName());
                    textFieldAddTrustedPlayer.setText("");
                    this.base.trustedPlayerIndex = 0;
                    player.openGui(OpenModularTurrets.instance, 6, player.getEntityWorld(), base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
                } else if (base.getTrustedPlayer(player.getUniqueID()).admin) {
                    if (this.base.getTrustedPlayers().get(
                            base.trustedPlayerIndex) != null && this.base.getTrustedPlayers().size() > 0) {
                        sendChangeToServerRemoveTrusted();
                        base.removeTrustedPlayer(base.getTrustedPlayers().get(base.trustedPlayerIndex).getName());
                        textFieldAddTrustedPlayer.setText("");
                        this.base.trustedPlayerIndex = 0;
                        if (this.base.getTrustedPlayers().get(base.trustedPlayerIndex).uuid.equals(
                                player.getUniqueID()) && !player.getUniqueID().toString().equals(base.getOwner())) {
                            mc.displayGuiScreen(null);
                            return;
                        }
                        player.openGui(OpenModularTurrets.instance, 6, player.getEntityWorld(), base.getPos().getX(), base.getPos().getY(),
                                base.getPos().getZ());
                    }
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

        if (guibutton.id == 8) { //change trusted player permission for GUI opening
            if (PlayerUtil.isPlayerOwner(player, base) && this.base.getTrustedPlayers().get(
                    base.trustedPlayerIndex) != null) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), "gui",
                        !base.getTrustedPlayers().get(base.trustedPlayerIndex).canOpenGUI);
                guibutton.displayString = !base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).canOpenGUI ? "\u00A72Y" : "\u00A7cN";
            } else if (this.base.getTrustedPlayers().get(base.trustedPlayerIndex) != null && trustedPlayer != null && trustedPlayer.admin) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), "gui",
                        !base.getTrustedPlayers().get(base.trustedPlayerIndex).canOpenGUI);
                guibutton.displayString = !base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).canOpenGUI ? "\u00A72Y" : "\u00A7cN";
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
            }
        }

        if (guibutton.id == 9) { //change trusted player permission for targeting

            if (this.base.getTrustedPlayers().size() <= base.trustedPlayerIndex) {
                return;
            }

            if (PlayerUtil.isPlayerOwner(player, base) && this.base.getTrustedPlayers().get(
                    base.trustedPlayerIndex) != null) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), "targeting",
                        !base.getTrustedPlayers().get(base.trustedPlayerIndex).canChangeTargeting);
                guibutton.displayString = !base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).canChangeTargeting ? "\u00A72Y" : "\u00A7cN";
            } else if (this.base.getTrustedPlayers().get(base.trustedPlayerIndex) != null && trustedPlayer != null && trustedPlayer.admin) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), "targeting",
                        !base.getTrustedPlayers().get(base.trustedPlayerIndex).canChangeTargeting);
                guibutton.displayString = !base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).canChangeTargeting ? "\u00A72Y" : "\u00A7cN";
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
            }
        }

        if (guibutton.id == 10) { //change trusted player permission for administering

            if (this.base.getTrustedPlayers().size() <= base.trustedPlayerIndex) {
                return;
            }

            if (PlayerUtil.isPlayerOwner(player, base) && this.base.getTrustedPlayers().get(
                    base.trustedPlayerIndex) != null) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), "isAdmin",
                        !base.getTrustedPlayers().get(base.trustedPlayerIndex).admin);
                guibutton.displayString = !base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).admin ? "\u00A72Y" : "\u00A7cN";
            } else if (this.base.getTrustedPlayers().get(base.trustedPlayerIndex) != null && trustedPlayer != null && trustedPlayer.admin) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), "isAdmin",
                        !base.getTrustedPlayers().get(base.trustedPlayerIndex).admin);
                guibutton.displayString = !base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).admin ? "\u00A72Y" : "\u00A7cN";
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        fontRenderer.drawString(safeLocalize(OMTNames.Localizations.GUI.TARGETING_OPTIONS) + ": ", 10, 8, 0);
        fontRenderer.drawString(safeLocalize(OMTNames.Localizations.GUI.ADD_TRUSTED_PLAYER) + ": ", 10, 87, 0);

        if (this.base.getTrustedPlayers().size() == 0) {
            fontRenderer.drawString("\u00A7f" + safeLocalize(OMTNames.Localizations.GUI.NO_TRUSTED_PLAYERS), 10, 124, 0);
        } else {
            fontRenderer.drawString(base.getTrustedPlayers().get(base.trustedPlayerIndex).getName() + "'s " + safeLocalize(OMTNames.Localizations.GUI.PERMISSIONS),
                    10, 124, 0);
        }

        textFieldAddTrustedPlayer.drawTextBox();
        drawTooltips();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        ResourceLocation texture = (new ResourceLocation(OMTNames.Textures.configureGUI));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.mouseX = par1;
        this.mouseY = par2;
        super.drawScreen(par1, par2, par3);
    }

    private void sendChangeToServerMobs(boolean setTo) {
        MessageToggleAttackMobs message = new MessageToggleAttackMobs(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(), setTo);
        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendChangeToServerNeutrals(boolean setTo) {
        MessageToggleAttackNeutralMobs message = new MessageToggleAttackNeutralMobs(base.getPos().getX(), base.getPos().getY(),
                base.getPos().getZ(), setTo);
        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendChangeToServerPlayers(boolean setTo) {
        MessageToggleAttackPlayers message = new MessageToggleAttackPlayers(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(),
                setTo);

        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendChangeToServerAddTrusted() {
        MessageAddTrustedPlayer message = new MessageAddTrustedPlayer(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(),
                textFieldAddTrustedPlayer.getText());

        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendChangeToServerRemoveTrusted() {
        MessageRemoveTrustedPlayer message = new MessageRemoveTrustedPlayer(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(),
                base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).getName());

        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendChangeToServerModifyPermissions(String player, String perm, boolean canDo) {
        MessageModifyPermissions message = new MessageModifyPermissions(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(), player,
                perm, canDo);

        NetworkingHandler.INSTANCE.sendToServer(message);
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
}