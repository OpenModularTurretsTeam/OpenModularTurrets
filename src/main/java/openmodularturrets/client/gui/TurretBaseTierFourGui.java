package openmodularturrets.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.ModularTurrets;
import openmodularturrets.client.gui.containers.TurretBaseTierFourContainer;
import openmodularturrets.network.AdjustYAxisDetectMessage;
import openmodularturrets.network.DropBaseMessage;
import openmodularturrets.network.DropTurretsMessage;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.tileentity.turretbase.TrustedPlayer;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.tileentity.turretbase.TurretBaseTierFourTileEntity;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class TurretBaseTierFourGui extends GuiContainer {

    TurretBaseTierFourTileEntity base;
    private int mouseX;
    private int mouseY;
    private EntityPlayer player;

    public TurretBaseTierFourGui(InventoryPlayer inventoryPlayer, TurretBaseTierFourTileEntity tileEntity) {
        super(new TurretBaseTierFourContainer(inventoryPlayer, tileEntity));
        this.base = tileEntity;
        player = inventoryPlayer.player;
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
        this.buttonList.add(new GuiButton(5, x + 180, y + 50, 80, 20, "Configure"));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 1) {
            this.base.setyAxisDetect(this.base.getyAxisDetect() - 1);
            sendChangeToServer();
        }

        if (guibutton.id == 2) {
            this.base.setyAxisDetect(this.base.getyAxisDetect() + 1);
            sendChangeToServer();
        }

        if (guibutton.id == 3) {
            sendDropTurretsToServer();
        }

        if (guibutton.id == 4) {
            sendDropBaseToServer();
        }
        
        if (guibutton.id == 5) {
			player.openGui(ModularTurrets.instance, 5,
					player.worldObj, base.xCoord, base.yCoord, base.zCoord);
		}
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        fontRenderer.drawString("Addons:", 71, 6, 0);
        fontRenderer.drawString("Ammo", 8, 6, 0);
        fontRenderer.drawString("Inventory", 8, ySize - 97 + 4, 0);
        fontRenderer.drawStringWithShadow("" + base.getyAxisDetect(), 127, 39, 40000);
        fontRenderer.drawString("-Y", 123, 6, 0);

        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        if (mouseX > k + 153 && mouseX < k + 153 + 14) {
            if (mouseY > l + 17 && mouseY < l + 17 + 51) {
                ArrayList list = new ArrayList();
                list.add(base.getEnergyStored(ForgeDirection.UNKNOWN) + "/" + base.getMaxEnergyStored(
                        ForgeDirection.UNKNOWN));
                this.drawHoveringText(list, mouseX - k, mouseY - l, fontRenderer);
            }
        }

        ArrayList targetInfo = new ArrayList();

        targetInfo.add("\u00A76Owner: \u00A7f" + base.getOwner());
        targetInfo.add("");
        targetInfo.add("\u00A75-Trusted Players-");

        for (TrustedPlayer trusted_player : base.getTrustedPlayers()) {
            targetInfo.add("\u00A7b" + trusted_player.name);
        }

        targetInfo.add("");
        targetInfo.add("\u00A77Attack Mobs: \u00A7b" + base.isAttacksMobs());
        targetInfo.add("\u00A77Attack Neutrals: \u00A7b" + base.isAttacksNeutrals());
        targetInfo.add("\u00A77Attack Players: \u00A7b" + base.isAttacksPlayers());

        this.drawHoveringText(targetInfo, -128, 17, fontRenderer);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.mouseX = par1;
        this.mouseY = par2;
        super.drawScreen(par1, par2, par3);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        ResourceLocation texture = (new ResourceLocation(ModInfo.ID + ":textures/gui/baseInvTier4.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        int expression = (base.getEnergyStored(ForgeDirection.UNKNOWN) * 51) / base.getMaxEnergyStored(
                ForgeDirection.UNKNOWN);

        drawTexturedModalRect(x + 153, y + 17, 178, 17, 14, 51);
        drawTexturedModalRect(x + 153, y + 17 + 51 - expression, 196, 17, 14, expression);
    }

    public void sendChangeToServer() {
        AdjustYAxisDetectMessage message = new AdjustYAxisDetectMessage(base.xCoord, base.yCoord, base.zCoord,
                                                                        base.getyAxisDetect());

        ModularTurrets.networking.sendToServer(message);
    }

    public void sendDropTurretsToServer() {
        DropTurretsMessage message = new DropTurretsMessage(base.xCoord, base.yCoord, base.zCoord);
        ModularTurrets.networking.sendToServer(message);
    }

    public void sendDropBaseToServer() {
        DropBaseMessage message = new DropBaseMessage(base.xCoord, base.yCoord, base.zCoord);
        ModularTurrets.networking.sendToServer(message);
    }
}