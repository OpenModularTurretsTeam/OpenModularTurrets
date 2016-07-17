package openmodularturrets.ucdefinitions;

import api.undercurrent.iface.UCCollection;
import api.undercurrent.iface.UCTileDefinition;
import api.undercurrent.iface.editorTypes.BooleanEditorType;
import api.undercurrent.iface.editorTypes.InfoEditorType;
import api.undercurrent.iface.editorTypes.IntegerEditorType;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.tileentity.turretbase.TurretBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niel on 7/17/2016.
 */
public class TurretBaseUCDefinition extends UCTileDefinition {

   public TurretBaseUCDefinition(TurretBase base) throws Exception {
        UCCollection info = new UCCollection("Turret Info");
        UCCollection ammo = new UCCollection("Turret Ammo");
        UCCollection targeting = new UCCollection("Targeting settings");
        UCCollection operations = new UCCollection("Base Operations");

        //Info
        info.getEditableFields().add(new InfoEditorType("ownerName", "Owner Name", "This turret base's owner", base.getOwnerName()));
        info.getEditableFields().add(new InfoEditorType("storedEnergy", "Stored Energy", "Amount of power stored in this turret base", String.valueOf(base.getEnergyStored(ForgeDirection.UNKNOWN))));

        //Ammo
        HashMap<String, Integer> totals = new HashMap<String, Integer>();
        for (int i = 0; i < base.getSizeInventory(); i++) {
            if (base.getStackInSlot(i) != null) {

                Integer totalSoFar = totals.get(base.getStackInSlot(i).getDisplayName());
                {
                    if (totalSoFar == null) {
                        totals.put(base.getStackInSlot(i).getDisplayName(), 0);
                    }
                }
                totals.put(base.getStackInSlot(i).getDisplayName(), totals.get(base.getStackInSlot(i).getDisplayName()) + base.getStackInSlot(i).stackSize);
            }
        }
        for (Map.Entry<String, Integer> entry : totals.entrySet()) {
            ammo.getEditableFields().add(new InfoEditorType(entry.getKey(), entry.getKey(), "", String.valueOf(entry.getValue())));
        }

        //Targeting
        targeting.getEditableFields().add(new BooleanEditorType("attacksMobs", "Attack Mobs", "If this turret base should target Mobs", base.isAttacksMobs()));
        targeting.getEditableFields().add(new BooleanEditorType("attacksNeutrals", "Attack Neutrals", "If this turret base should target Neutral Mobs", base.isAttacksNeutrals()));
        targeting.getEditableFields().add(new BooleanEditorType("attacksPlayers", "Attack Players", "If this turret base should target Players", base.isAttacksPlayers()));
        targeting.getEditableFields().add(new BooleanEditorType("multiTargeting", "Multi Targeting", "If this turret base should try to target multiple targets at once.", base.isMultiTargeting()));
        targeting.getEditableFields().add(new IntegerEditorType("yAxisDetect", base.getyAxisDetect(), "Y-Axis detect", "Amount of blocks below the base should serach for targets", 0, 9));

        //Operations
        operations.getEditableFields().add(new BooleanEditorType("active", "Active", "If this turret base is active", base.isActive()));

        getCollections().add(info);
        getCollections().add(ammo);
        getCollections().add(targeting);
        getCollections().add(operations);


    }
}

