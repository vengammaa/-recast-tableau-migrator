package com.lti.data.recasttableaumigrator.model;

public class VisualModel {

	private String reportId;

	private String reportTabId;

	private String reportTabName;

	private String sourceComponentName;

	private String targetComponentName;

	private String sourcePositionX;

	private String sourcePositionY;

	private String sourceMinimalWidth;

	private String sourceMinimalHeight;

	private String targetPositionX;

	private String targetPositionY;

	private String targetMinimalWidth;

	private String targetMinimalHeight;

	private String formulaName;

	private String font;

	private String color;

	private String valueType;

	private String parentElement;

	private String reportName;

	private String calculations;

	private String dashboardName;

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportTabId() {
		return reportTabId;
	}

	public void setReportTabId(String reportTabId) {
		this.reportTabId = reportTabId;
	}

	public String getReportTabName() {
		return reportTabName;
	}

	public void setReportTabName(String reportTabName) {
		this.reportTabName = reportTabName;
	}

	public String getSourceComponentName() {
		return sourceComponentName;
	}

	public void setSourceComponentName(String sourceComponentName) {
		this.sourceComponentName = sourceComponentName;
	}

	public String getTargetComponentName() {
		return targetComponentName;
	}

	public void setTargetComponentName(String targetComponentName) {
		this.targetComponentName = targetComponentName;
	}

	public String getSourcePositionX() {
		return sourcePositionX;
	}

	public void setSourcePositionX(String sourcePositionX) {
		this.sourcePositionX = sourcePositionX;
	}

	public String getSourcePositionY() {
		return sourcePositionY;
	}

	public void setSourcePositionY(String sourcePositionY) {
		this.sourcePositionY = sourcePositionY;
	}

	public String getSourceMinimalWidth() {
		return sourceMinimalWidth;
	}

	public void setSourceMinimalWidth(String sourceMinimalWidth) {
		this.sourceMinimalWidth = sourceMinimalWidth;
	}

	public String getSourceMinimalHeight() {
		return sourceMinimalHeight;
	}

	public void setSourceMinimalHeight(String sourceMinimalHeight) {
		this.sourceMinimalHeight = sourceMinimalHeight;
	}

	public String getTargetPositionX() {
		return targetPositionX;
	}

	public void setTargetPositionX(String targetPositionX) {
		this.targetPositionX = targetPositionX;
	}

	public String getTargetPositionY() {
		return targetPositionY;
	}

	public void setTargetPositionY(String targetPositionY) {
		this.targetPositionY = targetPositionY;
	}

	public String getTargetMinimalWidth() {
		return targetMinimalWidth;
	}

	public void setTargetMinimalWidth(String targetMinimalWidth) {
		this.targetMinimalWidth = targetMinimalWidth;
	}

	public String getTargetMinimalHeight() {
		return targetMinimalHeight;
	}

	public void setTargetMinimalHeight(String targetMinimalHeight) {
		this.targetMinimalHeight = targetMinimalHeight;
	}

	public String getFormulaName() {
		return formulaName;
	}

	public void setFormulaName(String formulaName) {
		this.formulaName = formulaName;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getParentElement() {
		return parentElement;
	}

	public void setParentElement(String parentElement) {
		this.parentElement = parentElement;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getCalculations() {
		return calculations;
	}

	public void setCalculations(String calculations) {
		this.calculations = calculations;
	}

	public String getDashboardName() {
		return dashboardName;
	}

	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}

	@Override
	public String toString() {
		return "VisualModel [reportId=" + reportId + ", reportTabId=" + reportTabId + ", reportTabName=" + reportTabName
				+ ", sourceComponentName=" + sourceComponentName + ", targetComponentName=" + targetComponentName
				+ ", sourcePositionX=" + sourcePositionX + ", sourcePositionY=" + sourcePositionY
				+ ", sourceMinimalWidth=" + sourceMinimalWidth + ", sourceMinimalHeight=" + sourceMinimalHeight
				+ ", targetPositionX=" + targetPositionX + ", targetPositionY=" + targetPositionY
				+ ", targetMinimalWidth=" + targetMinimalWidth + ", targetMinimalHeight=" + targetMinimalHeight
				+ ", formulaName=" + formulaName + ", font=" + font + ", color=" + color + ", valueType=" + valueType
				+ ", parentElement=" + parentElement + ", reportName=" + reportName + ", calculations=" + calculations
				+ ", dashboardName=" + dashboardName + "]";
	}

}
