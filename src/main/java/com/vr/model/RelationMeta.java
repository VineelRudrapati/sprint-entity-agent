package com.vr.model;

public class RelationMeta {
    private final String field;
    private final String target;
    public RelationMeta(String field, String target) {
        this.field = field;
        this.target = target;
    }
    public String getField() {
        return field;
    }
    public String getTarget() {
        return target;
    }

}