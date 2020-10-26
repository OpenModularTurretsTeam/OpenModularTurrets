package omtteam.openmodularturrets.api.tileentity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import omtteam.openmodularturrets.api.network.IBaseController;

import javax.annotation.Nullable;
import java.util.List;

public interface ITurretBase { //TODO: javadoc
    void updateMaxRange();

    void turretResetCaches();

    boolean isAttacksMobs();

    void setAttacksMobs(boolean attacksMobs);

    boolean isAttacksNeutrals();

    void setAttacksNeutrals(boolean attacksNeutrals);

    boolean isAttacksPlayers();

    void setAttacksPlayers(boolean attacksPlayers);

    boolean isMultiTargeting();

    void setMultiTargeting(boolean multiTargeting);

    int getTier();

    void setTier(int tier);

    boolean isComputerAccessible();

    void increaseKillCounter();

    void increasePlayerKillCounter();

    int getKills();

    void setKills(int kills);

    int getPlayerKills();

    void setPlayerKills(int playerKills);

    int getRange();

    void setRange(int range);

    int getMaxRange();

    void setMaxRange(int range);

    @Nullable
    IBaseController getController();

    /**
     * Register the specified IBaseController to let it control the bases settings.
     *
     * @param controller the controller that should control the bases settings
     */
    boolean registerController(IBaseController controller);

    /**
     * List of all entities around the turret base in its range.
     *
     * @return List of EntityLivingBase
     */
    List<EntityLivingBase> getEntitiesWithinRange();

    /**
     * Rotate all turrets on the base to specified yaw and pitch.
     *
     * @param yaw   yaw to rotate to
     * @param pitch pitch to rotate to
     */
    void setAllTurretsYawPitch(float yaw, float pitch);

    /**
     * Rotate specific turret on the base to given yaw and pitch.
     *
     * @param facing TODO: another identification method?
     * @param yaw    yaw to rotate to
     * @param pitch  pitch to rotate to
     */
    boolean setTurretYawPitch(EnumFacing facing, float yaw, float pitch);

    /**
     * Set all turrets on the base to given autofire state.
     *
     * @param state autofire state
     */
    void setAllTurretsForceFire(boolean state);

    /**
     * Set specific turret on the base to given autofire state.
     *
     * @param facing the side the turret is on
     * @param state  autofire state
     */
    boolean setTurretForceFire(EnumFacing facing, boolean state);

    /**
     * Shoot specific turret on the base manually.
     *
     * @param facing the side the turret is on
     * @return if shooting was successful
     */
    boolean forceShootTurret(EnumFacing facing);

    /**
     * Shoot specific turret on the base manually.
     *
     * @return number of turrets that fired successfully
     */
    int forceShootAllTurrets();

    /**
     * This is used for inventory manipulation for ammo etc.
     *
     * @return the internal inventory of the base and all inventory expanders inventories
     */
    List<IItemHandler> getAmmoInventories();
}
