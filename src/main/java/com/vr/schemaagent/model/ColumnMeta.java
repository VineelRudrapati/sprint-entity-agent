package com.vr.schemaagent.model;

public class ColumnMeta {
    private final String name;
    private final String type;
    private boolean primaryKey;
    private boolean unique;

    public ColumnMeta(String name, String type, boolean primaryKey, boolean unique) {
        this.name = name;
        this.type = type;
        this.primaryKey = primaryKey;
        this.unique = unique;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }
}
