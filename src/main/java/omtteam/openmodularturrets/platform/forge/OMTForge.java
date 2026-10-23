package omtteam.openmodularturrets.platform.forge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.api.OMTPlugin;
import omtteam.openmodularturrets.block.InteractiveBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.misc.GeneratorBlockEntity;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.entity.projectile.KillCounter;
import omtteam.openmodularturrets.platform.Platform;
import omtteam.openmodularturrets.platform.PlatformServices;
import omtteam.openmodularturrets.registry.RegistrationStep;
import omtteam.openmodularturrets.registry.TurretRegistry;

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

    private final List<RegistrationStep> steps = OMT.registrationSteps();
    private final Set<RegistrationStep> done = new HashSet<>();

    public OMTForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        Platform.install(new PlatformServices(new ForgeLoaderInfo(), new ForgeFuel(), new ForgeNetwork(),
                new ForgeMenus(), new ForgeEnergy()));
        OMT.init(ForgePlugins.find(OMTPlugin.Register.class, OMTPlugin.class, TurretRegistry.Plugin::new));
        modBus.addListener(this::onRegister);
        modBus.addListener(this::onCommonSetup);
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, OMTForge::onAttachCapabilities);
        MinecraftForge.EVENT_BUS.addListener(OMTForge::onRightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(OMTForge::onPlayerLoggedIn);
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

    /** Items and energy of the turret base and the generator, on all sides (as the Fabric lookups). */
    private static void onAttachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        ForgeBlockCapabilities capabilities;
        if (event.getObject() instanceof TurretBaseBlockEntity base) {
            capabilities = new ForgeBlockCapabilities(base::getItemAccess, base::getEnergyAccess);
        } else if (event.getObject() instanceof GeneratorBlockEntity generator) {
            capabilities = new ForgeBlockCapabilities(generator::getItemAccess, generator::getEnergyAccess);
        } else {
            return;
        }
        event.addCapability(OMT.id("transfer"), capabilities);
        event.addListener(capabilities::invalidate);
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = InteractiveBlock.handleUse(event.getEntity(), event.getLevel(), event.getHand(),
                event.getHitVec());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            OMTConfig.onPlayerJoin(player);
        }
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            KillCounter.onDeath(event.getEntity(), event.getSource());
        }
    }
}
