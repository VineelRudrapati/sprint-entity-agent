package com.vr.schemaagent.model;

public class RelationMeta {

    private final String sourceTable;
    private final String sourceColumn;
    private final String targetTable;
    private final String targetColumn;
    private final RelationType relationType;

    public RelationMeta(
            String sourceTable,
            String sourceColumn,
            String targetTable,
            String targetColumn,
            RelationType relationType
    ) {
        this.sourceTable = sourceTable;
        this.sourceColumn = sourceColumn;
        this.targetTable = targetTable;
        this.targetColumn = targetColumn;
        this.relationType = relationType;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public String getSourceColumn() {
        return sourceColumn;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public String getTargetColumn() {
        return targetColumn;
    }

    public RelationType getRelationType() {
        return relationType;
    }
}
