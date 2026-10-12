package omtteam.openmodularturrets.security;

import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.security.SecurityEnvironment;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * {@link SecurityEnvironment} backed by a level's scoreboard and the mod config.
 */
public class LevelSecurityEnvironment implements SecurityEnvironment {
    private final Supplier<Level> level;

    /** @param level supplies the current level; may return {@code null} while the block entity is loading */
    public LevelSecurityEnvironment(Supplier<Level> level) {
        this.level = level;
    }

    @Override
    public Optional<String> getTeam(UUID uuid, String name) {
        Level current = level.get();
        if (current == null) return Optional.empty();
        PlayerTeam team = current.getScoreboard().getPlayersTeam(name);
        return team == null ? Optional.empty() : Optional.of(team.getName());
    }

    @Override
    public boolean operatorsCanAccessOwnedBlocks() {
        return OMTConfig.get().general.canOPAccessOwnedBlocks;
    }

    @Override
    public boolean turretsDamageTrustedPlayers() {
        return OMTConfig.get().turrets.turretDamageTrustedPlayers;
    }
}
