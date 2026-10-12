package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import org.jetbrains.annotations.Nullable;

/**
 * Damage dealt by a turret (oldmod {@code AbstractOMTDamageSource}). Remembers the base (kill counting) and whether
 * the victim may drop loot.
 */
public class TurretDamageSource extends DamageSource {
    @Nullable
    private final BlockPos basePos;
    private final int lootingLevel;

    private TurretDamageSource(Holder<DamageType> type, @Nullable Entity direct, @Nullable Entity causing,
                               @Nullable BlockPos basePos, int lootingLevel) {
        super(type, direct, causing);
        this.basePos = basePos;
        this.lootingLevel = lootingLevel;
    }

    /**
     * @param causing      the {@link TurretKiller} if the kill may drop player-kill loot, otherwise {@code null}
     * @param lootingLevel {@link TurretBaseLogic#NO_LOOT} or the base's Looting level
     */
    public static TurretDamageSource of(Level level, ResourceKey<DamageType> type, @Nullable Entity direct,
                                        @Nullable Entity causing, @Nullable BlockPos basePos, int lootingLevel) {
        Holder<DamageType> holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type);
        return new TurretDamageSource(holder, direct, causing, basePos, lootingLevel);
    }

    /** The position of the base that fired, if known. */
    @Nullable
    public BlockPos getBasePos() {
        return basePos;
    }

    /** Whether a non-player killed by this damage drops loot (needs a fake drops addon on the base). */
    public boolean allowsLoot() {
        return lootingLevel != TurretBaseLogic.NO_LOOT;
    }
}
