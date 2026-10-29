package omtteam.openmodularturrets.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import omtteam.omlib.compat.VersionCompat;
import omtteam.openmodularturrets.item.AddonItem;
import omtteam.openmodularturrets.item.ModuleToggles;
import omtteam.openmodularturrets.item.UpgradeItem;

import java.util.ArrayList;
import java.util.List;
//? if >=1.21.11 {
/*import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.data.AtlasIds;
*///?} else {
import net.minecraft.client.renderer.texture.TextureAtlas;
//?}

/**
 * Marks upgrades and addons that are disabled in the config ({@link ModuleToggles}): a red frame around their slot in
 * the machine GUIs, and vanilla's barrier sprite (a red "no" symbol) over their icon wherever an item is drawn in a GUI.
 * The icon overlay runs after the item decorations: a client mixin on Fabric, an item decorator on NeoForge and Forge.
 */
public final class DisabledModuleOverlay {
    private static final ResourceLocation SYMBOL = VersionCompat.id("minecraft", "item/barrier");
    private static final int FRAME_COLOR = 0xFFE01010;
    private static final int TINT_COLOR = 0x50FF0000;

    private DisabledModuleOverlay() {
    }

    /** Every upgrade and addon item, for the loaders' item decorator registration. */
    public static List<Item> moduleItems() {
        List<Item> items = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof UpgradeItem || item instanceof AddonItem) items.add(item);
        }
        return items;
    }

    /**
     * A red frame and tint behind every machine slot (not the player's inventory) that holds a disabled upgrade or
     * addon; call it from {@code renderBg}, after the background, so the item is drawn over it.
     */
    public static void renderSlotFrames(GuiGraphics graphics, AbstractContainerMenu menu, int left, int top) {
        for (Slot slot : menu.slots) {
            if (slot.container instanceof Inventory || !slot.isActive() || !ModuleToggles.isDisabled(slot.getItem())) {
                continue;
            }
            int x = left + slot.x;
            int y = top + slot.y;
            graphics.fill(x, y, x + 16, y + 16, TINT_COLOR);
            graphics.renderOutline(x - 1, y - 1, 18, 18, FRAME_COLOR);
        }
    }

    /** The "no" symbol over the icon of a disabled upgrade or addon at {@code x, y}; nothing for other stacks. */
    public static void renderItemOverlay(GuiGraphics graphics, ItemStack stack, int x, int y) {
        if (!ModuleToggles.isDisabled(stack)) return;
        //? if >=1.21.11 {
        /*TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.ITEMS).getSprite(SYMBOL);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, 16, 16);
        *///?} else {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(SYMBOL);
        // above the item (drawn around z 150), like the stack count
        graphics.blit(x, y, 200, 16, 16, sprite);
        //?}
    }
}
