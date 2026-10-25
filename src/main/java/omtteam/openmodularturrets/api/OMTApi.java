package omtteam.openmodularturrets.api;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.AbstractOMTDamageSource;
import omtteam.openmodularturrets.entity.projectile.ArmorBypassDamageSource;
import omtteam.openmodularturrets.entity.projectile.NormalDamageSource;
import omtteam.openmodularturrets.entity.projectile.TurretDamage;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.turret.TurretRegistry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Lookups for registered turrets, only usable after all {@link OMTPlugin}s ran (i.e. not inside
 * {@link OMTPlugin#registerTurrets}), and damage helpers for weapons (1.20.1+ {@code api.OMTApi}).
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

    /**
     * The block Open Modular Turrets registered for a turret type (after the block registry event).
     *
     * @throws IllegalStateException if the turret is disabled in the config or its block is not registered yet
     */
    public static Block block(TurretType type) {
        requireRegistered();
        Block block = TurretRegistry.block(type);
        if (block == null) throw new IllegalStateException("Turret " + type + " has no block (disabled or too early)");
        return block;
    }

    /** The item of a turret type's block, see {@link #block}. */
    public static Item item(TurretType type) {
        return Item.getItemFromBlock(block(type));
    }

    /**
     * Whether a turret on this base may damage the entity: never the owner, nor (by default) trusted players and team
     * members, nor their pets, nor turret projectiles. The same rule built-in turrets apply.
     */
    public static boolean canDamage(TurretBase base, Entity entity) {
        return TurretDamage.canDamageEntity(entity, Objects.requireNonNull(base, "base"));
    }

    /**
     * Damages an entity the way built-in turrets do: kills count for the base and the fake drops addon's looting
     * applies (without it, killed mobs drop nothing). Does not check {@link #canDamage}.
     *
     * @param damageType  name of the damage, for death messages ({@code death.attack.<damageType>})
     * @param bypassArmor whether armor is ignored
     * @param direct      the entity that deals the damage (e.g. a projectile), or null; it marks the damage as
     *                    projectile damage
     */
    public static void hurt(TurretBase base, Entity target, String damageType, float amount, boolean bypassArmor,
                            @Nullable Entity direct) {
        if (amount <= 0 || !(base.getWorld() instanceof WorldServer)) return;
        WorldServer world = (WorldServer) base.getWorld();
        int looting = base.getLogic().getLootingLevel(3);
        AbstractOMTDamageSource source = bypassArmor
                ? new ArmorBypassDamageSource(damageType, looting, base, world, direct != null)
                : new NormalDamageSource(damageType, looting, base, world, direct != null);
        if (!(target instanceof EntityPlayer)) {
            TurretDamage.setTagsForTurretHit(target, base);
        }
        target.attackEntityFrom(source, amount);
        target.hurtResistantTime = 0;
    }

    private static void requireRegistered() {
        if (!TurretRegistry.isLoaded()) {
            throw new IllegalStateException("Turrets are still being registered");
        }
    }
}
