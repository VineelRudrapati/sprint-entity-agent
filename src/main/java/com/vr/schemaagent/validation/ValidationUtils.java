package com.vr.schemaagent.validation;

import com.vr.schemaagent.model.SchemaMeta;
import com.vr.schemaagent.model.TableMeta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ValidationUtils {

    private ValidationUtils() {}

    public static String[] findDuplicateClassNames(SchemaMeta schema) {
        Set<String> seen = new HashSet<>();
        Set<String> dup = new HashSet<>();
        for (TableMeta t : schema.getTables()) {
            String name = t.getName().toLowerCase();
            if (!seen.add(name)) dup.add(name);
        }
        return dup.toArray(new String[0]);
    }

    public static String[] findInvalidNames(SchemaMeta schema) {
        // simple rule: no names starting with digit
        Set<String> bad = new HashSet<>();
        for (TableMeta t : schema.getTables()) {
            String n = t.getName();
            if (n.length() > 0 && Character.isDigit(n.charAt(0))) bad.add(n);
        }
        return bad.toArray(new String[0]);
    }
}
