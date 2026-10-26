package omtteam.openmodularturrets.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.compat.nbt.NbtCompat;
import omtteam.openmodularturrets.core.base.MachineMode;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

import java.util.List;

/**
 * Server half of the Jade tooltip of the turret base (ported from the 1.12.2 {@code WailaTurretBaseHandler}): machine
 * mode, whether the base is active and its owner (or that it has none and can be claimed). {@link #lines} turns the
 * data into the tooltip; the client half ({@code client.compat.jade}) shows it.
 */
public final class TurretBaseJadeProvider implements IServerDataProvider<BlockAccessor> {
    public static final TurretBaseJadeProvider INSTANCE = new TurretBaseJadeProvider();
    public static final ResourceLocation UID = OMT.id("turret_base");
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

    /** The tooltip lines for the data the server sent. */
    public static List<Component> lines(CompoundTag data) {
        if (!data.contains(MODE)) return List.of();
        MachineMode mode = MachineMode.bySerializedName(NbtCompat.getString(data, MODE, ""), MachineMode.INVERTED);
        return List.of(
                JadeLines.line("mode", Component.translatable(mode.getTranslationKey()).withStyle(ChatFormatting.GREEN)),
                JadeLines.line("active", JadeLines.yesNo(NbtCompat.getBoolean(data, ACTIVE, false))),
                JadeLines.ownerLine(data.contains(OWNER) ? NbtCompat.getString(data, OWNER, "") : null));
    }
}
