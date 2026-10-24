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
    void aConfigWithoutTheOldSectionIsNotRewritten() throws Exception {
        write(NEW, "baseRange", 20);
        byte[] before = Files.readAllBytes(file.toPath());

        ConfigMigration.migrate(file);

        assertArrayEquals(before, Files.readAllBytes(file.toPath()));
    }

    @Test
    void noConfigFileStaysNoConfigFile() {
        ConfigMigration.migrate(file);
        assertFalse(file.exists());
    }
}
