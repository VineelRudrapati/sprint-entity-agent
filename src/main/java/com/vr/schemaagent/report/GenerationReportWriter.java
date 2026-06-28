package com.vr.schemaagent.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenerationReportWriter {

    public void write(GenerationReport report, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        String json = "{\n" +
                "  \"tables\": " + report.getTables() + ",\n" +
                "  \"entitiesGenerated\": " + report.getEntitiesGenerated() + ",\n" +
                "  \"repositoriesGenerated\": " + report.getRepositoriesGenerated() + ",\n" +
                "  \"dtosGenerated\": " + report.getDtosGenerated() + ",\n" +
                "  \"servicesGenerated\": " + report.getServicesGenerated() + ",\n" +
                "  \"relationships\": " + report.getRelationships() + ",\n" +
                "  \"failed\": " + report.getFailed() + ",\n" +
                "  \"warnings\": " + report.getWarnings() + "\n}";

        Files.writeString(outputDir.resolve("generation-report.json"), json);
    }
}
