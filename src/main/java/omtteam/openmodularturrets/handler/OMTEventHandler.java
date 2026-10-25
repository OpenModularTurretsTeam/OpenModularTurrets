package omtteam.openmodularturrets.handler;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import omtteam.omlib.config.ConfigSync;
import omtteam.openmodularturrets.entity.projectile.AbstractOMTDamageSource;
import omtteam.openmodularturrets.entity.projectile.OMTFakePlayer;
import omtteam.openmodularturrets.entity.projectile.TurretDamage;
import omtteam.openmodularturrets.fixes.MissingIds;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;
import omtteam.openmodularturrets.sound.ModSounds;

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
    public void entityHurtEvent(LivingHurtEvent event) {
        attributeToFakePlayer(event.getEntityLiving());
    }

    @SubscribeEvent
    public void entityAttackedEvent(LivingAttackEvent event) {
        attributeToFakePlayer(event.getEntityLiving());
    }

    /**
     * An entity hit by a base with the fake drops addon was last attacked by the fake player, holding a sword with
     * the addons' looting level, so its drops are player drops.
     */
    private static void attributeToFakePlayer(EntityLivingBase entity) {
        int fakeDrops = TurretDamage.getFakeDropsLevel(entity);
        if (fakeDrops >= 0) {
            FakePlayer player = OMTFakePlayer.getFakePlayer((WorldServer) entity.getEntityWorld());
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
    public void missingBlockMappings(RegistryEvent.MissingMappings<Block> event) {
        MissingIds.handleBlocks(event);
    }

    @SubscribeEvent
    public void missingItemMappings(RegistryEvent.MissingMappings<Item> event) {
        MissingIds.handleItems(event);
    }

    @SubscribeEvent
    public void missingSoundMappings(RegistryEvent.MissingMappings<SoundEvent> event) {
        MissingIds.handleSounds(event);
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

    /**
     * Inverted looting (4.0.0, as 1.20.1+): entities killed by turrets drop nothing unless the base has a fake drops
     * addon. Turret damage decides by itself (its source names the fake player only with the addon); deaths by fire or
     * falls after a turret hit go by the hit tags; kills by a real player are left alone.
     */
    @SubscribeEvent
    public void lootEvent(LivingDropsEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityPlayer) {
            return;
        }
        DamageSource source = event.getSource();
        Entity killer = source.getTrueSource();
        boolean noLoot;
        if (source instanceof AbstractOMTDamageSource) {
            noLoot = killer == null;
        } else if (killer instanceof EntityPlayer && !(killer instanceof FakePlayer)) {
            noLoot = false;
        } else {
            noLoot = entity.getTags().contains("openmodularturrets:turret_hit") && TurretDamage.getFakeDropsLevel(entity) < 0;
        }
        if (noLoot) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void configReloadEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(Reference.MOD_ID)) {
            ConfigSync.syncLocal(Reference.MOD_ID);
        }
    }
}
