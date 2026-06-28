package com.vr.schemaagent.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.vr.schemaagent.model.TableMeta;
import com.vr.schemaagent.util.NameUtils;
import com.vr.schemaagent.writer.JavaFileWriter;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;

public class ServiceGenerator {

    private final JavaFileWriter writer = new JavaFileWriter();

    public void generate(TableMeta table, String packageName, Path outputDirectory) throws IOException {
        String entityName = NameUtils.toClassName(table.getName());
        String svcName = entityName + "Service";

        ClassName entityType = ClassName.get(packageName, entityName);

        MethodSpec findAll = MethodSpec.methodBuilder("findAll")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(java.util.List.class)
                .build();

        TypeSpec svc = TypeSpec.interfaceBuilder(svcName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(findAll)
                .build();

        JavaFile javaFile = JavaFile.builder(packageName + ".service", svc).build();
        writer.write(javaFile, outputDirectory);
    }
}
