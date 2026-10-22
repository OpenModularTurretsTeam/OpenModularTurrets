package omtteam.openmodularturrets.fixes;

import net.minecraft.nbt.NBTTagCompound;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TurretTileEntityFixTest {
    private static String fix(String id) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", id);
        tag.setFloat("yaw", 12F);
        NBTTagCompound fixed = new TurretTileEntityFix().fixTagCompound(tag);
        assertEquals(12F, fixed.getFloat("yaw"), "the other keys stay");
        return fixed.getString("id");
    }

    @Test
    void everyTurretOf3xBecomesTheTurret() {
        for (String old : new String[]{"disposable_item_turret", "potato_cannon_turret", "crossbow_turret",
                "melee_turret", "machine_gun_turret", "incendiary_turret", "grenade_turret", "relativistic_turret",
                "rocket_turret", "teleporter_turret", "arc_turret", "laser_turret", "rail_gun_turret",
                "plasma_turret"}) {
            assertEquals("openmodularturrets:turret", fix("openmodularturrets:" + old), old);
        }
    }

    @Test
    void otherTileEntitiesStay() {
        assertEquals("openmodularturrets:turret_base", fix("openmodularturrets:turret_base"));
        assertEquals("openmodularturrets:lever_block", fix("openmodularturrets:lever_block"));
        assertEquals("minecraft:chest", fix("minecraft:chest"));
    }
}
