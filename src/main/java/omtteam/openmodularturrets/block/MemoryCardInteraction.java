package omtteam.openmodularturrets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.defense.CamoTrapBlockEntity;
import omtteam.openmodularturrets.camo.CamoHost;
import omtteam.openmodularturrets.camo.CamoRules;
import omtteam.openmodularturrets.compat.block.BlockCompat;
import omtteam.openmodularturrets.core.card.MemoryCardData;
import omtteam.openmodularturrets.core.card.MemoryCardRules;
import omtteam.openmodularturrets.item.MemoryCardItem;
import omtteam.openmodularturrets.network.PacketValidation;
import omtteam.openmodularturrets.security.PlayerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * What a memory card does to a turret base or a camouflage trap. All permission checks happen here, on the server:
 * <ul>
 *     <li>turret base: copying and writing the settings (targeting, mode, trust list) needs the admin access level;
 *     the camouflage part follows the camouflage rules (disguise: owner, light: admin)</li>
 *     <li>camouflage trap: copying and writing (trust list, disguise, light) need an admin of the trap</li>
 * </ul>
 * Players without permission get nothing from a trap (it must not reveal itself), and a message from a base (its GUI
 * says the same).
 */
public final class MemoryCardInteraction {
    private MemoryCardInteraction() {
    }

    /**
     * Called before any block interaction. Sneaking copies, otherwise a card with data is written to the block; a
     * card without data lets the click through (opens the base GUI).
     */
    public static InteractionResult use(Player player, Level level, InteractionHand hand, BlockPos pos) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof MemoryCardItem) || player.isSpectator()) return InteractionResult.PASS;
        BlockEntity target = level.getBlockEntity(pos);
        if (!(target instanceof TurretBaseBlockEntity) && !(target instanceof CamoTrapBlockEntity)) {
            return InteractionResult.PASS;
        }
        boolean copy = player.isShiftKeyDown();
        Optional<MemoryCardData> data = MemoryCardItem.getData(stack);
        if (!copy && data.isEmpty()) return InteractionResult.PASS;
        if (player instanceof ServerPlayer serverPlayer) {
            if (target instanceof TurretBaseBlockEntity base) {
                if (copy) copyFromBase(serverPlayer, stack, base);
                else pasteToBase(serverPlayer, data.get(), base);
            } else if (copy) {
                copyFromTrap(serverPlayer, stack, (CamoTrapBlockEntity) target);
            } else {
                pasteToTrap(serverPlayer, data.get(), (CamoTrapBlockEntity) target);
            }
        }
        return BlockCompat.sidedSuccess(level.isClientSide());
    }

    // ---- Copy ----

    private static void copyFromBase(ServerPlayer player, ItemStack stack, TurretBaseBlockEntity base) {
        if (!MemoryCardRules.canCopy(base.getTrustManager(), PlayerAdapter.info(player))) {
            PacketValidation.sendNoPermission(player);
            return;
        }
        MemoryCardItem.setData(stack, new MemoryCardData(MemoryCardData.Source.TURRET_BASE,
                MemoryCardRules.capture(base.getLogic()), MemoryCardRules.captureTrust(base.getTrustManager()),
                base.isCamoEnabled() ? camoSettings(base) : null));
        notify(player, "card_copied");
    }

    private static void copyFromTrap(ServerPlayer player, ItemStack stack, CamoTrapBlockEntity trap) {
        if (!MemoryCardRules.canCopy(trap.getTrustManager(), PlayerAdapter.info(player))) return;
        MemoryCardItem.setData(stack, new MemoryCardData(MemoryCardData.Source.CAMO_TRAP, null,
                MemoryCardRules.captureTrust(trap.getTrustManager()), camoSettings(trap)));
        notify(player, "card_copied");
    }

    private static MemoryCardData.CamoSettings camoSettings(CamoHost host) {
        BlockState camo = host.getCamo();
        return new MemoryCardData.CamoSettings(camo == null ? null : CamoRules.idOf(camo),
                host.supportsLight() ? host.getLight() : null);
    }

    // ---- Paste ----

    private static void pasteToBase(ServerPlayer player, MemoryCardData data, TurretBaseBlockEntity base) {
        List<String> skipped = new ArrayList<>();
        boolean applied = false;
        if (MemoryCardRules.canPaste(base.getTrustManager(), PlayerAdapter.info(player))) {
            if (data.base() != null) {
                MemoryCardRules.apply(data.base(), base.getLogic());
            }
            MemoryCardRules.applyTrust(data.trust(), base.getTrustManager());
            applied = true;
        } else {
            skipped.add("settings");
        }
        if (data.camo() != null && base.isCamoEnabled()) {
            applied |= pasteCamo(player, data.camo(), base, skipped);
        }
        report(player, applied, skipped);
    }

    private static void pasteToTrap(ServerPlayer player, MemoryCardData data, CamoTrapBlockEntity trap) {
        if (!MemoryCardRules.canPaste(trap.getTrustManager(), PlayerAdapter.info(player))) return;
        MemoryCardRules.applyTrust(data.trust(), trap.getTrustManager());
        List<String> skipped = new ArrayList<>();
        if (data.camo() != null) {
            pasteCamo(player, data.camo(), trap, skipped);
        }
        report(player, true, skipped);
    }

    /** Disguise and light, each only if the player may change it. */
    private static boolean pasteCamo(ServerPlayer player, MemoryCardData.CamoSettings camo, CamoHost host, List<String> skipped) {
        boolean applied = false;
        if (host.canSetCamo(player)) {
            BlockState disguise = camo.disguise() == null ? null : CamoRules.disguiseById(camo.disguise());
            if (camo.disguise() == null || disguise != null) {
                host.setCamo(disguise);
                applied = true;
            } else {
                skipped.add("disguise_unavailable");
            }
        } else {
            skipped.add("disguise");
        }
        if (camo.light() != null && host.supportsLight()) {
            if (host.canChangeLight(player)) {
                host.setLight(camo.light());
                applied = true;
            } else {
                skipped.add("light");
            }
        }
        return applied;
    }

    private static void report(ServerPlayer player, boolean applied, List<String> skipped) {
        if (!applied) {
            PacketValidation.sendNoPermission(player);
            return;
        }
        notify(player, "card_pasted");
        for (String part : skipped) {
            player.sendSystemMessage(Component.translatable("message.openmodularturrets.card_skipped." + part));
        }
    }

    private static void notify(ServerPlayer player, String key) {
        player.displayClientMessage(Component.translatable("message.openmodularturrets." + key), true);
    }
}
