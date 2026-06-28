package com.vr.schemaagent;

import com.vr.schemaagent.generator.EntityGenerator;
import com.vr.schemaagent.model.SchemaMeta;
import com.vr.schemaagent.model.TableMeta;
import com.vr.schemaagent.parser.DumpParser;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;

@Command(
        name = "schema-agent",
        mixinStandardHelpOptions = true,
        version = "schema-agent 1.0",
        description = "Generates Spring JPA entity classes from a PostgreSQL schema dump."
)
public class SchemaAgentApplication implements Runnable {

    @Parameters(index = "0", description = "The PostgreSQL schema dump file.")
    private Path schemaFile;

    @Option(names = {"-p", "--package"}, description = "Package name for generated entities.", defaultValue = "com.vr.entity")
    private String packageName;

    @Option(names = {"-o", "--output"}, description = "Output directory for generated files.", defaultValue = "output")
    private Path outputDirectory;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new SchemaAgentApplication()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        try {
            if (!Files.exists(schemaFile)) {
                System.err.println("Schema file not found: " + schemaFile);
                return;
            }

            String content = Files.readString(schemaFile);
            DumpParser parser = new DumpParser();
            SchemaMeta schema = parser.parse(content);

            System.out.println("Found " + schema.getTables().size() + " tables.");
            System.out.println("Generating entities into " + outputDirectory.toAbsolutePath() + "\n");

            EntityGenerator generator = new EntityGenerator();
            for (TableMeta table : schema.getTables()) {
                generator.generate(schema, table, packageName, outputDirectory);
            }
        } catch (Exception e) {
            System.err.println("Generation failed: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
