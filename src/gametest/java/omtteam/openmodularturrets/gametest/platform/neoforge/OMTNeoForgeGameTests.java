package omtteam.openmodularturrets.gametest.platform.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;
import omtteam.openmodularturrets.gametest.OMTGameTestMod;
import omtteam.openmodularturrets.gametest.ProjectileTurretGameTests;
import omtteam.openmodularturrets.gametest.TestBattery;
import omtteam.omlib.registry.RegistrationStep;

import java.util.List;

/**
 * NeoForge entrypoint of the test mod. {@code NeoForgeGameTestRegistration} registers the test classes.
 */
@Mod(OMTGameTestMod.MOD_ID)
public final class OMTNeoForgeGameTests {
    private final List<RegistrationStep> steps = OMTGameTestMod.registrationSteps();

    public OMTNeoForgeGameTests(IEventBus modBus) {
        modBus.addListener((RegisterEvent event) -> steps.stream()
                .filter(step -> step.registry().equals(event.getRegistryKey()))
                .forEach(step -> step.register().run()));
        modBus.addListener((RegisterCapabilitiesEvent event) -> event.registerItem(Capabilities.Energy.ITEM,
                (stack, access) -> new ItemAccessEnergyHandler(access, TestBattery.Energy.COMPONENT, TestBattery.CAPACITY),
                TestBattery.ITEM));
        NeoForgeGameTestRegistration.register(modBus);
        NeoForge.EVENT_BUS.addListener((EntityJoinLevelEvent event) -> {
            if (!event.getLevel().isClientSide()) {
                ProjectileTurretGameTests.onEntityLoad(event.getEntity());
            }
        });
    }
}
