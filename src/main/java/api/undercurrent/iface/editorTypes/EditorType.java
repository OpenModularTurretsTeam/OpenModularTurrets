package api.undercurrent.iface.editorTypes;

/**
 * Created by Niel on 10/16/2015.
 */
public abstract class EditorType {

    public EditorTypes editorType;
    public String fieldName;
    public String displayName;
    public String displayDescription;
    public Object fieldValue;

    public EditorType(EditorTypes editorType) {
        this.editorType = editorType;
    }

    public abstract boolean validateValue(Object obj) throws Exception;

    public abstract Object getFieldValue();

    public enum EditorTypes {
        STRING, INT, DOUBLE, FLOAT, BOOLEAN, INFO, COMBO, CUSTOM
    }

    public EditorTypes getEditorType() {
        return editorType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

}
