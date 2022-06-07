package com.lti.data.recasttableaumigrator.model;

public class CalculatedFormulaModel {

	private String reportId;
	
	private String reportName;
	
	private String reportTabId;
	
	private String formula;
	
	private String calculatedFormula;
	
	private String columnQualification;

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

	public String getReportTabId() {
		return reportTabId;
	}

	public void setReportTabId(String reportTabId) {
		this.reportTabId = reportTabId;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getCalculatedFormula() {
		return calculatedFormula;
	}

	public void setCalculatedFormula(String calculatedFormula) {
		this.calculatedFormula = calculatedFormula;
	}

	public String getColumnQualification() {
		return columnQualification;
	}

	public void setColumnQualification(String columnQualification) {
		this.columnQualification = columnQualification;
	}

	@Override
	public String toString() {
		return "CalculatedFormulaModel [reportId=" + reportId + ", reportName=" + reportName + ", reportTabId="
				+ reportTabId + ", formula=" + formula + ", calculatedFormula=" + calculatedFormula
				+ ", columnQualification=" + columnQualification + "]";
	}

	
	
	
}
