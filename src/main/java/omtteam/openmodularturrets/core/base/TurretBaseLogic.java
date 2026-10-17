package omtteam.openmodularturrets.core.base;

import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.ItemSlot;
import omtteam.openmodularturrets.core.item.ReactorFuel;
import omtteam.openmodularturrets.core.item.UpgradeType;
import omtteam.openmodularturrets.core.security.PlayerInfo;
import omtteam.openmodularturrets.core.security.SecurityEnvironment;
import omtteam.openmodularturrets.core.security.TrustManager;
import omtteam.openmodularturrets.core.settings.BaseTierStats;
import omtteam.openmodularturrets.core.settings.LogicSettings;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.target.TargetEntity;
import omtteam.openmodularturrets.core.target.TargetSelector;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.BaseWorld;
import omtteam.openmodularturrets.core.world.Box;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Neighbour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.random.RandomGenerator;

/**
 * Game logic of one turret base, independent of Minecraft. Ported from 1.12.2 {@code TurretBase} and the base-related
 * parts of {@code TurretHeadUtil}, {@code OMTUtil} and OMLib's machine classes.
 * <p>
 * The block entity owns one instance, forwards world events to it ({@link #setRedstone}, {@link #onInventoryChanged},
 * {@link #updateNeighbours}, {@link #tick}) and persists its state. Turrets ask it for ammo, energy and their
 * upgraded stats.
 */
public class TurretBaseLogic {
    /** 1.12.2 {@code TurretHeadUtil.warnPlayers}: a warned player is warned again after this many ticks. */
    static final long WARNING_COOLDOWN_TICKS = 12000;

    private final int tier;
    private final LogicSettings settings;
    private final BaseWorld world;
    private final RandomGenerator random;
    private final ChangeListener listener;
    private final TrustManager trustManager;
    private final EnergyStore energy;

    private final TargetSelector targetSelector;
    /** Current target entity id per turret face (1.12.2 {@code TurretHead.target}). */
    private final Map<Face, Integer> targets = new EnumMap<>(Face.class);
    /** Ticks since the last target search per turret face (1.12.2 {@code TurretHead.targetingTicks}). */
    private final Map<Face, Integer> searchTicks = new EnumMap<>(Face.class);
    private final Map<Face, Neighbour> neighbours = new EnumMap<>(Face.class);
    private boolean neighboursKnown;
    private final Map<UUID, Long> warnedPlayers = new HashMap<>();

    private MachineMode mode = MachineMode.INVERTED;
    private boolean redstone;
    private TargetingSettings targeting = TargetingSettings.defaults();
    private boolean multiTargeting;
    private int kills;
    private int playerKills;
    private boolean concealTurrets;
    private int ticks;

    /**
     * Receives change notifications.
     */
    @FunctionalInterface
    public interface ChangeListener {
        /**
         * @param clientRelevant whether clients need the new state (owner, trust list); otherwise only saving is
         *                       required
         */
        void onChanged(boolean clientRelevant);
    }

    public TurretBaseLogic(int tier, LogicSettings settings, SecurityEnvironment security, BaseWorld world,
                           RandomGenerator random, ChangeListener listener) {
        BaseTier.validate(tier);
        this.tier = tier;
        this.settings = settings;
        this.world = world;
        this.random = random;
        this.listener = listener;
        this.trustManager = new TrustManager(security, () -> listener.onChanged(true));
        this.targetSelector = new TargetSelector(settings, trustManager, world);
        BaseTierStats stats = settings.baseTier(tier);
        this.energy = new EnergyStore(stats.maxCharge(), stats.maxIo(), this::markDirty);
    }

    /** Persistent state changed that clients do not need (energy, ammo). */
    private void markDirty() {
        listener.onChanged(false);
    }

    /** Persistent state changed that clients display (settings, mode, statistics). */
    private void markSync() {
        listener.onChanged(true);
    }

    // ---- Ticking ----

