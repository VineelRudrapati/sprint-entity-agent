package com.vr.schemaagent.writer;

import com.squareup.javapoet.JavaFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaFileWriter {

    public void write(JavaFile javaFile, Path outputDirectory) throws IOException {
        Files.createDirectories(outputDirectory);
        javaFile.writeTo(outputDirectory);
    }
}
