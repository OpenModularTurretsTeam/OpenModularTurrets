package omtteam.openmodularturrets.core.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Owner and trust list of a base, and every permission decision based on them. Ported from OMLib
 * {@code TrustedPlayersManager}, {@code ITrustedPlayersManager} and {@code PlayerUtil}, and from 1.12.2
 * {@code OMTUtil.canDamagePlayer} / {@code TurretTargetingUtils.validTarget}.
 * <p>
 * Rules:
 * <ul>
 *     <li>The owner always has {@link AccessLevel#ADMIN} rights. Nobody can change the owner through this class
 *     except the placement logic ({@link #setOwner}).</li>
 *     <li>Trusted players have the rights of their {@link AccessLevel} and the levels below it.</li>
 *     <li>Members of the owner's scoreboard team count as trusted at {@link AccessLevel#NONE} unless they are on
 *     the list with a higher level, so turrets do not shoot them.</li>
 *     <li>Operators may open every base and are never targeted if
 *     {@link SecurityEnvironment#operatorsCanAccessOwnedBlocks()} is set.</li>
 *     <li>Only admins (owner or {@link AccessLevel#ADMIN}) may change the trust list.</li>
 * </ul>
 */
public class TrustManager {
    private final SecurityEnvironment environment;
    private final Runnable onChange;
    private final AccessProfile profile;
    private final List<TrustedPlayer> trustedPlayers = new ArrayList<>();
    private PlayerIdentity owner;

    /** A manager that uses all access levels. */
    public TrustManager(SecurityEnvironment environment, Runnable onChange) {
        this(environment, onChange, AccessProfile.ALL);
    }

    /** @param profile the access levels this manager uses; stored and requested levels are clamped to it */
    public TrustManager(SecurityEnvironment environment, Runnable onChange, AccessProfile profile) {
        this.environment = Objects.requireNonNull(environment, "environment");
        this.onChange = Objects.requireNonNull(onChange, "onChange");
        this.profile = Objects.requireNonNull(profile, "profile");
    }

    public AccessProfile getProfile() {
        return profile;
    }

    // ---- State ----

    /** The owner, or {@code null} if the base has none yet (it then drops itself). */
    public PlayerIdentity getOwner() {
        return owner;
    }

    /** Sets the owner when the base is placed or loaded. Not reachable by players. */
    public void setOwner(PlayerIdentity owner) {
        this.owner = Objects.requireNonNull(owner, "owner");
        trustedPlayers.removeIf(entry -> owner.is(entry.getUuid()));
        onChange.run();
    }

    public List<TrustedPlayer> getTrustedPlayers() {
        return Collections.unmodifiableList(trustedPlayers);
    }

    public Optional<TrustedPlayer> getTrustedPlayer(UUID uuid) {
        for (TrustedPlayer entry : trustedPlayers) {
            if (entry.getUuid().equals(uuid)) return Optional.of(entry);
        }
        return Optional.empty();
    }

    /**
     * Replaces the whole state without permission checks (loading from disk, memory cards). Entries for the owner
     * and duplicate UUIDs are skipped; levels outside the {@link #getProfile() profile} are clamped down.
     */
    public void load(PlayerIdentity owner, List<TrustedPlayer> entries) {
        this.owner = owner;
        trustedPlayers.clear();
        for (TrustedPlayer entry : entries) {
            boolean isOwner = owner != null && owner.is(entry.getUuid());
            if (!isOwner && getTrustedPlayer(entry.getUuid()).isEmpty()) {
                trustedPlayers.add(new TrustedPlayer(entry.getIdentity(), profile.clamp(entry.getAccessLevel())));
            }
        }
        onChange.run();
    }

    // ---- Queries ----

    public boolean isOwner(UUID uuid) {
        return owner != null && owner.is(uuid);
    }

    /** OMLib {@code isPlayerOwner}. */
    public boolean isOwner(PlayerInfo player) {
        return isOwner(player.uuid());
    }

    /**
     * Whether the player is in the owner's scoreboard team. Players without a team are never team members.
     */
    public boolean isTeamMember(PlayerIdentity player) {
        if (owner == null || owner.is(player.uuid())) return false;
        Optional<String> ownerTeam = environment.getTeam(owner.uuid(), owner.name());
        if (ownerTeam.isEmpty() || ownerTeam.get().isEmpty()) return false;
        return ownerTeam.equals(environment.getTeam(player.uuid(), player.name()));
    }

    /**
     * Trust level of a player, ignoring ownership and operator rights: the list entry, otherwise
     * {@link AccessLevel#NONE} for members of the owner's team, otherwise empty.
     */
    public Optional<AccessLevel> getTrustLevel(PlayerIdentity player) {
        Optional<TrustedPlayer> entry = getTrustedPlayer(player.uuid());
        return entry.map(TrustedPlayer::getAccessLevel).or(()
                -> isTeamMember(player) ? Optional.of(AccessLevel.NONE) : Optional.empty());
    }

    /**
     * Effective access level of a player (OMLib {@code PlayerUtil.getPlayerAccessLevel}); empty if the player has
     * no relation to the base at all. Owner: {@link AccessLevel#ADMIN}; operators (if allowed): at least
     * {@link AccessLevel#OPEN_GUI}.
     */
    public Optional<AccessLevel> getAccessLevel(PlayerInfo player) {
        if (isOwner(player)) return Optional.of(AccessLevel.ADMIN);
        Optional<AccessLevel> level = getTrustLevel(player.identity());
        if (isPrivilegedOperator(player) && level.map(l -> !l.isAtLeast(AccessLevel.OPEN_GUI)).orElse(true)) {
            return Optional.of(profile.clamp(AccessLevel.OPEN_GUI));
        }
        return level;
    }

    public boolean hasAccess(PlayerInfo player, AccessLevel required) {
        return getAccessLevel(player).map(level -> level.isAtLeast(required)).orElse(false);
    }

    /** OMLib {@code canPlayerAccessBlock}: open the GUI and inventory. */
    public boolean canOpenGui(PlayerInfo player) {
        return hasAccess(player, AccessLevel.OPEN_GUI);
    }

    /** OMLib {@code canPlayerChangeSetting}: targeting toggles, targeting type, multi-targeting. */
    public boolean canChangeSettings(PlayerInfo player) {
        return hasAccess(player, AccessLevel.CHANGE_SETTINGS);
    }

    /**
     * OMLib {@code isPlayerAdmin}: trust list, redstone mode, range, light settings, dropping the base or turrets.
     */
    public boolean isAdmin(PlayerInfo player) {
        return hasAccess(player, AccessLevel.ADMIN);
    }

    /** Owner-only actions (OMLib camo handling, removing attached expanders). */
    public boolean canPerformOwnerAction(PlayerInfo player) {
        return isOwner(player);
    }

    /**
     * Whether turrets must never select this player as a target (1.12.2 {@code TurretTargetingUtils.validTarget}):
     * owner, trusted (any level, including team members), privileged operators and players that may not be
     * damaged.
     */
    public boolean isProtectedFromTargeting(PlayerInfo player) {
        return isOwnerOrTrusted(player.identity()) || isPrivilegedOperator(player) || !canDamagePlayer(player.identity());
    }

    /**
     * Whether a player (or a pet owned by that player) counts as friendly: owner or trusted at any level
     * (1.12.2 pet check in {@code validTarget}). Works for offline players.
     */
    public boolean isOwnerOrTrusted(PlayerIdentity player) {
        return isOwner(player.uuid()) || getTrustLevel(player).isPresent();
    }

    /**
     * Whether a projectile of this base may hurt the player (1.12.2 {@code OMTUtil.canDamagePlayer}). The owner and
     * the owner's team are never hurt; listed trusted players only if {@code turretDamageTrustedPlayers} is set.
     */
    public boolean canDamagePlayer(PlayerIdentity player) {
        if (isOwner(player.uuid()) || isTeamMember(player)) return false;
        return environment.turretsDamageTrustedPlayers() || getTrustedPlayer(player.uuid()).isEmpty();
    }

    private boolean isPrivilegedOperator(PlayerInfo player) {
        return player.operator() && environment.operatorsCanAccessOwnedBlocks();
    }

    // ---- Changes by players (all require admin rights) ----

    /**
     * Adds a player at {@link AccessLevel#NONE} (OMLib {@code addTrustedPlayer}). The caller resolves the name to a
     * UUID first.
     */
    public TrustResult addTrustedPlayer(PlayerInfo actor, PlayerIdentity player) {
        if (!isAdmin(actor)) return TrustResult.NO_PERMISSION;
        if (isOwner(player.uuid())) return TrustResult.IS_OWNER;
        if (getTrustedPlayer(player.uuid()).isPresent()) return TrustResult.ALREADY_TRUSTED;
        trustedPlayers.add(new TrustedPlayer(player, AccessLevel.NONE));
        onChange.run();
        return TrustResult.SUCCESS;
    }

    public TrustResult removeTrustedPlayer(PlayerInfo actor, UUID player) {
        if (!isAdmin(actor)) return TrustResult.NO_PERMISSION;
        if (!trustedPlayers.removeIf(entry -> entry.getUuid().equals(player))) return TrustResult.NOT_FOUND;
        onChange.run();
        return TrustResult.SUCCESS;
    }

    /**
     * Raises ({@code +1}) or lowers ({@code -1}) the level of a trusted player by one step of the profile (OMLib
     * {@code changePermission(String, int)}).
     */
    public TrustResult changeAccessLevel(PlayerInfo actor, UUID player, int delta) {
        if (!isAdmin(actor)) return TrustResult.NO_PERMISSION;
        Optional<TrustedPlayer> entry = getTrustedPlayer(player);
        if (entry.isEmpty()) return TrustResult.NOT_FOUND;
        AccessLevel newLevel = profile.step(entry.get().getAccessLevel(), delta);
        if (newLevel == null) return TrustResult.OUT_OF_RANGE;
        entry.get().setAccessLevel(newLevel);
        onChange.run();
        return TrustResult.SUCCESS;
    }

    public TrustResult setAccessLevel(PlayerInfo actor, UUID player, AccessLevel level) {
        if (!isAdmin(actor)) return TrustResult.NO_PERMISSION;
        if (!profile.allows(level)) return TrustResult.OUT_OF_RANGE;
        Optional<TrustedPlayer> entry = getTrustedPlayer(player);
        if (entry.isEmpty()) return TrustResult.NOT_FOUND;
        entry.get().setAccessLevel(level);
        onChange.run();
        return TrustResult.SUCCESS;
    }

    /**
     * Updates the stored display name of the owner or a trusted player after a rename. Names are display-only.
     */
    public void refreshName(PlayerInfo player) {
        boolean changed = false;
        if (owner != null && owner.is(player.uuid()) && !owner.name().equals(player.name())) {
            owner = owner.withName(player.name());
            changed = true;
        }
        Optional<TrustedPlayer> entry = getTrustedPlayer(player.uuid());
        if (entry.isPresent() && !entry.get().getName().equals(player.name())) {
            entry.get().setName(player.name());
            changed = true;
        }
        if (changed) onChange.run();
    }
}
