package omtteam.openmodularturrets.platform.neoforge;

import omtteam.omlib.platform.neoforge.NeoForgeNetwork;
import omtteam.omlib.platform.neoforge.NeoForgeCapabilities;
import omtteam.omlib.platform.neoforge.NeoForgeCapabilities;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.api.OMTPlugin;
import omtteam.openmodularturrets.entity.projectile.KillCounter;
import omtteam.omlib.platform.Platform;
import omtteam.omlib.registry.RegistrationStep;
import omtteam.openmodularturrets.registry.TurretRegistry;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.startup.OMTStartup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * NeoForge entrypoint: installs the platform services, starts the mod, runs each registration step in the
 * {@link RegisterEvent} of its registry and wires NeoForge's events to the shared code.
 */
@Mod(OMT.MOD_ID)
public final class OMTNeoForge {
    private final List<RegistrationStep> steps = OMTStartup.registrationSteps();
    private final Set<RegistrationStep> done = new HashSet<>();

    public OMTNeoForge(IEventBus modBus) {
        OMTStartup.init(NeoForgePlugins.find(OMTPlugin.Register.class, OMTPlugin.class, TurretRegistry.Plugin::new));
        modBus.addListener(this::onRegister);
        modBus.addListener(this::onCommonSetup);
        modBus.addListener((RegisterCapabilitiesEvent event) ->
                NeoForgeCapabilities.register(event, ModBlockEntities.transferTypes()));
        modBus.addListener((RegisterPayloadHandlersEvent event) ->
                ((NeoForgeNetwork) Platform.network()).applyRegistrations(event, OMT.MOD_ID));
        // lowest priority: mods that cancel the death have run, and cancelled deaths do not arrive here
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, OMTNeoForge::onLivingDeath);
    }

    private void onRegister(RegisterEvent event) {
        for (RegistrationStep step : steps) {
            if (step.registry().equals(event.getRegistryKey())) {
                step.register().run();
                done.add(step);
            }
        }
    }

    /**
     * A step whose registry fired no event would leave its content unregistered: fail loudly instead. With Create
     * installed, its contraptions may move bases and turrets ({@link NeoForgeCreateMovement}).
     */
    private void onCommonSetup(FMLCommonSetupEvent event) {
        if (done.size() != steps.size()) {
            List<RegistrationStep> missing = steps.stream().filter(step -> !done.contains(step)).toList();
            throw new IllegalStateException("Registration steps never ran: " + missing);
        }
        //? if <1.21.11 {
        if (Platform.loaderInfo().isModLoaded(NeoForgeCreateMovement.CREATE_MOD_ID)) {
            event.enqueueWork(NeoForgeCreateMovement::register);
        }
        //?}
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            KillCounter.onDeath(event.getEntity(), event.getSource());
        }
    }
}
