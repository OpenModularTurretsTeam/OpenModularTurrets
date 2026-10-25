package omtteam.openmodularturrets.compat.jade;

import omtteam.openmodularturrets.block.base.TurretBaseBlock;
import omtteam.openmodularturrets.block.defense.DefenseFenceBlock;
import omtteam.openmodularturrets.block.defense.DefenseWallBlock;
import omtteam.openmodularturrets.block.defense.HardenedBlock;
import omtteam.openmodularturrets.camo.CamoHost;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.defense.OwnedBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
//? if >=1.21 {
import snownee.jade.api.JadeIds;
//?} else {
/*import snownee.jade.api.Identifiers;
*///?}

/**
 * Jade integration, only loaded if Jade is installed: Fabric Jade finds it through the {@code jade} entrypoint in
 * fabric.mod.json, NeoForge Jade through {@link WailaPlugin} (Fabric has no annotation scanning, so it is not registered
 * twice). Replaces the 1.12.2 WAILA/HWYLA handlers.
 */
@WailaPlugin
public class OMTJadePlugin implements IWailaPlugin {
    /** Id Jade tags the lines of its universal item storage view with. */
    private static final ResourceLocation ITEM_STORAGE =
            //? if >=1.21 {
            JadeIds.UNIVERSAL_ITEM_STORAGE;
            //?} else {
            /*Identifiers.UNIVERSAL_ITEM_STORAGE;
            *///?}

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(TurretBaseJadeProvider.INSTANCE, TurretBaseBlockEntity.class);
        registration.registerBlockDataProvider(TurretJadeProvider.INSTANCE, TurretBlockEntity.class);
        registration.registerBlockDataProvider(OwnedBlockJadeProvider.INSTANCE, OwnedBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(TurretBaseJadeProvider.INSTANCE, TurretBaseBlock.class);
        registration.registerBlockComponent(TurretJadeProvider.INSTANCE, TurretBlock.class);
        registration.registerBlockComponent(OwnedBlockJadeProvider.INSTANCE, HardenedBlock.class);
        registration.registerBlockComponent(OwnedBlockJadeProvider.INSTANCE, DefenseWallBlock.class);
        registration.registerBlockComponent(OwnedBlockJadeProvider.INSTANCE, DefenseFenceBlock.class);
        // players who may not see through the camouflage get the disguise instead of the real block
        registration.addRayTraceCallback((hit, accessor, original) -> disguised(registration, accessor));
        // Jade's item view would list the ammo slots of the base; it stalls on "Collecting items..." there
        registration.addTooltipCollectedCallback(
                //? if >=1.21 {
                (box, accessor) -> hideItemStorage(box.getTooltip(), accessor));
                //?} else {
                /*(tooltip, accessor) -> hideItemStorage(tooltip, accessor));
                *///?}
    }

    /**
     * The disguise as the block Jade looks at: without block entity, so none of our providers (nor Jade's own
     * storage views) run for it, and with the disguise's item as icon and name.
     */
    private static Accessor<?> disguised(IWailaClientRegistration registration, Accessor<?> accessor) {
        if (accessor instanceof BlockAccessor block && block.getBlockEntity() instanceof CamoHost host
                && host.isCamoEnabled() && host.getCamo() != null && !host.canSeeThroughCamo(block.getPlayer())) {
            return registration.blockAccessor().from(block).blockState(host.getCamo()).blockEntity((BlockEntity) null)
                    .fakeBlock(new ItemStack(host.getCamo().getBlock())).build();
        }
        return accessor;
    }

    private static void hideItemStorage(ITooltip tooltip, Accessor<?> accessor) {
        if (accessor instanceof BlockAccessor block && (block.getBlockEntity() instanceof TurretBaseBlockEntity
                || block.getBlockEntity() instanceof TurretBlockEntity)) {
            tooltip.remove(ITEM_STORAGE);
        }
    }
}
