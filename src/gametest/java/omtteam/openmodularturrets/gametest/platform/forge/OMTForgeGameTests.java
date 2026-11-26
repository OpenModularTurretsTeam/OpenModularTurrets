package omtteam.openmodularturrets.gametest.platform.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import omtteam.openmodularturrets.gametest.OMTGameTestMod;
import omtteam.openmodularturrets.gametest.ProjectileTurretGameTests;
import omtteam.openmodularturrets.gametest.TestBattery;
import omtteam.omlib.registry.RegistrationStep;

import java.util.List;

/**
 * Forge entrypoint of the test mod. The test classes are found through their {@code @GameTestHolder} annotation.
 * <p>
 * Forge 1.20.1 builds its game test server from the dedicated server's settings, so the world has the default game
 * rules instead of vanilla's test rules; the server start turns the weather cycle and mob spawning off, as vanilla's
 * {@code GameTestServer} (and so the Fabric node) does.
 */
@Mod(OMTGameTestMod.MOD_ID)
public final class OMTForgeGameTests {
    private final List<RegistrationStep> steps = OMTGameTestMod.registrationSteps();

    public OMTForgeGameTests() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener((RegisterEvent event) -> steps.stream()
                .filter(step -> step.registry().equals(event.getRegistryKey()))
                .forEach(step -> step.register().run()));
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, (AttachCapabilitiesEvent<ItemStack> event) -> {
            if (event.getObject().is(TestBattery.ITEM)) {
                event.addCapability(TestBattery.id("energy"), new ForgeTestBatteryEnergy(event.getObject()));
            }
        });
        MinecraftForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            GameRules rules = event.getServer().getGameRules();
            rules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, event.getServer());
            rules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, event.getServer());
        });
        MinecraftForge.EVENT_BUS.addListener((EntityJoinLevelEvent event) -> {
            if (!event.getLevel().isClientSide()) {
                ProjectileTurretGameTests.onEntityLoad(event.getEntity());
            }
        });
    }
}
