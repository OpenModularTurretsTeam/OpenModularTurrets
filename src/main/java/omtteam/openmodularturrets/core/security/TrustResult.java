package omtteam.openmodularturrets.core.security;

/**
 * Outcome of a change to a trust list.
 */
public enum TrustResult {
    SUCCESS,
    /** The acting player may not manage trusted players. */
    NO_PERMISSION,
    /** The player is not on the trust list. */
    NOT_FOUND,
    ALREADY_TRUSTED,
    /** The owner cannot be put on their own trust list. */
    IS_OWNER,
    /** The access level is already the lowest / highest one. */
    OUT_OF_RANGE;

    public boolean isSuccess() {
        return this == SUCCESS;
    }
}
