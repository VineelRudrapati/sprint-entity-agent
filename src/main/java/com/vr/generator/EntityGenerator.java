package com.vr.generator;

import com.vr.model.ColumnMeta;
import com.vr.model.RelationMeta;
import com.vr.model.TableMeta;
import java.nio.file.Files;
import java.nio.file.Path;

public class EntityGenerator {
	public void generate(TableMeta table) throws Exception {
		String className = toClassName(table.getName());
		StringBuilder code = new StringBuilder();
		code.append("""
				import jakarta.persistence.*;
				@Entity
				@Table(name="%s")
				public class %s {
				""".formatted(table.getName(), className));
		for (ColumnMeta col : table.getColumns()) {
			boolean relation = false;
			for (RelationMeta r : table.getRelations()) {
				if (toFieldName(col.getName()).equals(r.getField())) {
					code.append("""
							@ManyToOne
							@JoinColumn(
							name="%s"
							)
							private %s %s;
							""".formatted(col.getName(), toClassName(r.getTarget()), r.getTarget()));
					relation = true;
					break;
				}
			}
			if (relation) {
				continue;
			}
			if ("id".equalsIgnoreCase(col.getName())) {
				code.append("""
						@Id
						""");
			}
			code.append("""
					@Column(name="%s")
					private %s %s;
					""".formatted(
					col.getName(),
					map(col.getType()),
					toFieldName(col.getName())
			));
		}
		code.append("""
				}
				""");
		Files.writeString(
				Path.of("output", className + ".java"),
				code.toString()
		);
		System.out.println("Generated → " + className);
	}

	private String map(String sql) {
		sql = sql.toUpperCase();
		if (sql.contains("SERIAL"))
			return "Integer";
		if (sql.contains("VARCHAR"))
			return "String";
		if (sql.contains("INT"))
			return "Integer";
		return "String";
	}
	private String toClassName(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}
	private String toFieldName(String field) {
		String[] parts = field.split("_");
		StringBuilder sb = new StringBuilder(parts[0]);
		for (int i = 1; i < parts.length; i++) {
			sb.append(Character.toUpperCase(parts[i].charAt(0)));
			sb.append(parts[i].substring(1));
		}
		return sb.toString();

	}
}