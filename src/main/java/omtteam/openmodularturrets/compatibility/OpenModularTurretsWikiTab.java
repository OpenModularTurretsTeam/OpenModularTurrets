package omtteam.openmodularturrets.compatibility;

import igwmod.gui.GuiWiki;
import igwmod.gui.tabs.BaseWikiTab;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.handler.OMTConfigHandler;
import omtteam.openmodularturrets.init.ModBlocks;

/**
 * Created by Keridos on 23/01/2015.
 * This Class
 */
class OpenModularTurretsWikiTab extends BaseWikiTab {
    public OpenModularTurretsWikiTab() {
        pageEntries.add("block/turret_base");
        pageEntries.add("block/lever_block");

        if (OMTConfigHandler.getDisposableTurretSettings().isEnabled()) {
            pageEntries.add("block/disposable_item_turret");
        }

        if (OMTConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            pageEntries.add("block/potato_cannon_turret");
        }

        if (OMTConfigHandler.getGunTurretSettings().isEnabled()) {
            pageEntries.add("block/machine_gun_turret");
        }

        if (OMTConfigHandler.getIncendiaryTurretSettings().isEnabled()) {
            pageEntries.add("block/incendiary_turret");
        }

        if (OMTConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            pageEntries.add("block/grenade_turret");
        }

        if (OMTConfigHandler.getRelativisticTurretSettings().isEnabled()) {
            pageEntries.add("block/relativistic_turret");
        }
        if (OMTConfigHandler.getRocketTurretSettings().isEnabled()) {
            pageEntries.add("block/rocket_turret");
        }

        if (OMTConfigHandler.getTeleporterTurretSettings().isEnabled()) {
            pageEntries.add("block/teleporter_turret");
        }

        if (OMTConfigHandler.getLaserTurretSettings().isEnabled()) {
            pageEntries.add("block/laser_turret");
        }

        if (OMTConfigHandler.getRailgunTurretSettings().isEnabled()) {
            pageEntries.add("block/rail_gun_turret");
        }
    }

    @Override
    public String getName() {
        return "OpenModularTurrets";
    }

    @Override
    public ItemStack renderTabIcon(GuiWiki gui) {
        return new ItemStack(ModBlocks.machineGunTurret);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getPageName(String pageEntry) {
        if (pageEntry.startsWith("item") || pageEntry.startsWith("block")) {
            return I18n.format(pageEntry.replace("/", ".").replace("block", "tile") + ".name");
        } else {
            return I18n.format("igwtab.entry." + pageEntry);
        }
    }

    @Override
    protected String getPageLocation(String pageEntry) {
        if (pageEntry.startsWith("item") || pageEntry.startsWith("block")) {
            return pageEntry;
        }
        return "omtteam.openmodularturrets:menu/" + pageEntry;
    }
}
