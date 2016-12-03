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
import net.minecraft.util.text.translation.I18n;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.client.gui.containers.ConfigContainer;
import omtteam.openmodularturrets.handler.NetworkingHandler;
import omtteam.openmodularturrets.network.messages.*;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.tileentity.TurretBase;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

public class ConfigureGui extends GuiContainer {
    private final TurretBase base;
    private GuiTextField textFieldAddTrustedPlayer;
    private final EntityPlayer player;
    private int mouseX;
    private int mouseY;

    public ConfigureGui(InventoryPlayer inventoryPlayer, TurretBase tileEntity) {
        super(new ConfigContainer(tileEntity));
        this.base = tileEntity;
        player = inventoryPlayer.player;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        String mobsButton = "Attack Mobs: " + (base.isAttacksMobs() ? "\u00A72Yes" : "\u00A7cNo");
        String neutralsButton = "Attack Neutrals: " + (base.isAttacksNeutrals() ? "\u00A72Yes" : "\u00A7cNo");
        String playersButton = "Attack Players: " + (base.isAttacksPlayers() ? "\u00A72Yes" : "\u00A7cNo");

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
            this.buttonList.add(new GuiButton(999, x + 70, y + 135, 23, 20, "?"));
            this.buttonList.add(new GuiButton(999, x + 93, y + 135, 23, 20, "?"));
            this.buttonList.add(new GuiButton(999, x + 116, y + 135, 23, 20, "?"));
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k) {
        try {
            super.mouseClicked(i, j, k);
        } catch (IOException e) {

        }
        textFieldAddTrustedPlayer.mouseClicked(i - guiLeft, j - guiTop, k);
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        if (!textFieldAddTrustedPlayer.isFocused()) {
            try {
                super.keyTyped(par1, par2);
            } catch (IOException e) {

            }
        } else {
            textFieldAddTrustedPlayer.textboxKeyTyped(par1, par2);
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 1) { //change Attack Mobs
            if (player.getUniqueID().toString().equals(base.getOwner())) {
                sendChangeToServerMobs(!base.isAttacksMobs());
                guibutton.displayString = "Attack Mobs: " + (!base.isAttacksMobs() ? "\u00A72Yes" : "\u00A7cNo");
            } else if (base.getTrustedPlayer(player.getUniqueID()).canChangeTargeting) {
                sendChangeToServerMobs(!base.isAttacksMobs());
                guibutton.displayString = "Attack Mobs: " + (!base.isAttacksMobs() ? "\u00A72Yes" : "\u00A7cNo");
            } else {
                player.addChatMessage(new TextComponentString(I18n.translateToLocal("status.ownership")));
            }
        }

        if (guibutton.id == 2) { //change Attack Neutrals
            if (player.getUniqueID().toString().equals(base.getOwner())) {
                sendChangeToServerNeutrals(!base.isAttacksNeutrals());
                guibutton.displayString = "Attack Neutrals: " + (!base.isAttacksNeutrals() ? "\u00A72Yes" : "\u00A7cNo");
            } else if (base.getTrustedPlayer(player.getUniqueID()).canChangeTargeting) {
                sendChangeToServerNeutrals(!base.isAttacksNeutrals());
                guibutton.displayString = "Attack Neutrals: " + (!base.isAttacksNeutrals() ? "\u00A72Yes" : "\u00A7cNo");
            } else {
                player.addChatMessage(new TextComponentString(I18n.translateToLocal("status.ownership")));
            }
        }

        if (guibutton.id == 3) { // change Attack Players
            if (player.getUniqueID().toString().equals(base.getOwner())) {
                sendChangeToServerPlayers(!base.isAttacksPlayers());
                guibutton.displayString = "Attack Players: " + (!base.isAttacksPlayers() ? "\u00A72Yes" : "\u00A7cNo");
            } else if (base.getTrustedPlayer(player.getUniqueID()).canChangeTargeting) {
                sendChangeToServerPlayers(!base.isAttacksPlayers());
                guibutton.displayString = "Attack Players: " + (!base.isAttacksPlayers() ? "\u00A72Yes" : "\u00A7cNo");
            } else {
                player.addChatMessage(new TextComponentString(I18n.translateToLocal("status.ownership")));
            }
        }

        if (guibutton.id == 4) { //add trusted player
            if (player.getUniqueID().toString().equals(base.getOwner())) {
                if (!textFieldAddTrustedPlayer.getText().equals("") || !textFieldAddTrustedPlayer.getText().isEmpty()) {
                    this.base.addTrustedPlayer(textFieldAddTrustedPlayer.getText());
                    sendChangeToServerAddTrusted();
                    textFieldAddTrustedPlayer.setText("");
                    this.base.trustedPlayerIndex = 0;
                    player.openGui(OpenModularTurrets.instance, 6, player.worldObj, base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
                }
            } else if (base.getTrustedPlayer(player.getUniqueID()).admin) {
                if (!textFieldAddTrustedPlayer.getText().equals("") || !textFieldAddTrustedPlayer.getText().isEmpty()) {
                    base.addTrustedPlayer(textFieldAddTrustedPlayer.getText());
                    sendChangeToServerAddTrusted();
                    textFieldAddTrustedPlayer.setText("");
                    this.base.trustedPlayerIndex = 0;
                    player.openGui(OpenModularTurrets.instance, 6, player.worldObj, base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
                }
            } else {
                player.addChatMessage(new TextComponentString(I18n.translateToLocal("status.ownership")));
            }
        }

        if (guibutton.id == 5) { //remove trusted player
            if (base.getTrustedPlayers().size() > 0) {
                if (this.base.getTrustedPlayers().get(
                        base.trustedPlayerIndex) != null && player.getUniqueID().toString().equals(base.getOwner())) {
                    sendChangeToServerRemoveTrusted();
                    base.removeTrustedPlayer(base.getTrustedPlayers().get(base.trustedPlayerIndex).getName());
                    textFieldAddTrustedPlayer.setText("");
                    this.base.trustedPlayerIndex = 0;
                    player.openGui(OpenModularTurrets.instance, 6, player.worldObj, base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
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
                        player.openGui(OpenModularTurrets.instance, 6, player.worldObj, base.getPos().getX(), base.getPos().getY(),
                                base.getPos().getZ());
                    }
                } else {
                    player.addChatMessage(new TextComponentString(I18n.translateToLocal("status.ownership")));
                }
            }
        }

        if (guibutton.id == 6) { //decrease index of trusted player list
            if ((this.base.trustedPlayerIndex - 1 >= 0)) {
                this.base.trustedPlayerIndex--;
                player.openGui(OpenModularTurrets.instance, 6, player.worldObj, base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
            }
        }

        if (guibutton.id == 7) { //increase index of trusted player list
            if (!((this.base.trustedPlayerIndex + 1) > (base.getTrustedPlayers().size() - 1))) {
                this.base.trustedPlayerIndex++;
                player.openGui(OpenModularTurrets.instance, 6, player.worldObj, base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
            }
        }

        if (guibutton.id == 8) { //change trusted player permission for GUI opening
            if (player.getUniqueID().toString().equals(base.getOwner()) && this.base.getTrustedPlayers().get(
                    base.trustedPlayerIndex) != null) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), "gui",
                        !base.getTrustedPlayers().get(base.trustedPlayerIndex).canOpenGUI);
                guibutton.displayString = !base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).canOpenGUI ? "\u00A72Y" : "\u00A7cN";
            } else if (this.base.getTrustedPlayers().get(base.trustedPlayerIndex) != null && base.getTrustedPlayer(
                    player.getUniqueID()).admin) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), "gui",
                        !base.getTrustedPlayers().get(base.trustedPlayerIndex).canOpenGUI);
                guibutton.displayString = !base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).canOpenGUI ? "\u00A72Y" : "\u00A7cN";
            } else {
                player.addChatMessage(new TextComponentString(I18n.translateToLocal("status.ownership")));
            }
        }

        if (guibutton.id == 9) { //change trusted player permission for targeting
            if (player.getUniqueID().toString().equals(base.getOwner()) && this.base.getTrustedPlayers().get(
                    base.trustedPlayerIndex) != null) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), "targeting",
                        !base.getTrustedPlayers().get(base.trustedPlayerIndex).canChangeTargeting);
                guibutton.displayString = !base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).canChangeTargeting ? "\u00A72Y" : "\u00A7cN";
            } else if (this.base.getTrustedPlayers().get(base.trustedPlayerIndex) != null && base.getTrustedPlayer(
                    player.getUniqueID()).admin) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), "targeting",
                        !base.getTrustedPlayers().get(base.trustedPlayerIndex).canChangeTargeting);
                guibutton.displayString = !base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).canChangeTargeting ? "\u00A72Y" : "\u00A7cN";
            } else {
                player.addChatMessage(new TextComponentString(I18n.translateToLocal("status.ownership")));
            }
        }

        if (guibutton.id == 10) { //change trusted player permission for administering
            if (player.getUniqueID().toString().equals(base.getOwner()) && this.base.getTrustedPlayers().get(
                    base.trustedPlayerIndex) != null) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), "isAdmin",
                        !base.getTrustedPlayers().get(base.trustedPlayerIndex).admin);
                guibutton.displayString = !base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).admin ? "\u00A72Y" : "\u00A7cN";
            } else if (this.base.getTrustedPlayers().get(base.trustedPlayerIndex) != null && base.getTrustedPlayer(
                    player.getUniqueID()).admin) {
                sendChangeToServerModifyPermissions(
                        this.base.getTrustedPlayers().get(base.trustedPlayerIndex).getName(), "isAdmin",
                        !base.getTrustedPlayers().get(base.trustedPlayerIndex).admin);
                guibutton.displayString = !base.getTrustedPlayers().get(
                        base.trustedPlayerIndex).admin ? "\u00A72Y" : "\u00A7cN";
            } else {
                player.addChatMessage(new TextComponentString(I18n.translateToLocal("status.ownership")));
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        fontRenderer.drawString("Targeting options:", 10, 8, 0);
        fontRenderer.drawString("Add Trusted Player:", 10, 87, 0);

        if (this.base.getTrustedPlayers().size() == 0) {
            fontRenderer.drawString("\u00A7f<No trusted players to edit>", 10, 124, 0);
        } else {
            fontRenderer.drawString(base.getTrustedPlayers().get(base.trustedPlayerIndex).getName() + "'s Permissions:",
                    10, 124, 0);
        }

        textFieldAddTrustedPlayer.drawTextBox();

        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        // x + 114, y + 98, 51, 20
        if (mouseX > k + 114 && mouseX < k + 114 + 51) {
            if (mouseY > l + 98 && mouseY < l + 98 + 20) {
                ArrayList list = new ArrayList();
                list.add("Adds the given player to the turret's trusted list.");
                this.drawHoveringText(list, mouseX - k, mouseY - l, fontRenderer);
            }
        }

        if (mouseX > k + 35 && mouseX < k + 35 + 30) {
            if (mouseY > l + 135 && mouseY < l + 135 + 20) {
                ArrayList list = new ArrayList();
                list.add("Removes the viewed trusted player from the turret's trusted player list.");
                this.drawHoveringText(list, mouseX - k, mouseY - l, fontRenderer);
            }
        }

        if (mouseX > k + 10 && mouseX < k + 10 + 20) {
            if (mouseY > l + 135 && mouseY < l + 135 + 20) {
                ArrayList list = new ArrayList();
                list.add("View previous trusted player.");
                this.drawHoveringText(list, mouseX - k, mouseY - l, fontRenderer);
            }
        }

        if (mouseX > k + 145 && mouseX < k + 145 + 20) {
            if (mouseY > l + 135 && mouseY < l + 135 + 20) {
                ArrayList list = new ArrayList();
                list.add("View next trusted player.");
                this.drawHoveringText(list, mouseX - k, mouseY - l, fontRenderer);
            }
        }

        // x + 70,y + 135,23,20,
        if (mouseX > k + 70 && mouseX < k + 70 + 23) {
            if (mouseY > l + 135 && mouseY < l + 135 + 20) {
                ArrayList list = new ArrayList();
                list.add("If the viewed trusted player can open this turret base's gui.");
                this.drawHoveringText(list, mouseX - k, mouseY - l, fontRenderer);
            }
        }

        // x + 93,y + 135,23,20,
        if (mouseX > k + 93 && mouseX < k + 93 + 23) {
            if (mouseY > l + 135 && mouseY < l + 135 + 20) {
                ArrayList list = new ArrayList();
                list.add("If the viewed trusted player can change the base's targeting parameters.");
                this.drawHoveringText(list, mouseX - k, mouseY - l, fontRenderer);
            }
        }

        // x + 116,y + 135,23,20,
        if (mouseX > k + 116 && mouseX < k + 116 + 23) {
            if (mouseY > l + 135 && mouseY < l + 135 + 20) {
                ArrayList list = new ArrayList();
                list.add("If the viewed trusted player can administer this turret (trusted players and dropping it).");
                this.drawHoveringText(list, mouseX - k, mouseY - l, fontRenderer);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        ResourceLocation texture = (new ResourceLocation(Names.Textures.configureGUI));
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
}