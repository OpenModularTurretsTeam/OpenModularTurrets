package omtteam.openmodularturrets.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.security.PlayerAdapter;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.Locale;
import java.util.Optional;

/**
 * Jade tooltip of a turret (ported from the 1.12.2 {@code WailaTurretHandler}): whether it is active, its owner and,
 * for players who may open the base, ammo, damage amp, accuracy and rate of fire. The stats show what the base
 * contains, so the server only sends them to players with that permission.
 */
final class TurretJadeProvider extends JadeBlockProvider {
    static final TurretJadeProvider INSTANCE = new TurretJadeProvider();
    private static final ResourceLocation UID = OMT.id("turret");
    private static final String ACTIVE = "Active";
    private static final String OWNER = "Owner";
    private static final String AMMO = "Ammo";
    private static final String DAMAGE_AMP = "DamageAmp";
    private static final String ACCURACY = "Accuracy";
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
        data.putDouble(ACCURACY, Math.min(100.0D, logic.getAccuracyDeviation(type)));
        double cooldown = logic.getFireCooldownTicks(type);
        data.putDouble(RATE_OF_FIRE, cooldown > 0 ? 20.0D / cooldown : 0);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockState().getValue(TurretBlock.CONCEALED)) return;
        CompoundTag data = accessor.getServerData();
        if (!data.contains(ACTIVE)) return;
        tooltip.add(line("active", yesNo(data.getBoolean(ACTIVE))));
        if (data.contains(OWNER)) tooltip.add(line("owner", data.getString(OWNER)));
        if (!data.contains(AMMO)) return;
        int ammo = data.getInt(AMMO);
        tooltip.add(line("ammo", ammo < 0 ? Component.translatable("gui.openmodularturrets.unlimited")
                .withStyle(ChatFormatting.WHITE) : Component.literal(String.valueOf(ammo)).withStyle(ChatFormatting.WHITE)));
        tooltip.add(line("damage_amp", format(data.getDouble(DAMAGE_AMP)) + "%"));
        tooltip.add(line("accuracy", format(data.getDouble(ACCURACY)) + "%"));
        tooltip.add(line("rate_of_fire", format(data.getDouble(RATE_OF_FIRE)) + "/s"));
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
