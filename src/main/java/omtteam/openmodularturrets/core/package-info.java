/**
 * Core game logic of Open Modular Turrets: turret base behaviour, the trusted player permission system and the
 * abstract target representation.
 * <p>
 * Everything in this package (and its subpackages) must stay free of Minecraft, Fabric and other game imports so it
 * can be unit-tested in isolation. The game is reached only through small interfaces (adapters) that the
 * Minecraft layer implements.
 */
package omtteam.openmodularturrets.core;
