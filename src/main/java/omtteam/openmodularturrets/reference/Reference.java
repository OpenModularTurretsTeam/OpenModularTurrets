package omtteam.openmodularturrets.reference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reference {
    public static final String MOD_ID = "openmodularturrets";
    public static final String NAME = "Open Modular Turrets";
    public static final String VERSION = "@VERSION@";
    public static final String ACCEPTED_MINECRAFT_VERSION = "[1.12,1.12.9]";
    public static final String DEPENDENCIES = "required-after:omlib@[0.0.0,)";
    /** The mod's log (the one FML's {@code getModLog} returns). */
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
}
