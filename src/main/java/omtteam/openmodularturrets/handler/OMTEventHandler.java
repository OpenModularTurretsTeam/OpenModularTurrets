package omtteam.openmodularturrets.handler;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.entity.projectiles.damagesources.AbstractOMTDamageSource;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.util.OMTFakePlayer;
import omtteam.openmodularturrets.util.OMTUtil;

/**
 * Created by Keridos on 02/05/17.
 * This class is the listener for all the Events we need to watch & modify.
 */
public class OMTEventHandler {
    private static OMTEventHandler instance;

    private OMTEventHandler() {
    }

    public static OMTEventHandler getInstance() {
        if (instance == null) {
            instance = new OMTEventHandler();
        }
        return instance;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.MOD_ID)) {
            ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
        }
    }

    @SubscribeEvent
    public void entityHurtEvent(LivingHurtEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        int fakeDrops = OMTUtil.getFakeDropsLevel(entity);
        if (fakeDrops >= 0) {
            FakePlayer player = OMTFakePlayer.getFakePlayer((WorldServer) event.getEntityLiving().getEntityWorld());
            player.setHeldItem(EnumHand.MAIN_HAND, OMTFakePlayer.getSword(fakeDrops));
            entity.setLastAttackedEntity(player);
        }
    }

    @SubscribeEvent
    public void entityAttackedEvent(LivingAttackEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        int fakeDrops = OMTUtil.getFakeDropsLevel(entity);
        if (fakeDrops >= 0) {
            FakePlayer player = OMTFakePlayer.getFakePlayer((WorldServer) event.getEntityLiving().getEntityWorld());
            player.setHeldItem(EnumHand.MAIN_HAND, OMTFakePlayer.getSword(fakeDrops));
            entity.setLastAttackedEntity(player);
        }
    }

    @SubscribeEvent
    public void entityDeathEvent(LivingDeathEvent event) {
        if (event.getSource() instanceof AbstractOMTDamageSource) {
            ((AbstractOMTDamageSource) event.getSource()).getBase().increaseKillCounter();
            if (event.getEntity() instanceof EntityPlayer) {
                ((AbstractOMTDamageSource) event.getSource()).getBase().increasePlayerKillCounter();
            }
        }
    }

    @SubscribeEvent
    public void blockRegisterEvent(RegistryEvent.Register<Block> event) {
        ModBlocks.initBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public void itemRegisterEvent(RegistryEvent.Register<Item> event) {
        ModItems.init(event.getRegistry());
    }

    @SubscribeEvent
    public void soundRegistryEvent(RegistryEvent.Register<SoundEvent> event) {
        ModSounds.init(event.getRegistry());
    }

    @SubscribeEvent
    public void renderRegisterEvent(ModelRegistryEvent event) {
        OpenModularTurrets.proxy.initModelLoaders();
    }

    @SubscribeEvent
    public void lootEvent(LivingDropsEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        int fakeDrops = OMTUtil.getFakeDropsLevel(entity);
        if (((entity.getTags().contains("openmodularturrets:turret_hit") && !OMTConfig.GENERAL.doTurretsKillsDropMobLoot)
                && !(fakeDrops >= 0 && OMTConfig.GENERAL.doLootAddonsOverrideMobLootSetting)) ||
                entity.getTags().contains("openmodularturrets:dont_drop_loot")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void configReloadEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(Reference.MOD_ID)) {
            ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
        }
    }
}
