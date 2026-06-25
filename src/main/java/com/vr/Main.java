package com.vr;
import com.vr.generator.EntityGenerator;
import com.vr.model.TableMeta;
import com.vr.parser.DumpParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
public class Main {

    static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Provide dump file");
            return;
        }
        String content = Files.readString(Path.of(args[0]));
        DumpParser parser = new DumpParser();
        List<TableMeta> tables = parser.extractTables(content);
        System.out.println("\nTables Found:\n");
        tables.forEach(System.out::println);
        System.out.println("\nGenerating Entities\n");
        EntityGenerator generator = new EntityGenerator();
        for (TableMeta table : tables) {
            generator.generate(table);
        }
    }
}
