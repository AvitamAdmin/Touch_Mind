package com.cheil.excel;

public class ObjectWapper {
    private Double doubleValue;
    private String stringValue;
    private Boolean booleanValue;

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Boolean isBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public String getValue() {
        String value = null;
        if (this.booleanValue != null) {
            value = isBooleanValue().toString();
        }
        if (this.stringValue != null) {
            return getStringValue();
        }
        if (this.doubleValue != null) {
            if (this.doubleValue % 1 == 0) {
                value = String.valueOf((int) getDoubleValue().doubleValue());
            } else {
                value = String.valueOf(this.doubleValue);
            }
        }
        return value;
    }

}
