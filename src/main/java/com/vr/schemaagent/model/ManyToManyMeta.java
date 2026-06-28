package com.vr.schemaagent.model;

public class ManyToManyMeta {

    private final String leftTable;
    private final String rightTable;
    private final String leftJoinColumn;
    private final String rightJoinColumn;
    private final String joinTable;

    public ManyToManyMeta(String leftTable, String rightTable, String leftJoinColumn, String rightJoinColumn, String joinTable) {
        this.leftTable = leftTable;
        this.rightTable = rightTable;
        this.leftJoinColumn = leftJoinColumn;
        this.rightJoinColumn = rightJoinColumn;
        this.joinTable = joinTable;
    }

    public String getLeftTable() {
        return leftTable;
    }

    public String getRightTable() {
        return rightTable;
    }

    public String getLeftJoinColumn() {
        return leftJoinColumn;
    }

    public String getRightJoinColumn() {
        return rightJoinColumn;
    }

    public String getJoinTable() {
        return joinTable;
    }
}
