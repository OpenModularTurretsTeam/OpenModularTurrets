package modularTurrets.misc;

import java.util.logging.Level;
import java.util.logging.Logger;

import modularTurrets.ModInfo;
import cpw.mods.fml.common.FMLLog;

public class LogHelper {

	private static Logger logger;

	public static void init() {

		logger = Logger.getLogger(ModInfo.NAME);
		logger.setParent(FMLLog.getLogger());
	}

	public static void log(Level logLevel, String message) {		
		logger.log(logLevel, message);
	}
}