    /**
     * Server tick (1.12.2 {@code TurretBase.update}). Every tick: solar panel, player warnings (throttled); every 5
     * ticks: concealment, energy capacity and max range; every 20 ticks: redstone reactor and range clamp. The
     * creative upgrade fills the energy storage every tick, right before the targeting.
     */
    public void tick(long gameTime) {
        ticks++;
        updateSolarPanel();
        chargeCreative();
        updateTargets();
        if (gameTime % Math.max(1, settings.targetSearchTicks()) == 0) {
            warnPlayers(gameTime);
        }
        if (ticks % 5 == 0) {
            concealTurrets = shouldConcealTurrets();
            BaseTierStats stats = settings.baseTier(tier);
            energy.setCapacity(stats.maxCharge());
            energy.setMaxInsert(stats.maxIo());
            updateMaxRange();
            if (ticks % 20 == 0) {
                ticks = 0;
                updateRedstoneReactor();
                targeting.setMaxRange(targeting.getMaxRange()); // clamps the range
            }
        }
    }

    // ---- World events ----

    /**
     * Replaces what is attached to the faces of the base. Call when a neighbour changes and once after loading, when
     * all neighbour blocks are loaded.
     */
    public void updateNeighbours(Map<Face, Neighbour> attached) {
        boolean wasKnown = neighboursKnown;
        int previousMaxRange = targeting.getMaxRange();
        Map<Face, TurretType> previousTurrets = getTurrets();
        neighbours.clear();
        neighbours.putAll(attached);
        neighboursKnown = true;
        Map<Face, TurretType> turrets = getTurrets();
        targets.keySet().retainAll(turrets.keySet());
        searchTicks.keySet().retainAll(turrets.keySet());
        BaseTierStats stats = settings.baseTier(tier);
        energy.setCapacity(stats.maxCharge());
        updateMaxRange();
        // 1.12.2 BlockAbstractTurretHead.onBlockPlacedBy: a newly placed turret that reaches further than the old
        // max range raises the range to the new maximum (not on the first scan after loading)
        if (wasKnown) {
            for (Map.Entry<Face, TurretType> turret : turrets.entrySet()) {
                boolean added = previousTurrets.get(turret.getKey()) != turret.getValue();
                if (added && settings.turret(turret.getValue()).baseRange() > previousMaxRange) {
                    targeting.setRange(targeting.getMaxRange());
                    break;
                }
            }
        }
    }

    /** Redstone signal changed (OMLib {@code TileEntityMachine.setRedstone}); clients show the active state. */
    public void setRedstone(boolean powered) {
        if (redstone != powered) {
            redstone = powered;
            markSync();
        }
    }

    /** The base inventory changed (ammo, upgrades or addons), e.g. through the GUI or automation. */
    public void onInventoryChanged() {
        concealTurrets = shouldConcealTurrets();
        updateMaxRange();
        markDirty();
    }

    // ---- Neighbours / turrets ----

    public Map<Face, Neighbour> getNeighbours() {
        return Collections.unmodifiableMap(neighbours);
    }

    public Map<Face, TurretType> getTurrets() {
        Map<Face, TurretType> turrets = new EnumMap<>(Face.class);
        neighbours.forEach((face, neighbour) -> {
            if (neighbour instanceof Neighbour.Turret turret) turrets.put(face, turret.type());
        });
        return turrets;
    }

    /**
     * How many more turrets of this type may be attached (1.12.2 {@code OMTUtil.getRemainingTurretSlots}): limited by
     * the tier's {@code baseMaxTurrets} and the type's {@code maxSimultaneous}.
     */
    public int getRemainingTurretSlots(TurretType type) {
        Map<Face, TurretType> turrets = getTurrets();
        long sameType = turrets.values().stream().filter(type::equals).count();
        int byTier = settings.baseTier(tier).maxTurrets() - turrets.size();
        int byType = settings.turret(type).maxSimultaneous() - (int) sameType;
        return Math.max(0, Math.min(byTier, byType));
    }

