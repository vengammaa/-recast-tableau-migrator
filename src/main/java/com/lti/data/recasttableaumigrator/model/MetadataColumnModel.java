package com.lti.data.recasttableaumigrator.model;

public class MetadataColumnModel {

	private String reportId;
    
    private String reportName;
	
    private String metadataColumnName;
    
    private String datatype;
    
    private String semanticsType;
    
    private String tableName;

    private String valueType;
    
//    private String type;
    
	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}



	public String getMetadataColumnName() {
		return metadataColumnName;
	}

	public void setMetadataColumnName(String metadataColumnName) {
		this.metadataColumnName = metadataColumnName;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getSemanticsType() {
		return semanticsType;
	}

	public void setSemanticsType(String semanticsType) {
		this.semanticsType = semanticsType;
	}

	
	
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	
//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}

	@Override
	public String toString() {
		return "MetadataColumnModel [reportId=" + reportId + ", reportName=" + reportName + ", metadataColumnName="
				+ metadataColumnName + ", datatype=" + datatype + ", semanticsType=" + semanticsType + ", tableName="
				+ tableName + ", valueType=" + valueType + "]";
	}



    
	
}
