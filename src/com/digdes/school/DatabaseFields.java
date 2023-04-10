package com.digdes.school;

public enum DatabaseFields {
    ID("id"),
    LASTNAME("lastName"),
    AGE("age"),
    ACTIVE("active"),
    COST("cost");

    private final String key;

    DatabaseFields(String key) {
        this.key = key;
    }

    public String getFieldName() {
        return key;
    }
}
