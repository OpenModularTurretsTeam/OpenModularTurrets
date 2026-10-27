package omtteam.openmodularturrets.core.settings;

/**
 * Configurable stats of a base tier (1.12.2 {@code BaseSetting}; hardness and blast resistance are block properties
 * and stay in the Minecraft layer).
 *
 * @param ammoStacks stacks each ammo slot holds without inventory expanders ({@code baseAmmoStacks}, new in 4.x)
 */
public record BaseTierStats(int maxCharge, int maxIo, int maxTurrets, int ammoStacks) {
}
