package com.vr.schemaagent.generator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.vr.schemaagent.mapper.SqlTypeMapper;
import com.vr.schemaagent.model.ColumnMeta;
import com.vr.schemaagent.model.ManyToManyMeta;
import com.vr.schemaagent.model.RelationMeta;
import com.vr.schemaagent.model.RelationType;
import com.vr.schemaagent.model.SchemaMeta;
import com.vr.schemaagent.model.TableMeta;
import com.vr.schemaagent.util.NameUtils;
import com.vr.schemaagent.writer.JavaFileWriter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class EntityGenerator {

    private final JavaFileWriter fileWriter = new JavaFileWriter();

    public void generate(SchemaMeta schema, TableMeta table, String packageName, Path outputDirectory) throws IOException {
        String className = NameUtils.toClassName(table.getName());

        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Entity.class)
                .addAnnotation(AnnotationSpec.builder(Table.class)
                        .addMember("name", "$S", table.getName())
                        .build());

        buildColumnsAndRelations(builder, table, packageName);
        buildReverseRelations(builder, schema, table, packageName);
        buildManyToManyFields(builder, schema, table, packageName);

        JavaFile javaFile = JavaFile.builder(packageName, builder.build())
                .build();

        fileWriter.write(javaFile, outputDirectory);
        System.out.println("Generated -> " + className);
    }

    private void buildColumnsAndRelations(TypeSpec.Builder builder, TableMeta table, String packageName) {
        for (ColumnMeta column : table.getColumns()) {
            RelationMeta relation = table.findRelationForColumn(column.getName());

            if (relation != null) {
                builder.addField(buildRelationField(relation, packageName));
                continue;
            }

            builder.addField(buildColumnField(column));
        }
    }

    private FieldSpec buildColumnField(ColumnMeta column) {
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(
                TypeName.get(SqlTypeMapper.toJavaType(column.getType())),
                NameUtils.toFieldName(column.getName()),
                Modifier.PRIVATE
        ).addAnnotation(AnnotationSpec.builder(Column.class)
                .addMember("name", "$S", column.getName())
                .build());

        if (column.isPrimaryKey()) {
            fieldBuilder.addAnnotation(Id.class);
        }

        return fieldBuilder.build();
    }

    private FieldSpec buildRelationField(RelationMeta relation, String packageName) {
        ClassName targetType = ClassName.get(packageName, NameUtils.toClassName(relation.getTargetTable()));
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(targetType, NameUtils.toFieldName(relation.getTargetTable()), Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(JoinColumn.class)
                        .addMember("name", "$S", relation.getSourceColumn())
                        .build());

        if (relation.getRelationType() == null || relation.getRelationType() == RelationType.MANY_TO_ONE) {
            fieldBuilder.addAnnotation(AnnotationSpec.builder(ManyToOne.class)
                    .addMember("fetch", "$T.$L", FetchType.class, "LAZY")
                    .build());
        } else if (relation.getRelationType() == RelationType.ONE_TO_ONE) {
            fieldBuilder.addAnnotation(AnnotationSpec.builder(OneToOne.class)
                    .addMember("fetch", "$T.$L", FetchType.class, "LAZY")
                    .build());
        }

        return fieldBuilder.build();
    }

    private void buildReverseRelations(TypeSpec.Builder builder, SchemaMeta schema, TableMeta table, String packageName) {
        for (TableMeta source : schema.getTables()) {
            if (schema.isJoinTable(source)) {
                continue;
            }

            for (RelationMeta relation : source.getRelations()) {
                if (!relation.getTargetTable().equalsIgnoreCase(table.getName())) {
                    continue;
                }

                if (relation.getRelationType() == RelationType.ONE_TO_ONE) {
                    builder.addField(buildOneToOneField(relation, source, packageName));
                } else if (relation.getRelationType() == RelationType.MANY_TO_ONE || relation.getRelationType() == null) {
                    builder.addField(buildOneToManyField(relation, source, packageName));
                }
            }
        }
    }

    private FieldSpec buildOneToOneField(RelationMeta relation, TableMeta sourceTable, String packageName) {
        ClassName targetType = ClassName.get(packageName, NameUtils.toClassName(sourceTable.getName()));

        return FieldSpec.builder(targetType, NameUtils.toFieldName(sourceTable.getName()), Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(OneToOne.class)
                        .addMember("mappedBy", "$S", NameUtils.toFieldName(relation.getTargetTable()))
                        .build())
                .build();
    }

    private FieldSpec buildOneToManyField(RelationMeta relation, TableMeta sourceTable, String packageName) {
        ClassName targetElementType = ClassName.get(packageName, NameUtils.toClassName(sourceTable.getName()));
        ParameterizedTypeName listType = ParameterizedTypeName.get(
                ClassName.get(List.class),
                targetElementType
        );

        String fieldName = NameUtils.toPluralFieldName(sourceTable.getName());

        return FieldSpec.builder(listType, fieldName, Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(OneToMany.class)
                        .addMember("mappedBy", "$S", NameUtils.toFieldName(relation.getTargetTable()))
                        .addMember("fetch", "$T.$L", FetchType.class, "LAZY")
                        .build())
                .build();
    }

    private void buildManyToManyFields(TypeSpec.Builder builder, SchemaMeta schema, TableMeta table, String packageName) {
        for (ManyToManyMeta meta : schema.detectManyToManyRelations()) {
            if (table.getName().equalsIgnoreCase(meta.getLeftTable())) {
                builder.addField(buildManyToManyOwningField(meta, packageName));
            }
            if (table.getName().equalsIgnoreCase(meta.getRightTable())) {
                builder.addField(buildManyToManyInverseField(meta, packageName));
            }
        }
    }

    private FieldSpec buildManyToManyOwningField(ManyToManyMeta meta, String packageName) {
        ClassName targetType = ClassName.get(packageName, NameUtils.toClassName(meta.getRightTable()));
        ParameterizedTypeName listType = ParameterizedTypeName.get(ClassName.get(List.class), targetType);
        String fieldName = NameUtils.toPluralFieldName(meta.getRightTable());

        AnnotationSpec joinColumn = AnnotationSpec.builder(JoinColumn.class)
                .addMember("name", "$S", meta.getLeftJoinColumn())
                .build();

        AnnotationSpec inverseJoinColumn = AnnotationSpec.builder(JoinColumn.class)
                .addMember("name", "$S", meta.getRightJoinColumn())
                .build();

        return FieldSpec.builder(listType, fieldName, Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(ManyToMany.class)
                        .build())
                .addAnnotation(AnnotationSpec.builder(JoinTable.class)
                        .addMember("name", "$S", meta.getJoinTable())
                        .addMember("joinColumns", "{$L}", joinColumn)
                        .addMember("inverseJoinColumns", "{$L}", inverseJoinColumn)
                        .build())
                .build();
    }

    private FieldSpec buildManyToManyInverseField(ManyToManyMeta meta, String packageName) {
        ClassName targetType = ClassName.get(packageName, NameUtils.toClassName(meta.getLeftTable()));
        ParameterizedTypeName listType = ParameterizedTypeName.get(ClassName.get(List.class), targetType);
        String fieldName = NameUtils.toPluralFieldName(meta.getLeftTable());

        return FieldSpec.builder(listType, fieldName, Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(ManyToMany.class)
                        .addMember("mappedBy", "$S", NameUtils.toPluralFieldName(meta.getRightTable()))
                        .build())
                .build();
    }
}
