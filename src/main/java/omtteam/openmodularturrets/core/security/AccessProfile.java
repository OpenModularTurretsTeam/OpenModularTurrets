package omtteam.openmodularturrets.core.security;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * The access levels a block uses. Turret bases know all four; a block that only needs "trusted" and "admin" (the
 * camouflage trap) uses {@link #TRUSTED_AND_ADMIN}. A {@link TrustManager} with a profile only hands out, accepts and
 * loads levels of its profile: levels of other origins (saved data, memory cards) are clamped down to the nearest
 * allowed one.
 * <p>
 * Every profile contains {@link AccessLevel#NONE} (the baseline of new trusted players and team members) and
 * {@link AccessLevel#ADMIN} (needed to manage the list).
 */
public final class AccessProfile {
    public static final AccessProfile ALL = of(AccessLevel.values());
    /** Trusted players (walk through, no rights) and admins (manage the block). */
    public static final AccessProfile TRUSTED_AND_ADMIN = of(AccessLevel.NONE, AccessLevel.ADMIN);

    private final List<AccessLevel> levels;

    private AccessProfile(Set<AccessLevel> allowed) {
        if (!allowed.contains(AccessLevel.NONE) || !allowed.contains(AccessLevel.ADMIN)) {
            throw new IllegalArgumentException("A profile needs at least the levels NONE and ADMIN: " + allowed);
        }
        this.levels = List.copyOf(new ArrayList<>(allowed));
    }

    public static AccessProfile of(AccessLevel... allowed) {
        Set<AccessLevel> set = EnumSet.noneOf(AccessLevel.class);
        set.addAll(List.of(allowed));
        return new AccessProfile(set);
    }

    /** The allowed levels in increasing order. */
    public List<AccessLevel> levels() {
        return levels;
    }

    public boolean allows(AccessLevel level) {
        return levels.contains(level);
    }

    /** The highest allowed level that is not above {@code level} (always exists: {@code NONE} is allowed). */
    public AccessLevel clamp(AccessLevel level) {
        AccessLevel result = AccessLevel.NONE;
        for (AccessLevel allowed : levels) {
            if (allowed.isAtLeast(result) && level.isAtLeast(allowed)) result = allowed;
        }
        return result;
    }

    /**
     * The allowed level {@code steps} places above (positive) or below (negative) the clamped {@code level}, or
     * {@code null} if that leaves the profile.
     */
    public AccessLevel step(AccessLevel level, int steps) {
        int index = levels.indexOf(clamp(level)) + steps;
        return index >= 0 && index < levels.size() ? levels.get(index) : null;
    }
}
