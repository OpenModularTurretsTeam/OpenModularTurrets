package omtteam.openmodularturrets.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

/**
 * Only 1.21.11 has special model renderers, and Fabric needs this mixin to add its own types to them; here the class
 * only keeps the client mixin config {@code openmodularturrets.client.mixins.json} valid.
 */
@Pseudo
@Mixin(targets = "net.minecraft.client.renderer.special.SpecialModelRenderers")
public abstract class SpecialModelRenderersMixin {
}
