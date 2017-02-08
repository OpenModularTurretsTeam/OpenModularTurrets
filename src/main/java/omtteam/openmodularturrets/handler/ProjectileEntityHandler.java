package omtteam.openmodularturrets.handler;

import net.minecraft.util.ResourceLocation;
import omtteam.omlib.util.compat.EntityTools;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.entity.projectiles.*;
import omtteam.openmodularturrets.reference.Reference;

public class ProjectileEntityHandler {
    public static void registerProjectiles(OpenModularTurrets mod) {
        EntityTools.registerModEntity(new ResourceLocation(Reference.MOD_ID), RocketProjectile.class, Reference.MOD_ID + ":rocketProjectile", 1, mod, 16, 1, true);
        EntityTools.registerModEntity(new ResourceLocation(Reference.MOD_ID), GrenadeProjectile.class, Reference.MOD_ID + ":grenadeProjectile", 2, mod, 16, 5, true);
        EntityTools.registerModEntity(new ResourceLocation(Reference.MOD_ID), BulletProjectile.class, Reference.MOD_ID + ":bulletProjectile", 3, mod, 16, 5, true);
        EntityTools.registerModEntity(new ResourceLocation(Reference.MOD_ID), LaserProjectile.class, Reference.MOD_ID + ":laserProjectile", 4, mod, 16, 5, true);
        EntityTools.registerModEntity(new ResourceLocation(Reference.MOD_ID), BlazingClayProjectile.class, Reference.MOD_ID + ":blazingClayProjectile", 5, mod, 16, 5, true);
    }
}
