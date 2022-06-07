package com.lti.data.recasttableaumigrator.model;

import java.util.List;

public class CalculationModel {
	
	private String name;
	private List<String> columns;
	private String formula;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	

}