    /** A turret on a base of a lower tier breaks (1.12.2 {@code AbstractDirectedTurret.update}). */
    public boolean supportsTurret(TurretType type) {
        return tier >= type.getRequiredBaseTier();
    }

    /**
     * Max range = best {@code baseRange + range upgrades} of the attached turrets (1.12.2 {@code updateMaxRange}).
     * Skipped until the neighbours are known, so the player's range is not reset while loading.
     */
    public void updateMaxRange() {
        if (!neighboursKnown) return;
        int maxRange = 0;
        for (TurretType type : getTurrets().values()) {
            maxRange = Math.max(maxRange, getRange(type));
        }
        if (maxRange != targeting.getMaxRange()) {
            targeting.setMaxRange(maxRange);
            markSync();
        }
    }

    // ---- Ammo ----

    /**
     * The ammunition a turret type consumes from this base, or empty if it fires without ammo (energy turrets,
     * {@code doTurretsNeedAmmo} disabled, or a creative upgrade in the base).
     */
    public Optional<AmmoType> getRequiredAmmo(TurretType type) {
        return settings.turretsNeedAmmo() && !hasCreativeUpgrade() ? type.getAmmo() : Optional.empty();
    }

    /**
     * Shots available to a turret (1.12.2 {@code TurretHeadUtil.getAmmoLevel}): matching items in the base and its
     * inventory expanders; for energy turrets the stored energy divided by the power usage; unlimited if ammo is
     * disabled or the base has a creative upgrade.
     */
    public int getAmmoLevel(TurretType type) {
        Optional<AmmoType> ammo = type.getAmmo();
        if (ammo.isEmpty()) {
            int usage = settings.turret(type).powerUsage();
            return usage <= 0 ? Integer.MAX_VALUE : (int) Math.min(Integer.MAX_VALUE, energy.getAmount() / usage);
        }
        if (getRequiredAmmo(type).isEmpty()) return Integer.MAX_VALUE;
        return countAmmo(ammo.get());
    }

    public int countAmmo(AmmoType type) {
        int count = 0;
        for (ItemSlot slot : world.getAmmoSlots()) {
            if (slot.getCount() > 0 && slot.isAmmo(type)) count += slot.getCount();
        }
        return count;
    }

    /**
     * Takes the ammunition for one shot: {@code 1 + scattershot upgrades} items, all or nothing (1.12.2
     * {@code TurretHead.getAmmoStack} / {@code deductItemStackFromInventories}). With a recycler addon there is a
     * {@code recyclerNegateChance} chance that nothing is removed.
     * <p>
     * Only call for turrets with {@link #getRequiredAmmo} present.
     *
     * @return the draw, or empty if there is not enough ammo
     */
    public Optional<AmmoDraw> drawAmmo(TurretType type) {
        AmmoType ammo = getRequiredAmmo(type).orElseThrow(
                () -> new IllegalArgumentException(type + " does not need ammo"));
        int needed = getScattershotCount(type) + 1;

        List<ItemSlot> matching = new ArrayList<>();
        int available = 0;
        for (ItemSlot slot : world.getAmmoSlots()) {
            if (slot.getCount() > 0 && slot.isAmmo(ammo)) {
                matching.add(slot);
                available += slot.getCount();
                if (available >= needed) break;
            }
        }
        if (available < needed) return Optional.empty();
        int damageBonus = ammo == AmmoType.DISPOSABLE ? matching.get(0).getDisposableDamageBonus() : 0;
        Object item = matching.get(0).snapshot();

        if (hasAddon(AddonType.RECYCLER) && random.nextDouble() < settings.turret(type).recyclerNegateChance()) {
            return Optional.of(new AmmoDraw(ammo, needed, damageBonus, true, item));
        }
        int remaining = needed;
        for (ItemSlot slot : matching) {
            remaining -= slot.extract(Math.min(remaining, slot.getCount()));
            if (remaining <= 0) break;
        }
        markDirty();
        return Optional.of(new AmmoDraw(ammo, needed, damageBonus, false, item));
    }

