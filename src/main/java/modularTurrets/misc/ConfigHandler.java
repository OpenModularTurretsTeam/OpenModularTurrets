package modularTurrets.misc;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class ConfigHandler {

	public static int blockIdsLowerBound;
	public static int itemIdsLowerBound;

	public static int baseTierWoodMaxCharge;
	public static int baseTierOneMaxCharge;
	public static int baseTierTwoMaxCharge;
	public static int baseTierThreeMaxCharge;
	public static int baseTierFourMaxCharge;
	public static int baseTierWoodMaxIo;
	public static int baseTierOneMaxIo;
	public static int baseTierTwoMaxIo;
	public static int baseTierThreeMaxIo;
	public static int baseTierFourMaxIo;
	public static int turretWarningDistance;

	public static void init(File configFile) {

		Configuration config = new Configuration(configFile);
		config.load();

		// Blocks

		blockIdsLowerBound = config.get("BaseValues", "BlockIdsLowerBound", 3333).getInt();
		itemIdsLowerBound = config.get("BaseValues", "ItemIdsLowerBound", 7030)
				.getInt();

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
				"baseTierTwoMaxIo", 550).getInt();
		baseTierThreeMaxIo = config.get("TurretBaseConstants",
				"baseTierThreeMaxIo", 1500).getInt();
		baseTierFourMaxIo = config.get("TurretBaseConstants",
				"baseTierFourMaxIo", 12500).getInt();
		turretWarningDistance = config.get("TurretBaseConstants",
			"warningDistance", 10).getInt();

		config.save();
	}
	
	

	public static int getTurretWarningDistance() {
	    return turretWarningDistance;
	}

	public static void setTurretWarningDistance(int turretWarningDistance) {
	    ConfigHandler.turretWarningDistance = turretWarningDistance;
	}

	public static int getBaseTierWoodMaxCharge() {
		return baseTierWoodMaxCharge;
	}

	public static void setBaseTierWoodMaxCharge(int baseTierWoodMaxCharge) {
		ConfigHandler.baseTierWoodMaxCharge = baseTierWoodMaxCharge;
	}

	public static int getBaseTierWoodMaxIo() {
		return baseTierWoodMaxIo;
	}

	public static void setBaseTierWoodMaxIo(int baseTierWoodMaxIo) {
		ConfigHandler.baseTierWoodMaxIo = baseTierWoodMaxIo;
	}

	public static int getBaseTierOneMaxIo() {
		return baseTierOneMaxIo;
	}

	public static void setBaseTierOneMaxIo(int baseTierOneMaxIo) {
		ConfigHandler.baseTierOneMaxIo = baseTierOneMaxIo;
	}

	public static int getBaseTierTwoMaxIo() {
		return baseTierTwoMaxIo;
	}

	public static void setBaseTierTwoMaxIo(int baseTierTwoMaxIo) {
		ConfigHandler.baseTierTwoMaxIo = baseTierTwoMaxIo;
	}

	public static int getBaseTierThreeMaxIo() {
		return baseTierThreeMaxIo;
	}

	public static void setBaseTierThreeMaxIo(int baseTierThreeMaxIo) {
		ConfigHandler.baseTierThreeMaxIo = baseTierThreeMaxIo;
	}

	public static int getBaseTierFourMaxIo() {
		return baseTierFourMaxIo;
	}

	public static void setBaseTierFourMaxIo(int baseTierFourMaxIo) {
		ConfigHandler.baseTierFourMaxIo = baseTierFourMaxIo;
	}

	public static int getBaseTierTwoMaxCharge() {
		return baseTierTwoMaxCharge;
	}

	public static void setBaseTierTwoMaxCharge(int baseTierTwoMaxCharge) {
		ConfigHandler.baseTierTwoMaxCharge = baseTierTwoMaxCharge;
	}

	public static int getBaseTierThreeMaxCharge() {
		return baseTierThreeMaxCharge;
	}

	public static void setBaseTierThreeMaxCharge(int baseTierThreeMaxCharge) {
		ConfigHandler.baseTierThreeMaxCharge = baseTierThreeMaxCharge;
	}

	public static int getBaseTierFourMaxCharge() {
		return baseTierFourMaxCharge;
	}

	public static void setBaseTierFourMaxCharge(int baseTierFourMaxCharge) {
		ConfigHandler.baseTierFourMaxCharge = baseTierFourMaxCharge;
	}

	public static int getBlockIdsLowerBound() {
		return blockIdsLowerBound;
	}

	public static void setBlockIdsLowerBound(int blockIdsLowerBound) {
		ConfigHandler.blockIdsLowerBound = blockIdsLowerBound;
	}

	public static int getItemIdsLowerBound() {
		return itemIdsLowerBound;
	}

	public static void setItemIdsLowerBound(int itemIdsLowerBound) {
		ConfigHandler.itemIdsLowerBound = itemIdsLowerBound;
	}

	public static int getBaseTierOneMaxCharge() {
		return baseTierOneMaxCharge;
	}

	public static void setBaseTierOneMaxCharge(int baseTierOneMaxCharge) {
		ConfigHandler.baseTierOneMaxCharge = baseTierOneMaxCharge;
	}

}