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

    public static String toPluralFieldName(String tableName) {
        String singular = toFieldName(tableName);
        if (singular.endsWith("y") && singular.length() > 1 && !isVowel(singular.charAt(singular.length() - 2))) {
            return singular.substring(0, singular.length() - 1) + "ies";
        }
        if (singular.endsWith("s") || singular.endsWith("x") || singular.endsWith("z") || singular.endsWith("ch") || singular.endsWith("sh")) {
            return singular + "es";
        }
        return singular + "s";
    }

    private static boolean isVowel(char ch) {
        return "aeiou".indexOf(Character.toLowerCase(ch)) >= 0;
    }

}