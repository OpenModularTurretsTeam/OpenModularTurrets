package omtteam.openmodularturrets.client.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import omtteam.openmodularturrets.client.OMTClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric only (NeoForge has {@code RegisterSpecialModelRendererEvent}): Fabric API has no way to register the type of
 * a special item model renderer, so the types are added to vanilla's table once it has its own (client mixin config
 * {@code openmodularturrets.client.mixins.json}). The variant for 1.21.11; before, the class is an empty stub.
 */
@Mixin(SpecialModelRenderers.class)
public abstract class SpecialModelRenderersMixin {
    @Shadow
    @Final
    private static ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void openmodularturrets$registerItemModels(CallbackInfo ci) {
        OMTClient.registerSpecialItemModels(ID_MAPPER::put);
    }
}
