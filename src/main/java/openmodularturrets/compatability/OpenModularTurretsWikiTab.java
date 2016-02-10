package openmodularturrets.compatability;

import igwmod.gui.GuiWiki;
import igwmod.gui.tabs.BaseWikiTab;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.handler.ConfigHandler;

/**
 * Created by Keridos on 23/01/2015.
 * This Class
 */
class OpenModularTurretsWikiTab extends BaseWikiTab {
    public OpenModularTurretsWikiTab() {
        pageEntries.add("block/turretBase");
        pageEntries.add("block/leverBlock");

        if (ConfigHandler.getDisposableTurretSettings().isEnabled()) {
            pageEntries.add("block/disposeItemTurret");
        }

        if (ConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            pageEntries.add("block/potatoCannonTurret");
        }

        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            pageEntries.add("block/machineGunTurret");
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            pageEntries.add("block/incendiaryTurret");
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            pageEntries.add("block/grenadeTurret");
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            pageEntries.add("block/relativisticTurret");
        }
        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            pageEntries.add("block/rocketTurret");
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            pageEntries.add("block/teleporterTurret");
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            pageEntries.add("block/laserTurret");
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            pageEntries.add("block/railGunTurret");
        }
    }

    @Override
    public String getName() {
        return "OpenModularTurrets";
    }

    @Override
    public ItemStack renderTabIcon(GuiWiki gui) {
        return new ItemStack(Blocks.machineGunTurret);
    }

    @Override
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
        return "openmodularturrets:menu/" + pageEntry;
    }
}
