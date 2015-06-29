package openmodularturrets.compatability;

import igwmod.gui.GuiWiki;
import igwmod.gui.tabs.BaseWikiTab;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import openmodularturrets.blocks.Blocks;

/**
 * Created by Keridos on 23/01/2015.
 * This Class
 */
public class OpenModularTurretsWikiTab extends BaseWikiTab {
    public OpenModularTurretsWikiTab() {

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
        if (pageEntry.startsWith("item") || pageEntry.startsWith("block"))
            return pageEntry.replace("/openmodularturrets:", "/");
        return "menu/" + pageEntry;
    }
}
