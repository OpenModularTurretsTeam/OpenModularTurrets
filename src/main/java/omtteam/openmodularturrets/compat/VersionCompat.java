package omtteam.openmodularturrets.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.OwnableEntity;
import org.jetbrains.annotations.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
//? if fabric {
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
//?}
//? if fabric && >=1.21.11 {
/*import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
*///?}
//? if neoforge {
/*import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
*///?}
//? if forge {
/*import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
*///?}
//? if >=1.21 {
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}
//? if <1.21.11 {
import net.minecraft.server.players.GameProfileCache;
//?}
//? if >=1.21.11 {
/*import com.mojang.serialization.MapCodec;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
*///?}

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Hides differences between the supported Minecraft versions.
 * Version-conditional (Stonecutter) code should live here instead of in gameplay code.
 * <p>
 * Methods taking a {@link HolderLookup.Provider} need it on 1.21+; on 1.20.1 it is ignored.
 */
public final class VersionCompat {
    private VersionCompat() {
    }

    //? if >=1.21.11 {
    /*/^* A whole compound as a map codec: 1.21.11 stores data through ValueOutput, our code still reads and writes NBT. ^/
    static final MapCodec<CompoundTag> WHOLE_TAG = MapCodec.assumeMapUnsafe(CompoundTag.CODEC);
    *///?}

    // ---- Resource locations ----

    public static ResourceLocation id(String namespace, String path) {
        //? if >=1.21 {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        //?} else {
        /*return new ResourceLocation(namespace, path);
        *///?}
    }

    // ---- Registration (1.21.11 needs the registry id in the properties before a block or item is created) ----

    /** Properties of the block that will be registered as {@code id}. */
    public static BlockBehaviour.Properties blockProperties(BlockBehaviour.Properties properties, ResourceLocation id) {
        //? if >=1.21.11 {
        /*return properties.setId(ResourceKey.create(Registries.BLOCK, id));
        *///?} else {
        return properties;
        //?}
    }

    /** Properties of the item that will be registered as {@code id}. */
    public static Item.Properties itemProperties(Item.Properties properties, ResourceLocation id) {
        //? if >=1.21.11 {
        /*return properties.setId(ResourceKey.create(Registries.ITEM, id));
        *///?} else {
        return properties;
        //?}
    }

    /**
     * Properties of the block item that will be registered as {@code id}. It keeps the block's name
     * ({@code block.<mod>.<id>}); 1.21.11 would otherwise name it like an item.
     */
    public static Item.Properties blockItemProperties(Item.Properties properties, ResourceLocation id) {
        //? if >=1.21.11 {
        /*return itemProperties(properties, id).useBlockDescriptionPrefix();
        *///?} else {
        return itemProperties(properties, id);
        //?}
    }

    /**
     * A block entity type for these blocks (1.21.11 removed {@code BlockEntityType.Builder}; vanilla's factory interface
     * is only accessible on NeoForge there, Fabric has its own builder).
     */
    public static <T extends BlockEntity> BlockEntityType<T> blockEntityType(BiFunction<BlockPos, BlockState, T> factory,
                                                                             Block... blocks) {
        //? if fabric && >=1.21.11 {
        /*return FabricBlockEntityTypeBuilder.create(factory::apply, blocks).build();
        *///?} else if >=1.21.11 {
        /*return new BlockEntityType<>(factory::apply, blocks);
        *///?} else {
        return BlockEntityType.Builder.of(factory::apply, blocks).build(null);
        //?}
    }

    /** The entity type of a builder, to be registered as {@code id} (1.21.11 builds it with the registry key). */
    public static <T extends Entity> EntityType<T> buildEntityType(EntityType.Builder<T> builder, ResourceLocation id) {
        //? if >=1.21.11 {
        /*return builder.build(ResourceKey.create(Registries.ENTITY_TYPE, id));
        *///?} else {
        return builder.build(id.toString());
        //?}
    }

    // ---- Entities and players ----

