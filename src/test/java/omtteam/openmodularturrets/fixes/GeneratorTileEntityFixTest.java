package omtteam.openmodularturrets.fixes;

import net.minecraft.nbt.NBTTagCompound;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneratorTileEntityFixTest {
    private static NBTTagCompound fix(String id) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", id);
        tag.setLong("Energy", 1234L);
        NBTTagCompound fixed = new GeneratorTileEntityFix().fixTagCompound(tag);
        assertEquals(1234L, fixed.getLong("Energy"), "the other keys stay");
        return fixed;
    }

    @Test
    void thePowerGeneratorOf3xBecomesTheGenerator() {
        assertEquals("openmodularturrets:generator", fix("openmodularturrets:power_generator").getString("id"));
    }

    @Test
    void otherTileEntitiesStay() {
        assertEquals("openmodularturrets:generator", fix("openmodularturrets:generator").getString("id"));
        assertEquals("openmodularturrets:turret_base", fix("openmodularturrets:turret_base").getString("id"));
        assertEquals("minecraft:furnace", fix("minecraft:furnace").getString("id"));
    }
}
