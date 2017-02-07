package omtteam.openmodularturrets.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import omtteam.omlib.client.gui.BlockingAbstractGui;
import omtteam.omlib.util.PlayerUtil;
import omtteam.omlib.util.TrustedPlayer;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.NetworkingHandler;
import omtteam.openmodularturrets.network.messages.MessageAdjustYAxisDetect;
import omtteam.openmodularturrets.network.messages.MessageDropBase;
import omtteam.openmodularturrets.network.messages.MessageDropTurrets;
import omtteam.openmodularturrets.network.messages.MessageSetBaseTargetingType;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.TurretBase;

import java.awt.*;
import java.util.ArrayList;

import static omtteam.omlib.util.GeneralUtil.getColoredBooleanLocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.safeLocalize;

/**
 * Created by nico on 6/4/15.
 * Abstract class for all turret base GUIs.
 */

public class TurretBaseAbstractGui extends BlockingAbstractGui {
    private int mouseX;
    private int mouseY;
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
                    base.isMultiTargeting() ? safeLocalize(OMTNames.Localizations.GUI.TARGET) + ": "
                            + safeLocalize(OMTNames.Localizations.GUI.MULTI) : safeLocalize(OMTNames.Localizations.GUI.TARGET)
                            + ": " + safeLocalize(OMTNames.Localizations.GUI.SINGLE)));
        } else if (trustedPlayer != null) {
            if (trustedPlayer.admin) {
                this.buttonList.add(new GuiButton(3, x + 180, y, 80, 20, "Drop Turrets"));
                this.buttonList.add(new GuiButton(4, x + 180, y + 25, 80, 20, "Drop Base"));
            }
            if (trustedPlayer.canChangeTargeting || trustedPlayer.admin) {
                this.buttonList.add(new GuiButton(5, x + 180, y + 50, 80, 20, "Configure"));
                this.buttonList.add(new GuiButton(6, x + 180, y + 75, 80, 20,
                        base.isMultiTargeting() ? safeLocalize(OMTNames.Localizations.GUI.TARGET) + ": "
                                + safeLocalize(OMTNames.Localizations.GUI.MULTI) : safeLocalize(OMTNames.Localizations.GUI.TARGET)
                                + ": " + safeLocalize(OMTNames.Localizations.GUI.SINGLE)));
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
            player.openGui(OpenModularTurrets.instance, 6, player.getEntityWorld(), base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
        }

        if (guibutton.id == 6) {
            sendSetBaseTargetingToServer();
            for (Object button : buttonList) {
                if (((GuiButton) button).id == 6) {
                    this.base.setMultiTargeting(!this.base.isMultiTargeting());
                    ((GuiButton) button).displayString = base.isMultiTargeting() ? safeLocalize(OMTNames.Localizations.GUI.TARGET) + ": " + safeLocalize(OMTNames.Localizations.GUI.MULTI) : safeLocalize(OMTNames.Localizations.GUI.TARGET) + ": " + safeLocalize(OMTNames.Localizations.GUI.SINGLE);
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        fontRenderer.drawString(safeLocalize(OMTNames.Localizations.GUI.ADDONS), 71, 6, 0);
        fontRenderer.drawString(safeLocalize(OMTNames.Localizations.GUI.UPGRADES), 71, 39, 0);
        fontRenderer.drawString(safeLocalize(OMTNames.Localizations.GUI.AMMO), 8, 6, 0);
        fontRenderer.drawString(safeLocalize(OMTNames.Localizations.GUI.INVENTORY), 8, ySize - 97 + 4, 0);
        fontRenderer.drawStringWithShadow("" + base.getyAxisDetect(), 127, 39, 40000);
        fontRenderer.drawString("-Y", 123, 6, 0);

        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        if (mouseX > k + 153 && mouseX < k + 153 + 14) {
            if (mouseY > l + 17 && mouseY < l + 17 + 51) {
                ArrayList list = new ArrayList();
                list.add(base.getEnergyStored(EnumFacing.DOWN) + "/" + base.getMaxEnergyStored(
                        EnumFacing.DOWN) + " RF");
                this.drawHoveringText(list, mouseX - k, mouseY - l, fontRenderer);
            }
        }

        ArrayList targetInfo = new ArrayList();

        targetInfo.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.OWNER) + ": \u00A7f" + base.getOwnerName());
        boolean isCurrentlyOn = base.isActive();
        targetInfo.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.ACTIVE) + ": " + (getColoredBooleanLocalizationYesNo(isCurrentlyOn)));
        targetInfo.add("");
        if (base.getTrustedPlayers().size() != 0) {
            targetInfo.add("\u00A75" + safeLocalize(OMTNames.Localizations.GUI.TRUSTED_PLAYERS) + ":");
            for (TrustedPlayer trusted_player : base.getTrustedPlayers()) {
                targetInfo.add("\u00A7b" + trusted_player.name);
            }
        } else {
            targetInfo.add("\u00A75" + safeLocalize(OMTNames.Localizations.GUI.TRUSTED_PLAYERS) + ": " + getColoredBooleanLocalizationYesNo(false));
        }

        targetInfo.add("");
        targetInfo.add("\u00A77" + safeLocalize(OMTNames.Localizations.GUI.ATTACK_MOBS) + ": " + getColoredBooleanLocalizationYesNo(base.isAttacksMobs()));
        targetInfo.add("\u00A77" + safeLocalize(OMTNames.Localizations.GUI.ATTACK_NEUTRALS) + ": " + getColoredBooleanLocalizationYesNo(base.isAttacksNeutrals()));
        targetInfo.add("\u00A77" + safeLocalize(OMTNames.Localizations.GUI.ATTACK_PLAYERS) + ": " + getColoredBooleanLocalizationYesNo(base.isAttacksPlayers()));

        this.drawHoveringText(targetInfo, -128, 17, fontRenderer);
    }

    private void sendChangeToServer() {
        MessageAdjustYAxisDetect message = new MessageAdjustYAxisDetect(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ(),
                base.getyAxisDetect());

        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendDropTurretsToServer() {
        MessageDropTurrets message = new MessageDropTurrets(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendDropBaseToServer() {
        MessageDropBase message = new MessageDropBase(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    private void sendSetBaseTargetingToServer() {
        MessageSetBaseTargetingType message = new MessageSetBaseTargetingType(base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
        NetworkingHandler.INSTANCE.sendToServer(message);
    }

    @Override
    public ArrayList<Rectangle> getBlockingAreas() {
        ArrayList<Rectangle> list = new ArrayList<>();
        Rectangle rectangleGUI = new Rectangle(0, 0, 0, 0);
        if (player.getUniqueID().toString().equals(base.getOwner())) {
            rectangleGUI = new Rectangle((width - xSize) / 2 + 180, (height - ySize) / 2, 80, 95);
        } else if (base.getTrustedPlayer(player.getUniqueID()) != null) {
            if (base.getTrustedPlayer(player.getUniqueID()).admin) {
                rectangleGUI = new Rectangle((width - xSize) / 2 + 180, (height - ySize) / 2, 80, 45);
            }
            if (base.getTrustedPlayer(player.getUniqueID()).canChangeTargeting || base.getTrustedPlayer(
                    player.getUniqueID()).admin) {
                rectangleGUI = new Rectangle((width - xSize) / 2 + 180, (height - ySize) / 2 + 50, 80, 20);
            }
        }
        list.add(rectangleGUI);
        return list;
    }
}
