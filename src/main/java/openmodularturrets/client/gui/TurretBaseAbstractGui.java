package openmodularturrets.client.gui;

import codechicken.lib.vec.Rectangle4i;
import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import openmodularturrets.ModularTurrets;
import openmodularturrets.handler.NetworkingHandler;
import openmodularturrets.network.messages.MessageAdjustYAxisDetect;
import openmodularturrets.network.messages.MessageDropBase;
import openmodularturrets.network.messages.MessageDropTurrets;
import openmodularturrets.network.messages.MessageSetBaseTargetingType;
import openmodularturrets.tileentity.turretbase.TrustedPlayer;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.util.PlayerUtil;

import java.util.List;

/**
 * Created by nico on 6/4/15.
 */

@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
class TurretBaseAbstractGui extends GuiContainer implements INEIGuiHandler {
    int mouseX;
    int mouseY;
    private final EntityPlayer player;
    final TurretBase base;

    TurretBaseAbstractGui(InventoryPlayer inventoryPlayer, TurretBase tileEntity, Container container) {
        super(container);
        this.base = tileEntity;
        player = inventoryPlayer.player;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        TrustedPlayer trustedPlayer = PlayerUtil.getTrustedPlayer(player, base);

        this.buttonList.add(new GuiButton(1, x + 120, y + 15, 20, 20, "-"));
        this.buttonList.add(new GuiButton(2, x + 120, y + 50, 20, 20, "+"));
        if (PlayerUtil.isPlayerOwner(player, base)) {
            this.buttonList.add(new GuiButton(3, x + 180, y, 80, 20, "Drop Turrets"));
            this.buttonList.add(new GuiButton(4, x + 180, y + 25, 80, 20, "Drop Base"));
            this.buttonList.add(new GuiButton(5, x + 180, y + 50, 80, 20, "Configure"));
            this.buttonList.add(new GuiButton(6, x + 180, y + 75, 80, 20,
                    base.isMultiTargeting() ? "Target: Multi" : "Target: Single"));
        } else if (trustedPlayer != null) {
            if (trustedPlayer.admin) {
                this.buttonList.add(new GuiButton(3, x + 180, y, 80, 20, "Drop Turrets"));
                this.buttonList.add(new GuiButton(4, x + 180, y + 25, 80, 20, "Drop Base"));
            }
            if (trustedPlayer.canChangeTargeting || trustedPlayer.admin) {
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
            player.openGui(ModularTurrets.instance, 6, player.worldObj, base.xCoord, base.yCoord, base.zCoord);
        }

        if (guibutton.id == 6) {
            sendSetBaseTargetingToServer();
            for (Object button : buttonList) {
                if (((GuiButton) button).id == 6) {
                    this.base.setMultiTargeting(!this.base.isMultiTargeting());
                    ((GuiButton) button).displayString = base.isMultiTargeting() ? "Target: Multi" : "Target: Single";
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
    }

    private void sendChangeToServer() {
        MessageAdjustYAxisDetect message = new MessageAdjustYAxisDetect(base.xCoord, base.yCoord, base.zCoord,
                base.getyAxisDetect());

        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendDropTurretsToServer() {
        MessageDropTurrets message = new MessageDropTurrets(base.xCoord, base.yCoord, base.zCoord);
        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendDropBaseToServer() {
        MessageDropBase message = new MessageDropBase(base.xCoord, base.yCoord, base.zCoord);
        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendSetBaseTargetingToServer() {
        MessageSetBaseTargetingType message = new MessageSetBaseTargetingType(base.xCoord, base.yCoord, base.zCoord);
        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    @Optional.Method(modid = "NotEnoughItems")
    @Override
    public VisiblityData modifyVisiblity(GuiContainer guiContainer, VisiblityData visiblityData) {
        return visiblityData;
    }

    @Optional.Method(modid = "NotEnoughItems")
    @Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer guiContainer, ItemStack itemStack) {
        return null;
    }

    @Optional.Method(modid = "NotEnoughItems")
    @Override
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer guiContainer) {
        return null;
    }

    @Optional.Method(modid = "NotEnoughItems")
    @Override
    public boolean handleDragNDrop(GuiContainer guiContainer, int i, int i1, ItemStack itemStack, int i2) {
        return false;
    }

    @Optional.Method(modid = "NotEnoughItems")
    @Override
    public boolean hideItemPanelSlot(GuiContainer guiContainer, int x, int y, int w, int h) {
        boolean intersects = false;
        if (guiContainer instanceof TurretBaseAbstractGui) {
            Rectangle4i rectangle = new Rectangle4i(x, y, w, h);
            Rectangle4i rectangleGUI;
            if (player.getUniqueID().toString().equals(base.getOwner())) {
                rectangleGUI = new Rectangle4i((width - xSize) / 2 + 180, (height - ySize) / 2, 80, 95);
                intersects = rectangle.intersects(rectangleGUI);
            } else if (base.getTrustedPlayer(player.getUniqueID()) != null) {
                if (base.getTrustedPlayer(player.getUniqueID()).admin) {
                    rectangleGUI = new Rectangle4i((width - xSize) / 2 + 180, (height - ySize) / 2, 80, 45);
                    intersects = rectangle.intersects(rectangleGUI);
                }
                if (base.getTrustedPlayer(player.getUniqueID()).canChangeTargeting || base.getTrustedPlayer(
                        player.getUniqueID()).admin) {
                    rectangleGUI = new Rectangle4i((width - xSize) / 2 + 180, (height - ySize) / 2 + 50, 80, 20);
                    if (!intersects) {
                        intersects = rectangle.intersects(rectangleGUI);
                    }
                }
            }
        }
        return intersects;
    }
}
