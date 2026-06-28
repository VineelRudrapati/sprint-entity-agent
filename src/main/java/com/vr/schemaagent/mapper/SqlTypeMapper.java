package com.vr.schemaagent.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class SqlTypeMapper {

    private SqlTypeMapper() {}

    public static Class<?> toJavaType(String sqlType) {
        String normalized = sqlType.toUpperCase();

        if (normalized.contains("BIGSERIAL") || normalized.contains("BIGINT")) {
            return Long.class;
        }

        if (normalized.contains("SERIAL") || normalized.matches(".*\\bINT\\b.*") || normalized.contains("INTEGER")) {
            return Integer.class;
        }

        if (normalized.contains("BOOLEAN")) {
            return Boolean.class;
        }

        if (normalized.contains("TIMESTAMP")) {
            return LocalDateTime.class;
        }

        if (normalized.contains("DATE")) {
            return LocalDate.class;
        }

        if (normalized.contains("TIME")) {
            return LocalTime.class;
        }

        if (normalized.contains("DECIMAL") || normalized.contains("NUMERIC")) {
            return BigDecimal.class;
        }

        return String.class;
    }
}
