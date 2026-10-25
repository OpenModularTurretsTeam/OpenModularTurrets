package omtteam.openmodularturrets.blockentity.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.compat.CompatBlockEntity;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.security.OwnedHost;
import omtteam.openmodularturrets.security.PlayerAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A passive defense block with an owner (OMPD {@code TileEntityPassiveTiered} / OMLib {@code TileEntityOwnedBlock}).
 * The tier is the block itself now. OMPD only ever checked the owner (not trusted players), so no trust list is kept.
 */
public class OwnedBlockEntity extends CompatBlockEntity implements OwnedHost {
    @Nullable
    private PlayerIdentity owner;

    public OwnedBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.OWNED, pos, state);
    }

    protected OwnedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nullable
    @Override
    public PlayerIdentity getOwner() {
        return owner;
    }

    @Override
    public void setOwner(PlayerIdentity owner) {
        this.owner = Objects.requireNonNull(owner, "owner");
        syncToClient();
    }

    @Override
    public boolean claim(PlayerIdentity player) {
        Objects.requireNonNull(player, "player");
        if (owner != null) return false;
        setOwner(player);
        return true;
    }

    @Override
    public void clearOwner() {
        owner = null;
        syncToClient();
    }

    /** OMLib {@code isPlayerOwner}. Blocks without owner (placed by machines, or disowned) belong to nobody. */
    public boolean isOwner(Player player) {
        return owner != null && owner.is(player.getUUID());
    }

    /** OMPD {@code BlockHelper.onBlockPlacedBy}. */
    public static void setOwnerOnPlace(@Nullable OwnedBlockEntity blockEntity, @Nullable LivingEntity placer) {
        if (blockEntity != null && placer instanceof Player player) {
            blockEntity.setOwner(PlayerAdapter.identity(player));
        }
    }

    @Override
    protected void readData(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag ownerTag = tag.getCompound("Owner");
        owner = tag.contains("Owner", Tag.TAG_COMPOUND) && ownerTag.hasUUID("UUID")
                ? new PlayerIdentity(ownerTag.getUUID("UUID"), ownerTag.getString("Name")) : null;
    }

    @Override
    protected void writeData(CompoundTag tag, HolderLookup.Provider registries) {
        if (owner != null) {
            CompoundTag ownerTag = new CompoundTag();
            ownerTag.putUUID("UUID", owner.uuid());
            ownerTag.putString("Name", owner.name());
            tag.put("Owner", ownerTag);
        }
    }
}
