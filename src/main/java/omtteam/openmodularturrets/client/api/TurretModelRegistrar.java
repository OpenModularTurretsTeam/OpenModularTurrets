package omtteam.openmodularturrets.client.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.client.model.ModelAbstractTurret;
import omtteam.openmodularturrets.core.turret.TurretType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Passed to {@link OMTClientPlugin#registerModels}. Turrets without a registered model are drawn with the machine
 * gun's model and texture.
 */
@SideOnly(Side.CLIENT)
public interface TurretModelRegistrar {
    /**
     * @param model         the turret's model; the renderer turns it in the frame of the base face it sits on and calls
     *                      {@link ModelAbstractTurret#setRotationForTarget} and {@link ModelAbstractTurret#renderAll}
     * @param texture       the model's texture (64×64)
     * @param activeTexture drawn while the turret is active (the melee turret's extended blades); null for the same
     *                      texture
     * @throws IllegalArgumentException if the type already has a model or is no registered turret
     */
    void register(TurretType type, Supplier<ModelAbstractTurret> model, ResourceLocation texture,
                  @Nullable ResourceLocation activeTexture);
}
