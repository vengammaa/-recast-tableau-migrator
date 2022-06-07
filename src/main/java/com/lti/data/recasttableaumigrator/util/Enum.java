package com.lti.data.recasttableaumigrator.util;

public class Enum {

	public enum CHART_TYPE {

		MAP(1, "Map"), AREA(2, "Area"), PIE(3, "Pie"), LINEAR_GAUGE(4, "LinearGauge"), WATERFALL(5, "Waterfall"),
		TABLE(6, "Table"), BAR(7, "Bar"), DONUT(8, "Donut");

		private final int id;
		private final String value;

		public int getId() {
			return id;
		}

		public String getValue() {
			return value;
		}

		CHART_TYPE(int id, String value) {
			this.id = id;
			this.value = value;
		}

		public static CHART_TYPE getViaKey(int id) {
			CHART_TYPE nature = null;

			if (id == MAP.getId()) {
				nature = MAP;
			} else if (id == AREA.getId()) {
				nature = AREA;
			} else if (id == PIE.getId()) {
				nature = PIE;
			} else if (id == LINEAR_GAUGE.getId()) {
				nature = LINEAR_GAUGE;
			} else if (id == WATERFALL.getId()) {
				nature = WATERFALL;
			} else if (id == TABLE.getId()) {
				nature = TABLE;
			} else if (id == BAR.getId()) {
				nature = BAR;
			} else if (id == DONUT.getId()) {
				nature = DONUT;
			}
			return nature;

		}

		public static CHART_TYPE getViaText(String text) {
			CHART_TYPE nature = null;

			if (text.equalsIgnoreCase(MAP.getValue())) {
				nature = MAP;
			} else if (text.equalsIgnoreCase(AREA.getValue())) {
				nature = AREA;
			} else if (text.equalsIgnoreCase(PIE.getValue())) {
				nature = PIE;
			} else if (text.equalsIgnoreCase(LINEAR_GAUGE.getValue())) {
				nature = LINEAR_GAUGE;
			} else if (text.equalsIgnoreCase(WATERFALL.getValue())) {
				nature = WATERFALL;
			} else if (text.equalsIgnoreCase(TABLE.getValue())) {
				nature = TABLE;
			} else if (text.equalsIgnoreCase(BAR.getValue())) {
				nature = BAR;
			} else if (text.equalsIgnoreCase(DONUT.getValue())) {
				nature = DONUT;
			}
			return nature;
		}

	}

}
