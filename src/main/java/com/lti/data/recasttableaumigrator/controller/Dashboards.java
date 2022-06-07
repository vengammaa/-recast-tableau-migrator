package com.lti.data.recasttableaumigrator.controller;

import java.util.HashSet;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.lti.data.recasttableaumigrator.model.MigratorModel;
import com.lti.data.recasttableaumigrator.model.VisualModel;

public class Dashboards {

	public Dashboards() {
	}

	public static void writeDashBoards(XMLStreamWriter prettyPrintWriter, MigratorModel migratorModel)
			throws XMLStreamException {

		Set<String> hashSet = new HashSet<>();
		migratorModel.getVisualModelList().forEach(value -> {
			hashSet.add(value.getDashboardName() == null ? "Dashboard" : value.getDashboardName() );
		});

		prettyPrintWriter.writeStartElement("dashboards");

		for (String dashboardName : hashSet) {
			int i = 0;
			prettyPrintWriter.writeStartElement("dashboard");
			prettyPrintWriter.writeAttribute("_.fcp.AccessibleZoneTabOrder.true...enable-sort-zone-taborder", "true");
			prettyPrintWriter.writeAttribute("name", dashboardName);
			prettyPrintWriter.writeEmptyElement("style");
			prettyPrintWriter.writeEmptyElement("size");
			prettyPrintWriter.writeAttribute("maxheight", "800");
			prettyPrintWriter.writeAttribute("maxwidth", "1000");
			prettyPrintWriter.writeAttribute("minheight", "800");
			prettyPrintWriter.writeAttribute("minwidth", "1000");
			prettyPrintWriter.writeStartElement("zones");

			prettyPrintWriter.writeStartElement("zone"); // zone 1
			prettyPrintWriter.writeAttribute("h", "100000");
			prettyPrintWriter.writeAttribute("id", Integer.toString(++i));
			prettyPrintWriter.writeAttribute("type-v2", "layout-basic");
			prettyPrintWriter.writeAttribute("w", "100000");
			prettyPrintWriter.writeAttribute("x", "0");
			prettyPrintWriter.writeAttribute("y", "0");

			prettyPrintWriter.writeStartElement("zone"); // zone 2
			prettyPrintWriter.writeAttribute("h", "100000");
			prettyPrintWriter.writeAttribute("id", Integer.toString(++i));
			prettyPrintWriter.writeAttribute("param", "horz");
			prettyPrintWriter.writeAttribute("type-v2", "layout-flow");
			prettyPrintWriter.writeAttribute("w", "100000");
			prettyPrintWriter.writeAttribute("x", "0");
			prettyPrintWriter.writeAttribute("y", "0");

			prettyPrintWriter.writeStartElement("zone"); // zone 3
			prettyPrintWriter.writeAttribute("h", "98000");
			prettyPrintWriter.writeAttribute("id", Integer.toString(++i));
			prettyPrintWriter.writeAttribute("type-v2", "layout-basic");
			prettyPrintWriter.writeAttribute("w", "98000");
			prettyPrintWriter.writeAttribute("x", "800");
			prettyPrintWriter.writeAttribute("y", "1000");

			for (VisualModel visual_Mdl : migratorModel.getVisualModelList()) {

				if (dashboardName.trim().equalsIgnoreCase(visual_Mdl.getDashboardName())) {

					prettyPrintWriter.writeStartElement("zone"); // zone 4
					prettyPrintWriter.writeAttribute("h",
							visual_Mdl.getTargetMinimalHeight() != null ? visual_Mdl.getTargetMinimalHeight() : "0");
					prettyPrintWriter.writeAttribute("id", Integer.toString(++i));
					prettyPrintWriter.writeAttribute("name", visual_Mdl.getReportName());
					prettyPrintWriter.writeAttribute("w",
							visual_Mdl.getTargetMinimalWidth() != null ? visual_Mdl.getTargetMinimalWidth() : "0");
					prettyPrintWriter.writeAttribute("x",
							visual_Mdl.getTargetPositionX() != null ? visual_Mdl.getTargetPositionX() : "0");
					prettyPrintWriter.writeAttribute("y",
							visual_Mdl.getTargetPositionY() != null ? visual_Mdl.getTargetPositionY() : "0");

					prettyPrintWriter.writeStartElement("zone-style");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "border-color");
					prettyPrintWriter.writeAttribute("value", "#000000");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "border-style");
					prettyPrintWriter.writeAttribute("value", "none");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "border-width");
					prettyPrintWriter.writeAttribute("value", "0");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "margin");
					prettyPrintWriter.writeAttribute("value", "4");
					prettyPrintWriter.writeEndElement();// zone-style

