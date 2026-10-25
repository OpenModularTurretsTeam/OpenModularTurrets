package omtteam.openmodularturrets.core.base;

import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.PlayerInfo;
import omtteam.openmodularturrets.core.target.EntityCategory;
import omtteam.openmodularturrets.core.target.TargetEntity;

import java.util.Optional;

/** Test entity at a fixed position. */
final class FakeTarget extends TargetEntity {
    /** A player's eye height. */
    private static final double EYE_HEIGHT = 1.62;
    private static int nextId;
    private final int id = nextId++;
    private final double x;
    private final double y;
    private final double z;
    private final PlayerInfo player;
    boolean creative;
    boolean alive = true;
    float health = 20;
    float maxHealth = 20;
    int armor;
    String typeId;
    EntityCategory category = EntityCategory.OTHER;
    PlayerIdentity owner;
    boolean tamedHorse;
    boolean slowed;

    FakeTarget(double x, double y, double z, PlayerInfo player) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = player;
        this.typeId = player != null ? "minecraft:player" : "minecraft:zombie";
    }

    static FakeTarget mob(double x, double y, double z, String typeId, EntityCategory category) {
        FakeTarget target = new FakeTarget(x, y, z, null);
        target.typeId = typeId;
        target.category = category;
        return target;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getTypeId() {
        return typeId;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public double getEyeX() {
        return x;
    }

    @Override
    public double getEyeY() {
        return y + EYE_HEIGHT;
    }

    @Override
    public double getEyeZ() {
        return z;
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public float getMaxHealth() {
        return maxHealth;
    }

    @Override
    public int getArmor() {
        return armor;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public Optional<PlayerInfo> getPlayer() {
        return Optional.ofNullable(player);
    }

    @Override
    public boolean isCreativeOrSpectator() {
        return creative;
    }

    @Override
    public Optional<PlayerIdentity> getOwner() {
        return Optional.ofNullable(owner);
    }

    @Override
    public boolean isTamedHorse() {
        return tamedHorse;
    }

    @Override
    public boolean hasSlowness() {
        return slowed;
    }

    @Override
    public EntityCategory getCategory() {
        return category;
    }
}
