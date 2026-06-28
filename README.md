# Spring Entity Generator Agent

## Project Overview

Spring Entity Generator Agent is a local-first Java application that automatically generates Spring Boot JPA Entity classes from a PostgreSQL schema.

The tool accepts a PostgreSQL schema (generated using `pg_dump`) and analyzes the database structure to generate production-ready Java Entity classes with proper JPA annotations and relationships.

The long-term goal is to evolve this project into an AI-powered schema understanding agent capable of generating an entire Spring Boot project's persistence layer.

---

# Motivation

Creating Entity classes manually for large enterprise databases is repetitive and time-consuming.

A medium-sized enterprise database may contain:

* 100+ tables
* Hundreds of relationships
* Thousands of columns

Developers spend significant time creating:

* Entity classes
* JPA annotations
* Relationships
* Repositories
* DTOs

This project automates that process.

---

# Vision

Input:

```bash
schema-agent \
    --schema company.dump \
    --package com.company.entity \
    --output ./src/main/java
```

Output:

```
Employee.java
Department.java
Project.java
Address.java
...
```

Generated entities should include:

* @Entity
* @Table
* @Id
* @Column
* @ManyToOne
* @OneToMany
* @OneToOne
* @ManyToMany
* @JoinColumn
* Proper Java naming conventions
* Java type mapping
* Clean formatting

---

# Technology Stack

Language

* Java 25

Build Tool

* Gradle

Database

* PostgreSQL

Libraries

* JavaPoet (planned)
* Picocli
* SLF4J
* Logback

Future

* Ollama
* Qwen
* DeepSeek
* Local LLMs

---

# Current Architecture

```
Main
    │
    ▼
DumpParser
    │
    ▼
SchemaMeta
    │
    ▼
EntityGenerator
    │
    ▼
JavaFileWriter
```

Current packages

```
generator/
mapper/
model/
parser/
util/
writer/
```

---

# Current Features

Implemented

* Read PostgreSQL dump
* Parse CREATE TABLE statements
* Extract table names
* Extract columns
* SQL type mapping
* Generate Java classes
* Generate @Entity
* Generate @Table
* Generate @Column
* Generate @Id
* Generate @ManyToOne
* Generate @JoinColumn
* Convert snake_case to camelCase
* Convert table names to Java class names

---

# Current Project Structure

```
generator/
    EntityGenerator.java

mapper/
    SqlTypeMapper.java

model/
    ColumnMeta.java
    RelationMeta.java
    TableMeta.java

parser/
    DumpParser.java

util/
    NameUtils.java

writer/
    JavaFileWriter.java

Main.java
```

---

# Current Limitations

Current parser assumes:

* Simple CREATE TABLE statements
* Simple FOREIGN KEY definitions
* PostgreSQL only
* Convention-based relationship detection

Not yet supported:

* Composite primary keys
* Composite foreign keys
* Sequences
* Views
* Indexes
* Triggers
* Enums
* Check constraints
* Real pg_dump syntax

---

# Refactoring Plan (Version 2)

Goal

Create a clean architecture before adding more functionality.

New Domain Model

```
SchemaMeta
    │
    ▼
TableMeta
    ├── List<ColumnMeta>
    └── List<RelationMeta>
```

Model classes

```
SchemaMeta
TableMeta
ColumnMeta
RelationMeta
RelationType
```

Parser

```
DumpParser
```

Later

```
TableParser
ColumnParser
RelationshipParser
```

---

# Planned Features

## Phase 1

* SchemaMeta
* Better parser
* Cleaner architecture

## Phase 2

Replace manual StringBuilder generation with JavaPoet.

Goals

* Automatic imports
* Automatic formatting
* Automatic indentation
* Cleaner code generation

## Phase 3

Relationship improvements

* OneToMany
* OneToOne
* ManyToMany
* Composite Keys

## Phase 4

Generate

* Repository
* DTO
* Service
* Mapper

## Phase 5

Validation

* Generated code compilation
* Duplicate detection
* Naming validation

## Phase 6

Reports

Generate

```
generation-report.json
```

Example

```json
{
  "tables": 52,
  "entitiesGenerated": 52,
  "relationships": 91,
  "failed": 0,
  "warnings": 2
}
```

## Phase 7

AI Integration

Local LLM only.

Supported models

* Qwen
* DeepSeek
* Llama

LLM responsibilities

* Better Java naming
* JavaDoc generation
* Code improvement suggestions
* Relationship validation

The LLM should **not** generate entities from scratch.

---

# Future Vision

The project should eventually generate:

```
Database Schema
        │
        ▼
Entity Classes
        │
        ├── Repository
        ├── DTO
        ├── Service
        ├── Mapper
        ├── Controller
        └── OpenAPI
```

Eventually this project should function as a local AI agent capable of understanding an enterprise PostgreSQL schema and generating a production-ready Spring Boot persistence layer.

---

# Development Principles

* Java 25
* Local-first
* No cloud dependency
* Modular architecture
* Single Responsibility Principle
* Immutable domain models
* Production-quality code
* Small incremental refactoring
* Every milestone must compile successfully before moving to the next feature.