					prettyPrintWriter.writeEndElement();// zone 4
				}
			}

			prettyPrintWriter.writeEndElement();// zone 3

			prettyPrintWriter.writeEndElement();// zone 2

			prettyPrintWriter.writeStartElement("zone-style");
			prettyPrintWriter.writeEmptyElement("format");
			prettyPrintWriter.writeAttribute("attr", "border-color");
			prettyPrintWriter.writeAttribute("value", "#000000");
			prettyPrintWriter.writeEmptyElement("format");
			prettyPrintWriter.writeAttribute("attr", "border-style");
			prettyPrintWriter.writeAttribute("value", "none");
			prettyPrintWriter.writeEmptyElement("format");
			prettyPrintWriter.writeAttribute("attr", "border-width");
			prettyPrintWriter.writeAttribute("value", "0");
			prettyPrintWriter.writeEmptyElement("format");
			prettyPrintWriter.writeAttribute("attr", "margin");
			prettyPrintWriter.writeAttribute("value", "8");
			prettyPrintWriter.writeEndElement();// zone-style

			prettyPrintWriter.writeEndElement();// zone 1
			prettyPrintWriter.writeEndElement();// zones

			prettyPrintWriter.writeStartElement("devicelayouts");
			prettyPrintWriter.writeStartElement("devicelayout");
			prettyPrintWriter.writeAttribute("auto-generated", "true");
			prettyPrintWriter.writeAttribute("name", "Phone");
			prettyPrintWriter.writeEmptyElement("size");
			prettyPrintWriter.writeAttribute("maxheight", "1200");
			prettyPrintWriter.writeAttribute("minheight", "1200");
			prettyPrintWriter.writeAttribute("sizing-mode", "vscroll");
			prettyPrintWriter.writeStartElement("zones");// zones

			prettyPrintWriter.writeStartElement("zone");// zone 1
			prettyPrintWriter.writeAttribute("h", "100000");
			prettyPrintWriter.writeAttribute("id", Integer.toString(++i));
			prettyPrintWriter.writeAttribute("type-v2", "layout-basic");
			prettyPrintWriter.writeAttribute("w", "100000");
			prettyPrintWriter.writeAttribute("x", "0");
			prettyPrintWriter.writeAttribute("y", "0");

			prettyPrintWriter.writeStartElement("zone");// zone 2
			prettyPrintWriter.writeAttribute("h", "98000");
			prettyPrintWriter.writeAttribute("id", Integer.toString(++i));
			prettyPrintWriter.writeAttribute("param", "vert");
			prettyPrintWriter.writeAttribute("type-v2", "layout-flow");
			prettyPrintWriter.writeAttribute("w", "98400");
			prettyPrintWriter.writeAttribute("x", "800");
			prettyPrintWriter.writeAttribute("y", "1000");

			i = 0;
			for (VisualModel visual_Mdl : migratorModel.getVisualModelList()) {

				if (dashboardName.trim().equalsIgnoreCase(visual_Mdl.getDashboardName())) {

					prettyPrintWriter.writeStartElement("zone"); // zone 3
					prettyPrintWriter.writeAttribute("fixed-size", "280");
					prettyPrintWriter.writeAttribute("h",
							visual_Mdl.getTargetMinimalHeight() != null ? visual_Mdl.getTargetMinimalHeight() : "0");
					prettyPrintWriter.writeAttribute("id", Integer.toString(++i));
					prettyPrintWriter.writeAttribute("is-fixed", "true");
					prettyPrintWriter.writeAttribute("name", visual_Mdl.getReportName());
					prettyPrintWriter.writeAttribute("w",
							visual_Mdl.getTargetMinimalWidth() != null ? visual_Mdl.getTargetMinimalWidth() : "0");
					prettyPrintWriter.writeAttribute("x",
							visual_Mdl.getTargetPositionX() != null ? visual_Mdl.getTargetPositionX() : "0");
					prettyPrintWriter.writeAttribute("y",
							visual_Mdl.getTargetPositionY() != null ? visual_Mdl.getTargetPositionY() : "0");

					prettyPrintWriter.writeStartElement("zone-style");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "border-color");
					prettyPrintWriter.writeAttribute("value", "#000000");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "border-style");
					prettyPrintWriter.writeAttribute("value", "none");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "border-width");
					prettyPrintWriter.writeAttribute("value", "0");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "margin");
					prettyPrintWriter.writeAttribute("value", "4");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "padding");
					prettyPrintWriter.writeAttribute("value", "0");
					prettyPrintWriter.writeEndElement();// zone-style

					prettyPrintWriter.writeEndElement();// zone 3

				}
			}
			prettyPrintWriter.writeEndElement();// zone 2 Loop End

			prettyPrintWriter.writeStartElement("zone-style");// zone-style
			prettyPrintWriter.writeEmptyElement("format");
			prettyPrintWriter.writeAttribute("attr", "border-color");
			prettyPrintWriter.writeAttribute("value", "#000000");
			prettyPrintWriter.writeEmptyElement("format");
			prettyPrintWriter.writeAttribute("attr", "border-style");
			prettyPrintWriter.writeAttribute("value", "none");
			prettyPrintWriter.writeEmptyElement("format");
			prettyPrintWriter.writeAttribute("attr", "border-width");
			prettyPrintWriter.writeAttribute("value", "0");
			prettyPrintWriter.writeEmptyElement("format");
			prettyPrintWriter.writeAttribute("attr", "margin");
			prettyPrintWriter.writeAttribute("value", "8");
			prettyPrintWriter.writeEndElement();// zone-style

			prettyPrintWriter.writeEndElement();// zone 1
			prettyPrintWriter.writeEndElement();// zones

			prettyPrintWriter.writeEndElement();// devicelayout
			prettyPrintWriter.writeEndElement();// devicelayouts

			prettyPrintWriter.writeEmptyElement("simple-id");
			prettyPrintWriter.writeAttribute("uuid",
					"{" + BOTableauReportMigrator.getAlphaNumericString(8) + "-"
							+ BOTableauReportMigrator.getAlphaNumericString(4) + "-"
							+ BOTableauReportMigrator.getAlphaNumericString(4) + "-"
							+ BOTableauReportMigrator.getAlphaNumericString(4) + "-"
							+ BOTableauReportMigrator.getAlphaNumericString(12) + "}");

			prettyPrintWriter.writeEndElement();// dashboard
		}
		prettyPrintWriter.writeEndElement();// dashboards
	}

}
