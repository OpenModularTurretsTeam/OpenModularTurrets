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
import net.minecraft.core.registries.Registries;
//?}
//? if >=1.21.11 {
/*import net.minecraft.world.entity.animal.wolf.Wolf;
*///?} else {
import net.minecraft.world.entity.animal.Wolf;
//?}
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import omtteam.omlib.compat.CommonTags;
import omtteam.omlib.compat.VersionCompat;
import omtteam.omlib.compat.nbt.NbtCompat;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.openmodularturrets.blockentity.base.LivingTargetEntity;
import omtteam.openmodularturrets.core.world.Pose;
import omtteam.openmodularturrets.entity.projectile.TurretKiller;
import omtteam.openmodularturrets.registry.ModBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * OMLib's version helpers where they need a running game (registries, tags, entities), through the mod code that
 * uses them: item stacks and their custom data saved and loaded, the common tags the recipes use, the Looting sword of
 * the turret fake player, the owner of a tamed animal, and the render shape of blocks drawn by their renderer.
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class CompatGameTests implements OMTGameTests {
    /** A stack keeps item, count and custom data through saving and loading; empty or broken data loads as empty. */
    @GameTest(template = EMPTY_STRUCTURE)
    public void stacksKeepTheirDataWhenSavedAndLoaded(GameTestHelper helper) {
        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        ItemStack stack = new ItemStack(Items.IRON_INGOT, 5);
        helper.assertFalse(VersionCompat.hasCustomData(stack), "A new stack has custom data");
        CompoundTag data = new CompoundTag();
        data.putInt("Charge", 42);
        VersionCompat.setCustomData(stack, data);
        helper.assertTrue(VersionCompat.hasCustomData(stack), "The stack has no custom data after setting it");

        ItemStack loaded = VersionCompat.loadStack(VersionCompat.saveStack(stack, registries), registries);
        helper.assertTrue(ItemStack.matches(stack, loaded), "Loaded " + loaded + ", saved " + stack);
        helper.assertTrue(NbtCompat.getInt(VersionCompat.getCustomData(loaded), "Charge", 0) == 42,
                "The loaded stack lost its custom data");
        helper.assertTrue(VersionCompat.loadStack(VersionCompat.saveStack(ItemStack.EMPTY, registries), registries).isEmpty(),
                "An empty stack does not load as empty");
        helper.assertTrue(VersionCompat.loadStack(new CompoundTag(), registries).isEmpty(), "An empty tag loads as a stack");
        helper.succeed();
    }

    /** The common tags hold the vanilla items the recipes ask for (wrenches come from other mods). */
    @GameTest(template = EMPTY_STRUCTURE)
    public void commonTagsHoldTheVanillaItems(GameTestHelper helper) {
        Map<TagKey<Item>, Item> members = Map.of(
                CommonTags.IRON_INGOTS, Items.IRON_INGOT,
                CommonTags.REDSTONE_DUSTS, Items.REDSTONE,
                CommonTags.GOLD_INGOTS, Items.GOLD_INGOT,
                CommonTags.QUARTZ_GEMS, Items.QUARTZ,
                CommonTags.DIAMOND_GEMS, Items.DIAMOND,
                CommonTags.GLASS_PANES, Items.GLASS_PANE);
        List<String> missing = new ArrayList<>();
        members.forEach((tag, item) -> {
            if (!new ItemStack(item).is(tag)) missing.add(item + " in " + tag.location());
        });
        helper.assertTrue(missing.isEmpty(), "Missing from the common tags: " + missing);
        helper.assertTrue(CommonTags.WRENCHES.location().getNamespace().equals("c"), "Wrenches are not a common tag");
        helper.succeed();
    }

    /** Turret kills with fake drops are credited to a player holding a sword with the base's Looting level. */
    @GameTest(template = EMPTY_STRUCTURE)
    public void turretKillerHoldsASwordWithTheLootingLevel(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ItemStack plain = TurretKiller.withLooting(level, 0).getMainHandItem();
        ItemStack looting = TurretKiller.withLooting(level, 3).getMainHandItem();
        helper.assertFalse(plain.isEnchanted(), "The sword without Looting is enchanted");
        helper.assertTrue(lootingLevel(level, looting) == 3, "The sword has Looting " + lootingLevel(level, looting));
        helper.succeed();
    }

    /** A tamed animal belongs to its owner (turrets spare the pets of trusted players); a wild one to nobody. */
    @GameTest(template = EMPTY_STRUCTURE)
    public void tamedAnimalsBelongToTheirOwner(GameTestHelper helper) {
        Wolf wolf = helper.spawn(EntityType.WOLF, new BlockPos(1, 1, 1));
        helper.assertTrue(new LivingTargetEntity(wolf, Pose.IDENTITY).getOwner().isEmpty(), "A wild wolf has an owner");
        ServerPlayer player = VersionCompat.fakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "Owner"));
        //? if >=1.21.11 {
        /*wolf.setOwner(player);
        *///?} else {
        wolf.setOwnerUUID(player.getUUID());
        //?}
        Optional<PlayerIdentity> owner = new LivingTargetEntity(wolf, Pose.IDENTITY).getOwner();
        helper.assertTrue(owner.isPresent() && owner.get().is(player.getUUID()), "The wolf's owner is " + owner);
        wolf.discard();
        helper.succeed();
    }

    /** Turrets and cranks are drawn by their block entity renderers only, not as a block model. */
    @GameTest(template = EMPTY_STRUCTURE)
    public void turretsAndCranksAreDrawnByTheirRenderers(GameTestHelper helper) {
        //? if >=1.21.11 {
        /*RenderShape expected = RenderShape.INVISIBLE;
        *///?} else {
        RenderShape expected = RenderShape.ENTITYBLOCK_ANIMATED;
        //?}
        List<Block> blocks = new ArrayList<>(ModBlocks.TURRETS.values());
        blocks.add(ModBlocks.LEVER);
        for (Block block : blocks) {
            RenderShape shape = block.defaultBlockState().getRenderShape();
            helper.assertTrue(shape == expected, block + " renders as " + shape);
        }
        helper.succeed();
    }

    private static int lootingLevel(ServerLevel level, ItemStack stack) {
        //? if >=1.21 {
        return EnchantmentHelper.getItemEnchantmentLevel(
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING), stack);
        //?} else {
        /*return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, stack);
        *///?}
    }
}
