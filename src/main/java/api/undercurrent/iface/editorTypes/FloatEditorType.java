package api.undercurrent.iface.editorTypes;

import java.lang.Float;

public class FloatEditorType extends EditorType {

    private int fieldPrecision;
    private double maxValue;
    private double minValue;

    public FloatEditorType(String fieldName, float fieldValue, String displayName, String displayDescription, int fieldPrecision, float maxValue, float minValue) throws Exception {
        super(EditorTypes.FLOAT);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.fieldPrecision = fieldPrecision;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return (Float) fieldValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public int getFieldPrecision() {
        return fieldPrecision;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    @Override
    public boolean validateValue(Object obj) throws Exception {
        try {
            double objcast = Float.valueOf(String.valueOf(obj));

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
