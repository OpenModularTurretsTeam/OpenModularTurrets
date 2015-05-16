package openmodularturrets.tileentity.turretbase;

public class TrustedPlayer {

	public String name;
	public boolean canOpenGUI = false;
	public boolean canChangeTargeting = false;
	public boolean canAddTrustedPlayers = false;
	public boolean isAdmin = false;

	public TrustedPlayer(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCanOpenGUI() {
		return canOpenGUI;
	}

	public void setCanOpenGUI(boolean canOpenGUI) {
		this.canOpenGUI = canOpenGUI;
	}

	public boolean isCanChangeTargeting() {
		return canChangeTargeting;
	}

	public void setCanChangeTargeting(boolean canChangeTargeting) {
		this.canChangeTargeting = canChangeTargeting;
	}

	public boolean isCanAddTrustedPlayers() {
		return canAddTrustedPlayers;
	}

	public void setCanAddTrustedPlayers(boolean canAddTrustedPlayers) {
		this.canAddTrustedPlayers = canAddTrustedPlayers;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
}
