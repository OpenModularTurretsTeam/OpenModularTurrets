package omtteam.openmodularturrets.client.compat.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import omtteam.openmodularturrets.block.base.TurretBaseBlock;
import omtteam.openmodularturrets.block.defense.DefenseFenceBlock;
import omtteam.openmodularturrets.block.defense.DefenseWallBlock;
import omtteam.openmodularturrets.block.defense.HardenedBlock;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.camo.CamoHost;
import omtteam.openmodularturrets.compat.jade.OwnedBlockJadeProvider;
import omtteam.openmodularturrets.compat.jade.TurretBaseJadeProvider;
import omtteam.openmodularturrets.compat.jade.TurretJadeProvider;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
//? if >=1.21 {
import snownee.jade.api.JadeIds;
//?} else {
/*import snownee.jade.api.Identifiers;
*///?}

/**
 * Client half of the Jade integration ({@code compat.jade.OMTJadePlugin} has the server half), found the same way: the
 * {@code jade} entrypoint on Fabric, {@link WailaPlugin} on NeoForge and Forge. Jade also creates it on a dedicated
 * server, but calls {@link #registerClient} only on a client, so no client class is loaded there.
 */
@WailaPlugin
public class OMTJadeClientPlugin implements IWailaPlugin {
    /** Id Jade tags the lines of its universal item storage view with. */
    private static final ResourceLocation ITEM_STORAGE =
            //? if >=1.21 {
            JadeIds.UNIVERSAL_ITEM_STORAGE;
            //?} else {
            /*Identifiers.UNIVERSAL_ITEM_STORAGE;
            *///?}

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        JadeLinesComponent turretBase = new JadeLinesComponent(TurretBaseJadeProvider.UID,
                accessor -> TurretBaseJadeProvider.lines(accessor.getServerData()));
        JadeLinesComponent turret = new JadeLinesComponent(TurretJadeProvider.UID,
                accessor -> TurretJadeProvider.lines(accessor.getServerData(),
                        accessor.getBlockState().getValue(TurretBlock.CONCEALED)));
        JadeLinesComponent owned = new JadeLinesComponent(OwnedBlockJadeProvider.UID,
                accessor -> OwnedBlockJadeProvider.lines(accessor.getServerData()));
        registration.registerBlockComponent(turretBase, TurretBaseBlock.class);
        registration.registerBlockComponent(turret, TurretBlock.class);
        registration.registerBlockComponent(owned, HardenedBlock.class);
        registration.registerBlockComponent(owned, DefenseWallBlock.class);
        registration.registerBlockComponent(owned, DefenseFenceBlock.class);
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
            //? if >=1.21.11 {
            /*// Jade 21 takes name and icon from the block state
            return registration.blockAccessor().from(block).blockState(host.getCamo()).blockEntity((BlockEntity) null)
                    .build();
            *///?} else {
            return registration.blockAccessor().from(block).blockState(host.getCamo()).blockEntity((BlockEntity) null)
                    .fakeBlock(new ItemStack(host.getCamo().getBlock())).build();
            //?}
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
