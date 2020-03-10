package omtteam.openmodularturrets.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import omtteam.omlib.util.TargetingSettings;
import omtteam.omlib.util.world.Pos;

public enum EnumTargetingPriority {
    MAX_HP, HP_REMAINING, DISTANCE, ARMOR, PLAYER;

    public static int getValueByPriority(EntityLivingBase entity, Pos pos, EnumTargetingPriority priority, TargetingSettings settings, Integer[] priorities) {
        if (priority == MAX_HP)
            return (int) Math.floor(entity.getHealth()) * priorities[MAX_HP.ordinal()];
        if (priority == HP_REMAINING)
            return (int) Math.floor(entity.getMaxHealth() - entity.getHealth()) * priorities[HP_REMAINING.ordinal()];
        if (priority == DISTANCE)
            return (int) Math.floor(pos.distance(entity)) * priorities[DISTANCE.ordinal()];
        if (priority == ARMOR)
            return (int) (Math.floor(entity.getTotalArmorValue()) + 1) * priorities[ARMOR.ordinal()];
        if (priority == PLAYER)
            return entity instanceof EntityPlayer ? 100 * priorities[PLAYER.ordinal()] : -100;
        return 1;
    }
}
