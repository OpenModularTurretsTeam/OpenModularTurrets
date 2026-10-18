package omtteam.openmodularturrets.client.api;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.client.model.PortedTurretModel;
import omtteam.openmodularturrets.client.model.TurretModel;
import omtteam.openmodularturrets.core.turret.TurretType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Passed to {@link OMTClientPlugin#registerTurretModels}. Models are Java entity models (Y down, base plate at the
 * bottom, pivot 16 pixels above it, like the 1.12.2 models); the renderer orients them on the mounting face and the
 * same model is drawn for the turret item.
 */
public interface TurretModelRegistrar {
    /**
     * Registers a model.
     *
     * @param layer   the model's geometry, registered as an entity model layer
     * @param factory builds the model from the baked layer
     * @throws IllegalArgumentException if the turret already has a model
     */
    void register(TurretType type, Supplier<LayerDefinition> layer, Function<ModelPart, TurretModel> factory);

    /**
     * Registers a {@link PortedTurretModel}: the part at {@code yawPath} turns around the face normal, the part at
     * {@code pitchPath} raises the weapon (both may be the same part, or null for a static model).
     *
     * @param texture e.g. {@code mymod:textures/block/turret/my_turret.png}
     */
    default void register(TurretType type, Supplier<LayerDefinition> layer, ResourceLocation texture,
                          @Nullable String yawPath, @Nullable String pitchPath) {
        register(type, layer, part -> new PortedTurretModel(part, texture, yawPath, pitchPath));
    }
}
