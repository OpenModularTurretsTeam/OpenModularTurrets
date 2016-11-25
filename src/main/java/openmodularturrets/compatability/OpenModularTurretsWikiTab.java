package openmodularturrets.compatability;

import igwmod.gui.GuiWiki;
import igwmod.gui.tabs.BaseWikiTab;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import openmodularturrets.init.ModBlocks;
import openmodularturrets.handler.ConfigHandler;

/**
 * Created by Keridos on 23/01/2015.
 * This Class
 */
public class OpenModularTurretsWikiTab extends BaseWikiTab {
    public OpenModularTurretsWikiTab() {
        pageEntries.add("block/turret_base");
        pageEntries.add("block/lever_block");

        if (ConfigHandler.getDisposableTurretSettings().isEnabled()) {
            pageEntries.add("block/disposable_item_turret");
        }

        if (ConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            pageEntries.add("block/potato_cannon_turret");
        }

        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            pageEntries.add("block/machine_gun_turret");
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            pageEntries.add("block/incendiary_turret");
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            pageEntries.add("block/grenade_turret");
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            pageEntries.add("block/relativistic_turret");
        }
        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            pageEntries.add("block/rocket_turret");
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            pageEntries.add("block/teleporter_turret");
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            pageEntries.add("block/laser_turret");
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
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
