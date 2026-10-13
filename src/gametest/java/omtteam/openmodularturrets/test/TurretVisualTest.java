package omtteam.openmodularturrets.test;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.block.misc.GeneratorBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.defense.CamoTrapBlockEntity;
import omtteam.openmodularturrets.client.screen.ConfigureScreen;
import omtteam.openmodularturrets.client.screen.GeneratorScreen;
import omtteam.openmodularturrets.client.screen.TrustedPlayersScreen;
import omtteam.openmodularturrets.client.screen.TurretBaseScreen;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.network.BasePacketHandler;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;
import omtteam.openmodularturrets.security.PlayerAdapter;

import java.util.List;

/**
 * Builds a small turret test site, lets the turrets shoot at a zombie and takes screenshots of the world, the item
 * icons and every screen. Run with {@code gradlew runClientGameTest}; screenshots go to
 * {@code build/run/clientGameTest/screenshots}.
 */
public class TurretVisualTest implements FabricClientGameTest {
    private static final BlockPos BASE_A = new BlockPos(3, 101, 6);
    private static final BlockPos BASE_B = new BlockPos(7, 101, 6);
    private static final BlockPos BASE_LEVER = new BlockPos(1, 101, 9);
    private static final BlockPos CAMO_PLAIN = new BlockPos(4, 101, 3);
    private static final BlockPos CAMO_GRASS = new BlockPos(6, 101, 3);
    private static final BlockPos GENERATOR = new BlockPos(9, 101, 6);

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            TestServerContext server = singleplayer.getServer();
            server.runCommand("gamerule advance_time false");
            server.runCommand("gamerule spawn_mobs false");
            server.runCommand("time set noon");
            server.runCommand("weather clear");
            server.runCommand("gamemode creative @a");
            // hover at the site (looking south, +z) and let the client load it before building
            server.runCommand("tp @a 5.5 102 -3.5 0 20");
            server.runOnServer(minecraft -> {
                ServerPlayer player = player(minecraft);
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
            });
            context.waitTicks(40);
            server.runCommand("fill -2 100 -2 12 100 16 minecraft:stone");
            server.runCommand("fill -2 101 -2 12 110 16 minecraft:air");

            String turret = "openmodularturrets:";
            // bases without an owner drop themselves (like oldmod), so they are placed with the player as owner
            server.runOnServer(minecraft -> {
                ServerLevel level = minecraft.overworld();
                placeBase(level, BASE_A, ModBlocks.TURRET_BASE_TIER_FIVE, player(minecraft));
                placeBase(level, BASE_B, ModBlocks.TURRET_BASE_TIER_FIVE, player(minecraft));
                placeBase(level, BASE_LEVER, ModBlocks.TURRET_BASE_TIER_ONE, player(minecraft));
            });
            // like a player: turrets are added to a base that already looked at its neighbours
            context.waitTicks(5);
            setBlock(server, BASE_A.above(), turret + "rocket_turret[facing=up]");
            setBlock(server, BASE_A.east(), turret + "laser_turret[facing=east]");
            setBlock(server, BASE_A.west(), turret + "machine_gun_turret[facing=west]");
            setBlock(server, BASE_A.north(), turret + "railgun_turret[facing=north]");
            setBlock(server, BASE_B.above(), turret + "plasma_turret[facing=up]");
            setBlock(server, BASE_B.east(), turret + "crossbow_turret[facing=east]");
            setBlock(server, BASE_B.north(), turret + "arc_turret[facing=north]");
            setBlock(server, BASE_LEVER.north(), turret + "lever_block[facing=south]");
            setBlock(server, BASE_LEVER.above(), turret + "disposable_turret[facing=up]");
            setBlock(server, CAMO_PLAIN, turret + "camo_trap");
            setBlock(server, CAMO_GRASS, turret + "camo_trap");
            setBlock(server, GENERATOR, turret + "generator[facing=north]");
            setBlock(server, new BlockPos(9, 101, 3), turret + "hardened_tier_three");
            setBlock(server, new BlockPos(10, 101, 3), turret + "wall_tier_two");
            setBlock(server, new BlockPos(11, 101, 3), turret + "fence_tier_four");

            server.runOnServer(minecraft -> {
                ServerLevel level = minecraft.overworld();
                if (level.getBlockEntity(CAMO_GRASS) instanceof CamoTrapBlockEntity trap) {
                    trap.setCamo(Blocks.GRASS_BLOCK.defaultBlockState());
                }
                for (BlockPos pos : List.of(BASE_A, BASE_B)) {
                    if (level.getBlockEntity(pos) instanceof TurretBaseBlockEntity base) {
                        try (Transaction transaction = Transaction.openOuter()) {
                            base.getEnergyStorage().insert(Long.MAX_VALUE, transaction);
                            transaction.commit();
                        }
                        TurretBaseInventory inventory = base.getInventory();
                        inventory.setItem(TurretBaseInventory.AMMO_START, new ItemStack(ModItems.AMMO_BULLET, 64));
                        inventory.setItem(TurretBaseInventory.AMMO_START + 1, new ItemStack(ModItems.AMMO_ROCKET, 64));
                        inventory.setItem(TurretBaseInventory.AMMO_START + 2, new ItemStack(ModItems.AMMO_FERRO_SLUG, 64));
                    }
                }
            });

