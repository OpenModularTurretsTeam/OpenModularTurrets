package omtteam.openmodularturrets.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Turret model geometry, converted box by box from the 1.12.2 {@code client/render/models/Model*} classes by a
 * script (same pivots, rotations, texture offsets and mirror flags). Models are Y-down with the base plate at the
 * bottom; {@link omtteam.openmodularturrets.client.render.blockentity.TurretRenderer} orients them.
 */
@SuppressWarnings("unused")
public final class TurretModelDefinitions {
    private TurretModelDefinitions() {
    }

    /** 1.12.2 {@code ModelMachineGun}. */
    public static LayerDefinition machineGunTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 37).mirror(true).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                .texOffs(0, 28).mirror(true).addBox(-2.0F, 4.0F, -2.0F, 4, 4, 4)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition holder = base.addOrReplaceChild("holder", CubeListBuilder.create()
                .texOffs(0, 0).mirror(true).addBox(-4.0F, -2.0F, 0.0F, 8, 1, 1)
                .texOffs(0, 15).mirror(true).addBox(-4.0F, 3.0F, -4.0F, 8, 1, 8)
                , PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition boxright_r1 = holder.addOrReplaceChild("boxright_r1", CubeListBuilder.create()
                .texOffs(0, 15).mirror(true).addBox(-12.0F, 4.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 15).mirror(true).addBox(-12.0F, -5.0F, -4.0F, 8, 1, 8)
                , PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 1.5708F));
        PartDefinition weapon = holder.addOrReplaceChild("weapon", CubeListBuilder.create()
                .texOffs(32, 21).mirror(true).addBox(-1.0F, 0.0F, -15.0F, 2, 2, 14)
                .texOffs(36, 0).mirror(true).addBox(-2.0F, -5.0F, -2.0F, 4, 8, 8)
                , PartPose.offset(0.0F, -1.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** 1.12.2 {@code ModelDisposableItemTurret}. */
    public static LayerDefinition disposableTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 37).mirror(true).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                .texOffs(0, 28).mirror(true).addBox(-2.0F, 4.0F, -2.0F, 4, 4, 4)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition holder = base.addOrReplaceChild("holder", CubeListBuilder.create()
                .texOffs(0, 19).mirror(true).addBox(-4.0F, 3.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 0).mirror(true).addBox(-4.0F, -2.0F, 0.0F, 8, 1, 1)
                , PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition boxleft_r1 = holder.addOrReplaceChild("boxleft_r1", CubeListBuilder.create()
                .texOffs(0, 19).mirror(true).addBox(-12.0F, 4.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 19).mirror(true).addBox(-12.0F, -5.0F, -4.0F, 8, 1, 8)
                , PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 1.5708F));
        PartDefinition weapon = holder.addOrReplaceChild("weapon", CubeListBuilder.create()
                .texOffs(20, 0).mirror(true).addBox(-2.0F, -3.0F, -12.0F, 4, 4, 14)
                , PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** 1.12.2 {@code ModelPotatoCannonTurret}. */
    public static LayerDefinition potatoCannonTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 28).mirror(true).addBox(-2.0F, 4.0F, -2.0F, 4, 4, 4)
                .texOffs(0, 37).mirror(true).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition holder = base.addOrReplaceChild("holder", CubeListBuilder.create()
                .texOffs(0, 15).mirror(true).addBox(-4.0F, 3.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 0).mirror(true).addBox(-4.0F, -2.0F, 0.0F, 8, 1, 1)
                , PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition boxleft_r1 = holder.addOrReplaceChild("boxleft_r1", CubeListBuilder.create()
                .texOffs(0, 15).mirror(true).addBox(-12.0F, 4.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 15).mirror(true).addBox(-12.0F, -5.0F, -4.0F, 8, 1, 8)
                , PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 1.5708F));
        PartDefinition weapon = holder.addOrReplaceChild("weapon", CubeListBuilder.create()
                .texOffs(0, 4).mirror(true).addBox(-2.0F, -3.0F, 1.0F, 4, 4, 4)
                .texOffs(36, 0).mirror(true).addBox(-1.0F, -2.0F, -11.0F, 2, 3, 12)
                , PartPose.offset(0.0F, -1.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** 1.12.2 {@code ModelCrossbowTurret}. */
    public static LayerDefinition crossbowTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 0).mirror(false).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                .texOffs(0, 23).mirror(false).addBox(-2.0F, 4.0F, -2.0F, 4, 3, 4)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition holder = base.addOrReplaceChild("holder", CubeListBuilder.create()
                .texOffs(0, 30).mirror(false).addBox(-4.0F, -1.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 39).mirror(false).addBox(4.0F, -8.0F, -4.0F, 1, 8, 8)
                .texOffs(24, 31).mirror(false).addBox(-5.0F, -8.0F, -4.0F, 1, 8, 8)
                .texOffs(34, 30).mirror(false).addBox(1.5F, -6.0F, 1.0F, 3, 1, 1)
                .texOffs(40, 30).mirror(false).addBox(-4.5F, -6.0F, 1.0F, 3, 1, 1)
                , PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition weapon = holder.addOrReplaceChild("weapon", CubeListBuilder.create()
                .texOffs(20, 13).mirror(false).addBox(-1.5F, -0.5F, -11.5F, 3, 1, 14)
                .texOffs(12, 23).mirror(false).addBox(-3.5F, -0.5F, -13.5F, 7, 1, 2)
                .texOffs(5, 1).mirror(false).addBox(3.5F, -0.5F, -12.5F, 2, 1, 1)
                .texOffs(0, 2).mirror(false).addBox(4.5F, -0.5F, -11.5F, 2, 1, 1)
                .texOffs(0, 4).mirror(false).addBox(-5.5F, -0.5F, -12.5F, 2, 1, 1)
                .texOffs(0, 0).mirror(false).addBox(-6.5F, -0.5F, -11.5F, 2, 1, 1)
                .texOffs(0, 13).mirror(false).addBox(-6.0F, 0.0F, -10.5F, 12, 0, 10)
                , PartPose.offset(0.0F, -5.5F, 1.5F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** 1.12.2 {@code ModelIncendiaryTurret}. */
    public static LayerDefinition incendiaryTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 37).mirror(true).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                .texOffs(0, 28).mirror(true).addBox(-2.0F, 3.0F, -2.0F, 4, 4, 4)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition holder = base.addOrReplaceChild("holder", CubeListBuilder.create()
                .texOffs(0, 15).mirror(true).addBox(-4.0F, 3.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 0).mirror(true).addBox(-4.0F, -2.0F, 0.0F, 8, 1, 1)
                , PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition boxleft_r1 = holder.addOrReplaceChild("boxleft_r1", CubeListBuilder.create()
                .texOffs(0, 15).mirror(true).addBox(-12.0F, 4.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 15).mirror(true).addBox(-12.0F, -5.0F, -4.0F, 8, 1, 8)
                , PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 1.5708F));
        PartDefinition weapon = holder.addOrReplaceChild("weapon", CubeListBuilder.create()
                .texOffs(0, 0).mirror(true).addBox(1.0F, -1.0F, -10.0F, 2, 2, 11)
                .texOffs(0, 0).mirror(true).addBox(-3.0F, -1.0F, -10.0F, 2, 2, 11)
                .texOffs(29, 0).mirror(true).addBox(-3.0F, -2.0F, -6.0F, 6, 4, 10)
                , PartPose.offset(0.0F, -1.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** 1.12.2 {@code ModelGrenadeLauncher}. */
    public static LayerDefinition grenadeTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 28).mirror(true).addBox(-2.0F, 4.0F, -2.0F, 4, 4, 4)
                .texOffs(0, 37).mirror(true).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition holder = base.addOrReplaceChild("holder", CubeListBuilder.create()
                .texOffs(0, 0).mirror(true).addBox(-4.0F, -2.0F, 0.0F, 8, 1, 1)
                .texOffs(0, 15).mirror(true).addBox(-4.0F, 3.0F, -4.0F, 8, 1, 8)
                , PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition boxright_r1 = holder.addOrReplaceChild("boxright_r1", CubeListBuilder.create()
                .texOffs(0, 15).mirror(true).addBox(-12.0F, -5.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 15).mirror(true).addBox(-12.0F, 4.0F, -4.0F, 8, 1, 8)
                , PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 1.5708F));
        PartDefinition weapon = holder.addOrReplaceChild("weapon", CubeListBuilder.create()
                .texOffs(0, 5).mirror(true).addBox(-1.0F, -6.0F, -7.0F, 2, 2, 4)
                .texOffs(32, 0).mirror(true).addBox(-2.0F, -4.0F, -8.0F, 4, 4, 12)
                .texOffs(35, 20).mirror(true).addBox(-3.0F, -5.0F, 3.0F, 6, 6, 6)
                , PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** 1.12.2 {@code ModelRocketTurret}. */
    public static LayerDefinition rocketTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 37).mirror(true).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                .texOffs(0, 28).mirror(true).addBox(-2.0F, 4.0F, -2.0F, 4, 4, 4)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition holder = base.addOrReplaceChild("holder", CubeListBuilder.create()
                .texOffs(0, 0).mirror(true).addBox(-4.0F, -2.0F, 0.0F, 8, 1, 1)
                .texOffs(0, 15).mirror(true).addBox(-4.0F, 3.0F, -4.0F, 8, 1, 8)
                , PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition boxright_r1 = holder.addOrReplaceChild("boxright_r1", CubeListBuilder.create()
                .texOffs(0, 15).mirror(true).addBox(-12.0F, -5.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 15).mirror(true).addBox(-12.0F, 4.0F, -4.0F, 8, 1, 8)
                , PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 1.5708F));
        PartDefinition weapon = holder.addOrReplaceChild("weapon", CubeListBuilder.create()
                .texOffs(36, 0).mirror(true).addBox(-2.0F, -5.0F, -2.0F, 4, 8, 8)
                .texOffs(0, 6).mirror(true).addBox(-1.0F, 0.0F, -4.0F, 2, 2, 2)
                .texOffs(0, 6).mirror(true).addBox(-1.0F, -4.0F, -4.0F, 2, 2, 2)
                , PartPose.offset(0.0F, -1.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** 1.12.2 {@code ModelLaserTurret} (fixed: holder attached, pivots made relative). */
    public static LayerDefinition laserTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 37).mirror(true).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                .texOffs(0, 28).mirror(true).addBox(-2.0F, 4.0F, -2.0F, 4, 4, 4)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition holder = base.addOrReplaceChild("holder", CubeListBuilder.create()
                .texOffs(0, 15).mirror(true).addBox(-4.0F, 3.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 0).mirror(true).addBox(-4.0F, -2.0F, 0.0F, 8, 1, 1)
                , PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition boxleft_r1 = holder.addOrReplaceChild("boxleft_r1", CubeListBuilder.create()
                .texOffs(0, 15).mirror(true).addBox(-12.0F, -5.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 15).mirror(true).addBox(-12.0F, 4.0F, -4.0F, 8, 1, 8)
                , PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 1.5708F));
        PartDefinition weapon = holder.addOrReplaceChild("weapon", CubeListBuilder.create()
                .texOffs(20, 0).mirror(true).addBox(-2.0F, -7.0F, -3.0F, 4, 7, 4)
                .texOffs(0, 28).mirror(true).addBox(-2.0F, -5.0F, 0.9F, 4, 4, 4)
                .texOffs(37, 16).mirror(true).addBox(-1.0F, -4.0F, -8.1F, 2, 1, 7)
                .texOffs(37, 0).mirror(true).addBox(-1.0F, -2.0F, -12.1F, 2, 1, 10)
                .texOffs(39, 26).mirror(true).addBox(-1.0F, -6.0F, -6.1F, 2, 1, 7)
                , PartPose.offset(0.0F, 0.0F, 0.1F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** 1.12.2 {@code ModelRailgun}. */
    public static LayerDefinition railgunTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 0).mirror(true).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition weapon = base.addOrReplaceChild("weapon", CubeListBuilder.create()
                .texOffs(25, 27).mirror(true).addBox(-1.0F, 2.0F, -16.0F, 2, 1, 17)
                .texOffs(25, 45).mirror(true).addBox(1.0F, -1.0F, -16.0F, 1, 2, 17)
                .texOffs(25, 27).mirror(true).addBox(-1.0F, -1.0F, -16.0F, 2, 1, 17)
                .texOffs(25, 45).mirror(true).addBox(-2.0F, -1.0F, -16.0F, 1, 2, 17)
                .texOffs(0, 29).mirror(true).addBox(-3.0F, 2.0F, 0.0F, 6, 2, 6)
                .texOffs(0, 37).mirror(true).addBox(-3.0F, -3.0F, 1.0F, 6, 4, 6)
                .texOffs(0, 21).mirror(true).addBox(-1.0F, 1.0F, 3.0F, 2, 1, 1)
                .texOffs(0, 25).mirror(true).addBox(-6.0F, -0.9F, 0.0F, 12, 1, 1)
                .texOffs(0, 47).mirror(true).addBox(5.1F, -5.0F, -3.0F, 1, 8, 8)
                .texOffs(0, 47).mirror(true).addBox(-6.1F, -5.0F, -3.0F, 1, 8, 8)
                , PartPose.offset(0.0F, -1.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** 1.12.2 {@code ModelPlasmaLauncherTurret}. */
    public static LayerDefinition plasmaTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 0).mirror(false).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                .texOffs(38, 24).mirror(false).addBox(-2.0F, 4.0F, -2.0F, 4, 3, 4)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition holder = base.addOrReplaceChild("holder", CubeListBuilder.create()
                .texOffs(14, 15).mirror(false).addBox(-4.0F, -1.0F, -4.0F, 8, 1, 8)
                .texOffs(0, 30).mirror(false).addBox(-5.0F, -8.0F, -4.0F, 1, 8, 8)
                .texOffs(28, 28).mirror(false).addBox(4.0F, -8.0F, -4.0F, 1, 8, 8)
                .texOffs(22, 13).mirror(false).addBox(-4.0F, -6.0F, 0.0F, 8, 1, 1)
                , PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition weapon = holder.addOrReplaceChild("weapon", CubeListBuilder.create()
                .texOffs(38, 13).mirror(false).addBox(-2.0F, -2.0F, -3.0F, 4, 4, 6, new CubeDeformation(0.5F))
                .texOffs(36, 0).mirror(false).addBox(-1.0F, -1.0F, -12.0F, 2, 2, 9)
                .texOffs(12, 24).mirror(false).addBox(-2.0F, -2.0F, -11.0F, 4, 4, 8)
                .texOffs(0, 36).mirror(false).addBox(0.0F, -2.5F, -12.0F, 0, 5, 10)
                .texOffs(24, 44).mirror(false).addBox(-2.5F, 0.0F, -12.0F, 5, 0, 10)
                , PartPose.offset(0.0F, -5.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /**
     * Melee turret (no model in 1.12.2), laid out after its 128×128 textures: base plate and shaft stay still, the
     * {@code top} part (hub, cap and a flat 32×32 blade cross through the cap's middle) spins.
     */
    public static LayerDefinition meleeTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 32).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                .texOffs(0, 17).addBox(-2.0F, 4.0F, -2.0F, 4, 3, 4)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition top = base.addOrReplaceChild("top", CubeListBuilder.create()
                .texOffs(0, 10).addBox(-3.0F, 3.0F, -3.0F, 6, 1, 6)
                .texOffs(0, 0).addBox(-4.0F, 1.0F, -4.0F, 8, 2, 8)
                .texOffs(0, 0).addBox(-16.0F, 2.0F, -16.0F, 32, 0, 32)
                , PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    /** 1.12.2 {@code ModelArcTurret}. */
    public static LayerDefinition arcTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 0).mirror(false).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                .texOffs(0, 13).mirror(false).addBox(-3.0F, 3.0F, -3.0F, 6, 4, 6)
                .texOffs(0, 23).mirror(false).addBox(-0.5F, -6.0F, -0.5F, 1, 9, 1)
                .texOffs(0, 8).mirror(false).addBox(-1.0F, -8.0F, -1.0F, 2, 2, 2)
                .texOffs(18, 13).mirror(false).addBox(-2.0F, -1.25F, -2.0F, 4, 1, 4)
                .texOffs(0, 4).mirror(false).addBox(-1.5F, -3.25F, -1.5F, 3, 1, 3)
                .texOffs(0, 0).mirror(false).addBox(-1.5F, -5.0F, -1.5F, 3, 1, 3, new CubeDeformation(-0.25F))
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** 1.12.2 {@code ModelRelativisticTurret}. */
    public static LayerDefinition slowingTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(24, 0).mirror(true).addBox(-6.0F, -1.0F, -6.0F, 1, 8, 1)
                .texOffs(24, 0).mirror(true).addBox(-6.0F, -1.0F, 5.0F, 1, 8, 1)
                .texOffs(24, 0).mirror(true).addBox(5.0F, -1.0F, -6.0F, 1, 8, 1)
                .texOffs(24, 0).mirror(true).addBox(5.0F, -1.0F, 5.0F, 1, 8, 1)
                .texOffs(0, 0).mirror(true).addBox(-2.0F, 5.0F, -2.0F, 4, 2, 4)
                .texOffs(0, 37).mirror(true).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        PartDefinition crystal_r1 = base.addOrReplaceChild("crystal_r1", CubeListBuilder.create()
                .texOffs(0, 25).mirror(true).addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4)
                , PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.7854F, 0.0F, 0.6109F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** 1.12.2 {@code ModelTeleporterTurret}. */
    public static LayerDefinition teleporterTurret() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(0, 0).mirror(true).addBox(-2.0F, -3.0F, -2.0F, 4, 10, 4)
                .texOffs(0, 14).mirror(true).addBox(-5.0F, -2.0F, -2.0F, 1, 8, 4)
                .texOffs(0, 26).mirror(true).addBox(-2.0F, -2.0F, 4.0F, 4, 8, 1)
                .texOffs(0, 26).mirror(true).addBox(-2.0F, -2.0F, -5.0F, 4, 8, 1)
                .texOffs(0, 14).mirror(true).addBox(4.0F, -2.0F, -2.0F, 1, 8, 4)
                .texOffs(0, 51).mirror(true).addBox(-6.0F, -4.0F, -6.0F, 12, 1, 12)
                .texOffs(0, 37).mirror(true).addBox(-6.0F, 7.0F, -6.0F, 12, 1, 12)
                , PartPose.offset(0.0F, 16.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }
}
