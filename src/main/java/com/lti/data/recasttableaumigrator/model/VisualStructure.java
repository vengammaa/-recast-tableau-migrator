package com.lti.data.recasttableaumigrator.model;

import java.util.ArrayList;
import java.util.List;

public class VisualStructure {

	private String type;

	private String header;

	private String xaxis;

	private List<String> filter;

	private List<String> rows;

	private List<String> cols;

	private List<String> ref;

	private List<String> rowsOrdinal;

	private List<String> colsOrdinal;

	private List<Filter> filterList;

	private List<Structure> structureList;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getRows() {
		return rows;
	}

	public void setRows(List<String> rows) {
		this.rows = rows;
	}

	public List<String> getCols() {
		return cols;
	}

	public void setCols(List<String> cols) {
		this.cols = cols;
	}

	public List<Structure> getStructureList() {
		return structureList;
	}

	public void setStructureList(List<Structure> structureList) {
		this.structureList = structureList;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public List<String> getRef() {
		return ref;
	}

	public void setRef(List<String> ref) {
		this.ref = ref;
	}

	public String getXaxis() {
		return xaxis;
	}

	public void setXaxis(String xaxis) {
		this.xaxis = xaxis;
	}

	public List<String> getFilter() {
		return filter;
	}

	public void setFilter(List<String> filter) {
		this.filter = filter;
	}

	public List<String> getRowsOrdinal() {
		return rowsOrdinal;
	}

	public void setRowsOrdinal(List<String> rowsOrdinal) {
		this.rowsOrdinal = rowsOrdinal;
	}

	public List<String> getColsOrdinal() {
		return colsOrdinal;
	}

	public void setColsOrdinal(List<String> colsOrdinal) {
		this.colsOrdinal = colsOrdinal;
	}

	public List<Filter> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<Filter> filterList) {
		this.filterList = filterList;
	}

	@Override
	public String toString() {
		return "VisualStructure [type=" + type + ", header=" + header + ", xaxis=" + xaxis + ", rows=" + rows
				+ ", cols=" + cols + ", ref=" + ref + ", rowsOrdinal=" + rowsOrdinal + ", colsOrdinal=" + colsOrdinal
				+ ", filterList=" + filterList + ", structureList=" + structureList + "]";
	}

}
