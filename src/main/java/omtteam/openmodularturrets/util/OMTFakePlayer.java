package omtteam.openmodularturrets.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import omtteam.omlib.init.OMLibItems;

import java.util.UUID;

/**
 * Created by Keridos on 02/06/17.
 * This Class
 */
public class OMTFakePlayer {
    private static final GameProfile profile = new GameProfile(UUID.fromString("c5c97afa-fc98-44ab-944a-e67681a66b19"), "openmodularturrets:fakeplayer");
    private static ItemStack swordLooting0;
    private static ItemStack swordLooting1;
    private static ItemStack swordLooting2;
    private static ItemStack swordLooting3;

    public static void init() {
        swordLooting0 = new ItemStack(OMLibItems.fakeSword, 1);
        swordLooting1 = new ItemStack(OMLibItems.fakeSword, 1);
        swordLooting2 = new ItemStack(OMLibItems.fakeSword, 1);
        swordLooting3 = new ItemStack(OMLibItems.fakeSword, 1);
        swordLooting1.addEnchantment(Enchantments.LOOTING, 1);
        swordLooting2.addEnchantment(Enchantments.LOOTING, 2);
        swordLooting3.addEnchantment(Enchantments.LOOTING, 3);
    }

    public static FakePlayer getFakePlayer(WorldServer worldServer) {
        return FakePlayerFactory.get(worldServer, profile);
    }

    public static ItemStack getSword(int fakeDrops) {
        switch (fakeDrops) {
            case 0:
                return swordLooting0;
            case 1:
                return swordLooting1;
            case 2:
                return swordLooting2;
            case 3:
                return swordLooting3;
            default:
                return swordLooting0;
        }
    }
}
