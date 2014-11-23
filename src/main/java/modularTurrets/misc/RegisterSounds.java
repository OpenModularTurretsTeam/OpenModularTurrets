package modularTurrets.misc;

import modularTurrets.ModInfo;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class RegisterSounds {

	@ForgeSubscribe
	public void onSound(SoundLoadEvent event) {
		try {
			event.manager
					.addSound(ModInfo.ID.toLowerCase() + ":disposable.ogg");
			event.manager.addSound(ModInfo.ID.toLowerCase() + ":rocket.ogg");
			event.manager.addSound(ModInfo.ID.toLowerCase()
					+ ":machinegun1.ogg");
			event.manager.addSound(ModInfo.ID.toLowerCase()
					+ ":machinegun2.ogg");
			event.manager.addSound(ModInfo.ID.toLowerCase()
					+ ":machinegun3.ogg");
			event.manager.addSound(ModInfo.ID.toLowerCase() + ":grenade1.ogg");
			event.manager.addSound(ModInfo.ID.toLowerCase() + ":grenade2.ogg");
			event.manager
					.addSound(ModInfo.ID.toLowerCase() + ":bulletHit1.ogg");
			event.manager
					.addSound(ModInfo.ID.toLowerCase() + ":bulletHit2.ogg");

			event.manager.addSound(ModInfo.ID.toLowerCase() + ":laser1.ogg");
			event.manager.addSound(ModInfo.ID.toLowerCase() + ":laser2.ogg");
			event.manager.addSound(ModInfo.ID.toLowerCase() + ":laser3.ogg");
			event.manager.addSound(ModInfo.ID.toLowerCase() + ":amped.ogg");
			event.manager.addSound(ModInfo.ID.toLowerCase() + ":laserHit.ogg");
			event.manager.addSound(ModInfo.ID.toLowerCase() + ":windup.ogg");
			event.manager.addSound(ModInfo.ID.toLowerCase() + ":warning.ogg");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print(e.getMessage());
		}
	}

}