            server.runOnServer(minecraft -> {
                ServerPlayer player = player(minecraft);
                List<ItemStack> hotbar = List.of(new ItemStack(ModItems.TURRETS.values().iterator().next()),
                        new ItemStack(ModBlocks.TURRETS.keySet().stream().skip(5).findFirst()
                                .map(type -> ModItems.TURRETS.get(type)).orElseThrow()),
                        new ItemStack(ModItems.LEVER), new ItemStack(ModItems.GENERATOR),
                        new ItemStack(ModItems.CAMO_TRAP), new ItemStack(ModItems.HARDENED.get(0)),
                        new ItemStack(ModItems.AMMO_ROCKET), new ItemStack(ModItems.UPGRADE_RANGE),
                        new ItemStack(ModItems.ADDON_SOLAR_PANEL));
                for (int i = 0; i < hotbar.size(); i++) {
                    player.getInventory().setItem(i, hotbar.get(i));
                }
            });
            context.waitTicks(100);
            context.runOnClient(minecraft -> {
                if (!minecraft.level.getBlockState(BASE_A).is(ModBlocks.TURRET_BASE_TIER_FIVE)) {
                    throw new AssertionError("The turret base is missing on the client");
                }
            });
            context.takeScreenshot("omt-site");

            server.runCommand("summon minecraft:zombie 5.5 101 13.5 {PersistenceRequired:1b,equipment:{head:{id:\"minecraft:leather_helmet\",count:1}}}");
            server.runCommand("summon minecraft:zombie 8.5 101 13.5 {PersistenceRequired:1b,equipment:{head:{id:\"minecraft:leather_helmet\",count:1}}}");
            context.waitTicks(12);
            context.takeScreenshot("omt-firing-1");
            context.waitTicks(8);
            context.takeScreenshot("omt-firing-2");
            context.waitTicks(80);
            context.takeScreenshot("omt-after-fight");
            server.runOnServer(minecraft -> {
                if (!(minecraft.overworld().getBlockEntity(BASE_A) instanceof TurretBaseBlockEntity base)
                        || base.getLogic().getTurrets().size() != 4
                        || base.getLogic().getTargetingSettings().getRange() == 0) {
                    throw new AssertionError("Base A did not pick up its four turrets and their range");
                }
                int zombies = minecraft.overworld().getEntities(EntityTypes.ZOMBIE, zombie -> true).size();
                if (zombies > 0) {
                    throw new AssertionError(zombies + " zombie(s) survived the turrets");
                }
            });

            // the menus close when the player is more than a few blocks away
            server.runCommand("tp @a 3.5 101 3.5 0 30");
            context.waitTicks(10);

            // Turret base GUI (opened by the server like a right-click would)
            server.runOnServer(minecraft -> {
                if (minecraft.overworld().getBlockEntity(BASE_A) instanceof TurretBaseBlockEntity base) {
                    BasePacketHandler.openMenu(player(minecraft), base);
                }
            });
            context.waitForScreen(TurretBaseScreen.class);
            context.waitTicks(5);
            context.takeScreenshot("omt-gui-base");
            context.setScreen(() -> new ConfigureScreen(BASE_A));
            context.waitTicks(5);
            context.takeScreenshot("omt-gui-configure");
            context.setScreen(() -> new TrustedPlayersScreen(BASE_A, null));
            context.waitTicks(5);
            context.takeScreenshot("omt-gui-trusted");
            context.setScreen(() -> null);

            server.runOnServer(minecraft -> {
                ServerLevel level = minecraft.overworld();
                BlockState state = level.getBlockState(GENERATOR);
                if (state.getBlock() instanceof GeneratorBlock generator) {
                    generator.onUse(state, level, GENERATOR, player(minecraft), InteractionHand.MAIN_HAND,
                            new BlockHitResult(Vec3.atCenterOf(GENERATOR), Direction.NORTH, GENERATOR, false));
                }
            });
            context.waitForScreen(GeneratorScreen.class);
            context.waitTicks(5);
            context.takeScreenshot("omt-gui-generator");
            context.setScreen(() -> null);
        }
    }

    private static void placeBase(ServerLevel level, BlockPos pos, Block block, ServerPlayer owner) {
        level.setBlockAndUpdate(pos, block.defaultBlockState());
        if (level.getBlockEntity(pos) instanceof TurretBaseBlockEntity base) {
            base.getTrustManager().setOwner(PlayerAdapter.identity(owner));
        }
    }

    private static void setBlock(TestServerContext server, BlockPos pos, String block) {
        server.runCommand("setblock " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + block);
    }

    private static ServerPlayer player(MinecraftServer server) {
        return server.getPlayerList().getPlayers().getFirst();
    }
}
