package api.undercurrent.iface;

import java.util.ArrayList;

import api.undercurrent.iface.editorTypes.EditorType;

public class UCCollection {

    private String collectionName;
    private ArrayList<EditorType> editableFields;

    public UCCollection(String name) {
        this.collectionName = name;
        editableFields = new ArrayList<EditorType>();
    }

    public ArrayList<EditorType> getEditableFields() {
        return editableFields;
    }

    public String getCollectionName() {
        return collectionName;
    }
}
