package omtteam.openmodularturrets.handler;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.api.network.OMTNetwork;
import omtteam.openmodularturrets.entity.projectiles.damagesources.AbstractOMTDamageSource;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.util.OMTFakePlayer;
import omtteam.openmodularturrets.util.OMTUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Keridos on 02/05/17.
 * This class is the listener for all the Events we need to watch & modify.
 */
public class OMTEventHandler {
    private static OMTEventHandler instance;
    private HashMap<World, List<OMTNetwork>> networks = new HashMap<>();

    private OMTEventHandler() {
    }

    public static OMTEventHandler getInstance() {
        if (instance == null) {
            instance = new OMTEventHandler();
        }
        return instance;
    }

    @SubscribeEvent
    public void lootEvent(LivingDropsEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        int fakeDrops = OMTUtil.getFakeDropsLevel(entity);
        if (((entity.getTags().contains("openmodularturrets:turret_hit") && !OMTConfigHandler.doTurretsKillsDropMobLoot)
                && !(fakeDrops >= 0 && OMTConfigHandler.doLootAddonsOverrideMobLootSetting)) ||
                entity.getTags().contains("openmodularturrets:dont_drop_loot")) {
            event.setCanceled(true);
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

    private List<OMTNetwork> getNetworkListForWorld(World world) {
        networks.putIfAbsent(world, new ArrayList<>());
        return networks.get(world);
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.WorldTickEvent event) {
        for (OMTNetwork network : getNetworkListForWorld(event.world)) {
            network.tick();
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

    public void registerNetwork(OMTNetwork network) {
        getNetworkListForWorld(network.getWorld()).add(network);
    }

    public void removeNetwork(OMTNetwork network) {
        getNetworkListForWorld(network.getWorld()).remove(network);
    }

    @SubscribeEvent
    public void worldLoadEvent(WorldEvent.Load event) {
        try {
            HashMap<Integer, List<Tuple<UUID, String>>> tempList = new HashMap<>();
            Path fullpath = Paths.get(DimensionManager.getCurrentSaveRootDirectory().toString() + "/omt/networks.sav");
            FileInputStream saveFile = new FileInputStream(fullpath.toFile());
            ObjectInputStream save = new ObjectInputStream(saveFile);
            Object object = save.readObject();
            if (object instanceof HashMap) {
                tempList = (HashMap<Integer, List<Tuple<UUID, String>>>) object;
            }
            save.close();
            saveFile.close();
            for (Map.Entry<Integer, List<Tuple<UUID, String>>> entry : tempList.entrySet()) {
                World world = DimensionManager.getWorld(entry.getKey());
                for (Tuple<UUID, String> tuple : entry.getValue()) {
                    new OMTNetwork(world, tuple.getSecond(), tuple.getFirst());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void worldUnloadEvent(WorldEvent.Unload event) {
        HashMap<Integer, List<Tuple<UUID, String>>> list = new HashMap<>();
        for (Map.Entry<World, List<OMTNetwork>> entry : networks.entrySet()) {
            List<Tuple<UUID, String>> tempList = new ArrayList<>();
            for (OMTNetwork network : entry.getValue()) {
                tempList.add(new Tuple<>(network.getUuid(), network.getName()));
                network.saveToDisk();
            }
            list.put(entry.getKey().provider.getDimension(), tempList);
        }
        File saveRoot = DimensionManager.getCurrentSaveRootDirectory();
        if (saveRoot != null) {
            Path path = Paths.get(saveRoot.toString() + "/omt/");
            Path fullpath = Paths.get(saveRoot.toString() + "/omt/networks.sav");
            try {
                if (Files.notExists(path)) {
                    if (!path.toFile().mkdir()) {
                        throw new Exception("Failed to create dir");
                    }
                }
                FileOutputStream saveFile = new FileOutputStream(fullpath.toFile());
                ObjectOutputStream save = new ObjectOutputStream(saveFile);
                save.writeObject(list);
                save.close();
                saveFile.close();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Files.deleteIfExists(fullpath);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
