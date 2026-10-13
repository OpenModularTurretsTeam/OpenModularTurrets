package omtteam.openmodularturrets.core.settings;

/**
 * Configurable stats of a base tier (1.12.2 {@code BaseSetting}; hardness and blast resistance are block properties
 * and stay in the Minecraft layer).
 */
public record BaseTierStats(int maxCharge, int maxIo, int maxTurrets) {
}
