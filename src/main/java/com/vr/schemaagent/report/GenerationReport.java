package com.vr.schemaagent.report;

public class GenerationReport {
    private int tables;
    private int entitiesGenerated;
    private int repositoriesGenerated;
    private int dtosGenerated;
    private int servicesGenerated;
    private int relationships;
    private int failed;
    private int warnings;

    public GenerationReport() {}

    // getters and setters
    public int getTables() { return tables; }
    public void setTables(int tables) { this.tables = tables; }
    public int getEntitiesGenerated() { return entitiesGenerated; }
    public void setEntitiesGenerated(int entitiesGenerated) { this.entitiesGenerated = entitiesGenerated; }
    public int getRepositoriesGenerated() { return repositoriesGenerated; }
    public void setRepositoriesGenerated(int repositoriesGenerated) { this.repositoriesGenerated = repositoriesGenerated; }
    public int getDtosGenerated() { return dtosGenerated; }
    public void setDtosGenerated(int dtosGenerated) { this.dtosGenerated = dtosGenerated; }
    public int getServicesGenerated() { return servicesGenerated; }
    public void setServicesGenerated(int servicesGenerated) { this.servicesGenerated = servicesGenerated; }
    public int getRelationships() { return relationships; }
    public void setRelationships(int relationships) { this.relationships = relationships; }
    public int getFailed() { return failed; }
    public void setFailed(int failed) { this.failed = failed; }
    public int getWarnings() { return warnings; }
    public void setWarnings(int warnings) { this.warnings = warnings; }
}
