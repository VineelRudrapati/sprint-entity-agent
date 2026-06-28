package com.vr.schemaagent.generator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.vr.schemaagent.model.ColumnMeta;
import com.vr.schemaagent.model.TableMeta;
import com.vr.schemaagent.util.NameUtils;
import com.vr.schemaagent.writer.JavaFileWriter;
import jakarta.persistence.Column;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;

public class DTOGenerator {

    private final JavaFileWriter writer = new JavaFileWriter();

    public void generate(TableMeta table, String packageName, Path outputDirectory) throws IOException {
        String dtoName = NameUtils.toClassName(table.getName()) + "Dto";

        TypeSpec.Builder builder = TypeSpec.classBuilder(dtoName)
                .addModifiers(Modifier.PUBLIC);

        for (ColumnMeta col : table.getColumns()) {
            FieldSpec field = FieldSpec.builder(
                    com.squareup.javapoet.TypeName.get(com.vr.schemaagent.mapper.SqlTypeMapper.toJavaType(col.getType())),
                    NameUtils.toFieldName(col.getName()),
                    Modifier.PRIVATE
            ).addAnnotation(AnnotationSpec.builder(Column.class)
                    .addMember("name", "$S", col.getName())
                    .build()).build();

            builder.addField(field);
        }

        JavaFile javaFile = JavaFile.builder(packageName + ".dto", builder.build()).build();
        writer.write(javaFile, outputDirectory);
    }
}
