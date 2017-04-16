package omtteam.openmodularturrets.init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;
import omtteam.openmodularturrets.tileentity.turrets.*;

/**
 * Created by Niel Verster on 11/29/2016.
 */
@SuppressWarnings({"DefaultFileTemplate", "deprecation"})
public class ModTESRItems {

    @SuppressWarnings("ConstantConditions")
    @SideOnly(Side.CLIENT)
    public static void init() {
        if (ConfigHandler.getDisposableTurretSettings().isEnabled()) {
            ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.disposableItemTurret), 0, DisposableItemTurretTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.disposableItemTurret), 0, new ModelResourceLocation(ModBlocks.disposableItemTurret.getRegistryName(), "inventory"));
        }
        if (ConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.potatoCannonTurret), 0, PotatoCannonTurretTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.potatoCannonTurret), 0, new ModelResourceLocation(ModBlocks.potatoCannonTurret.getRegistryName(), "inventory"));
        }
        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.machineGunTurret), 0, GunTurretTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.machineGunTurret), 0, new ModelResourceLocation(ModBlocks.machineGunTurret.getRegistryName(), "inventory"));
        }
        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.incendiaryTurret), 0, IncendiaryTurretTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.incendiaryTurret), 0, new ModelResourceLocation(ModBlocks.incendiaryTurret.getRegistryName(), "inventory"));
        }
        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.relativisticTurret), 0, RelativisticTurretTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.relativisticTurret), 0, new ModelResourceLocation(ModBlocks.relativisticTurret.getRegistryName(), "inventory"));
        }
        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.grenadeLauncherTurret), 0, GrenadeLauncherTurretTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.grenadeLauncherTurret), 0, new ModelResourceLocation(ModBlocks.grenadeLauncherTurret.getRegistryName(), "inventory"));
        }
        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.rocketTurret), 0, RocketTurretTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.rocketTurret), 0, new ModelResourceLocation(ModBlocks.rocketTurret.getRegistryName(), "inventory"));
        }
        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.teleporterTurret), 0, TeleporterTurretTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.teleporterTurret), 0, new ModelResourceLocation(ModBlocks.teleporterTurret.getRegistryName(), "inventory"));
        }
        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.laserTurret), 0, LaserTurretTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.laserTurret), 0, new ModelResourceLocation(ModBlocks.laserTurret.getRegistryName(), "inventory"));
        }
        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.railGunTurret), 0, RailGunTurretTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.railGunTurret), 0, new ModelResourceLocation(ModBlocks.railGunTurret.getRegistryName(), "inventory"));
        }
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.leverBlock), 0, LeverTileEntity.class);
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.leverBlock), 0, new ModelResourceLocation(ModBlocks.leverBlock.getRegistryName(), "inventory"));
    }
}
