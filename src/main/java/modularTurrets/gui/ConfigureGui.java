package modularTurrets.gui;

import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import modularTurrets.gui.containers.ConfigContainer;
import modularTurrets.network.*;
import modularTurrets.tileentity.turretBase.TurretBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class ConfigureGui extends GuiContainer {

    TurretBase base;

    GuiTextField textFieldName;

    public ConfigureGui(InventoryPlayer inventoryPlayer, TurretBase tileEntity) {
        super(new ConfigContainer(inventoryPlayer, tileEntity));
        this.base = (TurretBase) tileEntity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        String mobsButton = "Attack Mobs: " + base.isAttacksMobs();
        String neutralsButton = "Attack Neutrals: " + base.isAttacksNeutrals();
        String playersButton = "Attack Players: " + base.isAttacksPlayers();

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        this.buttonList.add(new GuiButton(1, x + 23, y + 40, 130, 20, mobsButton));
        this.buttonList.add(new GuiButton(2, x + 23, y + 65, 130, 20, neutralsButton));
        this.buttonList.add(new GuiButton(3, x + 23, y + 90, 130, 20, playersButton));
        this.buttonList.add(new GuiButton(4, x + 140, y + 125, 20, 20, "+"));
        this.buttonList.add(new GuiButton(5, x + 15, y + 125, 20, 20, "-"));

        textFieldName = new GuiTextField(fontRenderer, 38, 127, 100, 16);
        textFieldName.setMaxStringLength(25);
        textFieldName.setFocused(false);
    }

    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        textFieldName.mouseClicked(i - guiLeft, j - guiTop, k);
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        if (!textFieldName.isFocused()) {
            super.keyTyped(par1, par2);
        } else {
            textFieldName.textboxKeyTyped(par1, par2);
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 1) {
            if (base.isAttacksMobs()) {
                guibutton.displayString = "Attack Mobs: false";
            } else {
                guibutton.displayString = "Attack Mobs: true";
            }

            base.setAttacksMobs(!base.isAttacksMobs());
            sendChangeToServerMobs();
        }

        if (guibutton.id == 2) {
            if (base.isAttacksNeutrals()) {
                guibutton.displayString = "Attack Neutrals: false";
            } else {
                guibutton.displayString = "Attack Neutrals: true";
            }

            base.setAttacksNeutrals(!base.isAttacksNeutrals());
            sendChangeToServerNeutrals();
        }

        if (guibutton.id == 3) {
            if (base.isAttacksPlayers()) {
                guibutton.displayString = "Attack Players: false";
            } else {
                guibutton.displayString = "Attack Players: true";
            }

            base.setAttacksPlayers(!base.isAttacksPlayers());
            sendChangeToServerPlayers();
        }

        if (guibutton.id == 4) {
            sendChangeToServerAddTrusted();
            textFieldName.setText("");
        }

        if (guibutton.id == 5) {
            sendChangeToServerRemoveTrusted();
            textFieldName.setText("");
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        fontRenderer.drawString("Turret Target Setup Menu", 21, 25, 0);
        fontRenderer.drawString("Add/Remove trusted players:", 16, 115, 0);
        textFieldName.drawTextBox();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        ResourceLocation texture = (new ResourceLocation(ModInfo.ID + ":textures/gui/configure.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);
        this.ySize = 133;
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    public void sendChangeToServerMobs() {
        ToggleAttackMobsMessage message = new ToggleAttackMobsMessage(base.xCoord, base.yCoord, base.zCoord, base.isAttacksMobs());

        ModularTurrets.networking.sendToServer(message);
    }

    public void sendChangeToServerNeutrals() {
        ToggleAttackNeutralMobsMessage message = new ToggleAttackNeutralMobsMessage(base.xCoord, base.yCoord, base.zCoord, base.isAttacksNeutrals());

        ModularTurrets.networking.sendToServer(message);
    }

    public void sendChangeToServerPlayers() {
        ToggleAttackPlayersMessage message = new ToggleAttackPlayersMessage(base.xCoord, base.yCoord, base.zCoord, base.isAttacksPlayers());

        ModularTurrets.networking.sendToServer(message);
    }

    public void sendChangeToServerAddTrusted() {
        AddTrustedPlayerMessage message = new AddTrustedPlayerMessage(base.xCoord, base.yCoord, base.zCoord, textFieldName.getText());

        ModularTurrets.networking.sendToServer(message);
    }

    public void sendChangeToServerRemoveTrusted() {
        RemoveTrustedPlayerMessage message = new RemoveTrustedPlayerMessage(base.xCoord, base.yCoord, base.zCoord, textFieldName.getText());

        ModularTurrets.networking.sendToServer(message);
    }
}