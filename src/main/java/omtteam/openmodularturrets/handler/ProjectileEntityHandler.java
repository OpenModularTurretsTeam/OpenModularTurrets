package omtteam.openmodularturrets.handler;

import net.minecraft.util.ResourceLocation;
import omtteam.omlib.util.compat.EntityTools;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.entity.projectiles.*;
import omtteam.openmodularturrets.reference.Reference;

public class ProjectileEntityHandler {
    public static void registerProjectiles(OpenModularTurrets turrets) {
        EntityTools.registerModEntity(new ResourceLocation(Reference.MOD_ID), RocketProjectile.class, "rocketProjectile", 1, turrets, 16, 1, true);
        EntityTools.registerModEntity(new ResourceLocation(Reference.MOD_ID), GrenadeProjectile.class, "grenadeProjectile", 2, turrets, 16, 5, true);
        EntityTools.registerModEntity(new ResourceLocation(Reference.MOD_ID), BulletProjectile.class, "bulletProjectile", 3, turrets, 16, 5, true);
        EntityTools.registerModEntity(new ResourceLocation(Reference.MOD_ID), LaserProjectile.class, "laserProjectile", 4, turrets, 16, 5, true);
        EntityTools.registerModEntity(new ResourceLocation(Reference.MOD_ID), BlazingClayProjectile.class, "blazingClayProjectile", 5, turrets, 16, 5, true);
    }
}
