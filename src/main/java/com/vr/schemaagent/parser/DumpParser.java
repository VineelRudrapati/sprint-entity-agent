package com.vr.schemaagent.parser;

import com.vr.schemaagent.model.ColumnMeta;
import com.vr.schemaagent.model.RelationMeta;
import com.vr.schemaagent.model.RelationType;
import com.vr.schemaagent.model.SchemaMeta;
import com.vr.schemaagent.model.TableMeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DumpParser {

    private static final Pattern TABLE_PATTERN = Pattern.compile(
            "CREATE TABLE\\s+(?:IF NOT EXISTS\\s+)?(?:\"?\\w+\"?\\.)?\"?(\\w+)\"?\\s*\\((.*?)\\);",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final Pattern COLUMN_PATTERN = Pattern.compile(
            "^\\s*\"?(\\w+)\"?\\s+([^,]+?)(?:,\\s*)?$$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PK_INLINE_PATTERN = Pattern.compile(
            "PRIMARY KEY",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PK_TABLE_PATTERN = Pattern.compile(
            "PRIMARY KEY\\s*\\(([^)]+)\\)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern FK_INLINE_PATTERN = Pattern.compile(
            "REFERENCES\\s+\"?(\\w+)\"?\\s*\\(\"?(\\w+)\"?\\)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern FK_TABLE_PATTERN = Pattern.compile(
            "FOREIGN KEY\\s*\\(([^)]+)\\)\\s*REFERENCES\\s+\"?(\\w+)\"?\\s*\\(\"?(\\w+)\"?\\)",
            Pattern.CASE_INSENSITIVE
    );

    public SchemaMeta parse(String sql) {
        SchemaMeta schema = new SchemaMeta();
        Matcher tableMatcher = TABLE_PATTERN.matcher(sql);

        while (tableMatcher.find()) {
            String tableName = tableMatcher.group(1);
            String tableBody = tableMatcher.group(2);

            TableMeta table = new TableMeta(tableName);
            parseColumns(tableBody, table);
            parseTableLevelConstraints(tableBody, table);
            schema.addTable(table);
        }

        return schema;
    }

    private void parseColumns(String body, TableMeta table) {
        String[] lines = body.split("\\r?\\n");

        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty() || line.toUpperCase().startsWith("CONSTRAINT")
                    || line.toUpperCase().startsWith("PRIMARY KEY")
                    || line.toUpperCase().startsWith("FOREIGN KEY")) {
                continue;
            }

            if (line.endsWith(",")) {
                line = line.substring(0, line.length() - 1).trim();
            }

            Matcher matcher = COLUMN_PATTERN.matcher(line);
            if (!matcher.find()) {
                continue;
            }

            String name = matcher.group(1);
            String typeDefinition = matcher.group(2).trim();
            boolean primaryKey = PK_INLINE_PATTERN.matcher(typeDefinition).find();

            typeDefinition = typeDefinition.replaceAll("(?i)PRIMARY KEY", "").trim();
            table.addColumn(new ColumnMeta(name, typeDefinition, primaryKey));
            parseInlineForeignKey(line, table, name);
        }
    }

    private void parseInlineForeignKey(String line, TableMeta table, String sourceColumn) {
        Matcher matcher = FK_INLINE_PATTERN.matcher(line);

        if (matcher.find()) {
            String targetTable = matcher.group(1);
            String targetColumn = matcher.group(2);

            table.addRelation(new RelationMeta(
                    table.getName(),
                    sourceColumn,
                    targetTable,
                    targetColumn,
                    RelationType.MANY_TO_ONE
            ));
        }
    }

    private void parseTableLevelConstraints(String body, TableMeta table) {
        Matcher fkMatcher = FK_TABLE_PATTERN.matcher(body);

        while (fkMatcher.find()) {
            String sourceColumns = fkMatcher.group(1).trim();
            String targetTable = fkMatcher.group(2);
            String targetColumn = fkMatcher.group(3).trim();

            if (!sourceColumns.contains(",")) {
                String sourceColumn = sourceColumns.replaceAll("\"", "").trim();
                String targetColumnName = targetColumn.replaceAll("\"", "").trim();
                table.addRelation(new RelationMeta(
                        table.getName(),
                        sourceColumn,
                        targetTable,
                        targetColumnName,
                        RelationType.MANY_TO_ONE
                ));
            }
        }

        Matcher pkMatcher = PK_TABLE_PATTERN.matcher(body);
        while (pkMatcher.find()) {
            String columnsGroup = pkMatcher.group(1);
            for (String column : columnsGroup.split("\\s*,\\s*")) {
                table.markPrimaryKey(column.replaceAll("\"", "").trim());
            }
        }
    }
}
