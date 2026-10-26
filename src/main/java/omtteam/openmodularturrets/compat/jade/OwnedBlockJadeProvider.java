package omtteam.openmodularturrets.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.omlib.blockentity.OwnedBlockEntity;
import omtteam.omlib.compat.nbt.NbtCompat;
import omtteam.omlib.core.security.PlayerIdentity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

import java.util.List;

/**
 * Server half of the Jade tooltip of the passive defense blocks (hardened blocks, walls, fences): their owner, or that
 * they have none and can be claimed. {@link #lines} turns the data into the tooltip; the client half
 * ({@code client.compat.jade}) shows it.
 */
public final class OwnedBlockJadeProvider implements IServerDataProvider<BlockAccessor> {
    public static final OwnedBlockJadeProvider INSTANCE = new OwnedBlockJadeProvider();
    public static final ResourceLocation UID = OMT.id("owned_block");
    private static final String KNOWN = "Known";
    private static final String OWNER = "Owner";

    private OwnedBlockJadeProvider() {
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof OwnedBlockEntity owned)) return;
        data.putBoolean(KNOWN, true);
        PlayerIdentity owner = owned.getOwner();
        if (owner != null) data.putString(OWNER, owner.name());
    }

    /** The tooltip lines for the data the server sent. */
    public static List<Component> lines(CompoundTag data) {
        if (!data.contains(KNOWN)) return List.of();
        return List.of(JadeLines.ownerLine(data.contains(OWNER) ? NbtCompat.getString(data, OWNER, "") : null));
    }
}
