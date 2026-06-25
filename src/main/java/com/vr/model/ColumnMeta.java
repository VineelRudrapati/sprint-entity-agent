package com.vr.model;
public class ColumnMeta {
    private String name;
    private String type;
    public ColumnMeta(
            String name,
            String type
    ){
        this.name=name;
        this.type=type;
    }
    public String getName(){
        return name;
    }
    public String getType(){
        return type;
    }
}