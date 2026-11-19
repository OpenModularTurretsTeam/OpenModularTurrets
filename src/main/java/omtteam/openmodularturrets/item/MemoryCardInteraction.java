package omtteam.openmodularturrets.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import omtteam.omlib.api.render.camo.DisguiseRules;
import omtteam.omlib.api.render.camo.ICamoSupport;
import omtteam.omlib.api.tile.IHasTrustManager;
import omtteam.omlib.core.camo.CamoLight;
import omtteam.omlib.core.card.MemoryCardData;
import omtteam.omlib.core.card.MemoryCardRules;
import omtteam.omlib.core.security.PlayerInfo;
import omtteam.omlib.core.security.TrustManager;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.camo.CamoSettings;
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.openmodularturrets.core.card.BaseCardRules;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * What a memory card does to a turret base or a camouflage trap (1.20.1+ {@code MemoryCardInteraction}). All
 * permission checks happen here, on the server:
 * <ul>
 *     <li>turret base: copying and writing the settings (targeting, mode, trust list) needs the admin access level;
 *     the camouflage part follows the camouflage rules (disguise: owner, light: admin)</li>
 *     <li>camouflage trap (any block with a trust list and camouflage that is not a base): copying and writing (trust
 *     list, disguise, light) need an admin of the trap</li>
 * </ul>
 * Players without permission get nothing from a trap (it must not reveal itself), and a message from a base.
 */
public final class MemoryCardInteraction {
    private MemoryCardInteraction() {
    }

    /** Whether the card acts on this tile entity at all (bases and traps); the client uses it to claim the click. */
    public static boolean isTarget(@Nullable TileEntity tileEntity) {
        return tileEntity instanceof TurretBase
                || tileEntity instanceof IHasTrustManager && tileEntity instanceof ICamoSupport;
    }

    /** Sneaking copies; otherwise a card with data is written to the block. Server side. */
    public static void use(EntityPlayerMP player, UsableMetaItem card, ItemStack stack,
                           TileEntity target, boolean copy) {
        PlayerInfo info = PlayerUtil.getPlayerInfo(player);
        if (target instanceof TurretBase) {
            TurretBase base = (TurretBase) target;
            if (copy) {
                copyFromBase(player, info, card, stack, base);
            } else {
                MemoryCardData data = card.getCardData(stack);
                if (data != null) {
                    pasteToBase(player, info, data, base);
                }
            }
            return;
        }
        IHasTrustManager trap = (IHasTrustManager) target;
        TrustManager trust = trap.getTrustManager().core();
        if (!MemoryCardRules.canCopy(trust, info)) {
            return;
        }
        if (copy) {
            card.setCardData(stack, new MemoryCardData(MemoryCardData.Source.CAMO_TRAP, null,
                    MemoryCardRules.captureTrust(trust), camoSettings((ICamoSupport) target)));
            notify(player, "card_copied");
        } else {
            MemoryCardData data = card.getCardData(stack);
            if (data == null) {
                return;
            }
            MemoryCardRules.applyTrust(data.trust(), trust);
            trap.informUpdate();
            List<String> skipped = new ArrayList<>();
            if (data.camo() != null) {
                pasteCamo(player, data.camo(), (ICamoSupport) target, skipped);
            }
            report(player, true, skipped);
        }
    }

    private static void copyFromBase(EntityPlayerMP player, PlayerInfo info, UsableMetaItem card,
                                     ItemStack stack, TurretBase base) {
        TrustManager trust = base.getTrustManager().core();
        if (!MemoryCardRules.canCopy(trust, info)) {
            player.sendMessage(new TextComponentTranslation(OMLibNames.Localizations.Text.STATUS_PERMISSION));
            return;
        }
        card.setCardData(stack, new MemoryCardData(MemoryCardData.Source.TURRET_BASE,
                BaseCardRules.capture(base.getLogic()), MemoryCardRules.captureTrust(trust),
                base.isCamoEnabled() ? camoSettings(base) : null));
        notify(player, "card_copied");
    }