    /** A new entity of this type in the level (1.21.11 asks for the reason), {@code null} if it cannot be created. */
    public static <T extends Entity> T createEntity(EntityType<T> type, Level level) {
        //? if >=1.21.11 {
        /*return type.create(level, EntitySpawnReason.TRIGGERED);
        *///?} else {
        return type.create(level);
        //?}
    }

    /** The UUID of the owner of a tamed or owned entity, {@code null} if it has none. */
    public static @Nullable UUID ownerUuid(OwnableEntity entity) {
        //? if >=1.21.11 {
        /*EntityReference<LivingEntity> owner = entity.getOwnerReference();
        return owner == null ? null : owner.getUUID();
        *///?} else {
        return entity.getOwnerUUID();
        //?}
    }

    /** Whether the level is in daylight (1.21.11 asks for {@code isBrightOutside}). */
    public static boolean isDay(Level level) {
        //? if >=1.21.11 {
        /*return level.isBrightOutside();
        *///?} else {
        return level.isDay();
        //?}
    }

    /** Plays a sound that only this player hears, at the player's position. */
    public static void playNotifySound(ServerPlayer player, SoundEvent sound, SoundSource source, float volume, float pitch) {
        //? if >=1.21.11 {
        /*player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), source,
                player.getX(), player.getY(), player.getZ(), volume, pitch, player.level().getRandom().nextLong()));
        *///?} else {
        player.playNotifySound(sound, source, volume, pitch);
        //?}
    }

    // ---- Players and profiles ----

    public static String profileName(GameProfile profile) {
        //? if >=1.21.11 {
        /*return profile.name();
        *///?} else {
        return profile.getName();
        //?}
    }

    public static UUID profileId(GameProfile profile) {
        //? if >=1.21.11 {
        /*return profile.id();
        *///?} else {
        return profile.getId();
        //?}
    }

    /**
     * Looks a name up in the server's profile cache; a name that is not cached is looked up at Mojang, a blocking web
     * request, so call it off the server thread.
     */
    public static Optional<GameProfile> cachedProfile(MinecraftServer server, String name) {
        //? if >=1.21.11 {
        /*return server.services().nameToIdCache().get(name).map(found -> new GameProfile(found.id(), found.name()));
        *///?} else {
        GameProfileCache cache = server.getProfileCache();
        return cache == null ? Optional.empty() : cache.get(name);
        //?}
    }

    /** The name the server's profile cache knows for this player, also while the player is offline. */
    public static Optional<String> cachedName(MinecraftServer server, UUID id) {
        //? if >=1.21.11 {
        /*return server.services().nameToIdCache().get(id).map(NameAndId::name);
        *///?} else {
        GameProfileCache cache = server.getProfileCache();
        return cache == null ? Optional.empty() : cache.get(id).map(GameProfile::getName);
        //?}
    }

    /** Whether the player has the highest operator level (OMLib {@code isPlayerOP}: {@code canUseCommand(4, "")}). */
    public static boolean isOwnerOperator(Player player) {
        //? if >=1.21.11 {
        /*return player.permissions().hasPermission(Permissions.COMMANDS_OWNER);
        *///?} else {
        return player.hasPermissions(4);
        //?}
    }

    /** Damages an entity (server side; 1.21.11 replaced {@code Entity#hurt} with {@code hurtServer}). */
    public static boolean hurt(Entity entity, DamageSource source, float amount) {
        //? if >=1.21.11 {
        /*return entity.hurtOrSimulate(source, amount);
        *///?} else {
        return entity.hurt(source, amount);
        //?}
    }

    /** Whether the player hosts the integrated (singleplayer or LAN) server it is on. */
    public static boolean isSingleplayerOwner(ServerPlayer player) {
        //? if >=1.21.11 {
        /*return player.level().getServer().isSingleplayerOwner(player.nameAndId());
        *///?} else {
        return player.server.isSingleplayerOwner(player.getGameProfile());
        //?}
    }

