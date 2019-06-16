package omtteam.openmodularturrets.turret;

import net.minecraft.entity.EntityLivingBase;
import omtteam.omlib.util.world.Pos;

public enum EnumTargetingPriority {
    HP, DISTANCE, ARMOR;

    public static int getValueByPriority(EntityLivingBase entity, Pos pos, EnumTargetingPriority priority) {
        if (priority == HP) return (int) Math.floor(entity.getHealth());
        if (priority == DISTANCE) return (int) Math.floor(pos.distance(entity));
        if (priority == ARMOR) return (int) Math.floor(entity.getTotalArmorValue()) + 1;
        return 0;
    }
}
