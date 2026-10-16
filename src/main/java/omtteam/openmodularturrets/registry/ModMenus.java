package omtteam.openmodularturrets.registry;

import net.minecraft.world.inventory.MenuType;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.compat.MenuCompat;
import omtteam.openmodularturrets.menu.CamoMenu;
import omtteam.openmodularturrets.menu.GeneratorMenu;
import omtteam.openmodularturrets.menu.TurretBaseMenu;

public final class ModMenus {
    public static final MenuType<TurretBaseMenu> TURRET_BASE = MenuCompat.register(OMT.id("turret_base"),
            TurretBaseMenu::client);
    public static final MenuType<GeneratorMenu> GENERATOR = MenuCompat.register(OMT.id("generator"),
            GeneratorMenu::client);

    public static final MenuType<CamoMenu> CAMO = MenuCompat.register(OMT.id("camo"), CamoMenu::client);

    private ModMenus() {
    }

    /** Loads the class, registering all menus. */
    public static void init() {
    }
}
