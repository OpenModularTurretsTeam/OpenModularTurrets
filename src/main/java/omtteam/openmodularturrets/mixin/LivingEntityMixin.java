package omtteam.openmodularturrets.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import omtteam.openmodularturrets.entity.projectile.TurretLoot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Suppresses death loot of turret kills without a fake drops addon (Fabric API has no drops event for 1.20.1/1.21.1).
 * Hooks the {@code shouldDropLoot()} check inside {@code dropAllDeathLoot}, whose name and call are the same on both
 * versions; this also covers mobs that override {@code shouldDropLoot}.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyExpressionValue(method = "dropAllDeathLoot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;shouldDropLoot()Z"))
    private boolean openmodularturrets$turretLootRule(boolean original, @Local(argsOnly = true) DamageSource source) {
        return original && TurretLoot.mayDropLoot((LivingEntity) (Object) this, source);
    }
}
