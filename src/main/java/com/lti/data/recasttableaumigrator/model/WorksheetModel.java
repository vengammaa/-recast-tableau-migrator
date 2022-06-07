package com.lti.data.recasttableaumigrator.model;

import java.util.HashMap;

//import java.util.List;

public class WorksheetModel {

		private String[] rows;
		private String[] cols;
		private static HashMap<String, String> columns = new HashMap<String, String>();
		private String[] header;
		private String chart;
		public String[] getRows() {
			return rows;
		}
		public void setRows(String[] rows) {
			this.rows = rows;
		}
		public String[] getCols() {
			return cols;
		}
		public void setCols(String[] cols) {
			this.cols = cols;
		}
		public static HashMap<String, String> getColumns() {
			return columns;
		}
		public static void setColumns(HashMap<String, String> columns) {
			WorksheetModel.columns = columns;
		}
		public String[] getHeader() {
			return header;
		}
		public void setHeader(String[] header) {
			this.header = header;
		}
		public String getChart() {
			return chart;
		}
		public void setChart(String chart) {
			this.chart = chart;
		}
		
}
