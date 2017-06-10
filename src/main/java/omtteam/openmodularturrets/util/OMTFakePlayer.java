package omtteam.openmodularturrets.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.UUID;

/**
 * Created by Keridos on 02/06/17.
 * This Class
 */
public class OMTFakePlayer {
    private static final GameProfile profile = new GameProfile(UUID.fromString("c5c97afa-fc98-44ab-944a-e67681a66b19"), "openmodularturrets:fakeplayer");

    public static FakePlayer getFakePlayer(WorldServer worldServer) {
        return FakePlayerFactory.get(worldServer, profile);
    }
}
