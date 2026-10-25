package omtteam.openmodularturrets.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.turret.TurretTypes;
import omtteam.openmodularturrets.entity.projectile.TurretDamage;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;
import omtteam.openmodularturrets.registry.TurretRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Lookups for registered turrets, only usable after all {@link OMTPlugin}s ran (i.e. not inside
 * {@link OMTPlugin#registerTurrets}), and damage helpers for weapons.
 */
public final class OMTApi {
    private OMTApi() {
    }

    /** All turrets, built-in first. */
    public static Collection<TurretDefinition> turrets() {
        requireRegistered();
        return TurretRegistry.turrets();
    }

    public static Optional<TurretDefinition> turret(TurretType type) {
        requireRegistered();
        return TurretRegistry.find(type);
    }

    public static Optional<AmmoDefinition> ammo(AmmoType type) {
        requireRegistered();
        return TurretRegistry.ammo(type);
    }

    /** The block Open Modular Turrets registered for a turret type. */
    public static TurretBlock block(TurretType type) {
        requireRegistered();
        return ModBlocks.TURRETS.get(type);
    }

    /** The block item Open Modular Turrets registered for a turret type. */
    public static BlockItem item(TurretType type) {
        requireRegistered();
        return ModItems.TURRETS.get(type);
    }

    /**
     * Whether a turret on this base may damage the entity: never the owner, nor (by default) trusted players and
     * team members, nor their pets, nor a player holding the ownership wand (map makers; turrets may still fire at
     * them). The same rule built-in turrets apply; check it before any effect, not only before damage.
     */
    public static boolean canDamage(TurretBaseBlockEntity base, Entity entity) {
        return TurretDamage.canDamage(Objects.requireNonNull(base.getLevel(), "level"), base.getBlockPos(), entity);
    }

    /**
     * Damages an entity the way built-in turrets do: kills count for the base and the fake drops addon's looting
     * applies. Does not check {@link #canDamage}, but never hurts a player holding the ownership wand.
     *
     * @param direct the entity that deals the damage (e.g. a projectile), or null
     */
    public static void hurt(TurretBaseBlockEntity base, Entity target, ResourceKey<DamageType> type, float amount,
                            @Nullable Entity direct) {
        Level level = Objects.requireNonNull(base.getLevel(), "level");
        int looting = base.getLogic().getLootingLevel(VersionCompat.maxLootingLevel(level));
        TurretDamage.hurt(level, target, type, amount, direct, base.getBlockPos(), looting);
    }

    private static void requireRegistered() {
        if (!TurretTypes.isFrozen()) {
            throw new IllegalStateException("Turrets are still being registered");
        }
    }
}
