package omtteam.openmodularturrets.gametest;

//? if neoforge && <1.21.11 {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if forge {
/*import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
*///?}
//? if (neoforge && <1.21.11) || forge {
/*import omtteam.openmodularturrets.OMT;
*///?}
//? if <1.21.11 {
import net.minecraft.gametest.framework.GameTest;
//?}
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import omtteam.omlib.camo.CamoHost;
import omtteam.omlib.compat.VersionCompat;
import omtteam.omlib.core.camo.CamoLight;
import omtteam.omlib.core.security.AccessLevel;
import omtteam.omlib.core.security.TrustManager;
import omtteam.omlib.network.CamoPacketHandler;
import omtteam.omlib.network.CamoPackets;
import omtteam.omlib.security.PlayerAdapter;
import omtteam.omlib.security.TrustHost;
import omtteam.openmodularturrets.camo.CamoRules;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.registry.ModBlocks;

import java.util.UUID;

/**
 * Which blocks may be used as a disguise: full blocks without a block entity, and hardened blocks, whose block entity
 * only holds the owner. The light of a camouflaged block as the server sets it from the camouflage menu's packet
 * (OMLib's {@code CamoPacketHandler}): only on tier 4 and 5 bases and the trap, only by admins within reach, and not
 * with base camouflage turned off.
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class CamoGameTests implements OMTGameTests {
    private static final BlockPos BLOCK = new BlockPos(1, 1, 1);

    @GameTest(template = EMPTY_STRUCTURE)
    public void hardenedBlocksAreValidDisguises(GameTestHelper helper) {
        assertDisguise(helper, Blocks.STONE, true);
        assertDisguise(helper, Blocks.GLASS, true);
        ModBlocks.HARDENED.forEach(block -> assertDisguise(helper, block, true));
        assertDisguise(helper, Blocks.FURNACE, false);
        assertDisguise(helper, ModBlocks.TURRET_BASES.get(0), false);
        assertDisguise(helper, ModBlocks.CAMO_TRAP, false);
        helper.succeed();
    }

    /** Owner and admins set the light of a tier 4 base; values outside 0–15 are clamped. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_light")
    public void adminsSetTheLightOfATierFourBase(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(3));
        ServerPlayer owner = owner(helper, base);
        ServerPlayer admin = trusted(helper, base, owner, "Admin", AccessLevel.ADMIN);

        setLight(helper, owner, 20, 3);
        assertLight(helper, base, new CamoLight(15, 3));
        setLight(helper, admin, 7, -2);
        assertLight(helper, base, new CamoLight(7, 0));
        helper.succeed();
    }

    /** The trap's admins set its light too. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_light")
    public void adminsSetTheLightOfTheTrap(GameTestHelper helper) {
        CamoHost trap = place(helper, ModBlocks.CAMO_TRAP);
        ServerPlayer owner = owner(helper, trap);

        setLight(helper, owner, 12, 4);
        assertLight(helper, trap, new CamoLight(12, 4));
        helper.succeed();
    }

    /** Bases below tier 4 have no adjustable light and ignore the request. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_light")
    public void lowerTierBasesKeepTheirLight(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(2));
        ServerPlayer owner = owner(helper, base);

        setLight(helper, owner, 12, 4);
        assertLight(helper, base, CamoLight.DEFAULT);
        helper.succeed();
    }

    /** A trusted player below admin cannot change the light. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_light")
    public void playersBelowAdminCannotChangeTheLight(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(3));
        ServerPlayer owner = owner(helper, base);
        ServerPlayer settings = trusted(helper, base, owner, "Settings", AccessLevel.CHANGE_SETTINGS);

        setLight(helper, settings, 12, 4);
        assertLight(helper, base, CamoLight.DEFAULT);
        helper.succeed();
    }

    /** Requests from players more than the container distance away are ignored. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_light")
    public void lightRequestsFromFarAwayAreIgnored(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(3));
        ServerPlayer owner = owner(helper, base);
        BlockPos pos = helper.absolutePos(BLOCK);
        owner.moveTo(pos.getX() + 9.5D, pos.getY(), pos.getZ() + 0.5D);

        setLight(helper, owner, 12, 4);
        assertLight(helper, base, CamoLight.DEFAULT);
        helper.succeed();
    }

    /** With {@code bases.allowBaseCamo} off, a base's light stays as it is (own batch: it changes the config). */
    @GameTest(template = EMPTY_STRUCTURE, batch = "camo_light_disabled")
    public void noLightChangesWithBaseCamouflageOff(GameTestHelper helper) {
        CamoHost base = place(helper, ModBlocks.TURRET_BASES.get(3));
        ServerPlayer owner = owner(helper, base);
        ConfigData config = OMTConfig.get();
        boolean allowBaseCamo = config.bases.allowBaseCamo;
        config.bases.allowBaseCamo = false;
        try {
            setLight(helper, owner, 12, 4);
        } finally {
            config.bases.allowBaseCamo = allowBaseCamo;
        }
        assertLight(helper, base, CamoLight.DEFAULT);
        helper.succeed();
    }

    /** Places {@code block} at {@link #BLOCK} and returns its block entity. */
    private static CamoHost place(GameTestHelper helper, Block block) {
        helper.setBlock(BLOCK, block);
        return (CamoHost) helper.getLevel().getBlockEntity(helper.absolutePos(BLOCK));
    }

    /** A fake player next to the block who owns it. */
    private static ServerPlayer owner(GameTestHelper helper, CamoHost host) {
        ServerPlayer owner = playerAt(helper, "Owner");
        ((TrustHost) host).getTrustManager().setOwner(PlayerAdapter.identity(owner));
        return owner;
    }

    /** A fake player next to the block, on its trust list with {@code level}. */
    private static ServerPlayer trusted(GameTestHelper helper, CamoHost host, ServerPlayer owner, String name, AccessLevel level) {
        ServerPlayer player = playerAt(helper, name);
        TrustManager trust = ((TrustHost) host).getTrustManager();
        trust.addTrustedPlayer(PlayerAdapter.info(owner), PlayerAdapter.identity(player));
        trust.setAccessLevel(PlayerAdapter.info(owner), player.getUUID(), level);
        return player;
    }

    /** A fake player standing on top of the block, within reach for packets. */
    private static ServerPlayer playerAt(GameTestHelper helper, String name) {
        ServerPlayer player = VersionCompat.fakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), name));
        BlockPos pos = helper.absolutePos(BLOCK);
        player.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
        return player;
    }

    private static void setLight(GameTestHelper helper, ServerPlayer player, int value, int opacity) {
        CamoPacketHandler.handleSetLight(new CamoPackets.SetLight(helper.absolutePos(BLOCK), value, opacity), player);
    }

    private static void assertLight(GameTestHelper helper, CamoHost host, CamoLight expected) {
        helper.assertTrue(host.getLight().equals(expected), "Light is " + host.getLight() + ", expected " + expected);
    }

    private static void assertDisguise(GameTestHelper helper, Block block, boolean valid) {
        helper.assertTrue(CamoRules.isValidDisguise(block.defaultBlockState()) == valid,
                block + (valid ? " is not a valid disguise" : " is a valid disguise"));
    }
}
