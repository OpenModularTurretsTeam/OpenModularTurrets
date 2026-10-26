package omtteam.openmodularturrets.platform.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.api.OMTPlugin;
import omtteam.openmodularturrets.entity.projectile.KillCounter;
import omtteam.omlib.registry.RegistrationStep;
import omtteam.openmodularturrets.registry.TurretRegistry;
import omtteam.openmodularturrets.startup.OMTStartup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Forge entrypoint: installs the platform services, starts the mod, runs each registration step in the
 * {@link RegisterEvent} of its registry and wires Forge's events to the shared code. Forge 1.20.1 has no client-only
 * {@code @Mod} classes, so it also starts the client entrypoint ({@code OMTForgeClient}) on a client.
 */
@Mod(OMT.MOD_ID)
public final class OMTForge {
    /** Started by name: main cannot see the client source set. */
    private static final String CLIENT_ENTRYPOINT = "omtteam.openmodularturrets.client.platform.forge.OMTForgeClient";

    private final List<RegistrationStep> steps = OMTStartup.registrationSteps();
    private final Set<RegistrationStep> done = new HashSet<>();

    public OMTForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        OMTStartup.init(ForgePlugins.find(OMTPlugin.Register.class, OMTPlugin.class, TurretRegistry.Plugin::new));
        modBus.addListener(this::onRegister);
        modBus.addListener(this::onCommonSetup);
        // lowest priority: mods that cancel the death have run, and cancelled deaths do not arrive here
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, OMTForge::onLivingDeath);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            startClient(modBus);
        }
    }

    private static void startClient(IEventBus modBus) {
        try {
            Class.forName(CLIENT_ENTRYPOINT).getConstructor(IEventBus.class).newInstance(modBus);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not start the client entrypoint " + CLIENT_ENTRYPOINT, e);
        }
    }

    private void onRegister(RegisterEvent event) {
        for (RegistrationStep step : steps) {
            if (step.registry().equals(event.getRegistryKey())) {
                step.register().run();
                done.add(step);
            }
        }
    }

    /** A step whose registry fired no event would leave its content unregistered: fail loudly instead. */
    private void onCommonSetup(FMLCommonSetupEvent event) {
        if (done.size() != steps.size()) {
            List<RegistrationStep> missing = steps.stream().filter(step -> !done.contains(step)).toList();
            throw new IllegalStateException("Registration steps never ran: " + missing);
        }
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            KillCounter.onDeath(event.getEntity(), event.getSource());
        }
    }
}
