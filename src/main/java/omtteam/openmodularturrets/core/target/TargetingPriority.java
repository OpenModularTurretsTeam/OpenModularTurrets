package omtteam.openmodularturrets.core.target;

import omtteam.openmodularturrets.core.world.BlockPosition;

/**
 * Factors that decide which valid target a turret prefers (1.12.2 {@code EnumTargetingPriority}). Each turret type
 * has one weight per factor; the score of an entity is the <b>sum</b> of all weighted factors, the highest score wins.
 * Negative weights invert a factor, a weight of 0 ignores it.
 * <p>
 * 1.12.2 multiplied the factors, which broke the ranking: a single zero factor (full health, weight 0) zeroed every
 * score, negative weights flipped the sign, and the constant -100 for non-players inverted the whole mob ranking
 * (user decision: fix).
 */
public enum TargetingPriority {
    MAX_HP,
    HP_REMAINING,
    DISTANCE,
    ARMOR,
    PLAYER;

    /**
     * 1.12.2 {@code getValueByPriority}. Distance is measured from the turret's block corner; 1.12.2 used a cube
     * root instead of a square root there by mistake (user decision: real distance).
     */
    public long value(TargetEntity entity, BlockPosition turret, int weight) {
        switch (this) {
            case MAX_HP:
                return clamp(entity.getHealth()) * weight;
            case HP_REMAINING:
                return clamp((double) entity.getMaxHealth() - entity.getHealth()) * weight;
            case DISTANCE:
                return clamp(distance(entity, turret)) * weight;
            case ARMOR:
                return (clamp(entity.getArmor()) + 1) * weight;
            case PLAYER:
                return entity.isPlayer() ? 100L * weight : 0L;
            default:
                throw new IllegalStateException("Unknown priority " + this);
        }
    }

    /** Sum of all weighted factors (in long, so it cannot overflow). */
    public static long score(TargetEntity entity, BlockPosition turret, int[] weights) {
        long score = 0;
        for (TargetingPriority priority : values()) {
            score += priority.value(entity, turret, weights[priority.ordinal()]);
        }
        return score;
    }

    /**
     * Floors a factor and limits it to the int range, so factor * weight and the sum of five such terms always fit
     * in a long (modded health values can be huge).
     */
    private static long clamp(double value) {
        return (long) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, Math.floor(value)));
    }

    private static double distance(TargetEntity entity, BlockPosition turret) {
        double dx = turret.x() - entity.getX();
        double dy = turret.y() - entity.getY();
        double dz = turret.z() - entity.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
