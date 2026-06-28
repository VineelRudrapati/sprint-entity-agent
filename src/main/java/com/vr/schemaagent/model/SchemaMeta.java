package com.vr.schemaagent.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SchemaMeta {

    private List<TableMeta> tables;

    public SchemaMeta() {
        this.tables = new ArrayList<>();
    }

    public void addTable(TableMeta table) {
        tables.add(table);
    }

    public List<TableMeta> getTables() {
        return tables;
    }

    public Optional<TableMeta> findTable(String name) {
        return tables.stream()
                .filter(table -> table.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<ManyToManyMeta> detectManyToManyRelations() {
        List<ManyToManyMeta> result = new ArrayList<>();
        for (TableMeta table : tables) {
            if (!isJoinTable(table)) {
                continue;
            }

            RelationMeta first = table.getRelations().get(0);
            RelationMeta second = table.getRelations().get(1);
            result.add(new ManyToManyMeta(
                    first.getTargetTable(),
                    second.getTargetTable(),
                    first.getSourceColumn(),
                    second.getSourceColumn(),
                    table.getName()
            ));
        }
        return result;
    }

    public boolean isJoinTable(TableMeta table) {
        if (table.getRelations().size() != 2) {
            return false;
        }

        int nonFkColumns = 0;
        for (ColumnMeta column : table.getColumns()) {
            if (!table.getForeignKeyColumnNames().contains(column.getName())
                    && !column.getName().equalsIgnoreCase("id")) {
                nonFkColumns++;
            }
        }

        return nonFkColumns <= 1;
    }
}
