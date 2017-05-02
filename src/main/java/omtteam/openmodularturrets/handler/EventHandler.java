package omtteam.openmodularturrets.handler;

import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Keridos on 02/05/17.
 * This Class
 */
public class EventHandler {
    private static EventHandler instance;

    private EventHandler() {
    }

    public static EventHandler getInstance() {
        if (instance == null) {
            instance = new EventHandler();
        }
        return instance;
    }

    @SubscribeEvent
    public void lootEvent(LivingDropsEvent event) {
        if (event.getEntityLiving().getTags().contains("openmodularturrets:turretHit") && !ConfigHandler.doTurretsKillsDropMobLoot) {
            event.setCanceled(true);
        }
    }
}
