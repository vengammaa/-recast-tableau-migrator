package com.lti.data.recasttableaumigrator.model;

public class CalculationsModel {

	private String reportId;
    
    private String reportName;
    
    private String calculationName;
    
    private String columnNames;
    
    private String formula;

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

	public String getCalculationName() {
		return calculationName;
	}

	public void setCalculationName(String calculationName) {
		this.calculationName = calculationName;
	}

	public String getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String columnNames) {
		this.columnNames = columnNames;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	@Override
	public String toString() {
		return "CalculationsModel [reportId=" + reportId + ", reportName=" + reportName + ", calculationName="
				+ calculationName + ", columnNames=" + columnNames + ", formula=" + formula + "]";
	}
    
    
	
}