    /** Sets an entity on fire for the given number of seconds. */
    public static void setOnFire(Entity entity, int seconds) {
        //? if >=1.21 {
        entity.igniteForSeconds(seconds);
        //?} else {
        /*entity.setSecondsOnFire(seconds);
        *///?}
    }

    // ---- Enchantments ----

    /** Maximum level of the Looting enchantment (data-driven on 1.21, so a datapack may change it). */
    public static int maxLootingLevel(Level level) {
        //? if >=1.21 {
        return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING)
                .value().getMaxLevel();
        //?} else {
        /*return Enchantments.MOB_LOOTING.getMaxLevel();
        *///?}
    }

    /** Adds Looting at the given level to a stack. */
    public static void enchantLooting(ItemStack stack, Level level, int lootingLevel) {
        //? if >=1.21 {
        stack.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),
                lootingLevel);
        //?} else {
        /*stack.enchant(Enchantments.MOB_LOOTING, lootingLevel);
        *///?}
    }

    // ---- ItemStack NBT ----

    /**
     * Writes a slot list to {@code tag} under {@code "Items"} (vanilla container format).
     */
    public static void saveItems(CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider registries) {
        //? if >=1.21.11 {
        /*TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        ContainerHelper.saveAllItems(output, items);
        tag.merge(output.buildResult());
        *///?} else if >=1.21 {
        ContainerHelper.saveAllItems(tag, items, registries);
        //?} else {
        /*ContainerHelper.saveAllItems(tag, items);
        *///?}
    }

    /**
     * Reads a slot list written by {@link #saveItems}. {@code items} must already have the correct size.
     */
    public static void loadItems(CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider registries) {
        //? if >=1.21.11 {
        /*ContainerHelper.loadAllItems(TagValueInput.create(ProblemReporter.DISCARDING, registries, tag), items);
        *///?} else if >=1.21 {
        ContainerHelper.loadAllItems(tag, items, registries);
        //?} else {
        /*ContainerHelper.loadAllItems(tag, items);
        *///?}
    }

    // ---- Custom item data (NBT on 1.20.1, CUSTOM_DATA component on 1.21+) ----

    /**
     * Returns a <b>copy</b> of the custom data of a stack (empty tag if none). Use {@link #setCustomData}
     * or {@link #updateCustomData} to change it.
     */
    public static CompoundTag getCustomData(ItemStack stack) {
        //? if >=1.21 {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        //?} else {
        /*CompoundTag tag = stack.getTag();
        return tag == null ? new CompoundTag() : tag.copy();
        *///?}
    }

    /**
     * Replaces the custom data of a stack. An empty tag removes it.
     */
    public static void setCustomData(ItemStack stack, CompoundTag tag) {
        //? if >=1.21 {
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        //?} else {
        /*stack.setTag(tag.isEmpty() ? null : tag.copy());
        *///?}
    }

    /**
     * Modifies the custom data of a stack in place.
     */
    public static void updateCustomData(ItemStack stack, Consumer<CompoundTag> updater) {
        CompoundTag tag = getCustomData(stack);
        updater.accept(tag);
        setCustomData(stack, tag);
    }

    // ---- Loader differences ----

    /**
     * Builder of a mod creative tab. Fabric places mod tabs on its own tab pages ({@code FabricItemGroup}); NeoForge
     * sorts mod tabs itself.
     */
    public static CreativeModeTab.Builder creativeTabBuilder() {
        //? if fabric {
        return FabricItemGroup.builder();
        //?} else {
        /*return CreativeModeTab.builder();
        *///?}
    }

    /**
     * The loader's fake player of this level for the profile (one per level and profile, created on first use).
     */
    public static ServerPlayer fakePlayer(ServerLevel level, GameProfile profile) {
        //? if fabric {
        return FakePlayer.get(level, profile);
        //?} else {
        /*return FakePlayerFactory.get(level, profile);
        *///?}
    }

    /** Whether the player is one of the loader's fake players (machines such as Create's deployer). */
    public static boolean isFakePlayer(Player player) {
        return player instanceof FakePlayer;
    }
}
