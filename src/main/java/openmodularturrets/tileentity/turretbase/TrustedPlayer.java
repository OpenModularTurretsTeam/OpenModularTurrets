package openmodularturrets.tileentity.turretbase;

import java.util.UUID;

import static openmodularturrets.util.PlayerUtil.getPlayerUIDUnstable;

public class TrustedPlayer {

	public String name = "";
	public boolean canOpenGUI = false;
	public boolean canChangeTargeting = false;
	public boolean canAddTrustedPlayers = false;
	public UUID uuid;

	public TrustedPlayer(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCanOpenGUI(boolean canOpenGUI) {
		this.canOpenGUI = canOpenGUI;
	}

	public void setCanChangeTargeting(boolean canChangeTargeting) {
		this.canChangeTargeting = canChangeTargeting;
	}

	public void setCanAddTrustedPlayers(boolean canAddTrustedPlayers) {
		this.canAddTrustedPlayers = canAddTrustedPlayers;
	}

	public void setUuidUnstable(String uuid) {
		this.uuid = getPlayerUIDUnstable(uuid);
	}
	
}
