package com.lti.data.recasttableaumigrator.model;

public class QueryModel {

	
	private String reportId;
	
	private String reportName;
	
	private String databaseType;
	
	private String hostname;
	
	private String databaseName;
	
	private String queryStatement;
	
	private String convertedQueryStatement;
    
	private String queryName;
	
	private String convertedQueryName;

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
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

	public String getConvertedQueryStatement() {
		return convertedQueryStatement;
	}

	public void setConvertedQueryStatement(String convertedQueryStatement) {
		this.convertedQueryStatement = convertedQueryStatement;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getConvertedQueryName() {
		return convertedQueryName;
	}

	public void setConvertedQueryName(String convertedQueryName) {
		this.convertedQueryName = convertedQueryName;
	}
	
	
	

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	@Override
	public String toString() {
		return "QueryModel [reportId=" + reportId + ", reportName=" + reportName + ", databaseType=" + databaseType
				+ ", hostname=" + hostname + ", databaseName=" + databaseName + ", queryStatement=" + queryStatement
				+ ", convertedQueryStatement=" + convertedQueryStatement + ", queryName=" + queryName
				+ ", convertedQueryName=" + convertedQueryName + "]";
	}


	
	
	
}
