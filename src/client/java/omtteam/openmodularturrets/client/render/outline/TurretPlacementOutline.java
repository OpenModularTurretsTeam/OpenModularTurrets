package omtteam.openmodularturrets.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.client.compat.ClientCompat;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.core.base.TurretPlacement;
import omtteam.openmodularturrets.core.turret.TurretType;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * While the player holds a turret and looks at a block next to a turret base, outlines where the turret would go:
 * cyan if it can be placed there, red if not (1.12.2 OMLib {@code ClientEventHandler.drawBlockOutline}), with a small
 * label saying why ({@link TurretPlacement.Result}). The loader client entrypoints call {@link #render} after the
 * translucent blocks and {@link #renderLabel} as late as their loader allows (Fabric: last, NeoForge and Forge: after
 * the particles).
 */
public final class TurretPlacementOutline {
    /** Half the width of the outline across the mount axis (1.12.2: 0.6 wide, a full block long). */
    private static final double HALF_WIDTH = 0.3D;
    private static final float ALPHA = 0.5F;
    /** The label: distance from the middle of the block towards the camera, size and colours. */
    private static final double LABEL_OFFSET = 0.5D;
    private static final float LABEL_SCALE = 0.01F;
    private static final int LABEL_COLOR = 0xFFFF5555;
    private static final int LABEL_BACKGROUND = 0x40000000;
    private static final Component BLOCKED = Component.translatable("message.openmodularturrets.placement.blocked");

    /** The reason found by the last {@link #render}, drawn by {@link #renderLabel} at the end of the same frame. */
    @Nullable
    private static Label pending;

    private record Label(BlockPos pos, Component text) {
    }

    private TurretPlacementOutline() {
    }

    /**
     * Draws the outline; called after the translucent blocks, with the world's pose stack and the camera position.
     * Remembers the reason for {@link #renderLabel}.
     */
    public static void render(PoseStack poseStack, Vec3 camera) {
        pending = null;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        if (poseStack == null || player == null || level == null
                || !(minecraft.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BlockItem item) || !(item.getBlock() instanceof TurretBlock turret)) return;

        BlockPlaceContext context = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, hit);
        BlockPos pos = context.getClickedPos();
        BlockState state = context.canPlace() ? turret.getStateForPlacement(context) : null;
        Direction facing = state != null ? state.getValue(TurretBlock.FACING) : baseFacing(level, pos, hit.getDirection());
        if (facing == null) return;

        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        float red = state != null ? 0F : 1F;
        float greenBlue = state != null ? 1F : 0F;
        LevelRenderer.renderLineBox(poseStack, buffers.getBuffer(RenderType.lines()), outline(pos, facing.getAxis()),
                red, greenBlue, greenBlue, ALPHA);
        poseStack.popPose();
        buffers.endBatch(RenderType.lines());
        if (state == null) {
            Component reason = context.canPlace() ? reason(level, pos, facing, turret) : BLOCKED;
            if (reason != null) pending = new Label(pos, reason);
        }
    }

    /**
     * Draws the reason of a red outline facing the camera (like a name tag), without depth test so blocks do not hide
     * it: called late in the level rendering, with the world's pose stack and the camera position. It sits half a
     * block from the middle of the turret's block towards the camera, so the box lines do not cross it.
     */
    public static void renderLabel(PoseStack poseStack, Vec3 camera) {
        Label label = pending;
        pending = null;
        if (label == null || poseStack == null) return;
        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        Vec3 centre = Vec3.atCenterOf(label.pos());
        Vec3 at = centre.add(camera.subtract(centre).normalize().scale(LABEL_OFFSET));
        Font font = minecraft.font;
        poseStack.pushPose();
        poseStack.translate(at.x - camera.x, at.y - camera.y, at.z - camera.z);
        ClientCompat.faceCamera(poseStack, minecraft.gameRenderer.getMainCamera(), LABEL_SCALE);
        font.drawInBatch(label.text(), -font.width(label.text()) / 2F, 0, LABEL_COLOR, false, poseStack.last().pose(),
                buffers, Font.DisplayMode.SEE_THROUGH, LABEL_BACKGROUND, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
        buffers.endBatch();
    }

    /** Why the turret cannot go onto the base behind {@code pos}, with the limit that stops it. */
    @Nullable
    private static Component reason(ClientLevel level, BlockPos pos, Direction facing, TurretBlock turret) {
        TurretPlacement.Result result = turret.attachResult(level, pos, facing);
        if (result == null || result.allowed()
                || !(level.getBlockEntity(pos.relative(facing.getOpposite())) instanceof TurretBaseBlockEntity base)) {
            return null;
        }
        TurretType type = turret.getTurretType();
        String key = "message.openmodularturrets.placement." + result.name().toLowerCase(Locale.ROOT);
        return switch (result) {
            case TIER_TOO_LOW -> Component.translatable(key, type.getRequiredBaseTier());
            case BASE_FULL -> Component.translatable(key,
                    ConfigLogicSettings.INSTANCE.baseTier(base.getLogic().getTier()).maxTurrets());
            case TYPE_LIMIT -> Component.translatable(key, ConfigLogicSettings.INSTANCE.turret(type).maxSimultaneous());
            default -> Component.translatable(key);
        };
    }

    /**
     * The direction away from a base next to {@code pos}, for the red outline: the clicked base first, like
     * {@link TurretBlock#getStateForPlacement}; {@code null} if no base is next to {@code pos} (then nothing is drawn).
     */
    @Nullable
    private static Direction baseFacing(ClientLevel level, BlockPos pos, Direction clicked) {
        if (level.getBlockEntity(pos.relative(clicked.getOpposite())) instanceof TurretBaseBlockEntity) return clicked;
        for (Direction direction : Direction.values()) {
            if (level.getBlockEntity(pos.relative(direction)) instanceof TurretBaseBlockEntity) {
                return direction.getOpposite();
            }
        }
        return null;
    }

    /** A box through the middle of {@code pos}, a full block long along {@code axis}. */
    private static AABB outline(BlockPos pos, Direction.Axis axis) {
        double min = 0.5D - HALF_WIDTH;
        double max = 0.5D + HALF_WIDTH;
        return new AABB(
                axis == Direction.Axis.X ? 0 : min, axis == Direction.Axis.Y ? 0 : min, axis == Direction.Axis.Z ? 0 : min,
                axis == Direction.Axis.X ? 1 : max, axis == Direction.Axis.Y ? 1 : max, axis == Direction.Axis.Z ? 1 : max)
                .move(pos);
    }
}
