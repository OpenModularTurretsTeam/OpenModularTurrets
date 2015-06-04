package openmodularturrets.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import openmodularturrets.ModularTurrets;
import openmodularturrets.network.AdjustYAxisDetectMessage;
import openmodularturrets.network.DropBaseMessage;
import openmodularturrets.network.DropTurretsMessage;
import openmodularturrets.tileentity.turretbase.TurretBase;

/**
 * Created by nico on 6/4/15.
 */
public class TurretBaseAbstractGui extends GuiContainer {

    TurretBase base;
    protected int mouseX;
    protected int mouseY;
    protected EntityPlayer player;

    public TurretBaseAbstractGui(InventoryPlayer inventoryPlayer, TurretBase tileEntity, Container container) {
        super(container);
        this.base = tileEntity;
        player = inventoryPlayer.player;
    }

    @Override
    public void initGui() {
        super.initGui();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        this.buttonList.add(new GuiButton(1, x + 120, y + 15, 20, 20, "-"));
        this.buttonList.add(new GuiButton(2, x + 120, y + 50, 20, 20, "+"));
        if (player.getUniqueID().toString().equals(base.getOwner())) {
            this.buttonList.add(new GuiButton(3, x + 180, y, 80, 20, "Drop Turrets"));
            this.buttonList.add(new GuiButton(4, x + 180, y + 25, 80, 20, "Drop Base"));
            this.buttonList.add(new GuiButton(5, x + 180, y + 50, 80, 20, "Configure"));
        } else if (base.getTrustedPlayer(player.getUniqueID()) != null) {
            if (base.getTrustedPlayer(player.getUniqueID()).admin) {
                this.buttonList.add(new GuiButton(3, x + 180, y, 80, 20, "Drop Turrets"));
                this.buttonList.add(new GuiButton(4, x + 180, y + 25, 80, 20, "Drop Base"));
            }
            if (base.getTrustedPlayer(player.getUniqueID()).canChangeTargeting ||
                    base.getTrustedPlayer(player.getUniqueID()).admin) {
                this.buttonList.add(new GuiButton(5, x + 180, y + 50, 80, 20, "Configure"));
            }
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.mouseX = par1;
        this.mouseY = par2;
        super.drawScreen(par1, par2, par3);
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
            player.openGui(ModularTurrets.instance, 6,
                    player.worldObj, base.xCoord, base.yCoord, base.zCoord);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {

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
