package com.lti.data.recasttableaumigrator.dto;

public class DatabaseSourceModel {
	
	private String databaseName;
	private String queryStatement;
	private String tableName;
	private String serverHostName;
	private String databaseType;
	private String tableId;
	private String metadataId;
	private String connId;
	
	
	public DatabaseSourceModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	public DatabaseSourceModel(String databaseName, String queryStatement, String tableName, String serverHostName,
			String databaseType, String tableId, String metadataId, String connId) {
		super();
		this.databaseName = databaseName;
		this.queryStatement = queryStatement;
		this.tableName = tableName;
		this.serverHostName = serverHostName;
		this.databaseType = databaseType;
		this.tableId = tableId;
		this.metadataId = metadataId;
		this.connId = connId;
	}
	
	
	
	@Override
	public String toString() {
		return "DatabaseSourceModel [databaseName=" + databaseName + ", queryStatement=" + queryStatement
				+ ", tableName=" + tableName + ", serverHostName=" + serverHostName + ", databaseType=" + databaseType
				+ ", tableId=" + tableId + ", metadataId=" + metadataId + ", connId=" + connId + "]";
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getQueryStatement() {
		return queryStatement;
	}
	public void setQueryStatement(String queryStatement) {
		this.queryStatement = queryStatement;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getServerHostName() {
		return serverHostName;
	}
	public void setServerHostName(String serverHostName) {
		this.serverHostName = serverHostName;
	}
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	public String getTableId() {
		return tableId;
	}
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	public String getMetadataId() {
		return metadataId;
	}
	public void setMetadataId(String metadataId) {
		this.metadataId = metadataId;
	}
	public String getConnId() {
		return connId;
	}
	public void setConnId(String connId) {
		this.connId = connId;
	}
	
	
	
	
	

}
