package omtteam.openmodularturrets.client.render.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

/** What the projectile renderers need of a projectile: where it points and how old it is. */
public class ProjectileRenderState extends EntityRenderState {
    public float yRot;
    public float xRot;
    public int tickCount;
}
