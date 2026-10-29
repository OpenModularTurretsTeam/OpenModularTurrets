package omtteam.openmodularturrets.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.omlib.compat.nbt.NbtCompat;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.omlib.security.PlayerAdapter;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Server half of the Jade tooltip of a turret (ported from the 1.12.2 {@code WailaTurretHandler}): whether it is active, its owner and,
 * for players who may open the base, ammo, damage amp, inaccuracy and rate of fire. The stats show what the base
 * contains, so the server only sends them to players with that permission. {@link #lines} turns the data into the tooltip; the
 * client half ({@code client.compat.jade}) shows it.
 */
public final class TurretJadeProvider implements IServerDataProvider<BlockAccessor> {
    public static final TurretJadeProvider INSTANCE = new TurretJadeProvider();
    public static final ResourceLocation UID = OMT.id("turret");
    private static final String ACTIVE = "Active";
    private static final String OWNER = "Owner";
    private static final String AMMO = "Ammo";
    private static final String DAMAGE_AMP = "DamageAmp";
    private static final String INACCURACY = "Inaccuracy";
    private static final String RATE_OF_FIRE = "RateOfFire";

    private TurretJadeProvider() {
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof TurretBlockEntity turret)) return;
        Optional<TurretBaseBlockEntity> found = turret.getBase();
        if (found.isEmpty()) return;
        TurretBaseBlockEntity base = found.get();
        TurretBaseLogic logic = base.getLogic();
        TurretType type = turret.getTurretType();
        data.putBoolean(ACTIVE, logic.isActive());
        PlayerIdentity owner = base.getOwner();
        if (owner != null) data.putString(OWNER, owner.name());
        if (!base.getTrustManager().canOpenGui(PlayerAdapter.info(accessor.getPlayer()))) return;
        int ammo = logic.getAmmoLevel(type);
        // -1: unlimited
        data.putInt(AMMO, ammo == Integer.MAX_VALUE ? -1 : ammo);
        data.putDouble(DAMAGE_AMP, logic.getDamageAmpBonus(type) * 100);
        data.putDouble(INACCURACY, Math.min(100.0D, logic.getAccuracyDeviation(type)));
        double cooldown = logic.getFireCooldownTicks(type);
        data.putDouble(RATE_OF_FIRE, cooldown > 0 ? 20.0D / cooldown : 0);
    }

    /** The tooltip lines for the data the server sent; a concealed turret shows nothing. */
    public static List<Component> lines(CompoundTag data, boolean concealed) {
        if (concealed || !data.contains(ACTIVE)) return List.of();
        List<Component> lines = new ArrayList<>();
        lines.add(JadeLines.line("active", JadeLines.yesNo(NbtCompat.getBoolean(data, ACTIVE, false))));
        lines.add(JadeLines.ownerLine(data.contains(OWNER) ? NbtCompat.getString(data, OWNER, "") : null));
        if (!data.contains(AMMO)) return lines;
        int ammo = NbtCompat.getInt(data, AMMO, 0);
        lines.add(JadeLines.line("ammo", ammo < 0 ? Component.translatable("gui.openmodularturrets.unlimited")
                .withStyle(ChatFormatting.WHITE) : Component.literal(String.valueOf(ammo)).withStyle(ChatFormatting.WHITE)));
        lines.add(JadeLines.line("damage_amp", format(NbtCompat.getDouble(data, DAMAGE_AMP, 0)) + "%"));
        lines.add(JadeLines.line("inaccuracy", format(NbtCompat.getDouble(data, INACCURACY, 0)) + "%"));
        lines.add(JadeLines.line("rate_of_fire", format(NbtCompat.getDouble(data, RATE_OF_FIRE, 0)) + "/s"));
        return lines;
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
