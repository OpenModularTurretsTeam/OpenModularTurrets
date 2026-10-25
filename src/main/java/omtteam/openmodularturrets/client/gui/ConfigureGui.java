package omtteam.openmodularturrets.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import omtteam.omlib.OMLib;
import omtteam.omlib.client.gui.GUIBackSystem;
import omtteam.omlib.client.gui.GuiParameters;
import omtteam.omlib.client.gui.IHasTooltips;
import omtteam.omlib.client.gui.ISupportsBackSystem;
import omtteam.omlib.network.MessageCloseGUITile;
import omtteam.omlib.network.MessageOpenCamoMenu;
import omtteam.omlib.network.MessageOpenGUITile;
import omtteam.omlib.network.OMLibNetworkingHandler;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.network.BasePackets;
import omtteam.openmodularturrets.network.OMTNetworkingHandler;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

import static omtteam.omlib.util.GeneralUtil.getColoredBooleanLocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.safeLocalize;
import static omtteam.omlib.util.player.PlayerUtil.addChatMessage;
import static omtteam.omlib.util.player.PlayerUtil.isPlayerOwner;

public class ConfigureGui extends GuiScreen implements IHasTooltips, ISupportsBackSystem {
    protected final int xSize = 176;
    /** Height of the panel in {@code configure.png}. */
    protected final int ySize = 166;
    private final TurretBase base;
    private final EntityPlayer player;
    protected int guiLeft;
    protected int guiTop;
    private int mouseX;
    private int mouseY;
    private boolean addedToSyncList;
    private int accessLevel = 0;

