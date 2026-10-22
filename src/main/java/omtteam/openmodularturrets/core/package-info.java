/**
 * Core game logic of Open Modular Turrets: turret base behaviour, targeting, turret types and the abstract target
 * representation. Ported from the 1.20.1+ port and rewritten for Java 8; the shared rules (trust lists, access
 * levels, energy buffer, machine mode, targeting settings) live in OMLib's {@code omtteam.omlib.core}.
 * <p>
 * Everything in this package (and its subpackages) must stay free of Minecraft, Forge and other game imports so it
 * can be unit-tested in isolation. The game is reached only through small interfaces (adapters) that the
 * Minecraft layer implements.
 */
package omtteam.openmodularturrets.core;
