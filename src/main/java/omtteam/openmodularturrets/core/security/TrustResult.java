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
    /** The access level is already the lowest / highest one, or is not part of the block's access profile. */
    OUT_OF_RANGE
}
