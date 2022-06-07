package com.lti.data.recasttableaumigrator.model;

import java.util.List;


public class MigratorModel {

	private int stategizerId;
	
	private List<QueryModel> queryModelList;
	
	private List<VisualModel> visualModelList;
	
	private List<CalculatedFormulaModel> calculatedFormualaModelList;
	
	private List<MetadataColumnModel> metadataColumnModelList;
	
	private List<DatasourceModel> datasourceModelList;
	
	private List<CalculationsModel> calculationsList;
	
	public int getStategizerId() {
		return stategizerId;
	}

	public void setStategizerId(int stategizerId) {
		this.stategizerId = stategizerId;
	}

	public List<QueryModel> getQueryModelList() {
		return queryModelList;
	}

	public void setQueryModelList(List<QueryModel> queryModelList) {
		this.queryModelList = queryModelList;
	}

	
	public List<VisualModel> getVisualModelList() {
		return visualModelList;
	}

	public void setVisualModelList(List<VisualModel> visualModelList) {
		this.visualModelList = visualModelList;
	}

	
	
	
	public List<CalculatedFormulaModel> getCalculatedFormualaModelList() {
		return calculatedFormualaModelList;
	}

	public void setCalculatedFormualaModelList(List<CalculatedFormulaModel> calculatedFormualaModelList) {
		this.calculatedFormualaModelList = calculatedFormualaModelList;
	}

	public List<MetadataColumnModel> getMetadataColumnModelList() {
		return metadataColumnModelList;
	}

	public void setMetadataColumnModelList(List<MetadataColumnModel> metadataColumnModelList) {
		this.metadataColumnModelList = metadataColumnModelList;
	}

	public List<DatasourceModel> getDatasourceModelList() {
		return datasourceModelList;
	}

	public void setDatasourceModelList(List<DatasourceModel> datasourceModelList) {
		this.datasourceModelList = datasourceModelList;
	}

	public List<CalculationsModel> getCalculationsList() {
		return calculationsList;
	}

	public void setCalculationsList(List<CalculationsModel> calculationsList) {
		this.calculationsList = calculationsList;
	}

	@Override
	public String toString() {
		return "MigratorModel [stategizerId=" + stategizerId + ", queryModelList=" + queryModelList
				+ ", visualModelList=" + visualModelList + ", calculatedFormualaModelList="
				+ calculatedFormualaModelList + ", metadataColumnModelList=" + metadataColumnModelList
				+ ", datasourceModelList=" + datasourceModelList + ", calculationsList=" + calculationsList + "]";
	}


	
	
	
}
