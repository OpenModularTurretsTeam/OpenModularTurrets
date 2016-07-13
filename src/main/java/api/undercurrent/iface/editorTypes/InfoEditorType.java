package api.undercurrent.iface.editorTypes;

/**
 * Created by Niel on 10/16/2015.
 */
public class InfoEditorType extends EditorType {

    public InfoEditorType(String fieldName, String displayName, String displayDescription, String fieldValue) throws Exception {
        super(EditorTypes.INFO);
        this.fieldName = fieldName;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.fieldValue = fieldValue;
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

    public String getFieldValue() {
        return (String) fieldValue;
    }

    @Override
    public boolean validateValue(Object obj) throws Exception {
        return false;
    }
}

