package com.vr.generator;

import java.nio.file.Files;
import java.nio.file.Path;

public class EntityGenerator {

    public void generate(
            String table
    ) throws Exception {

        String className =
                Character.toUpperCase(
                        table.charAt(0)
                )
                +
                table.substring(1);

        String code =
"""
public class %s {

}
"""
.formatted(
className
);

        Path output = Path.of(
                "output",
                className + ".java"
        );

        Files.writeString(
                output,
                code
        );

        System.out.println(
                "Generated → "
                +
                output
        );

    }

}
