package api.undercurrent.iface.editorTypes;

import java.util.List;

/**
 * Created by Niel on 10/16/2015.
 */
public class ComboBoxEditorType extends EditorType {

    private List<String> comboChoices;

    public ComboBoxEditorType(String fieldName, String fieldValue, String displayName, String displayDescription, List<String> comboChoices) throws Exception {
        super(EditorTypes.COMBO);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.comboChoices = comboChoices;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return (String) fieldValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public List<String> getComboChoices() {
        return comboChoices;
    }

    @Override
    public boolean validateValue(Object obj) throws Exception {
        if(comboChoices.contains(obj))
        {
            return true;
        }
        return false;
    }

}
