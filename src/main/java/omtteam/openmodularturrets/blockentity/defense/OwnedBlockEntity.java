package omtteam.openmodularturrets.blockentity.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.compat.CompatBlockEntity;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.security.PlayerAdapter;
import org.jetbrains.annotations.Nullable;

/**
 * A passive defense block with an owner (OMPD {@code TileEntityPassiveTiered} / OMLib {@code TileEntityOwnedBlock}).
 * The tier is the block itself now. OMPD only ever checked the owner (not trusted players), so no trust list is kept.
 */
public class OwnedBlockEntity extends CompatBlockEntity {
    @Nullable
    private PlayerIdentity owner;

    public OwnedBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.OWNED, pos, state);
    }

    protected OwnedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nullable
    public PlayerIdentity getOwner() {
        return owner;
    }

    public void setOwner(PlayerIdentity owner) {
        this.owner = owner;
        syncToClient();
    }

    /** OMLib {@code isPlayerOwner}. Blocks without owner (placed by machines) belong to nobody. */
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
    protected void readData(CompoundTag tag) {
        CompoundTag ownerTag = tag.getCompoundOrEmpty("Owner");
        owner = ownerTag.read("UUID", UUIDUtil.CODEC)
                .map(uuid -> new PlayerIdentity(uuid, ownerTag.getStringOr("Name", ""))).orElse(null);
    }

    @Override
    protected void writeData(CompoundTag tag) {
        if (owner != null) {
            CompoundTag ownerTag = new CompoundTag();
            ownerTag.store("UUID", UUIDUtil.CODEC, owner.uuid());
            ownerTag.putString("Name", owner.name());
            tag.put("Owner", ownerTag);
        }
    }
}