    private static void pasteToBase(EntityPlayerMP player, PlayerInfo info, MemoryCardData data, TurretBase base) {
        List<String> skipped = new ArrayList<>();
        boolean applied = false;
        TrustManager trust = base.getTrustManager().core();
        if (MemoryCardRules.canPaste(trust, info)) {
            if (data.base() != null) {
                BaseCardRules.apply(data.base(), base.getLogic());
            }
            MemoryCardRules.applyTrust(data.trust(), trust);
            base.markDirty();
            base.informUpdate();
            applied = true;
        } else {
            skipped.add("settings");
        }
        if (data.camo() != null && base.isCamoEnabled()) {
            applied |= pasteCamo(player, data.camo(), base, skipped);
        }
        report(player, applied, skipped);
    }

    /** Disguise ({@code registry@meta}, null without one) and light (null without adjustable light) of a host. */
    @SuppressWarnings("deprecation")
    private static MemoryCardData.CamoSettings camoSettings(ICamoSupport host) {
        CamoSettings settings = host.getCamoSettings();
        IBlockState camo = settings.getCamoBlockState();
        String disguise = null;
        if (camo != null && camo.getBlock() != host.getDefaultCamoState().getBlock()
                && camo.getBlock().getRegistryName() != null) {
            disguise = camo.getBlock().getRegistryName() + "@" + camo.getBlock().getMetaFromState(camo);
        }
        return new MemoryCardData.CamoSettings(disguise, host.supportsLight()
                ? new CamoLight(settings.getLightValue(), settings.getLightOpacity()) : null);
    }

    /** The disguise for a stored id, if the block still exists and is a valid disguise. */
    @Nullable
    @SuppressWarnings("deprecation")
    private static IBlockState disguiseById(String id, DisguiseRules rules) {
        int at = id.lastIndexOf('@');
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(at < 0 ? id : id.substring(0, at)));
        if (block == null || block.getRegistryName() == null || !id.startsWith(block.getRegistryName().toString())) {
            return null;
        }
        IBlockState state;
        try {
            state = block.getStateFromMeta(at < 0 ? 0 : Integer.parseInt(id.substring(at + 1)));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
        return rules.isValidDisguise(state) ? state : null;
    }

    /** Disguise and light, each only if the player may change it. */
    private static boolean pasteCamo(EntityPlayerMP player, MemoryCardData.CamoSettings camo, ICamoSupport host,
                                     List<String> skipped) {
        boolean applied = false;
        if (host.canSetCamo(player)) {
            IBlockState disguise = camo.disguise() == null ? host.getDefaultCamoState() : disguiseById(camo.disguise(), host.getDisguiseRules());
            if (disguise != null) {
                host.setCamoState(disguise);
                applied = true;
            } else {
                skipped.add("disguise_unavailable");
            }
        } else {
            skipped.add("disguise");
        }
        CamoLight light = camo.light();
        if (light != null && host.supportsLight()) {
            if (host.canChangeLight(player)) {
                CamoSettings settings = host.getCamoSettings();
                settings.setLightValue(light.value());
                settings.setLightOpacity(light.opacity());
                host.updateCamoSettingsToPlayers();
                host.getCamoTileEntity().getWorld().checkLight(host.getCamoTileEntity().getPos());
                applied = true;
            } else {
                skipped.add("light");
            }
        }
        host.getCamoTileEntity().markDirty();
        return applied;
    }

    private static void report(EntityPlayer player, boolean applied, List<String> skipped) {
        if (!applied) {
            player.sendMessage(new TextComponentTranslation(OMLibNames.Localizations.Text.STATUS_PERMISSION));
            return;
        }
        notify(player, "card_pasted");
        for (String part : skipped) {
            player.sendMessage(new TextComponentTranslation("message.openmodularturrets.card_skipped." + part));
        }
    }

    private static void notify(EntityPlayer player, String key) {
        player.sendStatusMessage(new TextComponentTranslation("message.openmodularturrets." + key), true);
    }
}
