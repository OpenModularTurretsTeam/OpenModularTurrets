package omtteam.openmodularturrets.api;

import omtteam.omlib.util.TrustedPlayer;
import omtteam.openmodularturrets.util.TargetingSettings;

import java.util.List;

/**
 * Created by Keridos on 17/08/17.
 * This Class
 */
public interface IBaseFullController extends IBaseTargetCheckController {

    /**
     * Return the overridden targeting settings for the base.
     *
     * @return TargetingSettings
     */
    TargetingSettings getTargetingSettings();


    /**
     * Return the overridden trusted player list for the base.
     *
     * @return List of TrustedPlayer
     */
    List<TrustedPlayer> getTrustedPlayerList();
}
