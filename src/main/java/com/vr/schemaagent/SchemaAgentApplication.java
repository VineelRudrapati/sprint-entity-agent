package com.vr.schemaagent;

import com.vr.schemaagent.generator.EntityGenerator;
import com.vr.schemaagent.generator.RepositoryGenerator;
import com.vr.schemaagent.generator.DTOGenerator;
import com.vr.schemaagent.generator.ServiceGenerator;
import com.vr.schemaagent.report.GenerationReport;
import com.vr.schemaagent.report.GenerationReportWriter;
import com.vr.schemaagent.validation.ValidationUtils;
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

            // validation
            String[] duplicates = com.vr.schemaagent.validation.ValidationUtils.findDuplicateClassNames(schema);
            String[] invalid = com.vr.schemaagent.validation.ValidationUtils.findInvalidNames(schema);

            int warnings = 0;
            if (duplicates.length > 0) {
                System.err.println("Duplicate table names detected: ");
                for (String d : duplicates) System.err.println(" - " + d);
                warnings += duplicates.length;
            }
            if (invalid.length > 0) {
                System.err.println("Invalid table names detected: ");
                for (String d : invalid) System.err.println(" - " + d);
                warnings += invalid.length;
            }

            EntityGenerator entityGenerator = new EntityGenerator();
            RepositoryGenerator repoGenerator = new RepositoryGenerator();
            DTOGenerator dtoGenerator = new DTOGenerator();
            ServiceGenerator svcGenerator = new ServiceGenerator();

            com.vr.schemaagent.report.GenerationReport report = new com.vr.schemaagent.report.GenerationReport();
            report.setTables(schema.getTables().size());

            int entities = 0, repos = 0, dtos = 0, svcs = 0, relations = 0, failed = 0;

            for (TableMeta table : schema.getTables()) {
                try {
                    entityGenerator.generate(schema, table, packageName, outputDirectory);
                    entities++;

                    repoGenerator.generate(table, packageName, outputDirectory);
                    repos++;

                    dtoGenerator.generate(table, packageName, outputDirectory);
                    dtos++;

                    svcGenerator.generate(table, packageName, outputDirectory);
                    svcs++;

                    relations += table.getRelations().size();
                } catch (Exception e) {
                    failed++;
                    System.err.println("Failed to generate for table " + table.getName() + ": " + e.getMessage());
                }
            }

            report.setEntitiesGenerated(entities);
            report.setRepositoriesGenerated(repos);
            report.setDtosGenerated(dtos);
            report.setServicesGenerated(svcs);
            report.setRelationships(relations);
            report.setFailed(failed);
            report.setWarnings(warnings);

            com.vr.schemaagent.report.GenerationReportWriter reportWriter = new com.vr.schemaagent.report.GenerationReportWriter();
            try {
                reportWriter.write(report, outputDirectory);
                System.out.println("Wrote generation-report.json to " + outputDirectory.toAbsolutePath());
            } catch (Exception e) {
                System.err.println("Failed to write report: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Generation failed: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
