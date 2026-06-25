package com.vr.model;

import java.util.ArrayList;
import java.util.List;

public class TableMeta {
    private final String name;
    private final List<ColumnMeta> columns = new ArrayList<>();
    private List<RelationMeta> relations= new ArrayList<>();
    public TableMeta(String name) {
        this.name = name;
    }
    public void addColumn(ColumnMeta column) {
        columns.add(column);
    }
    public String getName() {
        return name;
    }
    public List<ColumnMeta> getColumns() {
        return columns;
    }
    public void addRelation(RelationMeta r) {
        relations.add(r);
    }
    public List<RelationMeta> getRelations() {
        return relations;
    }
}