package com.vr.schemaagent.generator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.vr.schemaagent.mapper.SqlTypeMapper;
import com.vr.schemaagent.model.ColumnMeta;
import com.vr.schemaagent.model.RelationMeta;
import com.vr.schemaagent.model.TableMeta;
import com.vr.schemaagent.util.NameUtils;
import com.vr.schemaagent.writer.JavaFileWriter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;

public class EntityGenerator {

    private final JavaFileWriter fileWriter = new JavaFileWriter();

    public void generate(TableMeta table, String packageName, Path outputDirectory) throws IOException {
        String className = NameUtils.toClassName(table.getName());

        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Entity.class)
                .addAnnotation(AnnotationSpec.builder(Table.class)
                        .addMember("name", "$S", table.getName())
                        .build());

        for (ColumnMeta column : table.getColumns()) {
            RelationMeta relation = table.findRelationForColumn(column.getName());

            if (relation != null) {
                ClassName targetType = ClassName.get(packageName, NameUtils.toClassName(relation.getTargetTable()));
                FieldSpec relationField = FieldSpec.builder(targetType, NameUtils.toFieldName(relation.getTargetTable()), Modifier.PRIVATE)
                        .addAnnotation(AnnotationSpec.builder(ManyToOne.class)
                                .addMember("fetch", "$T.$L", FetchType.class, "LAZY")
                                .build())
                        .addAnnotation(AnnotationSpec.builder(JoinColumn.class)
                                .addMember("name", "$S", relation.getSourceColumn())
                                .build())
                        .build();
                builder.addField(relationField);
                continue;
            }

            FieldSpec.Builder fieldBuilder = FieldSpec.builder(
                    SqlTypeMapper.toJavaType(column.getType()),
                    NameUtils.toFieldName(column.getName()),
                    Modifier.PRIVATE
            ).addAnnotation(AnnotationSpec.builder(Column.class)
                    .addMember("name", "$S", column.getName())
                    .build());

            if (column.isPrimaryKey()) {
                fieldBuilder.addAnnotation(Id.class);
            }

            builder.addField(fieldBuilder.build());
        }

        JavaFile javaFile = JavaFile.builder(packageName, builder.build())
                .build();

        fileWriter.write(javaFile, outputDirectory);
        System.out.println("Generated -> " + className);
    }
}
