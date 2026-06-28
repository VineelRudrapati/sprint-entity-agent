package com.vr.schemaagent.util;

public final class NameUtils {

    private NameUtils() {}

    public static String toClassName(String tableName) {

        StringBuilder builder = new StringBuilder();

        for (String part : tableName.split("_")) {
            builder.append(
                    Character.toUpperCase(part.charAt(0)))
                   .append(part.substring(1).toLowerCase());
        }

        return builder.toString();
    }

    public static String toFieldName(String columnName) {

        String[] parts = columnName.split("_");

        StringBuilder builder = new StringBuilder(parts[0].toLowerCase());

        for (int i = 1; i < parts.length; i++) {
            builder.append(Character.toUpperCase(parts[i].charAt(0)))
                   .append(parts[i].substring(1).toLowerCase());
        }

        return builder.toString();
    }

}