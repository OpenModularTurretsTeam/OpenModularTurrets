package omtteam.openmodularturrets.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import omtteam.openmodularturrets.entity.projectile.TurretDamageSource;
import omtteam.openmodularturrets.entity.projectile.TurretImmunity;
import omtteam.openmodularturrets.entity.projectile.TurretLoot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Turret damage rules on the victim's side:
 * <ul>
 *     <li>Suppresses death loot of turret kills without a fake drops addon (Fabric API has no drops event for
 *     1.20.1/1.21.1). Hooks the {@code shouldDropLoot()} check inside {@code dropAllDeathLoot}, whose name and call
 *     are the same on both versions; this also covers mobs that override {@code shouldDropLoot}.</li>
 *     <li>Turret damage causes no knockback.</li>
 *     <li>Turret damage never hurts an {@linkplain TurretImmunity immune} entity (a player holding the ownership wand).
 *     Direct turret damage already skips it ({@code TurretDamage.canDamage}); this catches turret explosions.</li>
 * </ul>
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyExpressionValue(method = "dropAllDeathLoot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;shouldDropLoot()Z"))
    private boolean openmodularturrets$turretLootRule(boolean original, @Local(argsOnly = true) DamageSource source) {
        //noinspection ConstantValue
        return original && TurretLoot.mayDropLoot((LivingEntity) (Object) this, source);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void openmodularturrets$turretImmunity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source instanceof TurretDamageSource && TurretImmunity.isImmune((LivingEntity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Turret damage never knocks the victim back (user decision). {@code hurt} makes a single {@code knockback} call
     * on both versions.
     */
    @WrapWithCondition(method = "hurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private boolean openmodularturrets$noTurretKnockback(LivingEntity self, double strength, double x, double z,
                                                         @Local(argsOnly = true) DamageSource source) {
        return !(source instanceof TurretDamageSource);
    }
}
