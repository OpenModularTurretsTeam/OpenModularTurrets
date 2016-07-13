package api.undercurrent.iface.editorTypes;

/**
 * Created by Niel on 10/16/2015.
 */
public class StringEditorType extends EditorType {

    private int minLength;
    private int maxLength;

    public StringEditorType(String fieldName, String fieldValue, String displayName, String displayDescription, int minLength, int maxLength) {
        super(EditorTypes.STRING);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return (String) fieldValue;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public boolean validateValue(Object obj) throws Exception {

        try {
            String objcast = String.valueOf(obj);

            if (objcast.length() > getMaxLength()) {
                return false;
            }

            if (objcast.length() < getMinLength()) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}