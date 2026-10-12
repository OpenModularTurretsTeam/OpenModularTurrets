package omtteam.openmodularturrets.entity.projectile;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omtteam.openmodularturrets.compat.VersionCompat;

import java.util.UUID;

/**
 * The fake player credited with turret kills when a base has fake drops addons (oldmod {@code OMTFakePlayer}).
 * Vanilla then treats the kill as a player kill (player-only drops, experience), and the Looting level is read from
 * the sword the fake player holds.
 */
public final class TurretKiller {
    private static final GameProfile PROFILE = new GameProfile(
            UUID.fromString("5e7f1c0a-4c3b-4d8e-9f2a-6f6d4d0e7a11"), "[OpenModularTurrets]");

    private TurretKiller() {
    }

    /**
     * The level's turret fake player, holding a sword with the given Looting level (0 = no enchantment). Set right
     * before each hit: the loot is computed during the hit when it kills.
     */
    public static Player withLooting(ServerLevel level, int lootingLevel) {
        FakePlayer player = FakePlayer.get(level, PROFILE);
        ItemStack sword = new ItemStack(Items.IRON_SWORD);
        if (lootingLevel > 0) {
            VersionCompat.enchantLooting(sword, level, lootingLevel);
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, sword);
        return player;
    }
}
