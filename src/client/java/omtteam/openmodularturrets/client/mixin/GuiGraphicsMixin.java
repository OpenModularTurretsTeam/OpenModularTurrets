package omtteam.openmodularturrets.client.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import omtteam.openmodularturrets.client.render.DisabledModuleOverlay;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric only (the client mixin config is listed in {@code fabric.mod.json} alone): draws the "no" symbol over disabled
 * upgrades and addons after the item decorations, as NeoForge's and Forge's item decorators do there.
 */
@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At("TAIL"))
    private void openmodularturrets$disabledModule(Font font, ItemStack stack, int x, int y, @Nullable String text,
                                                   CallbackInfo ci) {
        DisabledModuleOverlay.renderItemOverlay((GuiGraphics) (Object) this, stack, x, y);
    }
}