    /** Removes {@code amount} items of a reactor fuel from the ammo slots, all or nothing. */
    private boolean extractFuel(ReactorFuel fuel) {
        List<ItemSlot> matching = new ArrayList<>();
        int available = 0;
        for (ItemSlot slot : world.getAmmoSlots()) {
            if (slot.getCount() > 0 && slot.isReactorFuel(fuel)) {
                matching.add(slot);
                available += slot.getCount();
                if (available >= 1) break;
            }
        }
        if (available < 1) return false;
        int remaining = 1;
        for (ItemSlot slot : matching) {
            remaining -= slot.extract(Math.min(remaining, slot.getCount()));
            if (remaining <= 0) break;
        }
        return true;
    }

    // ---- Upgrades and addons ----

    /** Upgrade slots usable on this tier. */
    private List<ItemSlot> usableUpgradeSlots() {
        List<ItemSlot> slots = world.getUpgradeSlots();
        return slots.subList(0, Math.min(slots.size(), BaseTier.upgradeSlots(tier)));
    }

    /** Addon slots usable on this tier. */
    private List<ItemSlot> usableAddonSlots() {
        List<ItemSlot> slots = world.getAddonSlots();
        return slots.subList(0, Math.min(slots.size(), BaseTier.addonSlots(tier)));
    }

    /** Number of upgrade items of a type (stack sizes count). */
    public int getUpgradeCount(UpgradeType type) {
        int count = 0;
        for (ItemSlot slot : usableUpgradeSlots()) {
            if (slot.getCount() > 0 && slot.getUpgrade().filter(type::equals).isPresent()) count += slot.getCount();
        }
        return count;
    }

    /** Number of addon items of a type (stack sizes count). */
    public int getAddonCount(AddonType type) {
        int count = 0;
        for (ItemSlot slot : usableAddonSlots()) {
            if (slot.getCount() > 0 && slot.getAddon().filter(type::equals).isPresent()) count += slot.getCount();
        }
        return count;
    }

    public boolean hasAddon(AddonType type) {
        return getAddonCount(type) > 0;
    }

    /** Whether a creative upgrade is installed: full energy, no ammo usage. */
    public boolean hasCreativeUpgrade() {
        return getUpgradeCount(UpgradeType.CREATIVE) > 0;
    }

    /** The creative upgrade tops up the energy storage completely (ignoring max IO). */
    private void chargeCreative() {
        if (hasCreativeUpgrade()) {
            energy.generate(energy.getFreeSpace());
        }
    }

    /**
     * Scattershot upgrades that apply to a turret (1.12.2 {@code TurretHeadUtil.getScattershotUpgrades}); always 0
     * for turrets that {@linkplain TurretType#usesScattershot() ignore them}.
     */
    public int getScattershotCount(TurretType type) {
        return type.usesScattershot() ? getUpgradeCount(UpgradeType.SCATTERSHOT) : 0;
    }

    /** 1.12.2 {@code TurretHeadUtil.getAmpLevel}. */
    public int getDamageAmpLevel() {
        return getAddonCount(AddonType.DAMAGE_AMP);
    }

    /** Value of {@link #getLootingLevel} when turret kills drop nothing. */
    public static final int NO_LOOT = -1;

    /**
     * Loot of entities killed by this base's turrets (user decision, inverting 1.12.2): without a fake drops addon
     * they drop nothing; the first addon enables player-kill loot, each further addon adds one level of Looting,
     * up to {@code maxLooting} (the enchantment's maximum level).
     *
     * @return {@link #NO_LOOT}, or the Looting level (0 for a single addon)
     */
    public int getLootingLevel(int maxLooting) {
        int addons = getAddonCount(AddonType.FAKE_DROPS);
        return addons == 0 ? NO_LOOT : Math.min(addons - 1, Math.max(0, maxLooting));
    }

    /** 1.12.2 {@code TurretHeadUtil.hasConcealmentAddon}. */
    public boolean shouldConcealTurrets() {
        return tier > 1 && (hasAddon(AddonType.CONCEALER) || settings.turretsConcealWithoutAddon());
    }

