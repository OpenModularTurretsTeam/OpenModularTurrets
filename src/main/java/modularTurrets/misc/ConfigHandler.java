package modularTurrets.misc;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
    private static int baseTierWoodMaxCharge;
    private static int baseTierOneMaxCharge;
    private static int baseTierTwoMaxCharge;
    private static int baseTierThreeMaxCharge;
    private static int baseTierFourMaxCharge;
    private static int baseTierWoodMaxIo;
    private static int baseTierOneMaxIo;
    private static int baseTierTwoMaxIo;
    private static int baseTierThreeMaxIo;
    private static int baseTierFourMaxIo;
    private static int turretWarningDistance;

    public static void init(File configFile) {

		Configuration config = new Configuration(configFile);
		config.load();

		baseTierWoodMaxCharge = config.get("TurretBaseConstants",
				"baseTierWoodMaxCharge", 500).getInt();
		baseTierOneMaxCharge = config.get("TurretBaseConstants",
				"baseTierOneMaxCharge", 50000).getInt();
		baseTierTwoMaxCharge = config.get("TurretBaseConstants",
				"baseTierTwoMaxCharge", 150000).getInt();
		baseTierThreeMaxCharge = config.get("TurretBaseConstants",
				"baseTierThreeMaxCharge", 500000).getInt();
		baseTierFourMaxCharge = config.get("TurretBaseConstants",
				"baseTierFourMaxCharge", 10000000).getInt();

		baseTierWoodMaxIo = config.get("TurretBaseConstants",
				"baseTierWoodMaxIo", 50).getInt();
		baseTierOneMaxIo = config.get("TurretBaseConstants",
				"baseTierOneMaxIo", 100).getInt();
		baseTierTwoMaxIo = config.get("TurretBaseConstants",
				"baseTierTwoMaxIo", 500).getInt();
		baseTierThreeMaxIo = config.get("TurretBaseConstants",
				"baseTierThreeMaxIo", 1500).getInt();
		baseTierFourMaxIo = config.get("TurretBaseConstants",
				"baseTierFourMaxIo", 5000).getInt();

        turretWarningDistance = config.get("TurretBaseConstants",
			"warningDistance", 10).getInt();

        if (config.hasChanged()) {
            config.save();
        }
	}

	public static int getBaseTierWoodMaxCharge() {
		return baseTierWoodMaxCharge;
	}

	public static int getBaseTierWoodMaxIo() {
		return baseTierWoodMaxIo;
	}

	public static int getBaseTierOneMaxIo() {
		return baseTierOneMaxIo;
	}

	public static int getBaseTierTwoMaxIo() {
		return baseTierTwoMaxIo;
	}

	public static int getBaseTierThreeMaxIo() {
		return baseTierThreeMaxIo;
	}

	public static int getBaseTierFourMaxIo() {
		return baseTierFourMaxIo;
	}

	public static int getBaseTierTwoMaxCharge() {
		return baseTierTwoMaxCharge;
	}

	public static int getBaseTierThreeMaxCharge() {
		return baseTierThreeMaxCharge;
	}

	public static int getBaseTierFourMaxCharge() {
		return baseTierFourMaxCharge;
	}

	public static int getBaseTierOneMaxCharge() {
		return baseTierOneMaxCharge;
	}

    public static int getTurretWarningDistance() {
        return turretWarningDistance;
    }
}