package com.vr.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DumpParser {

    public List<String> extractTables(
            String sql
    ) {

        Pattern pattern =
                Pattern.compile(
                        "CREATE TABLE\\s+(\\w+)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(sql);

        List<String> tables =
                new ArrayList<>();

        while (matcher.find()) {

            tables.add(
                    matcher.group(1)
            );

        }

        return tables;

    }

}
