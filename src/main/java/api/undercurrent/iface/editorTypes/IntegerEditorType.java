package api.undercurrent.iface.editorTypes;
/**
 * Created by Niel on 10/16/2015.
 */
public class IntegerEditorType extends EditorType {

    private int minValue;
    private int maxValue;

    public IntegerEditorType(String fieldName, int fieldValue, String displayName, String displayDescription, int minValue, int maxValue) {
        super(EditorTypes.INT);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return (Integer) fieldValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public boolean validateValue(Object obj) throws Exception {
        try {
            double objcast = Integer.valueOf(String.valueOf(obj));

            if (objcast > getMaxValue()) {
                return false;
            }

            if (objcast < getMinValue()) {
                return false;
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