    /** Cached result of {@link #shouldConcealTurrets()}, refreshed every 5 ticks and on inventory changes. */
    public boolean isConcealingTurrets() {
        return concealTurrets;
    }

    // ---- Upgraded turret stats ----

    /** Turret range: {@code baseRange + rangeUpgrade * range upgrades} (1.12.2 {@code getRangeUpgrades}). */
    public int getRange(TurretType type) {
        TurretStats stats = settings.turret(type);
        return stats.baseRange() + stats.rangeUpgrade() * getUpgradeCount(UpgradeType.RANGE);
    }

    /**
     * Energy per shot: {@code round(powerUsage * (1 - efficiency) * (1 + scattershot))}
     * (1.12.2 {@code TurretHead.getPowerRequiredForNextShot}).
     */
    public int getPowerPerShot(TurretType type) {
        TurretStats stats = settings.turret(type);
        double efficiency = stats.efficiencyUpgrade() * getUpgradeCount(UpgradeType.EFFICIENCY);
        return (int) Math.round(stats.powerUsage() * (1 - efficiency) * (1 + getScattershotCount(type)));
    }

    /**
     * Ticks between shots: {@code baseFireRate / (1 + fireRateUpgrade * fire rate upgrades)}
     * (1.12.2 {@code TurretHead.isOnCooldown}). A turret may fire once its tick counter reaches this value.
     */
    public double getFireCooldownTicks(TurretType type) {
        TurretStats stats = settings.turret(type);
        return stats.baseFireRate() / (1 + stats.fireRateUpgrade() * getUpgradeCount(UpgradeType.FIRE_RATE));
    }

    /**
     * Inaccuracy in percent: {@code base / (1 + accuracy)^1.5 * (1 + scattershot / 10)}
     * (1.12.2 {@code TurretHead.getActualTurretAccuracyDeviation}).
     */
    public double getAccuracyDeviation(TurretType type) {
        TurretStats stats = settings.turret(type);
        double accuracy = stats.accuracyUpgrade() * getUpgradeCount(UpgradeType.ACCURACY);
        return stats.baseAccuracyDeviation() / Math.pow(1 + accuracy, 1.5D) * (1 + getScattershotCount(type) / 10F);
    }

    /** Damage bonus factor of the damage amp addon: {@code damageAmp * amp level} (1.12.2 projectile code). */
    public double getDamageAmpBonus(TurretType type) {
        return settings.turret(type).damageAmp() * getDamageAmpLevel();
    }

    // ---- Energy ----

    public EnergyStore getEnergy() {
        return energy;
    }

    /** Whether there is enough energy for the next shot (1.12.2 power check before firing). */
    public boolean hasEnergyForShot(TurretType type) {
        return energy.getAmount() >= getPowerPerShot(type);
    }

    /** Consumes the energy for one shot; false (nothing consumed) if there is not enough. */
    public boolean consumeShotEnergy(TurretType type) {
        return energy.consume(getPowerPerShot(type));
    }

    /**
     * 1.12.2 {@code TurretBase.updateRedstoneReactor}: burns a redstone block if its full output fits, otherwise
     * a redstone dust if that fits.
     */
    private void updateRedstoneReactor() {
        if (!hasAddon(AddonType.REDSTONE_REACTOR)) return;
        int generation = settings.redstoneReactorGeneration();
        for (ReactorFuel fuel : new ReactorFuel[]{ReactorFuel.REDSTONE_BLOCK, ReactorFuel.REDSTONE}) {
            long output = (long) generation * fuel.getMultiplier();
            if (output < energy.getFreeSpace() && extractFuel(fuel)) {
                energy.generate(output);
                return;
            }
        }
    }

    /** 1.12.2 {@code TurretHeadUtil.updateSolarPanelAddon}; inserted like external energy (limited by max IO). */
    private void updateSolarPanel() {
        if (hasAddon(AddonType.SOLAR_PANEL) && world.canGenerateSolarPower()) {
            energy.insert(settings.solarPanelGeneration(), false);
        }
    }

