package com.vr.schemaagent.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.vr.schemaagent.model.ColumnMeta;
import com.vr.schemaagent.model.TableMeta;
import com.vr.schemaagent.writer.JavaFileWriter;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;

public class RepositoryGenerator {

    private final JavaFileWriter writer = new JavaFileWriter();

    public void generate(TableMeta table, String packageName, Path outputDirectory) throws IOException {
        String entityName = com.vr.schemaagent.util.NameUtils.toClassName(table.getName());

        ClassName entityType = ClassName.get(packageName, entityName);

        // determine id type
        Class<?> idType = Long.class;
        for (ColumnMeta c : table.getColumns()) {
            if (c.isPrimaryKey()) {
                idType = com.vr.schemaagent.mapper.SqlTypeMapper.toJavaType(c.getType());
                break;
            }
        }

        TypeName repoSuper = ParameterizedTypeName.get(ClassName.get(JpaRepository.class), entityType, TypeName.get(idType));

        TypeSpec repo = TypeSpec.interfaceBuilder(entityName + "Repository")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(repoSuper)
                .build();

        JavaFile javaFile = JavaFile.builder(packageName + ".repository", repo).build();
        writer.write(javaFile, outputDirectory);
    }
}