    public ConfigureGui(InventoryPlayer inventoryPlayer, TurretBase tileEntity) {
        super();
        this.base = tileEntity;
        player = inventoryPlayer.player;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void buttonInit() {

        if (PlayerUtil.canPlayerChangeSetting(player, base)) {
            String mobsButton = safeLocalize(OMLibNames.Localizations.GUI.ATTACK_MOBS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksMobs()));
            String neutralsButton = safeLocalize(OMLibNames.Localizations.GUI.ATTACK_NEUTRALS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksNeutrals()));
            String playersButton = safeLocalize(OMLibNames.Localizations.GUI.ATTACK_PLAYERS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksPlayers()));
            this.buttonList.add(new GuiButton(0, guiLeft + 10, guiTop + 20, 155, 20, mobsButton));
            this.buttonList.add(new GuiButton(1, guiLeft + 10, guiTop + 40, 155, 20, neutralsButton));
            this.buttonList.add(new GuiButton(2, guiLeft + 10, guiTop + 60, 155, 20, playersButton));
        }
        if (PlayerUtil.isPlayerAdmin(player, base)) {

            this.buttonList.add(new GuiButton(3, guiLeft + 10, guiTop + 95, 155, 20, safeLocalize(OMLibNames.Localizations.GUI.TRUSTED_PLAYERS)));
        }
        if (base.isCamoEnabled() && (base.canSetCamo(player) || base.canChangeLight(player))) {
            this.buttonList.add(new GuiButton(4, guiLeft + 10, guiTop + 132, 155, 20,
                                              safeLocalize(OMTNames.Localizations.GUI.CAMOUFLAGE)));
        }
        this.buttonList.add(new GuiButton(6, guiLeft + 185, guiTop + 20, 80, 20, safeLocalize(OMLibNames.Localizations.GUI.BACK)));
    }

    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        if (!addedToSyncList) {
            OMLibNetworkingHandler.INSTANCE.sendToServer(new MessageOpenGUITile(base));
            addedToSyncList = true;
        }

        accessLevel = PlayerUtil.getPlayerAccessLevel(player, base).ordinal();
        if (accessLevel < 2) {
            player.closeScreen();
        }

        this.buttonList.clear();
        this.buttonInit();
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.mouseX = par1;
        this.mouseY = par2;

        this.drawDefaultBackground();

        if (accessLevel != PlayerUtil.getPlayerAccessLevel(player, base).ordinal() && !isPlayerOwner(player, base)) {
            accessLevel = PlayerUtil.getPlayerAccessLevel(player, base).ordinal();
            if (accessLevel != 0) {
                this.initGui();
            }
        } else if (!isPlayerOwner(player, base) && accessLevel < 2) {
            player.closeScreen();
            player.openGui(OpenModularTurrets.instance, 1, player.getEntityWorld(), base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
            return;
        }
        ResourceLocation texture = (new ResourceLocation(OMTNames.Textures.configureGUI));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);
        this.drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawString(safeLocalize(OMLibNames.Localizations.GUI.TARGETING_OPTIONS) + ": ", guiLeft + 10, guiTop + 9, 0);
        if (accessLevel > 2) {
            fontRenderer.drawString(safeLocalize(OMLibNames.Localizations.GUI.TRUSTED_PLAYERS) + ": ", guiLeft + 10, guiTop + 84, 0);
        }
        if (base.isCamoEnabled() && (base.canSetCamo(player) || base.canChangeLight(player))) {
            fontRenderer.drawString(safeLocalize(OMTNames.Localizations.GUI.CAMOUFLAGE) + ": ", guiLeft + 10, guiTop + 121, 0);
        }
        buttonList.get(0).displayString = safeLocalize(OMLibNames.Localizations.GUI.ATTACK_MOBS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksMobs()));
        buttonList.get(1).displayString = safeLocalize(OMLibNames.Localizations.GUI.ATTACK_NEUTRALS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksNeutrals()));
        buttonList.get(2).displayString = safeLocalize(OMLibNames.Localizations.GUI.ATTACK_PLAYERS) + ": " + (getColoredBooleanLocalizationYesNo(base.isAttacksPlayers()));

        super.drawScreen(par1, par2, par3);
        drawTooltips();
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 0) { //change Attack Mobs
            if (PlayerUtil.canPlayerChangeSetting(player, base)) {
                sendTargeting(BasePackets.TargetCategory.MOBS, !base.isAttacksMobs());
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize(OMLibNames.Localizations.Text.STATUS_PERMISSION)));
            }
        }

        if (guibutton.id == 1) { //change Attack Neutrals
            if (PlayerUtil.canPlayerChangeSetting(player, base)) {
                sendTargeting(BasePackets.TargetCategory.NEUTRALS, !base.isAttacksNeutrals());
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize(OMLibNames.Localizations.Text.STATUS_PERMISSION)));
            }
        }

        if (guibutton.id == 2) { // change Attack Players
            if (PlayerUtil.canPlayerChangeSetting(player, base)) {
                sendTargeting(BasePackets.TargetCategory.PLAYERS, !base.isAttacksPlayers());
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize(OMLibNames.Localizations.Text.STATUS_PERMISSION)));
            }
        }
        if (guibutton.id == 3) { // Open TP GUI for this block
            if (PlayerUtil.isPlayerAdmin(player, base)) {
                player.openGui(OMLib.instance, 0, player.getEntityWorld(), base.getPos().getX(), base.getPos().getY(), base.getPos().getZ());
            } else {
                addChatMessage(player, new TextComponentString(safeLocalize(OMLibNames.Localizations.Text.STATUS_PERMISSION)));
            }
        }
        if (guibutton.id == 4) { // camouflage menu, opened by the server (it has a container)
            OMLibNetworkingHandler.INSTANCE.sendToServer(new MessageOpenCamoMenu(base.getPos()));
        }
        if (guibutton.id == 6) { //back button
            GUIBackSystem.getInstance().openLastGui(player);
        }
    }

    @Override
    public void drawTooltips() {
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        int tooltipToDraw = buttonList.stream().filter(GuiButton::isMouseOver).mapToInt(s -> s.id).sum();
        ArrayList<String> tooltip = new ArrayList<>();
        switch (tooltipToDraw) {
            case 0:
                if (buttonList.get(0).isMouseOver()) {
                    tooltip.add(safeLocalize(OMLibNames.Localizations.Tooltip.TARGET_MOBS));
                }
                break;
            case 1:
                tooltip.add(safeLocalize(OMLibNames.Localizations.Tooltip.TARGET_NEUTRALS));
                break;
            case 2:
                tooltip.add(safeLocalize(OMLibNames.Localizations.Tooltip.TARGET_PLAYERS));
                break;
            case 3:
                tooltip.add(safeLocalize(OMLibNames.Localizations.Tooltip.TRUSTED_PLAYER_GUI));
                break;
            case 4:
                tooltip.add(safeLocalize(OMTNames.Localizations.GUI.CAMOUFLAGE_TOOLTIP));
                break;
            default:
        }
        if (!tooltip.isEmpty())
            this.drawHoveringText(tooltip, guiLeft + mouseX - k, guiTop + mouseY - l, Minecraft.getMinecraft().fontRenderer);
    }

    private void sendTargeting(BasePackets.TargetCategory category, boolean enabled) {
        OMTNetworkingHandler.INSTANCE.sendToServer(new BasePackets.SetTargeting(base.getPos(), category, enabled));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        OMLibNetworkingHandler.INSTANCE.sendToServer(new MessageCloseGUITile(base));
        super.onGuiClosed();
    }

    @Override
    @Nullable
    public GuiParameters getGuiParameters() {
        return new GuiParameters(OpenModularTurrets.instance, 20, player.getEntityWorld(),
                                 base.getPos().getX(),
                                 base.getPos().getY(),
                                 base.getPos().getZ());
    }
}