    // ---- Redstone mode ----

    public boolean isActive() {
        return mode.isActive(redstone);
    }

    public boolean isRedstonePowered() {
        return redstone;
    }

    public MachineMode getMode() {
        return mode;
    }

    /** 1.12.2 {@code MessageToggleMode}: cycles always on, always off, inverted, non-inverted. */
    public void cycleMode() {
        setMode(mode.next());
    }

    public void setMode(MachineMode mode) {
        this.mode = mode;
        markSync();
    }

    // ---- Targeting settings ----

    public TargetingSettings getTargetingSettings() {
        return targeting;
    }

    public void setTargetPlayers(boolean value) {
        targeting.setTargetPlayers(value);
        markSync();
    }

    public void setTargetMobs(boolean value) {
        targeting.setTargetMobs(value);
        markSync();
    }

    public void setTargetPassive(boolean value) {
        targeting.setTargetPassive(value);
        markSync();
    }

    /** 1.12.2 {@code setRange}: refreshes the max range first; values outside {@code [0, maxRange]} are ignored. */
    public void setRange(int range) {
        updateMaxRange();
        targeting.setRange(range);
        markSync();
    }

    public boolean isMultiTargeting() {
        return multiTargeting;
    }

    public void setMultiTargeting(boolean multiTargeting) {
        this.multiTargeting = multiTargeting;
        markSync();
    }

    /**
     * Living entities in the cube {@code base ± (range + 1)} (1.12.2 {@code getEntitiesWithinRange}).
     */
    public List<TargetEntity> getEntitiesWithinRange() {
        return world.getLivingEntities(Box.around(world.getPosition(), targeting.getRange() + 1));
    }

    // ---- Targeting ----

    /**
     * Per-turret target tracking, from 1.12.2 {@code AbstractDirectedTurret.update} and
     * {@code TurretHead.targetingChecks}:
     * <ul>
     *     <li>inactive base: all targets are dropped;</li>
     *     <li>not enough energy for the turret's next shot: nothing happens;</li>
     *     <li>without a target the turret searches every {@code turretTargetSearchTicks} + 1 ticks;</li>
     *     <li>with a target it re-checks it every tick and searches a new one as soon as the old one is no longer
     *     valid.</li>
     * </ul>
     * 1.12.2 only dropped a target when it died or went out of sight; here it is dropped as soon as it is no
     * longer a valid target at all (out of range, became trusted, setting changed, …).
     */
    private void updateTargets() {
        if (!isActive()) {
            targets.clear();
            return;
        }
        for (Map.Entry<Face, TurretType> turret : getTurrets().entrySet()) {
            Face face = turret.getKey();
            TurretType type = turret.getValue();
            if (!hasEnergyForShot(type)) continue;
            boolean hasTarget = targets.containsKey(face);
            int waited = searchTicks.getOrDefault(face, 0);
            if (!hasTarget && waited < settings.targetSearchTicks()) {
                searchTicks.put(face, waited + 1);
                continue;
            }
            searchTicks.put(face, 0);
            if (getTarget(face).isEmpty()) {
                acquireTarget(face, type);
            }
        }
    }

    private void acquireTarget(Face face, TurretType type) {
        List<Integer> takenByOthers = new ArrayList<>();
        targets.forEach((otherFace, id) -> {
            if (otherFace != face) takenByOthers.add(id);
        });
        Optional<TargetEntity> target = targetSelector.findBest(type, world.getPosition().relative(face), face, targeting,
                multiTargeting, takenByOthers);
        if (target.isPresent()) {
            targets.put(face, target.get().getId());
        } else {
            targets.remove(face);
        }
    }

