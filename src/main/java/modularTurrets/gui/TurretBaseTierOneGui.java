package modularTurrets.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import modularTurrets.ModInfo;
import modularTurrets.gui.containers.TurretBaseTierOneContainer;
import modularTurrets.misc.PacketHandler;
import modularTurrets.tileentity.turretBase.TurretBaseTierOneTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class TurretBaseTierOneGui extends GuiContainer {

    TurretBaseTierOneTileEntity base;
    int change;
    private int mouseX;
    private int mouseY;

    public TurretBaseTierOneGui(InventoryPlayer inventoryPlayer, TurretBaseTierOneTileEntity tileEntity) {
        super(new TurretBaseTierOneContainer(inventoryPlayer, tileEntity));
        this.base = tileEntity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.buttonList.add(new GuiButton(1, x + 120, y + 15, 20, 20, "-"));
        this.buttonList.add(new GuiButton(2, x + 120, y + 50, 20, 20, "+"));
        this.buttonList.add(new GuiButton(3, x + 180, y, 80, 20, "Drop Turrets"));
        this.buttonList.add(new GuiButton(4, x + 180, y + 25, 80, 20, "Drop Base"));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 1) {
            change = -1;
            sendChangeToServer();
        }

        if (guibutton.id == 2) {
            change = 1;
            sendChangeToServer();
        }
        if (guibutton.id == 3) {
            sendChangeToServerDropTurrets();
        }

        if (guibutton.id == 4) {
            sendChangeToServerDropBase();
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.mouseX = par1;
        this.mouseY = par2;
        super.drawScreen(par1, par2, par3);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        fontRenderer.drawString("Addons:", 71, 6, 0);
        fontRenderer.drawString("Ammo", 8, 6, 0);
        fontRenderer.drawString("Inventory", 8, ySize - 97 + 4, 0);
        fontRenderer.drawStringWithShadow("" + base.getyAxisDetect(), 127, 39,
            40000);
        fontRenderer.drawString("-Y", 123, 6, 0);

        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        if (mouseX > k + 153 && mouseX < k + 153 + 14) {
            if (mouseY > l + 17 && mouseY < l + 17 + 51) {
                ArrayList list = new ArrayList();
                list.add(base.storage.getEnergyStored() + "/" + base.storage.getMaxEnergyStored());
                this.drawHoveringText(list, (int) mouseX - k, (int) mouseY - l, fontRenderer);
            }
        }

        ArrayList targetInfo = new ArrayList();

        targetInfo.add("\u00A76Owner: \u00A7f" + base.owner);
        targetInfo.add("");
        targetInfo.add("\u00A75-Trusted Players-");

        for (int i = 0; i < base.trustedPlayers.size(); i++) {
            if (!base.trustedPlayers.get(i).equals("") || !base.trustedPlayers.get(i).equals(" ")) {
                targetInfo.add("\u00A7b" + base.trustedPlayers.get(i));
            }
        }
        targetInfo.add("");
        targetInfo.add("\u00A77Attack Mobs: \u00A7b" + base.attacksMobs);
        targetInfo.add("\u00A77Attack Neutrals: \u00A7b" + base.attacksNeutrals);
        targetInfo.add("\u00A77Attack Players: \u00A7b" + base.attacksPlayers);

        this.drawHoveringText(targetInfo, -128, 17, fontRenderer);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        if (base.storage != null && base.getMaxEnergyStored(ForgeDirection.UNKNOWN) > 0) {
            ResourceLocation texture = (new ResourceLocation(ModInfo.ID + ":textures/gui/baseInvTier1.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(texture);
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
            int expression = (base.getEnergyStored(ForgeDirection.UNKNOWN) * 51) / base.getMaxEnergyStored(ForgeDirection.UNKNOWN);

            drawTexturedModalRect(x + 153, y + 17, 178, 17, 14, 51);
            drawTexturedModalRect(x + 153, y + 17 + 51 - expression, 196, 17, 14, expression);
        }
    }

    public void sendChangeToServer() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);

        try {
            outputStream.writeInt(PacketHandler.UPDATE_YAXISDETECT_ON_SERVER);
            outputStream.writeInt(base.xCoord);
            outputStream.writeInt(base.yCoord);
            outputStream.writeInt(base.zCoord);
            outputStream.writeInt(change);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = ModInfo.CHANNEL;
        packet.data = bos.toByteArray();
        packet.length = bos.size();

        PacketDispatcher.sendPacketToServer(packet);
        change = 0;
    }

    public void sendChangeToServerDropTurrets() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream
                .writeInt(PacketHandler.DROP_ALL_TURRETS_ATTACHED_TO_BASE);
            outputStream.writeInt(base.xCoord);
            outputStream.writeInt(base.yCoord);
            outputStream.writeInt(base.zCoord);
            outputStream.writeInt(0);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = ModInfo.CHANNEL;
        packet.data = bos.toByteArray();
        packet.length = bos.size();

        PacketDispatcher.sendPacketToServer(packet);
    }

    public void sendChangeToServerDropBase() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeInt(PacketHandler.DROP_BASE_ON_SERVER);
            outputStream.writeInt(base.xCoord);
            outputStream.writeInt(base.yCoord);
            outputStream.writeInt(base.zCoord);
            outputStream.writeInt(0);

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