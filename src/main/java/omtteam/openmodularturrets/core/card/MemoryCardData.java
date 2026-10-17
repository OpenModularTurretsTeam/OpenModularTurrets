package omtteam.openmodularturrets.core.card;

import omtteam.openmodularturrets.core.base.MachineMode;
import omtteam.openmodularturrets.core.camo.CamoLight;
import omtteam.openmodularturrets.core.security.TrustedPlayer;

import java.util.List;
import java.util.Objects;

/**
 * What a turret memory card stores (1.12.2 {@code UsableMetaItem} meta 2): the settings of a turret base or of a
 * camouflage trap, to be written to other ones.
 * <p>
 * A card from a base holds its {@link BaseSettings}, the {@link TrustSettings} and, if camouflage is enabled, its
 * {@link CamoSettings}; a card from a trap holds the trust and camouflage settings. Fields documented as nullable are
 * {@code null} when absent.
 *
 * @param source where the card was written
 * @param base   targeting and mode; present exactly on cards from a turret base
 * @param trust  the trust list of the source
 * @param camo   disguise and light; nullable on cards from a base (camouflage disabled), present on trap cards
 */
public record MemoryCardData(Source source, BaseSettings base, TrustSettings trust, CamoSettings camo) {
    public enum Source {
        TURRET_BASE,
        CAMO_TRAP
    }

    public MemoryCardData {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(trust, "trust");
        if ((source == Source.TURRET_BASE) == (base == null)) {
            throw new IllegalArgumentException("Base settings belong to cards from a turret base only");
        }
        if (source == Source.CAMO_TRAP && camo == null) {
            throw new IllegalArgumentException("A card from a trap stores its camouflage");
        }
    }

    /**
     * Settings of a turret base.
     *
     * @param range the chosen targeting range; the target base clamps it to its own maximum
     */
    public record BaseSettings(MachineMode mode, boolean multiTargeting, boolean targetPlayers, boolean targetMobs,
                               boolean targetPassive, int range) {
        public BaseSettings {
            Objects.requireNonNull(mode, "mode");
        }
    }

    /**
     * The trust list of a base or trap, without the owner. The target clamps the levels to the ones it uses (a trap
     * only knows "trusted" and "admin").
     */
    public record TrustSettings(List<TrustedPlayer> trustedPlayers) {
        public TrustSettings {
            trustedPlayers = trustedPlayers.stream().map(TrustedPlayer::copy).toList();
        }
    }

    /**
     * Camouflage settings.
     *
     * @param disguise registry id of the disguise block (e.g. {@code minecraft:stone}); nullable: no disguise
     * @param light    light settings; nullable: the source has no adjustable light (bases below tier 4)
     */
    public record CamoSettings(String disguise, CamoLight light) {
    }
}
