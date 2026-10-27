package omtteam.openmodularturrets.config;

import net.minecraftforge.common.config.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ConfigMigrationTest {
    private static final String OLD = "turrets.relativistic turret";
    private static final String NEW = "turrets.slowing turret";

    @TempDir
    Path directory;
    private File file;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        ForgeConfigFiles.setMinecraftHome(directory.toFile());
        file = directory.resolve("openmodularturrets.cfg").toFile();
    }

    private void write(String category, String key, int value) {
        Configuration config = new Configuration(file);
        config.get(category, key, value);
        config.save();
    }

    @Test
    void movesTheRelativisticTurretSectionOf3x() {
        write(OLD, "baseRange", 33);
        write("turrets.laser turret", "baseRange", 25);

        ConfigMigration.migrate(file);

        Configuration config = new Configuration(file);
        assertFalse(config.hasCategory(OLD));
        assertEquals(33, config.getCategory(NEW).get("baseRange").getInt());
        assertEquals(25, config.getCategory("turrets.laser turret").get("baseRange").getInt(), "other sections stay");
    }

    @Test
    void anExistingSlowingTurretSectionWins() {
        write(OLD, "baseRange", 33);
        write(NEW, "baseRange", 20);

        ConfigMigration.migrate(file);

        Configuration config = new Configuration(file);
        assertFalse(config.hasCategory(OLD), "the old section is removed anyway");
        assertEquals(20, config.getCategory(NEW).get("baseRange").getInt());
    }

    @Test
    void aConfigAtTheCurrentVersionIsNotRewritten() throws Exception {
        write(NEW, "baseRange", 20);
        write(ConfigMigration.VERSION_CATEGORY, ConfigMigration.VERSION_KEY, ConfigMigrationSteps.CURRENT_VERSION);
        byte[] before = Files.readAllBytes(file.toPath());

        ConfigMigration.migrate(file);

        assertArrayEquals(before, Files.readAllBytes(file.toPath()));
    }

    @Test
    void filesWithoutAVersionGetTheCurrentOne() {
        write(NEW, "baseRange", 20);

        ConfigMigration.migrate(file);

        assertEquals(ConfigMigrationSteps.CURRENT_VERSION, version());
        assertEquals(20, new Configuration(file).getCategory(NEW).get("baseRange").getInt());
    }

    @Test
    void oldBaseChargeDefaultsAreRaised() {
        int[] old = {500, 50_000, 150_000, 500_000, 10_000_000};
        for (int tier = 1; tier <= 5; tier++) {
            write("bases.tier " + tier, "baseMaxCharge", old[tier - 1]);
        }

        ConfigMigration.migrate(file);

        int[] expected = {5_000, 500_000, 1_500_000, 10_000_000, 100_000_000};
        for (int tier = 1; tier <= 5; tier++) {
            assertEquals(expected[tier - 1], baseCharge(tier), "tier " + tier);
        }
        assertEquals(ConfigMigrationSteps.CURRENT_VERSION, version());
    }

    @Test
    void chosenBaseChargesStay() {
        write("bases.tier 1", "baseMaxCharge", 800);
        write("bases.tier 2", "baseMaxCharge", 500);

        ConfigMigration.migrate(file);

        assertEquals(800, baseCharge(1));
        assertEquals(500, baseCharge(2), "tier 1's old default is no default of tier 2");
    }

    @Test
    void runsOncePerFile() {
        write(ConfigMigration.VERSION_CATEGORY, ConfigMigration.VERSION_KEY, 3);
        write("bases.tier 1", "baseMaxCharge", 500);
        write(OLD, "baseRange", 33);

        ConfigMigration.migrate(file);

        assertEquals(500, baseCharge(1), "an owner may set the old value again");
        assertEquals(33, new Configuration(file).getCategory(OLD).get("baseRange").getInt());
    }

    @Test
    void versionTwoFilesOnlyGetTheBaseCharges() {
        write(ConfigMigration.VERSION_CATEGORY, ConfigMigration.VERSION_KEY, 2);
        write("bases.tier 5", "baseMaxCharge", 10_000_000);
        write(OLD, "baseRange", 33);

        ConfigMigration.migrate(file);

        assertEquals(100_000_000, baseCharge(5));
        assertEquals(33, new Configuration(file).getCategory(OLD).get("baseRange").getInt(),
                "the version 2 step ran again");
    }

    private int baseCharge(int tier) {
        return new Configuration(file).getCategory("bases.tier " + tier).get("baseMaxCharge").getInt();
    }

    private int version() {
        return new Configuration(file).getCategory(ConfigMigration.VERSION_CATEGORY).get(ConfigMigration.VERSION_KEY)
                .getInt();
    }

    @Test
    void noConfigFileStaysNoConfigFile() {
        ConfigMigration.migrate(file);
        assertFalse(file.exists());
    }
}
