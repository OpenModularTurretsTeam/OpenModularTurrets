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
//? if >=1.21 {
import net.minecraft.core.HolderLookup;
//?}
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.omlib.blockentity.OwnedBlockEntity;
import omtteam.omlib.compat.VersionCompat;
import omtteam.omlib.compat.nbt.NbtCompat;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.security.PlayerAdapter;
import omtteam.openmodularturrets.registry.ModBlocks;

import java.util.List;
import java.util.UUID;

/**
 * Hardened blocks, walls and fences (OMLib's {@code OwnedBlockEntity}): the player who places one owns it, only the
 * owner picks it up again, and the owner is saved with the block and sent to clients.
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class PassiveDefenseGameTests implements OMTGameTests {
    private static final BlockPos BLOCK = new BlockPos(1, 1, 1);

    /** Placing a hardened block, a wall or a fence makes the player its owner. */
    @GameTest(template = EMPTY_STRUCTURE)
    public void placingMakesThePlayerTheOwner(GameTestHelper helper) {
        List<Block> blocks = List.of(ModBlocks.HARDENED.get(0), ModBlocks.WALLS.get(0), ModBlocks.FENCES.get(0));
        ServerPlayer player = player(helper, "Builder");
        ServerPlayer other = player(helper, "Other");
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            BlockPos floor = new BlockPos(1 + 2 * i, 1, 1);
            helper.setBlock(floor, Blocks.STONE);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(block));
            rightClick(helper, player, floor);
            helper.assertTrue(helper.getBlockState(floor.above()).is(block), block + " was not placed");
            OwnedBlockEntity owned = owned(helper, floor.above());
            helper.assertTrue(owned.isOwner(player), block + " is owned by " + owned.getOwner() + ", not the placer");
            helper.assertFalse(owned.isOwner(other), block + " is owned by another player");
        }
        helper.succeed();
    }

    /** A sneak-right-click with an empty hand picks the block up for its owner, not for anyone else. */
    @GameTest(template = EMPTY_STRUCTURE)
    public void onlyTheOwnerPicksTheBlockUp(GameTestHelper helper) {
        helper.setBlock(BLOCK, ModBlocks.WALLS.get(0));
        ServerPlayer owner = player(helper, "Owner");
        ServerPlayer stranger = player(helper, "Stranger");
        owned(helper, BLOCK).setOwner(PlayerAdapter.identity(owner));
        stranger.setShiftKeyDown(true);
        owner.setShiftKeyDown(true);

        rightClick(helper, stranger, BLOCK);
        helper.assertTrue(helper.getBlockState(BLOCK).is(ModBlocks.WALLS.get(0)), "A stranger picked the wall up");
        rightClick(helper, owner, BLOCK);
        helper.assertTrue(helper.getBlockState(BLOCK).isAir(), "The owner did not pick the wall up");
        helper.killAllEntitiesOfClass(ItemEntity.class);
        helper.succeed();
    }

    /** The owner (or none) survives saving and loading, and the update packet carries it to clients. */
    @GameTest(template = EMPTY_STRUCTURE)
    public void ownerIsSavedAndSentToClients(GameTestHelper helper) {
        helper.setBlock(BLOCK, ModBlocks.HARDENED.get(0));
        OwnedBlockEntity owned = owned(helper, BLOCK);
        helper.assertTrue(reload(helper, owned).getOwner() == null, "An unowned block has an owner after loading");

        PlayerIdentity owner = new PlayerIdentity(UUID.randomUUID(), "Owner");
        owned.setOwner(owner);
        PlayerIdentity loaded = reload(helper, owned).getOwner();
        helper.assertTrue(loaded != null && loaded.is(owner.uuid()) && loaded.name().equals(owner.name()),
                "Loaded owner is " + loaded + ", expected " + owner);

        ClientboundBlockEntityDataPacket packet = (ClientboundBlockEntityDataPacket) owned.getUpdatePacket();
        CompoundTag sent = NbtCompat.getCompound(packet.getTag(), "Owner");
        helper.assertTrue(owner.uuid().equals(NbtCompat.getUuid(sent, "UUID")),
                "The update packet has no owner: " + packet.getTag());
        helper.succeed();
    }

    /** Saves the block entity as a chunk does and loads a new one from the data. */
    private static OwnedBlockEntity reload(GameTestHelper helper, OwnedBlockEntity owned) {
        //? if >=1.21 {
        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        CompoundTag tag = owned.saveWithFullMetadata(registries);
        BlockEntity loaded = BlockEntity.loadStatic(owned.getBlockPos(), owned.getBlockState(), tag, registries);
        //?} else {
        /*CompoundTag tag = owned.saveWithFullMetadata();
        BlockEntity loaded = BlockEntity.loadStatic(owned.getBlockPos(), owned.getBlockState(), tag);
        *///?}
        helper.assertTrue(loaded instanceof OwnedBlockEntity, "Loaded " + loaded + " from " + tag);
        return (OwnedBlockEntity) loaded;
    }

    private static ServerPlayer player(GameTestHelper helper, String name) {
        return VersionCompat.fakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), name));
    }

    /** A main-hand right click on the top of the block, as the server handles it. */
    private static void rightClick(GameTestHelper helper, ServerPlayer player, BlockPos relative) {
        BlockPos pos = helper.absolutePos(relative);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos).add(0, 0.5, 0), Direction.UP, pos, false);
        player.gameMode.useItemOn(player, helper.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
    }

    private static OwnedBlockEntity owned(GameTestHelper helper, BlockPos relative) {
        return (OwnedBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(relative));
    }
}
