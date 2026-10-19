package omtteam.openmodularturrets.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * One registration step of the mod: the registry it fills, and the call that does it. Fabric runs all steps in order;
 * NeoForge runs each in the {@code RegisterEvent} of its registry (port in progress: {@code OMT} does not list its
 * steps yet; see {@code OML#registrationSteps} in OpenModularLighting).
 */
public record RegistrationStep(ResourceKey<? extends Registry<?>> registry, Runnable register) {
}
