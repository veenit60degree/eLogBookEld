package com.models;

public class SupportModel {

    String SupportDetailId;
    String Key;
    String Value;
    int KeyType;
    boolean IsActive;
    String CreatedDate;

    public SupportModel(String supportDetailId, String key, String value, int keyType, boolean isActive, String createdDate) {
        SupportDetailId = supportDetailId;
        Key = key;
        Value = value;
        KeyType = keyType;
        IsActive = isActive;
        CreatedDate = createdDate;
    }

    public String getSupportDetailId() {
        return SupportDetailId;
    }

    public String getKey() {
        return Key;
    }

    public String getValue() {
        return Value;
    }

    public int getKeyType() {
        return KeyType;
    }

    public boolean isActive() {
        return IsActive;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }
}
