package omtteam.openmodularturrets.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.core.base.MachineMode;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * Jade tooltip of the turret base (ported from the 1.12.2 {@code WailaTurretBaseHandler}): machine mode, whether the
 * base is active and its owner (or that it has none and can be claimed).
 */
final class TurretBaseJadeProvider extends JadeBlockProvider {
    static final TurretBaseJadeProvider INSTANCE = new TurretBaseJadeProvider();
    private static final ResourceLocation UID = OMT.id("turret_base");
    private static final String MODE = "Mode";
    private static final String ACTIVE = "Active";
    private static final String OWNER = "Owner";

    private TurretBaseJadeProvider() {
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof TurretBaseBlockEntity base)) return;
        TurretBaseLogic logic = base.getLogic();
        data.putString(MODE, logic.getMode().getSerializedName());
        data.putBoolean(ACTIVE, logic.isActive());
        PlayerIdentity owner = base.getOwner();
        if (owner != null) data.putString(OWNER, owner.name());
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains(MODE)) return;
        MachineMode mode = MachineMode.bySerializedName(data.getString(MODE), MachineMode.INVERTED);
        tooltip.add(line("mode", Component.translatable(mode.getTranslationKey()).withStyle(ChatFormatting.GREEN)));
        tooltip.add(line("active", yesNo(data.getBoolean(ACTIVE))));
        tooltip.add(ownerLine(data.contains(OWNER) ? data.getString(OWNER) : null));
    }
}