    /**
     * The current target of the turret on a face, if it is still loaded and valid. Invalid targets are dropped.
     */
    public Optional<TargetEntity> getTarget(Face face) {
        Integer id = targets.get(face);
        TurretType type = getTurrets().get(face);
        if (id == null || type == null) return Optional.empty();
        Optional<TargetEntity> target = world.getEntity(id)
                .filter(entity -> targetSelector.isValidTarget(entity, type, world.getPosition().relative(face), face,
                        targeting));
        if (target.isEmpty()) targets.remove(face);
        return target;
    }

    /** Ids of all current targets (for multi-targeting and debugging). */
    public Map<Face, Integer> getTargetIds() {
        return Collections.unmodifiableMap(targets);
    }

    public TargetSelector getTargetSelector() {
        return targetSelector;
    }

    // ---- Player warnings ----

    /**
     * 1.12.2 {@code TurretHeadUtil.warnPlayers}, called per turret while the base is active and has enough energy for
     * a shot: players in the cube {@code turret ± (baseRange + warning distance)} who are not owner, trusted or in
     * creative mode are warned once every {@value #WARNING_COOLDOWN_TICKS} ticks.
     */
    void warnPlayers(long gameTime) {
        if (!targeting.isTargetPlayers() || !settings.globalCanTargetPlayers() || !isActive()) return;
        warnedPlayers.values().removeIf(until -> until < gameTime);
        for (Map.Entry<Face, TurretType> turret : getTurrets().entrySet()) {
            if (!hasEnergyForShot(turret.getValue())) continue;
            int radius = settings.turret(turret.getValue()).baseRange() + settings.turretWarningDistance();
            Box box = Box.around(world.getPosition().relative(turret.getKey()), radius);
            for (TargetEntity entity : world.getLivingEntities(box)) {
                Optional<PlayerInfo> player = entity.getPlayer();
                if (player.isEmpty() || entity.isCreativeOrSpectator()) continue;
                if (trustManager.isOwnerOrTrusted(player.get().identity())) continue;
                if (warnedPlayers.containsKey(player.get().uuid())) continue;
                world.warnPlayer(entity);
                warnedPlayers.put(player.get().uuid(), gameTime + WARNING_COOLDOWN_TICKS);
            }
        }
    }

    // ---- Statistics ----

    public int getKills() {
        return kills;
    }

    public int getPlayerKills() {
        return playerKills;
    }

    public void increaseKillCounter() {
        kills++;
        markSync();
    }

    public void increasePlayerKillCounter() {
        playerKills++;
        markSync();
    }

    // ---- Other state ----

    public int getTier() {
        return tier;
    }

    public TrustManager getTrustManager() {
        return trustManager;
    }

    /** Persistent state for saving; see {@link #loadState}. */
    public State getState() {
        return new State(energy.getAmount(), mode, redstone, targeting.isTargetPlayers(), targeting.isTargetMobs(),
                targeting.isTargetPassive(), targeting.getRange(), targeting.getMaxRange(), multiTargeting, kills,
                playerKills);
    }

    /** Restores state saved with {@link #getState()}. Does not notify the listener. */
    public void loadState(State state) {
        energy.setAmountUnchecked(state.energy());
        mode = state.mode();
        redstone = state.redstone();
        targeting = new TargetingSettings(state.targetPlayers(), state.targetMobs(), state.targetPassive(),
                state.range(), state.maxRange());
        multiTargeting = state.multiTargeting();
        kills = state.kills();
        playerKills = state.playerKills();
    }

    /**
     * Saved base state (the trust list is saved separately through {@link #getTrustManager()}).
     */
    public record State(long energy, MachineMode mode, boolean redstone, boolean targetPlayers, boolean targetMobs,
                        boolean targetPassive, int range, int maxRange, boolean multiTargeting, int kills,
                        int playerKills) {
        /** State of a newly placed base. */
        public static State defaults() {
            TargetingSettings targeting = TargetingSettings.defaults();
            return new State(0, MachineMode.INVERTED, false, targeting.isTargetPlayers(), targeting.isTargetMobs(),
                    targeting.isTargetPassive(), 0, 0, false, 0, 0);
        }
    }
}
