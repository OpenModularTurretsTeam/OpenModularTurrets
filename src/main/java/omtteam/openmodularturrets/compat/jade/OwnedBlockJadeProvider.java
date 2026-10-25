package omtteam.openmodularturrets.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.defense.OwnedBlockEntity;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * Jade tooltip of the passive defense blocks (hardened blocks, walls, fences): their owner, or that they have none and
 * can be claimed.
 */
final class OwnedBlockJadeProvider extends JadeBlockProvider {
    static final OwnedBlockJadeProvider INSTANCE = new OwnedBlockJadeProvider();
    private static final ResourceLocation UID = OMT.id("owned_block");
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

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains(KNOWN)) return;
        tooltip.add(ownerLine(data.contains(OWNER) ? data.getString(OWNER) : null));
    }
}
