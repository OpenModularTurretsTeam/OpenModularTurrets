package modularTurrets.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import modularTurrets.ModInfo;
import modularTurrets.gui.containers.ConfigContainer;
import modularTurrets.gui.containers.TurretBaseTierFourContainer;
import modularTurrets.misc.PacketHandler;
import modularTurrets.tileEntities.turretBase.TurretBase;
import modularTurrets.tileEntities.turretBase.TurretBaseTierFourTileEntity;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;

public class ConfigureGui extends GuiContainer {

    TurretBase base;
    private int mouseX;
    private int mouseY;

    private String mobsButton;
    private String neutralsButton;
    private String playersButton;

    GuiTextField textFieldName;

    public ConfigureGui(InventoryPlayer inventoryPlayer, TurretBase tileEntity) {
	super(new ConfigContainer(inventoryPlayer, tileEntity));
	this.base = (TurretBase) tileEntity;
    }

    @Override
    public void initGui() {
	super.initGui();

	mobsButton = "Attack Mobs: " + base.isAttacksMobs();
	neutralsButton = "Attack Neutrals: " + base.isAttacksNeutrals();
	playersButton = "Attack Players: " + base.isAttacksPlayers();

	int x = (width - xSize) / 2;
	int y = (height - ySize) / 2;
	this.buttonList.add(new GuiButton(1, x + 23, y + 40, 130, 20,
		mobsButton));
	this.buttonList.add(new GuiButton(2, x + 23, y + 65, 130, 20,
		neutralsButton));
	this.buttonList.add(new GuiButton(3, x + 23, y + 90, 130, 20,
		playersButton));

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
	    if (base.isAttacksMobs() == true) {
		guibutton.displayString = "Attack Mobs: false";
	    }
	    if (base.isAttacksMobs() == false) {
		guibutton.displayString = "Attack Mobs: true";
	    }
	    sendChangeToServerMobs();

	}

	if (guibutton.id == 2) {
	    if (base.isAttacksNeutrals() == true) {
		guibutton.displayString = "Attack Neutrals: false";
	    }
	    if (base.isAttacksNeutrals() == false) {
		guibutton.displayString = "Attack Neutrals: true";
	    }
	    sendChangeToServerNeutrals();

	}

	if (guibutton.id == 3) {
	    if (base.isAttacksPlayers() == true) {
		guibutton.displayString = "Attack Players: false";
	    }
	    if (base.isAttacksPlayers() == false) {
		guibutton.displayString = "Attack Players: true";
	    }
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
	int x = (width - xSize) / 2;
	int y = (height - ySize) / 2;
	fontRenderer.drawString("Turret Target Setup Menu", 21, 25, 0);
	fontRenderer.drawString("Add/Remove trusted players:", 16, 115, 0);
	textFieldName.drawTextBox();
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
	this.mouseX = par1;
	this.mouseY = par2;
	super.drawScreen(par1, par2, par3);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
	    int par3) {
	ResourceLocation texture = (new ResourceLocation(ModInfo.ID
		+ ":textures/gui/configure.png"));
	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	this.mc.renderEngine.bindTexture(texture);
	this.ySize = 133;
	int x = (width - xSize) / 2;
	int y = (height - ySize) / 2;
	this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    public void sendChangeToServerMobs() {
	ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
	DataOutputStream outputStream = new DataOutputStream(bos);
	try {
	    outputStream.writeInt(PacketHandler.CHANGE_TURRETBASE_ATTACKS_MOBS);
	    outputStream.writeInt(base.xCoord);
	    outputStream.writeInt(base.yCoord);
	    outputStream.writeInt(base.zCoord);
	    if (base.attacksMobs == true) {
		outputStream.writeInt(0);
	    }
	    if (base.attacksMobs == false) {
		outputStream.writeInt(1);
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	Packet250CustomPayload packet = new Packet250CustomPayload();
	packet.channel = ModInfo.CHANNEL;
	packet.data = bos.toByteArray();
	packet.length = bos.size();
	PacketDispatcher.sendPacketToServer(packet);
    }

    public void sendChangeToServerNeutrals() {
	ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
	DataOutputStream outputStream = new DataOutputStream(bos);
	try {
	    outputStream
		    .writeInt(PacketHandler.CHANGE_TURRETBASE_ATTACKS_NEUTRALS);
	    outputStream.writeInt(base.xCoord);
	    outputStream.writeInt(base.yCoord);
	    outputStream.writeInt(base.zCoord);
	    if (base.isAttacksNeutrals() == true) {
		outputStream.writeInt(0);
	    }
	    if (base.isAttacksNeutrals() == false) {
		outputStream.writeInt(1);
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	Packet250CustomPayload packet = new Packet250CustomPayload();
	packet.channel = ModInfo.CHANNEL;
	packet.data = bos.toByteArray();
	packet.length = bos.size();
	PacketDispatcher.sendPacketToServer(packet);
    }

    public void sendChangeToServerPlayers() {
	ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
	DataOutputStream outputStream = new DataOutputStream(bos);
	try {
	    outputStream
		    .writeInt(PacketHandler.CHANGE_TURRETBASE_ATTACKS_PLAYERS);
	    outputStream.writeInt(base.xCoord);
	    outputStream.writeInt(base.yCoord);
	    outputStream.writeInt(base.zCoord);
	    if (base.isAttacksPlayers() == true) {
		outputStream.writeInt(0);
	    }
	    if (base.isAttacksPlayers() == false) {
		outputStream.writeInt(1);
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	Packet250CustomPayload packet = new Packet250CustomPayload();
	packet.channel = ModInfo.CHANNEL;
	packet.data = bos.toByteArray();
	packet.length = bos.size();
	PacketDispatcher.sendPacketToServer(packet);
    }

    public void sendChangeToServerAddTrusted() {
	ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
	DataOutputStream outputStream = new DataOutputStream(bos);
	try {
	    outputStream.writeInt(PacketHandler.BASE_ADD_TRUSTED_PLAYER);
	    outputStream.writeInt(base.xCoord);
	    outputStream.writeInt(base.yCoord);
	    outputStream.writeInt(base.zCoord);
	    outputStream.writeInt(0);
	    outputStream.writeUTF(textFieldName.getText());

	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	Packet250CustomPayload packet = new Packet250CustomPayload();
	packet.channel = ModInfo.CHANNEL;
	packet.data = bos.toByteArray();
	packet.length = bos.size();
	PacketDispatcher.sendPacketToServer(packet);
    }

    public void sendChangeToServerRemoveTrusted() {
	ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
	DataOutputStream outputStream = new DataOutputStream(bos);
	try {
	    outputStream.writeInt(PacketHandler.BASE_REMOVE_TRUSTED_PLAYER);
	    outputStream.writeInt(base.xCoord);
	    outputStream.writeInt(base.yCoord);
	    outputStream.writeInt(base.zCoord);
	    outputStream.writeInt(0);
	    outputStream.writeUTF(textFieldName.getText());

	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	Packet250CustomPayload packet = new Packet250CustomPayload();
	packet.channel = ModInfo.CHANNEL;
	packet.data = bos.toByteArray();
	packet.length = bos.size();
	PacketDispatcher.sendPacketToServer(packet);
    }

}