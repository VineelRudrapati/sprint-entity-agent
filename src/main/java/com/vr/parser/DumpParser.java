package com.vr.parser;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.vr.model.ColumnMeta;
import com.vr.model.RelationMeta;
import com.vr.model.TableMeta;
public class DumpParser {
    public List<TableMeta> extractTables(String sql) {
        List<TableMeta> tables = new ArrayList<>();
        Pattern p = Pattern.compile("CREATE TABLE\\s+(\\w+)\\s*\\((.*?)\\);", Pattern.DOTALL);
        Matcher m = p.matcher(sql);
        while (m.find()) {
            String tableName = m.group(1);
            String body = m.group(2);
            TableMeta table = new TableMeta(tableName);
            String[] lines = body.split(",");
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("CONSTRAINT")) continue;
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    table.addColumn(
                            new ColumnMeta(parts[0], parts[1])
                    );
                }
            }
            Pattern fk =
                    Pattern.compile("(\\w+)_id");
            Matcher fkMatcher = fk.matcher(body);
            while (fkMatcher.find()) {
                String target = fkMatcher.group(1);
                table.addRelation(
                        new RelationMeta(target + "Id", target)
                );

            }
            tables.add(table);
        }
        return tables;
    }

}
