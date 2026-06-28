package com.vr.schemaagent.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableMeta {
    private final String name;
    private final List<ColumnMeta> columns = new ArrayList<>();
    private final List<RelationMeta> relations = new ArrayList<>();

    public TableMeta(String name) {
        this.name = name;
    }

    public void addColumn(ColumnMeta column) {
        columns.add(column);
    }

    public Optional<ColumnMeta> getColumn(String name) {
        return columns.stream()
                .filter(column -> column.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public void markPrimaryKey(String columnName) {
        getColumn(columnName).ifPresent(column -> column.setPrimaryKey(true));
    }

    public RelationMeta findRelationForColumn(String columnName) {
        return relations.stream()
                .filter(relation -> relation.getSourceColumn().equalsIgnoreCase(columnName))
                .findFirst()
                .orElse(null);
    }

    public String getName() {
        return name;
    }

    public List<ColumnMeta> getColumns() {
        return columns;
    }

    public void addRelation(RelationMeta relation) {
        relations.add(relation);
    }

    public List<RelationMeta> getRelations() {
        return relations;
    }
}
