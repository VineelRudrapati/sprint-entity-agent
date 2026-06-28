package com.vr.schemaagent.model;

import java.util.List;
import java.util.ArrayList;

public class SchemaMeta {

	private List<TableMeta> tables;

	public SchemaMeta() {
		this.tables = new ArrayList<>();
	}

	public void addTable(TableMeta table) {
		tables.add(table);
	}

	public List<TableMeta> getTables() {
		return tables;
	}

}