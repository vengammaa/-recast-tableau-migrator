package com.lti.data.recasttableaumigrator.controller;

import java.io.File;
//import org.apache.commons.lang3.StringEscapeUtils;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lti.data.recasttableaumigrator.dto.DatabaseSourceModel;
import com.lti.data.recasttableaumigrator.model.CalculationsModel;
import com.lti.data.recasttableaumigrator.model.DatasourceModel;
import com.lti.data.recasttableaumigrator.model.Filter;
import com.lti.data.recasttableaumigrator.model.MetadataColumnModel;
import com.lti.data.recasttableaumigrator.model.MigratorModel;
import com.lti.data.recasttableaumigrator.model.QueryModel;
import com.lti.data.recasttableaumigrator.model.Structure;
import com.lti.data.recasttableaumigrator.model.VisualModel;
import com.lti.data.recasttableaumigrator.model.VisualStructure;
import com.lti.data.recasttableaumigrator.util.BOTableauMigratorUtility;
import com.lti.data.recasttableaumigrator.util.CommonConstants;
import com.lti.data.recasttableaumigrator.util.Enum.CHART_TYPE;

@RestController
@RequestMapping("/boToTableau")
public class BOTableauReportMigrator {

	private static DatabaseSourceModel datasrc = new DatabaseSourceModel();
	private static TreeMap<String, String[]> metadata = new TreeMap<String, String[]>();
	private static String strmeta = "";
	private static String ftable = "";
	private static HashMap<String, String[]> variables = new HashMap<String, String[]>();
	private static HashMap<String, VisualStructure> visuals = new HashMap<String, VisualStructure>();
	private static HashMap<String, String> calccols = new HashMap<String, String>();
	private static HashMap<String, String> calcformula = new HashMap<String, String>();
	private static final String MeasureNames = "Measure Names";
	private static final String MultipleValues = "Multiple Values";
	private static final String Calculation_1 = "Calculation_1";
	private static final String Calculation_2 = "Calculation_2";
	private static final String AVG = "AVG(0)";

	public static LinkedHashSet<String> tableorder = new LinkedHashSet<String>();

	@PostMapping(value = "migrator")
	public String migrate(@RequestBody MigratorModel migratorModel) {

		try {
			datasrc = BOTableauMigratorUtility.prepareDataSrcDetails(migratorModel.getQueryModelList());

			metadataprocessor(migratorModel);
			visulatizationprocessor(migratorModel);
			calcprocessor(migratorModel);
			writeTWBFILE(migratorModel);

			return "Success";

		} catch (Exception e) {
			e.printStackTrace();
			return "Failure";
		}

	}

	private void calcprocessor(MigratorModel migratorModel) {
		// TODO Auto-generated method stub
		for (CalculationsModel calc : migratorModel.getCalculationsList()) {
			calccols.put(calc.getCalculationName(), calc.getColumnNames());
			calcformula.put(calc.getCalculationName(), calc.getFormula());
		}
	}

	private void metadataprocessor(MigratorModel migratorModel) {
		// TODO Auto-generated method stub

		TreeMap<String, MetadataColumnModel> tmap = new TreeMap<>();
		for (MetadataColumnModel meta : migratorModel.getMetadataColumnModelList()) {
			tmap.put(meta.getMetadataColumnName(), meta);
		}
		Set<String> tset = tmap.keySet();
		for (String key : tset) {
			MetadataColumnModel meta = tmap.get(key);
			String[] a = new String[4];
			a[0] = meta.getDatatype();
			a[1] = meta.getTableName();
			a[2] = meta.getSemanticsType();
			a[3] = meta.getValueType();

			System.out.println(meta.getMetadataColumnName() + "  " + a[0]);

			if (metadata.get(meta.getMetadataColumnName()) == null) {
				metadata.put(meta.getMetadataColumnName(), a);
				strmeta = strmeta + "," + meta.getMetadataColumnName();

				System.out.println(metadata.get(meta.getMetadataColumnName())[0] + "///");
			}
		}
	}

	private void visulatizationprocessor(MigratorModel migratorModel) {
		String split1 = "||";
		String split2 = ";";
		String split3 = ",";
		List<VisualModel> list = migratorModel.getVisualModelList();

		for (int i = 0; i < list.size(); i++) {
			VisualModel m = list.get(i);

			if (m.getFormulaName() != null && !m.getFormulaName().isEmpty()) {
				// String formulaName = m.getFormulaName();
				JSONObject j = new JSONObject(m.getFormulaName());
				String type = j.getString("type");
				VisualStructure vobj = new VisualStructure();

				if (j.has("header")) {
					String header = j.getString("header");
					vobj.setHeader(header);
				}

				if (j.has("xaxis")) {
					String xaxis = j.getString("xaxis");
					vobj.setXaxis(xaxis);
				}

//				if (j.has("filter")) {
//					List<String> filterList = new LinkedList<String>();
//					JSONArray filterJSONArray = j.getJSONArray("filter");
//					filterJSONArray.forEach(x -> {
//						filterList.add(x.toString());
//
//					});
//					vobj.setFilter(filterList);
//				}

				if (j.has("rows")) {
					List<String> rowList = new LinkedList<String>();
					JSONArray rowJSONArray = j.getJSONArray("rows");

					rowJSONArray.forEach(x -> {
						rowList.add(x.toString());
					});
					vobj.setRows(rowList);
				}
				if (j.has("cols")) {
					List<String> colList = new LinkedList<String>();

					JSONArray colJSONArray = j.getJSONArray("cols");

					colJSONArray.forEach(x -> {
						colList.add(x.toString());
					});
					vobj.setCols(colList);
				}

				if (j.has("refline")) {
					List<String> refList = new LinkedList<String>();
					JSONArray refJSONArray = j.getJSONArray("refline");
					refJSONArray.forEach(x -> {
						refList.add(x.toString());
					});
					vobj.setRef(refList);
				}

				vobj.setType(type);

				if (j.has("rowsOrdinal")) {
					List<String> rowsOrdinalList = new LinkedList<String>();
					JSONArray rowsOrdinalJSONArray = j.getJSONArray("rowsOrdinal");

					rowsOrdinalJSONArray.forEach(x -> {
						rowsOrdinalList.add(x.toString());
					});
					vobj.setRowsOrdinal(rowsOrdinalList);
				}

				if (j.has("colsOrdinal")) {
					List<String> colsOrdinalList = new LinkedList<String>();
					JSONArray colsOrdinalJSONArray = j.getJSONArray("colsOrdinal");

					colsOrdinalJSONArray.forEach(x -> {
						colsOrdinalList.add(x.toString());
					});
					vobj.setColsOrdinal(colsOrdinalList);
				}

				List<Structure> abcList = new LinkedList<Structure>();
				JSONArray structJSONArray = j.getJSONArray("structure");

				for (int j1 = 0; j1 < structJSONArray.length(); j1++)

				{
//							
					JSONObject jsonObj = structJSONArray.getJSONObject(j1);
					Structure vobj1 = new Structure();
					String strtype = jsonObj.getString("type");
					vobj1.setType(strtype);

					if (jsonObj.has("color")) {
						List<String> colorList = new LinkedList<String>();
						JSONArray colorJSONArray = jsonObj.getJSONArray("color");
						colorJSONArray.forEach(x -> {
							colorList.add(x.toString());
						});
						vobj1.setColor(colorList);
					}

					if (jsonObj.has("size")) {
						List<String> sizeList = new LinkedList<String>();

						JSONArray sizeJSONArray = jsonObj.getJSONArray("size");

						sizeJSONArray.forEach(x -> {
							sizeList.add(x.toString());
						});
						vobj1.setSize(sizeList);
					}

					if (jsonObj.has("wsize")) {
						List<String> wsizeList = new LinkedList<String>();

						JSONArray wsizeJSONArray = jsonObj.getJSONArray("wsize");

						wsizeJSONArray.forEach(x -> {
							wsizeList.add(x.toString());
						});
						vobj1.setWsize(wsizeList);
					}

					if (jsonObj.has("text")) {
						List<String> textList = new LinkedList<String>();

						JSONArray textJSONArray = jsonObj.getJSONArray("text");

						textJSONArray.forEach(x -> {
							textList.add(x.toString());
						});
						vobj1.setText(textList);
					}
					if (jsonObj.has("lod")) {
						List<String> lodList = new LinkedList<String>();

						JSONArray lodJSONArray = jsonObj.getJSONArray("lod");

						lodJSONArray.forEach(x -> {
							lodList.add(x.toString());
						});
						vobj1.setLod(lodList);
					}
					if (jsonObj.has("xaxis")) {
						String x = jsonObj.getString("xaxis");
						vobj1.setXaxis(x);
					}
					if (jsonObj.has("yaxis")) {
						String y = jsonObj.getString("xaxis");
						vobj1.setYaxis(y);
					}

					abcList.add(vobj1);
				}

				vobj.setStructureList(abcList);

				if (j.has("filter")) {

					List<Filter> filterList = new LinkedList<Filter>();
					JSONArray filterJSONArray = j.getJSONArray("filter");

					for (int j1 = 0; j1 < filterJSONArray.length(); j1++) {
						JSONObject filerObj = filterJSONArray.getJSONObject(j1);
						Filter mdl = new Filter();
						String column = filerObj.getString("column");
						mdl.setColumn(column);

						String from = filerObj.getString("from");
						mdl.setFrom(from);

						String to = filerObj.getString("to");
						mdl.setTo(to);

						if (filerObj.has("members")) {
							List<String> membersList = new LinkedList<String>();

							JSONArray membersJSONArray = filerObj.getJSONArray("members");

							membersJSONArray.forEach(x -> {
								membersList.add(x.toString());
							});
							mdl.setMembersList(membersList);
						}
						filterList.add(mdl);
					}
					vobj.setFilterList(filterList);
				}
				visuals.put(m.getReportName(), vobj);
			}
		}

		for (VisualModel vm : migratorModel.getVisualModelList()) {
			String[] temp4 = vm.getReportTabName().split(split3);
			variables.put(vm.getReportName(), temp4);
		}
	}

	private void writeTWBFILE(MigratorModel migratorModel) {
		// TODO Auto-generated method stub
		try {
			writeTwbFile(migratorModel);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static String getAlphaNumericString(int n) {

		// chose a Character random from this String
		String AlphaNumericString = "ABCDEF" + "0123456789";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			// generate a random number between
			// 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());

			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));
		}

		return sb.toString();
	}

	static String getAlphaNumericString2(int n) {

		// chose a Character random from this String
		String AlphaNumericString = "abcdefghijklmnopqrstuvwxyz" + "0123456789";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			// generate a random number between
			// 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());

			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));
		}

		return sb.toString();
	}

	private static void writeTwbFile(MigratorModel migratorModel) throws XMLStreamException, IOException {
		// TODO Auto-generated method stub
		System.setProperty("javax.xml.stream.XMLOutputFactory", "com.sun.xml.internal.stream.XMLOutputFactoryImpl");
		XMLOutputFactory xof = javax.xml.stream.XMLOutputFactory.newInstance();
		// XMLOutputFactory xof = XMLOutputFactory.newInstance();
		xof.setProperty("escapeCharacters", false);
		XMLStreamWriter xtw = null;
		XMLStreamWriter prettyPrintWriter = null;

//		DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("MM-dd-uuuu HH:mm:ss");
//		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HH:mm:ss");
//
//		LocalDateTime date = LocalDateTime.now();
//		LocalDateTime time = LocalDateTime.now();
//		SYSTEM.OUT.PRINTLN();
//		COMMONCONSTANTS.FILEPATH + FILE.SEPARATOR
//		+ migratorModel.getQueryModelList().get(0).getReportName() + dtf.format(now).toString() + ".twb";

		String fileName = CommonConstants.FILEPATH + File.separator
				+ (migratorModel.getQueryModelList().get(0).getReportName() != null
				&& !migratorModel.getQueryModelList().get(0).getReportName().trim().isEmpty()
						? migratorModel.getQueryModelList().get(0).getReportName()
						: "" )+ ".twb";

		System.out.println(fileName);
		Writer writer = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
		xtw = xof.createXMLStreamWriter(writer);

		// Wrap with pretty print proxy
		PrettyPrintHandler handler = new PrettyPrintHandler(xtw);
		prettyPrintWriter = (XMLStreamWriter) Proxy.newProxyInstance(XMLStreamWriter.class.getClassLoader(),
				new Class[] { XMLStreamWriter.class }, handler);

		writeTwbCore(prettyPrintWriter);
		writeTwbDatasource(prettyPrintWriter, migratorModel);

		prettyPrintWriter.flush();
		prettyPrintWriter.close();
	}

	private static void writeTwbCore(XMLStreamWriter prettyPrintWriter) throws XMLStreamException {

		prettyPrintWriter.writeStartDocument("utf-8", "1.0");
		prettyPrintWriter.writeComment("\n");
		prettyPrintWriter.writeComment("build 20213.21.0822.2038 ");
		prettyPrintWriter.writeStartElement("workbook");
		prettyPrintWriter.writeAttribute("original-version", "18.1");
		prettyPrintWriter.writeAttribute("source-build", "2021.3.0 (20213.21.0822.2038)");
		prettyPrintWriter.writeAttribute("source-platform", "win");
		prettyPrintWriter.writeAttribute("version", "18.1");
		prettyPrintWriter.writeNamespace("user", "http://www.tableausoftware.com/xml/user");
		prettyPrintWriter.writeStartElement("document-format-change-manifest");
		prettyPrintWriter.writeEmptyElement("_.fcp.AccessibleZoneTabOrder.true...AccessibleZoneTabOrder");
		prettyPrintWriter.writeEmptyElement("_.fcp.AnimationOnByDefault.true...AnimationOnByDefault");
		prettyPrintWriter.writeEmptyElement("AutoCreateAndUpdateDSDPhoneLayouts");
		prettyPrintWriter.writeEmptyElement("MapboxVectorStylesAndLayers");
		prettyPrintWriter.writeEmptyElement("_.fcp.MarkAnimation.true...MarkAnimation");
		prettyPrintWriter.writeEmptyElement("_.fcp.ObjectModelEncapsulateLegacy.true...ObjectModelEncapsulateLegacy");
		prettyPrintWriter.writeEmptyElement("_.fcp.ObjectModelTableType.true...ObjectModelTableType");
		prettyPrintWriter.writeEmptyElement("_.fcp.SchemaViewerObjectModel.true...SchemaViewerObjectModel");
		prettyPrintWriter.writeEmptyElement("SetMembershipControl");
		prettyPrintWriter.writeEmptyElement("SheetIdentifierTracking");
		prettyPrintWriter.writeEmptyElement("WindowsPersistSimpleIdentifiers");
		prettyPrintWriter.writeEndElement();
		prettyPrintWriter.writeStartElement("preferences");
		prettyPrintWriter.writeEmptyElement("preference");
		prettyPrintWriter.writeAttribute("name", "ui.encoding.shelf.height");
		prettyPrintWriter.writeAttribute("value", "24");
		prettyPrintWriter.writeEmptyElement("preference");
		prettyPrintWriter.writeAttribute("name", "ui.shelf.height");
		prettyPrintWriter.writeAttribute("value", "26");
		prettyPrintWriter.writeEndElement();
		prettyPrintWriter.writeStartElement("_.fcp.AnimationOnByDefault.false...style");
		prettyPrintWriter.writeStartElement("_.fcp.AnimationOnByDefault.false..._.fcp.MarkAnimation.true...style-rule");
		prettyPrintWriter.writeAttribute("element", "animation");
		prettyPrintWriter.writeEmptyElement("_.fcp.AnimationOnByDefault.false...format");
		prettyPrintWriter.writeAttribute("attr", "animation-on");
		prettyPrintWriter.writeAttribute("value", "ao-on");
		prettyPrintWriter.writeEndElement();
		prettyPrintWriter.writeEndElement();

	}

	/*
	 * @SuppressWarnings("deprecation") private static void
	 * writeTwbDataSourceForCrossTab(XMLStreamWriter prettyPrintWriter) { try {
	 * prettyPrintWriter.writeStartElement("datasources");
	 * prettyPrintWriter.writeStartElement("datasource");
	 * prettyPrintWriter.writeAttribute("caption", datasrc.getDatabaseName());
	 * prettyPrintWriter.writeAttribute("inline", "true");
	 * prettyPrintWriter.writeAttribute("name", "federated." +
	 * datasrc.getTableId()); prettyPrintWriter.writeAttribute("version", "18.1");
	 * prettyPrintWriter.writeStartElement("connection");
	 * prettyPrintWriter.writeAttribute("class", "federated");
	 * prettyPrintWriter.writeStartElement("named-connections");
	 * prettyPrintWriter.writeStartElement("named-connection");
	 * prettyPrintWriter.writeAttribute("caption", (String)
	 * datasrc.getDatabaseName()); prettyPrintWriter.writeAttribute("name",
	 * datasrc.getDatabaseType() + "." + datasrc.getConnId()); if
	 * (datasrc.getDatabaseType().equalsIgnoreCase("excel-direct")) {
	 * prettyPrintWriter.writeEmptyElement("connection");
	 * prettyPrintWriter.writeAttribute("class", "" + datasrc.getDatabaseType());
	 * prettyPrintWriter.writeAttribute("cleaning", "no");
	 * prettyPrintWriter.writeAttribute("compat", "no");
	 * prettyPrintWriter.writeAttribute("dataRefreshTime", "");
	 * prettyPrintWriter.writeAttribute("filename",
	 * "C:/Users/10693394/Downloads/Sample - Superstore.xls");
	 * prettyPrintWriter.writeAttribute("interpretationMode", "0");
	 * prettyPrintWriter.writeAttribute("password", "");
	 * prettyPrintWriter.writeAttribute("server", "");
	 * prettyPrintWriter.writeAttribute("validate", "no"); }
	 * prettyPrintWriter.writeEndElement(); prettyPrintWriter.writeEndElement();
	 * 
	 * prettyPrintWriter.writeStartElement(
	 * "_.fcp.ObjectModelEncapsulateLegacy.false...relation");
	 * prettyPrintWriter.writeAttribute("connection", datasrc.getDatabaseType() +
	 * "." + datasrc.getConnId()); prettyPrintWriter.writeAttribute("name",
	 * datasrc.getTableName()); prettyPrintWriter.writeAttribute("table", "[" +
	 * datasrc.getTableName() + "$]"); prettyPrintWriter.writeAttribute("type",
	 * "table"); prettyPrintWriter.writeStartElement("columns");
	 * prettyPrintWriter.writeAttribute("gridOrigin", "A1:U9995:no:A1:U9995:0");
	 * prettyPrintWriter.writeAttribute("header", "yes");
	 * prettyPrintWriter.writeAttribute("outcome", "6");
	 * 
	 * 
	 * } catch (XMLStreamException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

	@SuppressWarnings("deprecation")
	private static void writeTwbDatasource(XMLStreamWriter prettyPrintWriter, MigratorModel migratorModel)
			throws XMLStreamException {
		try {

			prettyPrintWriter.writeStartElement("datasources");
			for (MetadataColumnModel meta : migratorModel.getMetadataColumnModelList()) {
				System.out.println(metadata.get(meta.getMetadataColumnName())[0] + "///datasource");
			}
			int i = 1;
			for (QueryModel qm : migratorModel.getQueryModelList()) {

				prettyPrintWriter.writeStartElement("datasource");
				if (qm.getDatabaseName().equalsIgnoreCase("Sample - Superstore")) {
					prettyPrintWriter.writeAttribute("caption", datasrc.getDatabaseName());
				} else {
					prettyPrintWriter.writeAttribute("caption",
							datasrc.getTableName() + " (" + datasrc.getDatabaseName() + ")");
				}

				prettyPrintWriter.writeAttribute("inline", "true");
				prettyPrintWriter.writeAttribute("name", "federated." + datasrc.getTableId());
				prettyPrintWriter.writeAttribute("version", "18.1");
				prettyPrintWriter.writeStartElement("connection");
				prettyPrintWriter.writeAttribute("class", "federated");
				prettyPrintWriter.writeStartElement("named-connections");
				prettyPrintWriter.writeStartElement("named-connection");
				prettyPrintWriter.writeAttribute("caption", (String) datasrc.getDatabaseName());
				prettyPrintWriter.writeAttribute("name", datasrc.getDatabaseType() + "." + datasrc.getConnId());

				if (datasrc.getDatabaseType().equalsIgnoreCase("mysql")) {
					prettyPrintWriter.writeEmptyElement("connection");
					prettyPrintWriter.writeAttribute("class", "" + datasrc.getDatabaseType());
					prettyPrintWriter.writeAttribute("dbname", (String) datasrc.getDatabaseName());
					prettyPrintWriter.writeAttribute("odbc-native-protocol", "");
					prettyPrintWriter.writeAttribute("one-time-sql", "");
					prettyPrintWriter.writeAttribute("port", "3306");
					prettyPrintWriter.writeAttribute("server", datasrc.getServerHostName() + "");
					prettyPrintWriter.writeAttribute("source-charset", "");
					prettyPrintWriter.writeAttribute("username", "root");
					prettyPrintWriter.writeAttribute("password", "root");
				} else if (datasrc.getDatabaseType().equalsIgnoreCase("msaccess")) {
					prettyPrintWriter.writeEmptyElement("connection");
					prettyPrintWriter.writeAttribute("authentication", "no");
					prettyPrintWriter.writeAttribute("class", "" + datasrc.getDatabaseType());
					prettyPrintWriter.writeAttribute("driver", "");
//				prettyPrintWriter.writeAttribute("filename", "C:/Users/Administrator/Desktop/efashion.mdb");
					prettyPrintWriter.writeAttribute("filename", CommonConstants.ACCESSFILE);
					prettyPrintWriter.writeAttribute("mdwpath", "");
					prettyPrintWriter.writeAttribute("server", "");
					prettyPrintWriter.writeAttribute("username", "");
				} else {
					throw new Exception("Not Found Data.");
				}

//			else if (datasrc.getDatabaseType().equalsIgnoreCase("excel-direct")) {
//				prettyPrintWriter.writeEmptyElement("connection");
//				prettyPrintWriter.writeAttribute("class", "" + datasrc.getDatabaseType());
//				prettyPrintWriter.writeAttribute("cleaning", "no");
//				prettyPrintWriter.writeAttribute("compat", "no");
//				prettyPrintWriter.writeAttribute("dataRefreshTime", "");
//				prettyPrintWriter.writeAttribute("filename", "C:/Users/10693394/Downloads/Sample - Superstore.xls");
//				prettyPrintWriter.writeAttribute("interpretationMode", "0");
//				prettyPrintWriter.writeAttribute("password", "");
//				prettyPrintWriter.writeAttribute("server", "");
//				prettyPrintWriter.writeAttribute("validate", "no");
//			}
				prettyPrintWriter.writeEndElement();
				prettyPrintWriter.writeEndElement();

				if (datasrc.getQueryStatement() != null && !datasrc.getQueryStatement().trim().isEmpty()) {
					prettyPrintWriter.writeStartElement("_.fcp.ObjectModelEncapsulateLegacy.false...relation");
					prettyPrintWriter.writeAttribute("connection",
							datasrc.getDatabaseType() + "." + datasrc.getConnId());
					prettyPrintWriter.writeAttribute("name", datasrc.getTableName() + "");
					prettyPrintWriter.writeAttribute("type", "text");
					prettyPrintWriter.writeCharacters((String) datasrc.getQueryStatement());
					prettyPrintWriter.writeEndElement();

					prettyPrintWriter.writeStartElement("_.fcp.ObjectModelEncapsulateLegacy.true...relation");
					prettyPrintWriter.writeAttribute("connection",
							datasrc.getDatabaseType() + "." + datasrc.getConnId());
					prettyPrintWriter.writeAttribute("name", datasrc.getTableName() + "");
					prettyPrintWriter.writeAttribute("type", "text");
					prettyPrintWriter.writeCharacters((String) datasrc.getQueryStatement());
					prettyPrintWriter.writeEndElement();
				} else {

					if (migratorModel.getDatasourceModelList().size() == 1
							&& migratorModel.getDatasourceModelList().get(0).getType().equalsIgnoreCase("No Join")) {
						prettyPrintWriter.writeEmptyElement("_.fcp.ObjectModelEncapsulateLegacy.false...relation");
						prettyPrintWriter.writeAttribute("connection",
								datasrc.getDatabaseType() + "." + datasrc.getConnId());

						prettyPrintWriter.writeAttribute("name",
								migratorModel.getDatasourceModelList().get(0).getLtable());
						prettyPrintWriter.writeAttribute("table",
								"[" + migratorModel.getDatasourceModelList().get(0).getLtable() + "]");
						prettyPrintWriter.writeAttribute("type", "table");

						prettyPrintWriter.writeEmptyElement("_.fcp.ObjectModelEncapsulateLegacy.true...relation");
						prettyPrintWriter.writeAttribute("connection",
								datasrc.getDatabaseType() + "." + datasrc.getConnId());

						prettyPrintWriter.writeAttribute("name",
								migratorModel.getDatasourceModelList().get(0).getLtable());
						prettyPrintWriter.writeAttribute("table",
								"[" + migratorModel.getDatasourceModelList().get(0).getLtable() + "]");
						prettyPrintWriter.writeAttribute("type", "table");

					} else {
						int j = 1;

						migratorModel.setDatasourceModelList(
								DatasourceModelingJoinsEngine.joinEngine(migratorModel.getDatasourceModelList()));

						for (DatasourceModel dmodel : migratorModel.getDatasourceModelList()) {

							if (j == 1) {
								prettyPrintWriter
										.writeStartElement("_.fcp.ObjectModelEncapsulateLegacy.false...relation");
								prettyPrintWriter.writeAttribute("join", dmodel.getType());
								prettyPrintWriter.writeAttribute("type", "join");
								prettyPrintWriter.writeStartElement("clause");
								prettyPrintWriter.writeAttribute("type", "join");
								prettyPrintWriter.writeStartElement("expression");
								prettyPrintWriter.writeAttribute("op", "=");
								prettyPrintWriter.writeEmptyElement("expression");
								prettyPrintWriter.writeAttribute("op",
										"[" + dmodel.getLtable() + "].[" + dmodel.getLcolumn().trim() + "]");
								prettyPrintWriter.writeEmptyElement("expression");
								prettyPrintWriter.writeAttribute("op",
										"[" + dmodel.getRtable() + "].[" + dmodel.getRcolumn().trim() + "]");

								tableorder.add(dmodel.getLtable());
								tableorder.add(dmodel.getRtable());

								prettyPrintWriter.writeEndElement(); // expression
								prettyPrintWriter.writeEndElement();// clause

							} else {
								prettyPrintWriter.writeStartElement("relation");
								prettyPrintWriter.writeAttribute("join", dmodel.getType());
								prettyPrintWriter.writeAttribute("type", "join");
								prettyPrintWriter.writeStartElement("clause");
								prettyPrintWriter.writeAttribute("type", "join");
								prettyPrintWriter.writeStartElement("expression");
								prettyPrintWriter.writeAttribute("op", "=");
								prettyPrintWriter.writeEmptyElement("expression");
								prettyPrintWriter.writeAttribute("op",
										"[" + dmodel.getLtable() + "].[" + dmodel.getLcolumn().trim() + "]");
								prettyPrintWriter.writeEmptyElement("expression");
								prettyPrintWriter.writeAttribute("op",
										"[" + dmodel.getRtable() + "].[" + dmodel.getRcolumn().trim() + "]");

								tableorder.add(dmodel.getLtable().trim());
								tableorder.add(dmodel.getRtable().trim());

								prettyPrintWriter.writeEndElement(); // expression
								prettyPrintWriter.writeEndElement();// clause
							}
							j++;

						}

						for (int k = migratorModel.getDatasourceModelList().size() - 1; k >= 0; k--) {
							if (k == migratorModel.getDatasourceModelList().size() - 1) {
								prettyPrintWriter.writeEmptyElement("relation");
								prettyPrintWriter.writeAttribute("connection",
										datasrc.getDatabaseType() + "." + datasrc.getConnId());
								prettyPrintWriter.writeAttribute("name",
										migratorModel.getDatasourceModelList().get(k).getLtable().trim());
								prettyPrintWriter.writeAttribute("table",
										"[" + migratorModel.getDatasourceModelList().get(k).getLtable().trim() + "]");
								prettyPrintWriter.writeAttribute("type", "table");

								prettyPrintWriter.writeEmptyElement("relation");
								prettyPrintWriter.writeAttribute("connection",
										datasrc.getDatabaseType() + "." + datasrc.getConnId());
								prettyPrintWriter.writeAttribute("name",
										migratorModel.getDatasourceModelList().get(k).getRtable());
								prettyPrintWriter.writeAttribute("table",
										"[" + migratorModel.getDatasourceModelList().get(k).getRtable().trim() + "]");
								prettyPrintWriter.writeAttribute("type", "table");
							} else {
								prettyPrintWriter.writeEndElement();// relation
								prettyPrintWriter.writeEmptyElement("relation");
								prettyPrintWriter.writeAttribute("connection",
										datasrc.getDatabaseType() + "." + datasrc.getConnId());
								prettyPrintWriter.writeAttribute("name",
										migratorModel.getDatasourceModelList().get(k).getRtable().trim());
								prettyPrintWriter.writeAttribute("table",
										"[" + migratorModel.getDatasourceModelList().get(k).getRtable().trim() + "]");
								prettyPrintWriter.writeAttribute("type", "table");
							}
						}
						prettyPrintWriter.writeEndElement();// _fcp

						j = 1;
						for (DatasourceModel dmodel : migratorModel.getDatasourceModelList()) {
							if (j == 1) {
								prettyPrintWriter
										.writeStartElement("_.fcp.ObjectModelEncapsulateLegacy.true...relation");
								prettyPrintWriter.writeAttribute("join", dmodel.getType());
								prettyPrintWriter.writeAttribute("type", "join");
								prettyPrintWriter.writeStartElement("clause");
								prettyPrintWriter.writeAttribute("type", "join");
								prettyPrintWriter.writeStartElement("expression");
								prettyPrintWriter.writeAttribute("op", "=");
								prettyPrintWriter.writeEmptyElement("expression");
								prettyPrintWriter.writeAttribute("op",
										"[" + dmodel.getLtable() + "].[" + dmodel.getLcolumn() + "]");
								prettyPrintWriter.writeEmptyElement("expression");
								prettyPrintWriter.writeAttribute("op",
										"[" + dmodel.getRtable() + "].[" + dmodel.getRcolumn() + "]");

								prettyPrintWriter.writeEndElement(); // expression
								prettyPrintWriter.writeEndElement();// clause

							} else {
								prettyPrintWriter.writeStartElement("relation");
								prettyPrintWriter.writeAttribute("join", dmodel.getType());
								prettyPrintWriter.writeAttribute("type", "join");
								prettyPrintWriter.writeStartElement("clause");
								prettyPrintWriter.writeAttribute("type", "join");
								prettyPrintWriter.writeStartElement("expression");
								prettyPrintWriter.writeAttribute("op", "=");
								prettyPrintWriter.writeEmptyElement("expression");
								prettyPrintWriter.writeAttribute("op",
										"[" + dmodel.getLtable() + "].[" + dmodel.getLcolumn() + "]");
								prettyPrintWriter.writeEmptyElement("expression");
								prettyPrintWriter.writeAttribute("op",
										"[" + dmodel.getRtable() + "].[" + dmodel.getRcolumn() + "]");

								prettyPrintWriter.writeEndElement(); // expression
								prettyPrintWriter.writeEndElement();// clause
							}
							j++;

						}

						for (int k = migratorModel.getDatasourceModelList().size() - 1; k >= 0; k--) {
							if (k == migratorModel.getDatasourceModelList().size() - 1) {
								prettyPrintWriter.writeEmptyElement("relation");
								prettyPrintWriter.writeAttribute("connection",
										datasrc.getDatabaseType() + "." + datasrc.getConnId());
								prettyPrintWriter.writeAttribute("name",
										migratorModel.getDatasourceModelList().get(k).getLtable());
								prettyPrintWriter.writeAttribute("table",
										"[" + migratorModel.getDatasourceModelList().get(k).getLtable() + "]");
								prettyPrintWriter.writeAttribute("type", "table");

								prettyPrintWriter.writeEmptyElement("relation");
								prettyPrintWriter.writeAttribute("connection",
										datasrc.getDatabaseType() + "." + datasrc.getConnId());
								prettyPrintWriter.writeAttribute("name",
										migratorModel.getDatasourceModelList().get(k).getRtable());
								prettyPrintWriter.writeAttribute("table",
										"[" + migratorModel.getDatasourceModelList().get(k).getRtable() + "]");
								prettyPrintWriter.writeAttribute("type", "table");
							} else {
								prettyPrintWriter.writeEndElement();// relation
								prettyPrintWriter.writeEmptyElement("relation");
								prettyPrintWriter.writeAttribute("connection",
										datasrc.getDatabaseType() + "." + datasrc.getConnId());
								prettyPrintWriter.writeAttribute("name",
										migratorModel.getDatasourceModelList().get(k).getRtable());
								prettyPrintWriter.writeAttribute("table",
										"[" + migratorModel.getDatasourceModelList().get(k).getRtable() + "]");
								prettyPrintWriter.writeAttribute("type", "table");
							}
						}

						prettyPrintWriter.writeEndElement();// _fcp

					}

					prettyPrintWriter.writeStartElement("cols");
					String a = "";
					System.out.println(metadata.size() + "\n\n");
					TreeMap<String, MetadataColumnModel> tmap = new TreeMap<>();
					for (MetadataColumnModel meta : migratorModel.getMetadataColumnModelList()) {
						if (tmap.containsKey(meta.getMetadataColumnName())) {
							boolean b1 = false;
							boolean b2 = false;
							String t1 = meta.getTableName();
							String t2 = tmap.get(meta.getMetadataColumnName()).getTableName();
							Object[] tablearray = tableorder.toArray();
							for (int k = 0; k < tablearray.length; k++) {
								if (tablearray[k].toString().equalsIgnoreCase(t1)) {
									b1 = true;
									break;
								}
								if (tablearray[k].toString().equalsIgnoreCase(t2)) {
									b2 = true;
									break;
								}
							}
							if (b1) {
								tmap.put(meta.getMetadataColumnName() + " (" + t2 + ")",
										tmap.get(meta.getMetadataColumnName()));
								tmap.put(meta.getMetadataColumnName(), meta);

							}
							if (b2) {
								tmap.put(meta.getMetadataColumnName() + " (" + t1 + ")", meta);
							}
						} else {
							tmap.put(meta.getMetadataColumnName(), meta);
						}
					}
					System.out.println(tmap);
					Set<String> tset = tmap.keySet();
					for (String key : tset) {
						System.out.println();
						MetadataColumnModel meta = tmap.get(key);
						prettyPrintWriter.writeEmptyElement("map");
						prettyPrintWriter.writeAttribute("key", "[" + key + "]");
						prettyPrintWriter.writeAttribute("value", "[" + meta.getTableName() + "].[" + key + "]");
						a = a + " , " + meta.getMetadataColumnName();
					}
					prettyPrintWriter.writeEndElement();// cols
				}
				prettyPrintWriter.writeEndElement();// connection
				prettyPrintWriter.writeEmptyElement("aliases");
				prettyPrintWriter.writeAttribute("enabled", "yes");

			}

			String a = "";

			for (CalculationsModel calc : migratorModel.getCalculationsList()) {
				prettyPrintWriter.writeStartElement("column");
				prettyPrintWriter.writeAttribute("caption", calc.getCalculationName());
				prettyPrintWriter.writeAttribute("datatype", "real");
				prettyPrintWriter.writeAttribute("name", "[" + calc.getCalculationName() + "]");
				prettyPrintWriter.writeAttribute("role", "measure");
				prettyPrintWriter.writeAttribute("type", "quantitative");
				prettyPrintWriter.writeEmptyElement("calculation");
				prettyPrintWriter.writeAttribute("class", "tableau");
				prettyPrintWriter.writeAttribute("formula", calc.getFormula());
				prettyPrintWriter.writeEndElement();// column
			}

			prettyPrintWriter.writeEmptyElement("_.fcp.ObjectModelTableType.true...column");
			prettyPrintWriter.writeAttribute("caption", (String) datasrc.getTableName());
			prettyPrintWriter.writeAttribute("datatype", "string");
			prettyPrintWriter.writeAttribute("name", "[__tableau_internal_object_id__].[" + datasrc.getTableName() + "_"
					+ datasrc.getMetadataId() + "]");
			prettyPrintWriter.writeAttribute("role", "measure");
			prettyPrintWriter.writeAttribute("type", "quantitative");

			prettyPrintWriter.writeEmptyElement("layout");
			prettyPrintWriter.writeAttribute("_.fcp.SchemaViewerObjectModel.false...dim-percentage", "0.5");
			prettyPrintWriter.writeAttribute("_.fcp.SchemaViewerObjectModel.false...measure-percentage", "0.4");
			prettyPrintWriter.writeAttribute("dim-ordering", "alphabetic");
			prettyPrintWriter.writeAttribute("measure-ordering", "alphabetic");
			prettyPrintWriter.writeAttribute("show-structure", "true");

			prettyPrintWriter.writeStartElement("semantic-values");
//		prettyPrintWriter.writeEmptyElement("semantic-value");
//		prettyPrintWriter.writeAttribute("key", "[City].[Name]");
//		prettyPrintWriter.writeAttribute("value", "%null%");
			prettyPrintWriter.writeEmptyElement("semantic-value");
			prettyPrintWriter.writeAttribute("key", "[Country].[Name]");
			prettyPrintWriter.writeAttribute("value", "\"United States\"");
//		prettyPrintWriter.writeEmptyElement("semantic-value");
//		prettyPrintWriter.writeAttribute("key", "[State].[Name]");
//		prettyPrintWriter.writeAttribute("value", "%null%");
			prettyPrintWriter.writeEndElement();

			prettyPrintWriter.writeStartElement("_.fcp.ObjectModelEncapsulateLegacy.true...object-graph");
			prettyPrintWriter.writeStartElement("objects");
			prettyPrintWriter.writeStartElement("object");
			prettyPrintWriter.writeAttribute("caption", (String) datasrc.getTableName());
			prettyPrintWriter.writeAttribute("id", datasrc.getTableName() + "_" + datasrc.getMetadataId());
			prettyPrintWriter.writeStartElement("properties");
			prettyPrintWriter.writeAttribute("context", "");

//		if (datasrc.getDatabaseType().equalsIgnoreCase("mysql")) {
//			prettyPrintWriter.writeStartElement("properties");
//			prettyPrintWriter.writeStartElement("relation");
//			prettyPrintWriter.writeAttribute("connection", datasrc.getDatabaseType() + "." + datasrc.getConnId());
//			prettyPrintWriter.writeAttribute("name", datasrc.getTableName() + "");
//			prettyPrintWriter.writeAttribute("type", "text");
//			prettyPrintWriter.writeCharacters((String) datasrc.getQueryStatement());
//			prettyPrintWriter.writeEndElement();
//			prettyPrintWriter.writeEndElement();
//		} else {

			if (migratorModel.getDatasourceModelList().size() == 1
					&& migratorModel.getDatasourceModelList().get(0).getType().equalsIgnoreCase("No Join")) {
				prettyPrintWriter.writeEmptyElement("relation");
				prettyPrintWriter.writeAttribute("connection", datasrc.getDatabaseType() + "." + datasrc.getConnId());
				prettyPrintWriter.writeAttribute("name", migratorModel.getDatasourceModelList().get(0).getLtable());
				prettyPrintWriter.writeAttribute("table",
						"[" + migratorModel.getDatasourceModelList().get(0).getLtable() + "]");
				prettyPrintWriter.writeAttribute("type", "table");

			} else {

//				CreateDatasource.dataSource(migratorModel.getDatasourceModelList());

				int j = 1;
				for (DatasourceModel dmodel : migratorModel.getDatasourceModelList()) {
					if (j == 1) {
						prettyPrintWriter.writeStartElement("relation");
						prettyPrintWriter.writeAttribute("join", dmodel.getType());
						prettyPrintWriter.writeAttribute("type", "join");
						prettyPrintWriter.writeStartElement("clause");
						prettyPrintWriter.writeAttribute("type", "join");
						prettyPrintWriter.writeStartElement("expression");
						prettyPrintWriter.writeAttribute("op", "=");
						prettyPrintWriter.writeEmptyElement("expression");
						prettyPrintWriter.writeAttribute("op",
								"[" + dmodel.getLtable() + "].[" + dmodel.getLcolumn() + "]");
						prettyPrintWriter.writeEmptyElement("expression");
						prettyPrintWriter.writeAttribute("op",
								"[" + dmodel.getRtable() + "].[" + dmodel.getRcolumn() + "]");

						prettyPrintWriter.writeEndElement(); // expression
						prettyPrintWriter.writeEndElement();// clause
					} else {
						prettyPrintWriter.writeStartElement("relation");
						prettyPrintWriter.writeAttribute("join", dmodel.getType());
						prettyPrintWriter.writeAttribute("type", "join");
						prettyPrintWriter.writeStartElement("clause");
						prettyPrintWriter.writeAttribute("type", "join");
						prettyPrintWriter.writeStartElement("expression");
						prettyPrintWriter.writeAttribute("op", "=");
						prettyPrintWriter.writeEmptyElement("expression");
						prettyPrintWriter.writeAttribute("op",
								"[" + dmodel.getLtable() + "].[" + dmodel.getLcolumn() + "]");
						prettyPrintWriter.writeEmptyElement("expression");
						prettyPrintWriter.writeAttribute("op",
								"[" + dmodel.getRtable() + "].[" + dmodel.getRcolumn() + "]");

						prettyPrintWriter.writeEndElement(); // expression
						prettyPrintWriter.writeEndElement();// clause
					}
					j++;
				}
				for (int k = migratorModel.getDatasourceModelList().size() - 1; k >= 0; k--) {
					if (k == migratorModel.getDatasourceModelList().size() - 1) {
						prettyPrintWriter.writeEmptyElement("relation");
						prettyPrintWriter.writeAttribute("connection",
								datasrc.getDatabaseType() + "." + datasrc.getConnId());
						prettyPrintWriter.writeAttribute("name",
								migratorModel.getDatasourceModelList().get(k).getLtable());
						prettyPrintWriter.writeAttribute("table",
								"[" + migratorModel.getDatasourceModelList().get(k).getLtable() + "]");
						prettyPrintWriter.writeAttribute("type", "table");

						prettyPrintWriter.writeEmptyElement("relation");
						prettyPrintWriter.writeAttribute("connection",
								datasrc.getDatabaseType() + "." + datasrc.getConnId());
						prettyPrintWriter.writeAttribute("name",
								migratorModel.getDatasourceModelList().get(k).getRtable());
						prettyPrintWriter.writeAttribute("table",
								"[" + migratorModel.getDatasourceModelList().get(k).getRtable() + "]");
						prettyPrintWriter.writeAttribute("type", "table");
					} else {
						prettyPrintWriter.writeEndElement();// relation
						prettyPrintWriter.writeEmptyElement("relation");
						prettyPrintWriter.writeAttribute("connection",
								datasrc.getDatabaseType() + "." + datasrc.getConnId());
						prettyPrintWriter.writeAttribute("name",
								migratorModel.getDatasourceModelList().get(k).getRtable());
						prettyPrintWriter.writeAttribute("table",
								"[" + migratorModel.getDatasourceModelList().get(k).getRtable() + "]");
						prettyPrintWriter.writeAttribute("type", "table");
					}
				}
				prettyPrintWriter.writeEndElement();// relation
				prettyPrintWriter.writeComment("Comment for  relation");
			}
//		}

			prettyPrintWriter.writeEndElement();// properties
			prettyPrintWriter.writeEndElement();// object
			prettyPrintWriter.writeEndElement();// objects

			prettyPrintWriter.writeEndElement(); // _.fcp
			prettyPrintWriter.writeEndElement(); // datasource
			prettyPrintWriter.writeEndElement();// datasources
			prettyPrintWriter.writeComment("Comment for  datasources");

			prettyPrintWriter.writeStartElement("mapsources");
			prettyPrintWriter.writeEmptyElement("mapsource");
			prettyPrintWriter.writeAttribute("name", "Tableau");
			prettyPrintWriter.writeEndElement();

			writeWorksheets(prettyPrintWriter, migratorModel);
			Dashboards.writeDashBoards(prettyPrintWriter, migratorModel);
			writeWindows(prettyPrintWriter, migratorModel);
			writeThumbnails(prettyPrintWriter, migratorModel);

			prettyPrintWriter.writeEndElement();
			prettyPrintWriter.writeEndDocument();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private static void writeThumbnails(XMLStreamWriter prettyPrintWriter, MigratorModel migratorModel)
			throws XMLStreamException {
		prettyPrintWriter.writeStartElement("thumbnails");
		for (VisualModel vm : migratorModel.getVisualModelList()) {
			prettyPrintWriter.writeStartElement("thumbnail");
			prettyPrintWriter.writeAttribute("height", "192");
			prettyPrintWriter.writeAttribute("name", vm.getReportName());
			prettyPrintWriter.writeAttribute("width", "192");
			prettyPrintWriter.writeCharacters(getAlphaNumericString(100));
			prettyPrintWriter.writeEndElement();// thumbnail
		}
		prettyPrintWriter.writeEndElement();// thumbnails
	}

	private static void writeWindows(XMLStreamWriter prettyPrintWriter, MigratorModel migratorModel)
			throws XMLStreamException {
		prettyPrintWriter.writeStartElement("windows");
		prettyPrintWriter.writeAttribute("saved-dpi-scale-factor", "1.5");
		prettyPrintWriter.writeAttribute("source-height", "44");

		for (int j = 0; j < migratorModel.getVisualModelList().size(); j++) {
			VisualModel m = migratorModel.getVisualModelList().get(j);
			VisualStructure vstr = visuals.get(m.getReportName());

			prettyPrintWriter.writeStartElement("window");
			prettyPrintWriter.writeAttribute("class", "worksheet");
			prettyPrintWriter.writeAttribute("name", m.getReportName());
			prettyPrintWriter.writeStartElement("cards");
			prettyPrintWriter.writeStartElement("edge");
			prettyPrintWriter.writeAttribute("name", "left");
			prettyPrintWriter.writeStartElement("strip");
			prettyPrintWriter.writeAttribute("size", "160");
			prettyPrintWriter.writeEmptyElement("card");
			prettyPrintWriter.writeAttribute("type", "pages");
			prettyPrintWriter.writeEmptyElement("card");
			prettyPrintWriter.writeAttribute("type", "filters");
			prettyPrintWriter.writeEmptyElement("card");
			prettyPrintWriter.writeAttribute("type", "marks");

			prettyPrintWriter.writeEndElement();// strip
			prettyPrintWriter.writeEndElement();// edge

			prettyPrintWriter.writeStartElement("edge");
			prettyPrintWriter.writeAttribute("name", "top");
			prettyPrintWriter.writeStartElement("strip");
			prettyPrintWriter.writeAttribute("size", "2147483647");
			prettyPrintWriter.writeEmptyElement("card");
			prettyPrintWriter.writeAttribute("type", "columns");
			prettyPrintWriter.writeEndElement();
			prettyPrintWriter.writeStartElement("strip");
			prettyPrintWriter.writeAttribute("size", "2147483647");
			prettyPrintWriter.writeEmptyElement("card");
			prettyPrintWriter.writeAttribute("type", "rows");
			prettyPrintWriter.writeEndElement();
			prettyPrintWriter.writeStartElement("strip");
			prettyPrintWriter.writeAttribute("size", "2147483647");
			prettyPrintWriter.writeEmptyElement("card");
			prettyPrintWriter.writeAttribute("type", "title");
			prettyPrintWriter.writeEndElement(); // strip
			prettyPrintWriter.writeEndElement();// edge

			if (vstr.getRows() != null) {
				prettyPrintWriter.writeStartElement("edge");
				prettyPrintWriter.writeAttribute("name", "right");
				prettyPrintWriter.writeStartElement("strip");
				prettyPrintWriter.writeAttribute("size", "160");
				for (String str : vstr.getRows()) {

					prettyPrintWriter.writeEmptyElement("card");
					prettyPrintWriter.writeAttribute("pane-specification-id", "0");
					if (strmeta.contains(str)) {
						switch (metadata.get(str)[0]) {
						case "string":
							prettyPrintWriter.writeAttribute("param",
									"[federated." + datasrc.getTableId() + "].[none:" + str + ":nk]");
							prettyPrintWriter.writeAttribute("type", "color");
							break;
						case "integer":
							prettyPrintWriter.writeAttribute("param",
									"[federated." + datasrc.getTableId() + "].[sum:" + str + ":qk]");
							prettyPrintWriter.writeAttribute("type", "color");
							break;
						default:
							prettyPrintWriter.writeAttribute("param",
									"[federated." + datasrc.getTableId() + "].[sum:" + str + ":qk]");
							prettyPrintWriter.writeAttribute("type", "color");
							break;
						}

					} else {
						prettyPrintWriter.writeAttribute("param",
								"[federated." + datasrc.getTableId() + "].[:" + str + ":qk]");
						prettyPrintWriter.writeAttribute("type", "color");
					}

				}
				prettyPrintWriter.writeEndElement();// strip
				prettyPrintWriter.writeEndElement();// edge

			}

			prettyPrintWriter.writeEndElement();// cards

			if (vstr.getType().equals(CHART_TYPE.PIE.getValue())
					|| vstr.getType().equals(CHART_TYPE.DONUT.getValue())) {
				prettyPrintWriter.writeStartElement("viewpoint");
				prettyPrintWriter.writeEmptyElement("zoom");
				prettyPrintWriter.writeAttribute("type", "entire-view");
				prettyPrintWriter.writeEndElement();// strip
			}

			prettyPrintWriter.writeEmptyElement("simple-id");
			prettyPrintWriter.writeAttribute("uuid",
					"{" + getAlphaNumericString(8) + "-" + getAlphaNumericString(4) + "-" + getAlphaNumericString(4)
							+ "-" + getAlphaNumericString(4) + "-" + getAlphaNumericString(12) + "}");
			prettyPrintWriter.writeEndElement();// window

		}

		prettyPrintWriter.writeStartElement("window");
		prettyPrintWriter.writeAttribute("class", "dashboard");
		prettyPrintWriter.writeAttribute("name", "Dashboard");
		prettyPrintWriter.writeStartElement("viewpoints");
		for (VisualModel vm : migratorModel.getVisualModelList()) {
			prettyPrintWriter.writeStartElement("viewpoint");
			prettyPrintWriter.writeAttribute("name", vm.getReportName());
			prettyPrintWriter.writeEmptyElement("zoom");
			prettyPrintWriter.writeAttribute("type", "entire-view");
			prettyPrintWriter.writeEndElement();// viewpoint
		}

		prettyPrintWriter.writeEndElement();// viewpoints
		prettyPrintWriter.writeEmptyElement("active");
		prettyPrintWriter.writeAttribute("id", "-1");
		prettyPrintWriter.writeEmptyElement("simple-id");
		prettyPrintWriter.writeAttribute("uuid", "{" + getAlphaNumericString(8) + "-" + getAlphaNumericString(4) + "-"
				+ getAlphaNumericString(4) + "-" + getAlphaNumericString(4) + "-" + getAlphaNumericString(12) + "}");
		prettyPrintWriter.writeEndElement();// window
		prettyPrintWriter.writeEndElement();
	}

//	private static void writeDashboards(XMLStreamWriter prettyPrintWriter, MigratorModel migratorModel)
//			throws XMLStreamException {
//		prettyPrintWriter.writeStartElement("dashboards");
//		for (VisualModel visuals_mdl : migratorModel.getVisualModelList()) {
//
//			prettyPrintWriter.writeStartElement("dashboard");
//			prettyPrintWriter.writeAttribute("_.fcp.AccessibleZoneTabOrder.true...enable-sort-zone-taborder", "true");
//			prettyPrintWriter.writeAttribute("name",visuals_mdl.getDashboardName());
//			prettyPrintWriter.writeEmptyElement("style");
//
//			prettyPrintWriter.writeEndElement();// dashboard
//		}
//		prettyPrintWriter.writeEndElement();// dashboards
//		prettyPrintWriter.writeComment("Comment for dashboards");
//
//	}
//
//	private static void writeDashboards(XMLStreamWriter prettyPrintWriter, MigratorModel migratorModel)
//			throws XMLStreamException {
//		prettyPrintWriter.writeStartElement("dashboards");
//		prettyPrintWriter.writeStartElement("dashboard");
//		prettyPrintWriter.writeAttribute("_.fcp.AccessibleZoneTabOrder.true...enable-sort-zone-taborder", "true");
//		prettyPrintWriter.writeAttribute("name", "Dashboard");
//		prettyPrintWriter.writeEmptyElement("style");
//		prettyPrintWriter.writeEmptyElement("size");
//		prettyPrintWriter.writeAttribute("maxheight", "800");
//		prettyPrintWriter.writeAttribute("maxwidth", "1000");
//		prettyPrintWriter.writeAttribute("minheight", "800");
//		prettyPrintWriter.writeAttribute("minwidth", "1000");
//		prettyPrintWriter.writeStartElement("zones");
//		prettyPrintWriter.writeStartElement("zone");
//		prettyPrintWriter.writeAttribute("_.fcp.SetMembershipControl.false...type", "layout-basic");
//		prettyPrintWriter.writeAttribute("_.fcp.SetMembershipControl.true...type-v2", "layout-basic");
//		prettyPrintWriter.writeAttribute("h", "100000");
//		prettyPrintWriter.writeAttribute("id", "1");
//		prettyPrintWriter.writeAttribute("w", "100000");
//		prettyPrintWriter.writeAttribute("x", "0");
//		prettyPrintWriter.writeAttribute("y", "0");
//		prettyPrintWriter.writeStartElement("zone");
//		prettyPrintWriter.writeAttribute("_.fcp.SetMembershipControl.false...type", "layout-flow");
//		prettyPrintWriter.writeAttribute("_.fcp.SetMembershipControl.true...type-v2", "layout-flow");
//		prettyPrintWriter.writeAttribute("h", "98000");
//		prettyPrintWriter.writeAttribute("id", "2");
//		prettyPrintWriter.writeAttribute("param", "horz");
//		prettyPrintWriter.writeAttribute("w", "98400");
//		prettyPrintWriter.writeAttribute("x", "800");
//		prettyPrintWriter.writeAttribute("y", "1000");
//		prettyPrintWriter.writeStartElement("zone");
//		prettyPrintWriter.writeAttribute("_.fcp.SetMembershipControl.false...type", "layout-basic");
//		prettyPrintWriter.writeAttribute("_.fcp.SetMembershipControl.true...type-v2", "layout-basic");
//		prettyPrintWriter.writeAttribute("h", "98000");
//		prettyPrintWriter.writeAttribute("id", "3");
//		prettyPrintWriter.writeAttribute("w", "98400");
//		prettyPrintWriter.writeAttribute("x", "800");
//		prettyPrintWriter.writeAttribute("y", "1000");
//
//		int i = 0;
//		for (VisualModel vm : migratorModel.getVisualModelList()) {
//			prettyPrintWriter.writeStartElement("zone");
////		prettyPrintWriter.writeAttribute("h", String.valueOf((int) Math.floor((100000/l)-1500)));
//			prettyPrintWriter.writeAttribute("h", vm.getTargetMinimalHeight());
//			prettyPrintWriter.writeAttribute("id", String.valueOf(i + 1));
//			prettyPrintWriter.writeAttribute("name", vm.getReportName());
////		prettyPrintWriter.writeAttribute("w", String.valueOf((int) (100000/b-1000)));
////		prettyPrintWriter.writeAttribute("x",  String.valueOf((int) (800+ (i%b)*100000/b)));
////		prettyPrintWriter.writeAttribute("y",  String.valueOf((int) (1000+ Math.floor(i/b)*(100000/l))));
//			prettyPrintWriter.writeAttribute("w", vm.getTargetMinimalWidth());
//			prettyPrintWriter.writeAttribute("x", vm.getTargetPositionX());
//			prettyPrintWriter.writeAttribute("y", vm.getTargetPositionY());
//
//			prettyPrintWriter.writeStartElement("zone-style");
//			prettyPrintWriter.writeEmptyElement("format");
//			prettyPrintWriter.writeAttribute("attr", "border-color");
//			prettyPrintWriter.writeAttribute("value", "#000000");
//			prettyPrintWriter.writeEmptyElement("format");
//			prettyPrintWriter.writeAttribute("attr", "border-style");
//			prettyPrintWriter.writeAttribute("value", "none");
//			prettyPrintWriter.writeEmptyElement("format");
//			prettyPrintWriter.writeAttribute("attr", "border-width");
//			prettyPrintWriter.writeAttribute("value", "0");
//			prettyPrintWriter.writeEmptyElement("format");
//			prettyPrintWriter.writeAttribute("attr", "margin");
//			prettyPrintWriter.writeAttribute("value", "4");
//			prettyPrintWriter.writeEndElement();// zone-style
//
//			prettyPrintWriter.writeEndElement();// zone
//			i++;
//		}
//
//		prettyPrintWriter.writeEndElement();// zone
//		prettyPrintWriter.writeEndElement();// zone
//		prettyPrintWriter.writeStartElement("zone-style");
//		prettyPrintWriter.writeEmptyElement("format");
//		prettyPrintWriter.writeAttribute("attr", "border-color");
//		prettyPrintWriter.writeAttribute("value", "#000000");
//		prettyPrintWriter.writeEmptyElement("format");
//		prettyPrintWriter.writeAttribute("attr", "border-style");
//		prettyPrintWriter.writeAttribute("value", "none");
//		prettyPrintWriter.writeEmptyElement("format");
//		prettyPrintWriter.writeAttribute("attr", "border-width");
//		prettyPrintWriter.writeAttribute("value", "0");
//		prettyPrintWriter.writeEmptyElement("format");
//		prettyPrintWriter.writeAttribute("attr", "margin");
//		prettyPrintWriter.writeAttribute("value", "8");
//		prettyPrintWriter.writeEndElement();// zone-style
//		prettyPrintWriter.writeEndElement();// zone
//		prettyPrintWriter.writeEndElement();// zones
//
//		prettyPrintWriter.writeStartElement("devicelayouts");
//		prettyPrintWriter.writeStartElement("devicelayout");
//		prettyPrintWriter.writeAttribute("auto-generated", "true");
//		prettyPrintWriter.writeAttribute("name", "Phone");
//		prettyPrintWriter.writeEmptyElement("size");
//		prettyPrintWriter.writeAttribute("maxheight", "1150");
//		prettyPrintWriter.writeAttribute("minheight", "1150");
//		prettyPrintWriter.writeAttribute("sizing-mode", "vscroll");
//		prettyPrintWriter.writeStartElement("zones");
//		prettyPrintWriter.writeStartElement("zone");
//		prettyPrintWriter.writeAttribute("_.fcp.SetMembershipControl.false...type", "layout-basic");
//		prettyPrintWriter.writeAttribute("_.fcp.SetMembershipControl.true...type-v2", "layout-basic");
//		prettyPrintWriter.writeAttribute("h", "100000");
//		prettyPrintWriter.writeAttribute("id", "1");
//		prettyPrintWriter.writeAttribute("w", "100000");
//		prettyPrintWriter.writeAttribute("x", "0");
//		prettyPrintWriter.writeAttribute("y", "0");
//		prettyPrintWriter.writeStartElement("zone");
//		prettyPrintWriter.writeAttribute("_.fcp.SetMembershipControl.false...type", "layout-flow");
//		prettyPrintWriter.writeAttribute("_.fcp.SetMembershipControl.true...type-v2", "layout-flow");
//		prettyPrintWriter.writeAttribute("h", "98000");
//		prettyPrintWriter.writeAttribute("id", "2");
//		prettyPrintWriter.writeAttribute("param", "vert");
//		prettyPrintWriter.writeAttribute("w", "98400");
//		prettyPrintWriter.writeAttribute("x", "800");
//		prettyPrintWriter.writeAttribute("y", "1000");
//
//		i = 0;
//		for (VisualModel vm : migratorModel.getVisualModelList()) {
//			prettyPrintWriter.writeStartElement("zone");
//			prettyPrintWriter.writeAttribute("fixed-size", "200");
////		prettyPrintWriter.writeAttribute("h", String.valueOf((int) Math.floor((100000/l)-1500)));
//			prettyPrintWriter.writeAttribute("h", vm.getTargetMinimalHeight());
//			prettyPrintWriter.writeAttribute("id", String.valueOf(i + 1));
//			prettyPrintWriter.writeAttribute("is-fixed", "true");
//			prettyPrintWriter.writeAttribute("name", vm.getReportName());
////		prettyPrintWriter.writeAttribute("w", String.valueOf((int) (100000/b-1000)));
////		prettyPrintWriter.writeAttribute("x",  String.valueOf((int) (800+ (i%b)*100000/b)));
////		prettyPrintWriter.writeAttribute("y",  String.valueOf((int) (1000+ Math.floor(i/b)*(100000/l))));
//			prettyPrintWriter.writeAttribute("w", vm.getTargetMinimalWidth());
//			prettyPrintWriter.writeAttribute("x", vm.getTargetPositionX());
//			prettyPrintWriter.writeAttribute("y", vm.getTargetPositionY());
//
//			prettyPrintWriter.writeStartElement("zone-style");
//			prettyPrintWriter.writeEmptyElement("format");
//			prettyPrintWriter.writeAttribute("attr", "border-color");
//			prettyPrintWriter.writeAttribute("value", "#000000");
//			prettyPrintWriter.writeEmptyElement("format");
//			prettyPrintWriter.writeAttribute("attr", "border-style");
//			prettyPrintWriter.writeAttribute("value", "none");
//			prettyPrintWriter.writeEmptyElement("format");
//			prettyPrintWriter.writeAttribute("attr", "border-width");
//			prettyPrintWriter.writeAttribute("value", "0");
//			prettyPrintWriter.writeEmptyElement("format");
//			prettyPrintWriter.writeAttribute("attr", "margin");
//			prettyPrintWriter.writeAttribute("value", "4");
//			prettyPrintWriter.writeEmptyElement("format");
//			prettyPrintWriter.writeAttribute("attr", "padding");
//			prettyPrintWriter.writeAttribute("value", "0");
//			prettyPrintWriter.writeEndElement();// zone-style
//
//			prettyPrintWriter.writeEndElement();// zone
//			i++;
//		}
//
//		prettyPrintWriter.writeEndElement();// zone
////	prettyPrintWriter.writeEndElement();//zone
//		prettyPrintWriter.writeStartElement("zone-style");
//		prettyPrintWriter.writeEmptyElement("format");
//		prettyPrintWriter.writeAttribute("attr", "border-color");
//		prettyPrintWriter.writeAttribute("value", "#000000");
//		prettyPrintWriter.writeEmptyElement("format");
//		prettyPrintWriter.writeAttribute("attr", "border-style");
//		prettyPrintWriter.writeAttribute("value", "none");
//		prettyPrintWriter.writeEmptyElement("format");
//		prettyPrintWriter.writeAttribute("attr", "border-width");
//		prettyPrintWriter.writeAttribute("value", "0");
//		prettyPrintWriter.writeEmptyElement("format");
//		prettyPrintWriter.writeAttribute("attr", "margin");
//		prettyPrintWriter.writeAttribute("value", "8");
//		prettyPrintWriter.writeEndElement();// zone-style
//
//		prettyPrintWriter.writeEndElement();// zones
//		prettyPrintWriter.writeEndElement();// device-layout
//
//		prettyPrintWriter.writeEndElement();// device-layout
//		prettyPrintWriter.writeEndElement();// device-layouts
//
//		prettyPrintWriter.writeEmptyElement("simple-id");
//		prettyPrintWriter.writeAttribute("uuid", "{" + getAlphaNumericString(8) + "-" + getAlphaNumericString(4) + "-"
//				+ getAlphaNumericString(4) + "-" + getAlphaNumericString(4) + "-" + getAlphaNumericString(12) + "}");
//
//		prettyPrintWriter.writeEndElement();// dashboard
//		prettyPrintWriter.writeEndElement();// dashboards
//	}

	private static void writeWorksheets(XMLStreamWriter prettyPrintWriter, MigratorModel migratorModel)
			throws XMLStreamException {
		prettyPrintWriter.writeStartElement("worksheets");
		int i = 1;
		for (int j = 0; j < migratorModel.getVisualModelList().size(); j++) {
			VisualModel m = migratorModel.getVisualModelList().get(j);
			VisualStructure vstr = visuals.get(m.getReportName());
			prettyPrintWriter.writeStartElement("worksheet");
			prettyPrintWriter.writeAttribute("name", m.getReportName());

			// header-code
			if (vstr.getHeader() != null) {
				prettyPrintWriter.writeStartElement("layout-options");
				prettyPrintWriter.writeStartElement("title");
				prettyPrintWriter.writeStartElement("formatted-text");
				prettyPrintWriter.writeStartElement("run");
				prettyPrintWriter.writeAttribute("bold", "true");
				prettyPrintWriter.writeAttribute("fontalignment", "1");
				prettyPrintWriter.writeCharacters(vstr.getHeader());
				prettyPrintWriter.writeEndElement();
				prettyPrintWriter.writeEndElement();
				prettyPrintWriter.writeEndElement();
				prettyPrintWriter.writeEndElement();
			} else {
				prettyPrintWriter.writeStartElement("layout-options");
				prettyPrintWriter.writeStartElement("title");
				prettyPrintWriter.writeStartElement("formatted-text");
				prettyPrintWriter.writeStartElement("run");
				prettyPrintWriter.writeAttribute("bold", "true");
				prettyPrintWriter.writeAttribute("fontalignment", "1");
				prettyPrintWriter.writeCharacters("");
				prettyPrintWriter.writeEndElement();
				prettyPrintWriter.writeEndElement();
				prettyPrintWriter.writeEndElement();
				prettyPrintWriter.writeEndElement();
			}

			prettyPrintWriter.writeStartElement("table");
			prettyPrintWriter.writeStartElement("view");
			prettyPrintWriter.writeStartElement("datasources");
			prettyPrintWriter.writeEmptyElement("datasource");
			if (datasrc.getDatabaseName().equalsIgnoreCase("Sample - Superstore")) {
				prettyPrintWriter.writeAttribute("caption", datasrc.getDatabaseName());
			} else {
				prettyPrintWriter.writeAttribute("caption",
						datasrc.getTableName() + " (" + datasrc.getDatabaseName() + ")");
			}
			prettyPrintWriter.writeAttribute("name", "federated." + datasrc.getTableId());
			prettyPrintWriter.writeEndElement(); // datasources

			int y = 0;

			if ((vstr.getType()).equalsIgnoreCase("map")) {
				prettyPrintWriter.writeStartElement("mapsources");
				prettyPrintWriter.writeEmptyElement("mapsource");
				prettyPrintWriter.writeAttribute("name", "Tableau");
				prettyPrintWriter.writeEndElement(); // mapsources
				y = 1;
			}

			prettyPrintWriter.writeStartElement("datasource-dependencies");
			prettyPrintWriter.writeAttribute("datasource", "federated." + datasrc.getTableId());
			String o = "";
			for (String str : variables.get(m.getReportName())) {
				if (strmeta.contains(str) && !o.contains(str)) {
					prettyPrintWriter.writeEmptyElement("column");
//					System.out.println("Column:" + str + ", datatype:" + (metadata.get("Manager"))[0]);

					switch (metadata.get(str)[0]) {
					case "string":
						prettyPrintWriter.writeAttribute("datatype", "string");
						prettyPrintWriter.writeAttribute("name", "[" + str + "]");
						prettyPrintWriter.writeAttribute("role", "dimension");
						if (metadata.get(str)[2] != null) {
							prettyPrintWriter.writeAttribute("semantic-role", "[" + str + "].[Name]");
						}

						prettyPrintWriter.writeAttribute("type", "nominal");
						break;
					case "real":
						prettyPrintWriter.writeAttribute("datatype", "real");
						prettyPrintWriter.writeAttribute("name", "[" + str + "]");
						prettyPrintWriter.writeAttribute("role", "measure");
						prettyPrintWriter.writeAttribute("type", "quantitative");
						break;
					case "integer":
						prettyPrintWriter.writeAttribute("datatype", "integer");
						prettyPrintWriter.writeAttribute("name", "[" + str + "]");
						prettyPrintWriter.writeAttribute("role", "measure");
						prettyPrintWriter.writeAttribute("type", "quantitative");
						break;
					}

					o = o + str + "   ";
				} else if (!o.contains(str)) {
					prettyPrintWriter.writeStartElement("column");
					prettyPrintWriter.writeAttribute("caption", str);
					prettyPrintWriter.writeAttribute("datatype", "real");
					prettyPrintWriter.writeAttribute("name", "[" + str + "]");
					prettyPrintWriter.writeAttribute("role", "measure");
					prettyPrintWriter.writeAttribute("type", "quantitative");
					prettyPrintWriter.writeEmptyElement("calculation");
					prettyPrintWriter.writeAttribute("class", "tableau");
					System.out.println(str);
					prettyPrintWriter.writeAttribute("formula",
							calcformula.get(str) != null ? calcformula.get(str) : "");
					prettyPrintWriter.writeEndElement();

					o = o + str + "   ";
				}

			}
			if (vstr.getType().equalsIgnoreCase(CHART_TYPE.DONUT.getValue())) {
				prettyPrintWriter.writeStartElement("column");
				prettyPrintWriter.writeAttribute("caption", AVG);
				prettyPrintWriter.writeAttribute("datatype", "real");
				prettyPrintWriter.writeAttribute("name", "[" + Calculation_1 + "]");
				prettyPrintWriter.writeAttribute("role", "measure");
				prettyPrintWriter.writeAttribute("type", "quantitative");
				prettyPrintWriter.writeAttribute("user:unnamed", m.getReportName());
				prettyPrintWriter.writeEmptyElement("calculation");
				prettyPrintWriter.writeAttribute("class", "tableau");
				prettyPrintWriter.writeAttribute("formula", AVG);
				prettyPrintWriter.writeEndElement();

				prettyPrintWriter.writeStartElement("column");
				prettyPrintWriter.writeAttribute("caption", AVG);
				prettyPrintWriter.writeAttribute("datatype", "real");
				prettyPrintWriter.writeAttribute("name", "[" + Calculation_2 + "]");
				prettyPrintWriter.writeAttribute("role", "measure");
				prettyPrintWriter.writeAttribute("type", "quantitative");
				prettyPrintWriter.writeAttribute("user:unnamed", m.getReportName());
				prettyPrintWriter.writeEmptyElement("calculation");
				prettyPrintWriter.writeAttribute("class", "tableau");
				prettyPrintWriter.writeAttribute("formula", AVG);
				prettyPrintWriter.writeEndElement();

				prettyPrintWriter.writeEmptyElement("column-instance");
				prettyPrintWriter.writeAttribute("column", "[" + Calculation_1 + "]");
				prettyPrintWriter.writeAttribute("derivation", "User");
				prettyPrintWriter.writeAttribute("name", "[usr:" + Calculation_1 + ":qk]");
				prettyPrintWriter.writeAttribute("pivot", "key");
				prettyPrintWriter.writeAttribute("type", "quantitative");

				prettyPrintWriter.writeEmptyElement("column-instance");
				prettyPrintWriter.writeAttribute("column", "[" + Calculation_2 + "]");
				prettyPrintWriter.writeAttribute("derivation", "User");
				prettyPrintWriter.writeAttribute("name", "[usr:" + Calculation_2 + ":qk]");
				prettyPrintWriter.writeAttribute("pivot", "key");
				prettyPrintWriter.writeAttribute("type", "quantitative");

			}

//		if(vstr.getRows()!=null) {
//			for(String str: vstr.getRows()) {
//				if(strmeta.contains(str)) {
//					prettyPrintWriter.writeEmptyElement("column");
//					System.out.println("Column:"+str+", datatype:"+(metadata.get("Manager"))[0]);
//				
//					switch(metadata.get(str)[0]) {
//					case "string":
//						prettyPrintWriter.writeAttribute("datatype", "string" );
//						prettyPrintWriter.writeAttribute("name", "["+str+"]" );
//						prettyPrintWriter.writeAttribute("role", "dimension" );
//						if(metadata.get(str)[2]!=null) {
//							prettyPrintWriter.writeAttribute("semantic-role","["+str+ "].[Name]" );
//						}
//						
//						prettyPrintWriter.writeAttribute("type", "nominal" );
//						break;
//					case "real":
//						prettyPrintWriter.writeAttribute("datatype", "real" );
//						prettyPrintWriter.writeAttribute("name", "["+str+"]" );
//						prettyPrintWriter.writeAttribute("role", "measure" );
//						prettyPrintWriter.writeAttribute("type", "quantitative" );
//						break;
//					case "integer":
//						prettyPrintWriter.writeAttribute("datatype", "integer" );
//						prettyPrintWriter.writeAttribute("name", "["+str+"]" );
//						prettyPrintWriter.writeAttribute("role", "measure" );
//						prettyPrintWriter.writeAttribute("type", "quantitative" );
//						break;
//					
//					case "datetime":
//						prettyPrintWriter.writeAttribute("datatype", "datetime" );
//						prettyPrintWriter.writeAttribute("name", "["+str+"]" );
//						prettyPrintWriter.writeAttribute("role", "dimension" );
//						prettyPrintWriter.writeAttribute("type", "ordinal" );
//						break;
//						
//					}
//		
//				}
//				else if(!o.contains(str)) {
//					prettyPrintWriter.writeStartElement("column");
//					prettyPrintWriter.writeAttribute("caption", str);
//					prettyPrintWriter.writeAttribute("datatype", "real");
//					prettyPrintWriter.writeAttribute("name", "["+str+"]");
//					prettyPrintWriter.writeAttribute("role", "measure");
//					prettyPrintWriter.writeAttribute("type", "quantitative");
//					prettyPrintWriter.writeEmptyElement("calculation");
//					prettyPrintWriter.writeAttribute("class", "tableau");
//					System.out.println(str);
//					prettyPrintWriter.writeAttribute("formula", calcformula.get(str) );
//					prettyPrintWriter.writeEndElement();
//					
//					
//				}
//				o = o + "--"+str + "--";
//			}
//		}
//		if(vstr.getCols()!=null) {
//			for(String str: vstr.getCols()) {
//				if(strmeta.contains(str)&& !o.contains(str)) {
//					prettyPrintWriter.writeEmptyElement("column");
//					System.out.println("Column:"+str+", datatype:"+(metadata.get("Manager"))[0]);
//				
//					switch(metadata.get(str)[0]) {
//					case "string":
//						prettyPrintWriter.writeAttribute("datatype", "string" );
//						prettyPrintWriter.writeAttribute("name", "["+str+"]" );
//						prettyPrintWriter.writeAttribute("role", "dimension" );
//						if(metadata.get(str)[2]!=null) {
//							prettyPrintWriter.writeAttribute("semantic-role","["+str+ "].[Name]" );
//						}
//						
//						prettyPrintWriter.writeAttribute("type", "nominal" );
//						break;
//					case "real":
//						prettyPrintWriter.writeAttribute("datatype", "real" );
//						prettyPrintWriter.writeAttribute("name", "["+str+"]" );
//						prettyPrintWriter.writeAttribute("role", "measure" );
//						prettyPrintWriter.writeAttribute("type", "quantitative" );
//						break;
//					case "integer":
//						prettyPrintWriter.writeAttribute("datatype", "integer" );
//						prettyPrintWriter.writeAttribute("name", "["+str+"]" );
//						prettyPrintWriter.writeAttribute("role", "measure" );
//						prettyPrintWriter.writeAttribute("type", "quantitative" );
//						break;
//					
//					case "datetime":
//						prettyPrintWriter.writeAttribute("datatype", "datetime" );
//						prettyPrintWriter.writeAttribute("name", "["+str+"]" );
//						prettyPrintWriter.writeAttribute("role", "dimension" );
//						prettyPrintWriter.writeAttribute("type", "ordinal" );
//						break;
//						
//					}
//		
//				}
//				else if(!o.contains(str)) {
//					prettyPrintWriter.writeStartElement("column");
//					prettyPrintWriter.writeAttribute("caption", str);
//					prettyPrintWriter.writeAttribute("datatype", "real");
//					prettyPrintWriter.writeAttribute("name", "["+str+"]");
//					prettyPrintWriter.writeAttribute("role", "measure");
//					prettyPrintWriter.writeAttribute("type", "quantitative");
//					prettyPrintWriter.writeEmptyElement("calculation");
//					prettyPrintWriter.writeAttribute("class", "tableau");
//					System.out.println(str);
//					prettyPrintWriter.writeAttribute("formula", calcformula.get(str) );
//					prettyPrintWriter.writeEndElement();
//					
//					
//				}
//				o = o + "--"+str + "--";
//			}
//		}

			o = "";
			for (String str : variables.get(m.getReportName())) {

//				if (str.equalsIgnoreCase("Mth")) {
//					prettyPrintWriter.writeEmptyElement("column-instance");
//					prettyPrintWriter.writeAttribute("column", "[" + str + "]");
//					prettyPrintWriter.writeAttribute("derivation", "None");
//					prettyPrintWriter.writeAttribute("name", "[none:" + str + ":qk]");
//					prettyPrintWriter.writeAttribute("pivot", "key");
//					prettyPrintWriter.writeAttribute("type", "quantitative");
//				} else if (str.equalsIgnoreCase("Calculation_343118028102935555")) {
//					prettyPrintWriter.writeEmptyElement("column-instance");
//					prettyPrintWriter.writeAttribute("column", "[" + str + "]");
//					prettyPrintWriter.writeAttribute("derivation", "Sum");
//					prettyPrintWriter.writeAttribute("name", "[sum:" + str + ":qk]");
//					prettyPrintWriter.writeAttribute("pivot", "key");
//					prettyPrintWriter.writeAttribute("type", "quantitative");
//				} else 
				if (strmeta.contains(str)) {

					if (metadata.get(str)[0].equalsIgnoreCase("string")) {
						prettyPrintWriter.writeEmptyElement("column-instance");
						prettyPrintWriter.writeAttribute("column", "[" + str + "]");
						prettyPrintWriter.writeAttribute("derivation", "None");
						prettyPrintWriter.writeAttribute("name", "[none:" + str + ":nk]");
						prettyPrintWriter.writeAttribute("pivot", "key");
						prettyPrintWriter.writeAttribute("type", "nominal");
					} else {

//						if (vstr.getType().equalsIgnoreCase("waterfall") && !o.contains(str)) {
						if (vstr.getType().equalsIgnoreCase(CHART_TYPE.WATERFALL.getValue()) && !o.contains(str)) {
							if (vstr.getRows().contains(str)) {
								prettyPrintWriter.writeStartElement("column-instance");
								prettyPrintWriter.writeAttribute("column", "[" + str + "]");
								prettyPrintWriter.writeAttribute("derivation", "Sum");
								prettyPrintWriter.writeAttribute("name", "[cum:sum:" + str + ":qk]");
								prettyPrintWriter.writeAttribute("pivot", "key");
								prettyPrintWriter.writeAttribute("type", "quantitative");
								prettyPrintWriter.writeEmptyElement("table-calc");
								prettyPrintWriter.writeAttribute("aggregation", "Sum");
								prettyPrintWriter.writeAttribute("ordering-type", "Rows");
								prettyPrintWriter.writeAttribute("type", "CumTotal");
								o = o + str + "  ";

								prettyPrintWriter.writeEndElement();// instance

							}
						} else {

							if (vstr.getRowsOrdinal() != null) {
								boolean isRowOrdinal = false;

								for (String rowsOrdinal : vstr.getRowsOrdinal()) {
									if (str.equalsIgnoreCase(rowsOrdinal)) {
										isRowOrdinal = true;
										prettyPrintWriter.writeEmptyElement("column-instance");
										prettyPrintWriter.writeAttribute("column", "[" + str + "]");
										prettyPrintWriter.writeAttribute("derivation", "Sum");
										prettyPrintWriter.writeAttribute("name", "[sum:" + str + ":ok]");
										prettyPrintWriter.writeAttribute("pivot", "key");
										prettyPrintWriter.writeAttribute("type", "ordinal");
										break;
									}
								}

								if (!isRowOrdinal) {
									prettyPrintWriter.writeEmptyElement("column-instance");
									prettyPrintWriter.writeAttribute("column", "[" + str + "]");
									prettyPrintWriter.writeAttribute("derivation", "Sum");
									prettyPrintWriter.writeAttribute("name", "[sum:" + str + ":qk]");
									prettyPrintWriter.writeAttribute("pivot", "key");
									prettyPrintWriter.writeAttribute("type", "quantitative");
								}

							} else if (vstr.getColsOrdinal() != null) {

								boolean isColsOrdinal = false;

								for (String colsOrdinal : vstr.getColsOrdinal()) {
									if (str.equalsIgnoreCase(colsOrdinal)) {
										isColsOrdinal = true;
										prettyPrintWriter.writeEmptyElement("column-instance");
										prettyPrintWriter.writeAttribute("column", "[" + str + "]");
										prettyPrintWriter.writeAttribute("derivation", "Sum");
										prettyPrintWriter.writeAttribute("name", "[sum:" + str + ":ok]");
										prettyPrintWriter.writeAttribute("pivot", "key");
										prettyPrintWriter.writeAttribute("type", "ordinal");
										break;
									}
								}

								if (!isColsOrdinal) {
									prettyPrintWriter.writeEmptyElement("column-instance");
									prettyPrintWriter.writeAttribute("column", "[" + str + "]");
									prettyPrintWriter.writeAttribute("derivation", "Sum");
									prettyPrintWriter.writeAttribute("name", "[sum:" + str + ":qk]");
									prettyPrintWriter.writeAttribute("pivot", "key");
									prettyPrintWriter.writeAttribute("type", "quantitative");
								}

							} else {
								prettyPrintWriter.writeEmptyElement("column-instance");
								prettyPrintWriter.writeAttribute("column", "[" + str + "]");
								prettyPrintWriter.writeAttribute("derivation", "Sum");
								prettyPrintWriter.writeAttribute("name", "[sum:" + str + ":qk]");
								prettyPrintWriter.writeAttribute("pivot", "key");
								prettyPrintWriter.writeAttribute("type", "quantitative");
							}

						}

					}
				} else {
					prettyPrintWriter.writeEmptyElement("column-instance");
					prettyPrintWriter.writeAttribute("column", "[" + str + "]");
					prettyPrintWriter.writeAttribute("derivation", "User");
					prettyPrintWriter.writeAttribute("name", "[usr:" + str + ":qk]");
					prettyPrintWriter.writeAttribute("pivot", "key");
					prettyPrintWriter.writeAttribute("type", "quantitative");
				}

			}

			prettyPrintWriter.writeEndElement(); // datasource-dependencies

			if (vstr.getFilterList() != null) {

				for (Filter filter : vstr.getFilterList()) {
					prettyPrintWriter.writeStartElement("filter");// filter
					prettyPrintWriter.writeAttribute("class", "categorical");
					if (metadata.get(filter.getColumn()) != null) {
						switch (metadata.get(filter.getColumn())[0]) {
						case "string":
							prettyPrintWriter.writeAttribute("column",
									"[federated." + datasrc.getTableId() + "].[none:" + filter.getColumn() + ":nk]");

							if (!filter.getFrom().isEmpty() && !filter.getTo().isEmpty()) {
								prettyPrintWriter.writeEmptyElement("groupfilter");
								prettyPrintWriter.writeAttribute("from", "\"" + filter.getFrom() + "\"");
								prettyPrintWriter.writeAttribute("function", "range");
								prettyPrintWriter.writeAttribute("level", "[none:" + filter.getColumn() + ":nk]");
								prettyPrintWriter.writeAttribute("to", "\"" + filter.getTo() + "\"");
								prettyPrintWriter.writeAttribute("user:ui-domain", "relevant");
								prettyPrintWriter.writeAttribute("user:ui-enumeration", "inclusive");
								prettyPrintWriter.writeAttribute("user:ui-marker", "enumerate");
							} else if (filter.getMembersList() != null) {
								prettyPrintWriter.writeStartElement("groupfilter");
								prettyPrintWriter.writeAttribute("function", "union");
								prettyPrintWriter.writeAttribute("user:ui-domain", "relevant");
								prettyPrintWriter.writeAttribute("user:ui-enumeration", "inclusive");
								prettyPrintWriter.writeAttribute("user:ui-marker", "enumerate");
								for (String members : filter.getMembersList()) {
									prettyPrintWriter.writeEmptyElement("groupfilter");
									prettyPrintWriter.writeAttribute("function", "member");
									prettyPrintWriter.writeAttribute("level", "[none:" + filter.getColumn() + ":nk]");
									prettyPrintWriter.writeAttribute("member", "\"" + members + "\"");
								}
								prettyPrintWriter.writeEndElement();
							}
							break;
						}
					} else {
						if (filter.getColumn().equalsIgnoreCase(MeasureNames)) {
							prettyPrintWriter.writeAttribute("column",
									"[federated." + datasrc.getTableId() + "].[:" + filter.getColumn() + "]");
							prettyPrintWriter.writeStartElement("groupfilter");
							prettyPrintWriter.writeAttribute("function", "union");
							prettyPrintWriter.writeAttribute("user:op", "manual");
							for (String members : filter.getMembersList()) {
								if (!members.trim().equals("")) {
									prettyPrintWriter.writeEmptyElement("groupfilter");
									prettyPrintWriter.writeAttribute("function", "member");
									prettyPrintWriter.writeAttribute("level", "[:" + MeasureNames + "]");
									if (metadata.get(members) != null) {
										prettyPrintWriter.writeAttribute("member", "\"" + "[federated."
												+ datasrc.getTableId() + "].[sum:" + members + ":qk]" + "\"");
									} else if (calccols.get(members) != null) {
										prettyPrintWriter.writeAttribute("member", "\"" + "[federated."
												+ datasrc.getTableId() + "].[usr:" + members + ":qk]" + "\"");
									}
								}
							}
							prettyPrintWriter.writeEndElement();// groupfilter

						}
					}
					prettyPrintWriter.writeEndElement();// filter

				}

				prettyPrintWriter.writeStartElement("slices");
				for (Filter filter : vstr.getFilterList()) {
					if (metadata.get(filter.getColumn()) != null) {
						switch (metadata.get(filter.getColumn())[0]) {
						case "string":
							prettyPrintWriter.writeStartElement("column");
							prettyPrintWriter.writeCharacters(
									"[federated." + datasrc.getTableId() + "].[none:" + filter.getColumn() + ":nk]");
							prettyPrintWriter.writeEndElement(); // column
							break;

						default:
							prettyPrintWriter.writeStartElement("column");
							prettyPrintWriter.writeCharacters("[federated." + datasrc.getTableId() + "].[sum:"
									+ vstr.getFilter().get(0) + ":qk]");
							prettyPrintWriter.writeEndElement(); // column
							break;
						}
					} else if (filter.getColumn().equalsIgnoreCase(MeasureNames)) {
						prettyPrintWriter.writeStartElement("column");
						prettyPrintWriter.writeCharacters(
								"[federated." + datasrc.getTableId() + "].[:" + filter.getColumn() + "]");
						prettyPrintWriter.writeEndElement(); // column
					}

				}

				prettyPrintWriter.writeEndElement(); // slices
			}

//				if (metadata.get(vstr.getFilter().get(0)) != null) {
//					prettyPrintWriter.writeStartElement("filter");
//					prettyPrintWriter.writeAttribute("class", "categorical");
//					switch (metadata.get(vstr.getFilter().get(0))[0]) {
//					case "string":
//						prettyPrintWriter.writeAttribute("column",
//								"[federated." + datasrc.getTableId() + "].[none:" + vstr.getFilter().get(0) + ":nk]");
//						prettyPrintWriter.writeEmptyElement("groupfilter");
//						prettyPrintWriter.writeAttribute("function", "member");
//						prettyPrintWriter.writeAttribute("level", "[none:" + vstr.getFilter().get(0) + ":nk]");
//						prettyPrintWriter.writeAttribute("member", "\"" + vstr.getFilter().get(1) + "\"");
//						prettyPrintWriter.writeAttribute("user:ui-domain", "relevant");
//						prettyPrintWriter.writeAttribute("user:ui-enumeration", "inclusive");
//						prettyPrintWriter.writeAttribute("user:ui-marker", "enumerate");
//
//						break;
//					default:
//						prettyPrintWriter.writeAttribute("column",
//								"[federated." + datasrc.getTableId() + "].[sum:" + vstr.getFilter().get(0) + ":qk]");
//						prettyPrintWriter.writeEmptyElement("groupfilter");
//						prettyPrintWriter.writeAttribute("function", "member");
//						prettyPrintWriter.writeAttribute("level", "[sum:" + vstr.getFilter().get(0) + ":qk]");
//						prettyPrintWriter.writeAttribute("member", "\"" + vstr.getFilter().get(1) + "\"");
//						prettyPrintWriter.writeAttribute("user:ui-domain", "relevant");
//						prettyPrintWriter.writeAttribute("user:ui-enumeration", "inclusive");
//						prettyPrintWriter.writeAttribute("user:ui-marker", "enumerate");
//						break;
//					}
//					prettyPrintWriter.writeEndElement(); // filter
//					prettyPrintWriter.writeStartElement("slices");
//					switch (metadata.get(vstr.getFilter().get(0))[0]) {
//					case "string":
//						prettyPrintWriter.writeStartElement("column");
//						prettyPrintWriter.writeCharacters(
//								"[federated." + datasrc.getTableId() + "].[none:" + vstr.getFilter().get(0) + ":nk]");
//						prettyPrintWriter.writeEndElement(); // column
//						break;
//
//					default:
//						prettyPrintWriter.writeStartElement("column");
//						prettyPrintWriter.writeCharacters(
//								"[federated." + datasrc.getTableId() + "].[sum:" + vstr.getFilter().get(0) + ":qk]");
//						prettyPrintWriter.writeEndElement(); // column
//						break;
//					}
//					prettyPrintWriter.writeEndElement(); // slices
//
//				}
//			}

			prettyPrintWriter.writeEmptyElement("aggregation");
			prettyPrintWriter.writeAttribute("value", "true");
			prettyPrintWriter.writeEndElement(); // view
			int h = 0;

			if (y == 1) { // y==1 is true if map
				prettyPrintWriter.writeStartElement("style");
				prettyPrintWriter.writeStartElement("style-rule");
				prettyPrintWriter.writeAttribute("element", "map");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "washout");
				prettyPrintWriter.writeAttribute("value", "0.0");
				prettyPrintWriter.writeEndElement();// style-rule

				if (vstr.getRef() != null) {
					prettyPrintWriter.writeStartElement("style-rule");
					prettyPrintWriter.writeAttribute("element", "refband");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "reverse-palette");
					prettyPrintWriter.writeAttribute("id", "refline0");
					prettyPrintWriter.writeAttribute("value", "false");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "stroke-color");
					prettyPrintWriter.writeAttribute("id", "refline0");
					prettyPrintWriter.writeAttribute("value", "#f28e2b");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "stroke-size");
					prettyPrintWriter.writeAttribute("id", "refline0");
					prettyPrintWriter.writeAttribute("value", "5");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "line-visibility");
					prettyPrintWriter.writeAttribute("id", "refline0");
					prettyPrintWriter.writeAttribute("value", "on");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "line-pattern-only");
					prettyPrintWriter.writeAttribute("id", "refline0");
					prettyPrintWriter.writeAttribute("value", "solid");
					prettyPrintWriter.writeEndElement();// style-rule

					h = 1;
				}

				prettyPrintWriter.writeEndElement();// style
				h = 1;
			} else if (vstr.getType().equalsIgnoreCase("LinearGauge")) {
				prettyPrintWriter.writeStartElement("style");
				prettyPrintWriter.writeStartElement("style-rule");
				prettyPrintWriter.writeAttribute("element", "axis");
				prettyPrintWriter.writeEmptyElement("encoding");
				prettyPrintWriter.writeAttribute("attr", "space");
				prettyPrintWriter.writeAttribute("class", "0");
				prettyPrintWriter.writeAttribute("field",
						"[federated." + datasrc.getTableId() + "].[usr:" + vstr.getCols().get(1) + ":qk]");
				prettyPrintWriter.writeAttribute("field-type", "quantitative");
				prettyPrintWriter.writeAttribute("fold", "true");
				prettyPrintWriter.writeAttribute("scope", "cols");
				prettyPrintWriter.writeAttribute("synchronized", "true");
				prettyPrintWriter.writeAttribute("type", "space");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "display");
				prettyPrintWriter.writeAttribute("class", "0");
				prettyPrintWriter.writeAttribute("field",
						"[federated." + datasrc.getTableId() + "].[usr:" + vstr.getCols().get(1) + ":qk]");
				prettyPrintWriter.writeAttribute("scope", "cols");
				prettyPrintWriter.writeAttribute("value", "false");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "display");
				prettyPrintWriter.writeAttribute("class", "0");
				prettyPrintWriter.writeAttribute("field",
						"[federated." + datasrc.getTableId() + "].[sum:" + vstr.getCols().get(0) + ":qk]");
				prettyPrintWriter.writeAttribute("scope", "cols");
				prettyPrintWriter.writeAttribute("value", "false");

				prettyPrintWriter.writeEndElement();// style-rule

				prettyPrintWriter.writeStartElement("style-rule");
				prettyPrintWriter.writeAttribute("element", "cell");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "height");
				prettyPrintWriter.writeAttribute("value", "40");
				prettyPrintWriter.writeEndElement();// style-rule

				if (vstr.getRef() != null) {
					prettyPrintWriter.writeStartElement("style-rule");
					prettyPrintWriter.writeAttribute("element", "refband");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "reverse-palette");
					prettyPrintWriter.writeAttribute("id", "refline0");
					prettyPrintWriter.writeAttribute("value", "false");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "stroke-color");
					prettyPrintWriter.writeAttribute("id", "refline0");
					prettyPrintWriter.writeAttribute("value", "#f28e2b");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "stroke-size");
					prettyPrintWriter.writeAttribute("id", "refline0");
					prettyPrintWriter.writeAttribute("value", "5");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "line-visibility");
					prettyPrintWriter.writeAttribute("id", "refline0");
					prettyPrintWriter.writeAttribute("value", "on");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "line-pattern-only");
					prettyPrintWriter.writeAttribute("id", "refline0");
					prettyPrintWriter.writeAttribute("value", "solid");
					prettyPrintWriter.writeEndElement();// style-rule

					h = 1;
				}

				prettyPrintWriter.writeEndElement();// style
				h = 1;
			} else if (vstr.getType().equalsIgnoreCase(CHART_TYPE.DONUT.getValue())) {
				prettyPrintWriter.writeStartElement("style");

				prettyPrintWriter.writeStartElement("style-rule");
				prettyPrintWriter.writeAttribute("element", "axis");
				prettyPrintWriter.writeEmptyElement("encoding");
				prettyPrintWriter.writeAttribute("attr", "space");
				prettyPrintWriter.writeAttribute("class", "0");
				prettyPrintWriter.writeAttribute("field",
						"[federated." + datasrc.getTableId() + "].[usr:" + Calculation_2 + ":qk]");
				prettyPrintWriter.writeAttribute("field-type", "quantitative");
				prettyPrintWriter.writeAttribute("fold", "true");
				prettyPrintWriter.writeAttribute("scope", "cols");
				prettyPrintWriter.writeAttribute("type", "space");
				prettyPrintWriter.writeEndElement();// style-rule

				prettyPrintWriter.writeStartElement("style-rule");
				prettyPrintWriter.writeAttribute("element", "gridline");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "stroke-size");
				prettyPrintWriter.writeAttribute("scope", "cols");
				prettyPrintWriter.writeAttribute("value", "0");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "line-visibility");
				prettyPrintWriter.writeAttribute("scope", "cols");
				prettyPrintWriter.writeAttribute("value", "off");
				prettyPrintWriter.writeEndElement();// style-rule

				prettyPrintWriter.writeStartElement("style-rule");
				prettyPrintWriter.writeAttribute("element", "zeroline");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "stroke-size");
				prettyPrintWriter.writeAttribute("scope", "cols");
				prettyPrintWriter.writeAttribute("value", "0");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "line-visibility");
				prettyPrintWriter.writeAttribute("scope", "cols");
				prettyPrintWriter.writeAttribute("value", "off");
				prettyPrintWriter.writeEndElement();// style-rule

				prettyPrintWriter.writeEndElement();// style

			}

			else if (vstr.getCols() != null) {
				if (vstr.getCols().get(0).equalsIgnoreCase("Mth")) {
					prettyPrintWriter.writeStartElement("style");
					prettyPrintWriter.writeStartElement("style-rule");
					prettyPrintWriter.writeAttribute("element", "axis");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "title");
					prettyPrintWriter.writeAttribute("class", "0");
					prettyPrintWriter.writeAttribute("field", "[federated." + datasrc.getTableId() + "].[none:Mth:qk]");

					prettyPrintWriter.writeAttribute("scope", "cols");
					prettyPrintWriter.writeAttribute("value", vstr.getXaxis());

					prettyPrintWriter.writeEndElement();// style
					prettyPrintWriter.writeEndElement();// style
				} else {
					prettyPrintWriter.writeEmptyElement("style");
				}

			} else {
				prettyPrintWriter.writeEmptyElement("style");
			}

			prettyPrintWriter.writeStartElement("panes");

			if (vstr.getType().equalsIgnoreCase(CHART_TYPE.DONUT.getValue())) {
				prettyPrintWriter.writeStartElement("pane");
//				prettyPrintWriter.writeAttribute("id", "1");
				prettyPrintWriter.writeAttribute("selection-relaxation-option", "selection-relaxation-allow");
				prettyPrintWriter.writeStartElement("view");
				prettyPrintWriter.writeEmptyElement("breakdown");
				prettyPrintWriter.writeAttribute("value", "auto");
				prettyPrintWriter.writeEndElement(); // view
				prettyPrintWriter.writeEmptyElement("mark");
				prettyPrintWriter.writeAttribute("class", CHART_TYPE.PIE.getValue());
				prettyPrintWriter.writeEmptyElement("mark-sizing");
				prettyPrintWriter.writeAttribute("mark-sizing-setting", "marks-scaling-off");

				prettyPrintWriter.writeStartElement("style");
				prettyPrintWriter.writeStartElement("style-rule");
				prettyPrintWriter.writeAttribute("element", "mark");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "mark-labels-cull");
				prettyPrintWriter.writeAttribute("value", "true");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "mark-labels-show");
				prettyPrintWriter.writeAttribute("value", "true");
				prettyPrintWriter.writeEndElement(); // style-rule
				prettyPrintWriter.writeEndElement(); // style\
				prettyPrintWriter.writeEndElement(); // pane

			}

			for (int k = 0; k < vstr.getStructureList().size(); k++) {

				prettyPrintWriter.writeStartElement("pane");
				if (vstr.getStructureList().size() > 1) {
					prettyPrintWriter.writeAttribute("id", String.valueOf(k + 3));
				}
				Structure st = vstr.getStructureList().get(k);
				prettyPrintWriter.writeAttribute("selection-relaxation-option", "selection-relaxation-allow");
				if (k != 0 && vstr.getType().equalsIgnoreCase("linear gauge")) {
					if (metadata.get(vstr.getCols().get(k - 1)) != null) {
						prettyPrintWriter.writeAttribute("x-axis-name",
								"[federated." + datasrc.getTableId() + "].[sum:" + vstr.getCols().get(k - 1) + ":qk]");
					} else {
						prettyPrintWriter.writeAttribute("x-axis-name",
								"[federated." + datasrc.getTableId() + "].[usr:" + vstr.getCols().get(k - 1) + ":qk]");
					}

				} else if (vstr.getType().equalsIgnoreCase(CHART_TYPE.DONUT.getValue())) {
					prettyPrintWriter.writeAttribute("x-axis-name",
							"[federated." + datasrc.getTableId() + "].[usr:" + Calculation_1 + ":qk]");

				}

				prettyPrintWriter.writeStartElement("view");
				prettyPrintWriter.writeEmptyElement("breakdown");
				prettyPrintWriter.writeAttribute("value", "auto");
				prettyPrintWriter.writeEndElement(); // view

				prettyPrintWriter.writeEmptyElement("mark");
				if (st.getType() != null) {
					if (st.getType().equalsIgnoreCase(CHART_TYPE.TABLE.getValue())
							|| st.getType().equalsIgnoreCase(CHART_TYPE.BAR.getValue())) {
						prettyPrintWriter.writeAttribute("class", "Automatic");
					} else {
						if (vstr.getType().equalsIgnoreCase(CHART_TYPE.DONUT.getValue())) {
							prettyPrintWriter.writeAttribute("class", CHART_TYPE.PIE.getValue());
						} else {
							prettyPrintWriter.writeAttribute("class", st.getType());
						}

						prettyPrintWriter.writeEmptyElement("mark-sizing");
						prettyPrintWriter.writeAttribute("mark-sizing-setting", "marks-scaling-off");
					}
				} else {
					prettyPrintWriter.writeAttribute("class", "Automatic");
				}

				prettyPrintWriter.writeStartElement("encodings");
				if (st.getColor() != null) {
					for (String a : st.getColor()) {
						prettyPrintWriter.writeEmptyElement("color");
						if (metadata.get(a) != null) {
							switch (metadata.get(a)[0]) {
							case "string":
								prettyPrintWriter.writeAttribute("column",
										"[federated." + datasrc.getTableId() + "].[none:" + a + ":nk]");
								break;

							default:
								prettyPrintWriter.writeAttribute("column",
										"[federated." + datasrc.getTableId() + "].[sum:" + a + ":qk]");
								break;
							}
						} else {
							prettyPrintWriter.writeAttribute("column",
									"[federated." + datasrc.getTableId() + "].[sum:" + a + ":qk]");
						}
					}
				}
				if (st.getSize() != null) {
					for (String a : st.getSize()) {
						prettyPrintWriter.writeEmptyElement("size");
						if (metadata.get(a) != null) {
							switch (metadata.get(a)[0]) {
							case "string":
								prettyPrintWriter.writeAttribute("column",
										"[federated." + datasrc.getTableId() + "].[none:" + a + ":nk]");
								break;

							default:
								prettyPrintWriter.writeAttribute("column",
										"[federated." + datasrc.getTableId() + "].[sum:" + a + ":qk]");
								break;
							}
						} else {
							prettyPrintWriter.writeAttribute("column",
									"[federated." + datasrc.getTableId() + "].[sum:" + a + ":qk]");
						}
					}
				}
				if (st.getWsize() != null) {
					for (String a : st.getWsize()) {
						prettyPrintWriter.writeEmptyElement("wedge-size");
						if (metadata.get(a) != null) {
							switch (metadata.get(a)[0]) {
							case "string":
								prettyPrintWriter.writeAttribute("column",
										"[federated." + datasrc.getTableId() + "].[none:" + a + ":nk]");
								break;

							default:
								prettyPrintWriter.writeAttribute("column",
										"[federated." + datasrc.getTableId() + "].[sum:" + a + ":qk]");
								break;
							}
						} else {
							prettyPrintWriter.writeAttribute("column",
									"[federated." + datasrc.getTableId() + "].[sum:" + a + ":qk]");
						}
					}
				}
				if (st.getText() != null) {
					for (String a : st.getText()) {
						prettyPrintWriter.writeEmptyElement("text");
						if (metadata.get(a) != null) {
							switch (metadata.get(a)[0]) {
							case "string":
								prettyPrintWriter.writeAttribute("column",
										"[federated." + datasrc.getTableId() + "].[none:" + a + ":nk]");
								break;

							default:
								prettyPrintWriter.writeAttribute("column",
										"[federated." + datasrc.getTableId() + "].[sum:" + a + ":qk]");
								break;
							}
						} else {
							if (a.equalsIgnoreCase(MultipleValues)) {// Changes for hiding ABC by Alok
								prettyPrintWriter.writeAttribute("column",
										"[federated." + datasrc.getTableId() + "].[" + a + "]");
							} else {
								prettyPrintWriter.writeAttribute("column",
										"[federated." + datasrc.getTableId() + "].[sum:" + a + ":qk]");
							}
						}
					}
				}
				if (st.getLod() != null) {
					for (String a : st.getLod()) {
						prettyPrintWriter.writeEmptyElement("lod");
						if (metadata.get(a) != null) {
							switch (metadata.get(a)[0]) {
							case "string":
								prettyPrintWriter.writeAttribute("column",
										"[federated." + datasrc.getTableId() + "].[none:" + a + ":nk]");
								break;

							default:
								prettyPrintWriter.writeAttribute("column",
										"[federated." + datasrc.getTableId() + "].[sum:" + a + ":qk]");
								break;
							}
						} else {
							prettyPrintWriter.writeAttribute("column",
									"[federated." + datasrc.getTableId() + "].[sum:" + a + ":qk]");
						}
					}

				}
				prettyPrintWriter.writeEndElement();// encodings

				if (vstr.getType() != null && vstr.getType().equalsIgnoreCase(CHART_TYPE.TABLE.getValue())) {
					prettyPrintWriter.writeStartElement("style");
					prettyPrintWriter.writeStartElement("style-rule");
					prettyPrintWriter.writeAttribute("element", "mark");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "mark-labels-show");
					prettyPrintWriter.writeAttribute("value", "true");
					prettyPrintWriter.writeEndElement();// style-rule
					prettyPrintWriter.writeEndElement();// style
				}

				if (vstr.getRef() != null && k == 0) {
					prettyPrintWriter.writeStartElement("reference-line");
					if (metadata.get((vstr.getRef()).get(0)) == null) {
						prettyPrintWriter.writeAttribute("axis-column",
								"[federated." + datasrc.getTableId() + "].[usr:" + vstr.getRef().get(0) + ":qk]");
						prettyPrintWriter.writeAttribute("enable-instant-analytics", "true");
						prettyPrintWriter.writeAttribute("fill-above", "false");
						prettyPrintWriter.writeAttribute("fill-below", "false");
						prettyPrintWriter.writeAttribute("formula", "average");
						prettyPrintWriter.writeAttribute("id", "refline0");
						prettyPrintWriter.writeAttribute("label-type", "automatic");
						prettyPrintWriter.writeAttribute("percentage-bands", "true");
						prettyPrintWriter.writeAttribute("probability", "95");
						prettyPrintWriter.writeAttribute("scope", "per-pane");
						prettyPrintWriter.writeAttribute("symmetric", "false");
						prettyPrintWriter.writeAttribute("value-column",
								"[federated." + datasrc.getTableId() + "].[usr:" + vstr.getRef().get(0) + ":qk]");
						prettyPrintWriter.writeAttribute("z-order", "1");
						prettyPrintWriter.writeEmptyElement("reference-line-value");
						prettyPrintWriter.writeAttribute("percentage", "80");

					} else {
						prettyPrintWriter.writeAttribute("axis-column",
								"[federated." + datasrc.getTableId() + "].[sum:" + vstr.getRef().get(0) + ":qk]");
						prettyPrintWriter.writeAttribute("enable-instant-analytics", "true");
						prettyPrintWriter.writeAttribute("fill-above", "false");
						prettyPrintWriter.writeAttribute("fill-below", "false");
						prettyPrintWriter.writeAttribute("formula", "average");
						prettyPrintWriter.writeAttribute("id", "refline0");
						prettyPrintWriter.writeAttribute("label-type", "automatic");
						prettyPrintWriter.writeAttribute("percentage-bands", "true");
						prettyPrintWriter.writeAttribute("probability", "95");
						prettyPrintWriter.writeAttribute("scope", "per-pane");
						prettyPrintWriter.writeAttribute("symmetric", "false");
						prettyPrintWriter.writeAttribute("value-column",
								"[federated." + datasrc.getTableId() + "].[sum:" + vstr.getRef().get(0) + ":qk]");
						prettyPrintWriter.writeAttribute("z-order", "1");
						prettyPrintWriter.writeEmptyElement("reference-line-value");
						prettyPrintWriter.writeAttribute("percentage", "80");
					}
					prettyPrintWriter.writeEndElement(); // reference-line
				}

				switch (st.getType()) {

				case "Map":
					prettyPrintWriter.writeStartElement("style");
					prettyPrintWriter.writeStartElement("style-rule");
					prettyPrintWriter.writeAttribute("element", "mark");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "size");
					prettyPrintWriter.writeAttribute("value", "2.1988949775695801");
					prettyPrintWriter.writeEndElement(); // style-rule
					prettyPrintWriter.writeEndElement(); // style
					break;

				case "Area":
					prettyPrintWriter.writeStartElement("style");
					prettyPrintWriter.writeStartElement("style-rule");
					prettyPrintWriter.writeAttribute("element", "mark");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "mark-labels-cull");
					prettyPrintWriter.writeAttribute("value", "true");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "mark-labels-show");
					prettyPrintWriter.writeAttribute("value", "false");
					prettyPrintWriter.writeEndElement(); // style-rule
					prettyPrintWriter.writeEndElement(); // style
					break;

				case "Pie":
					prettyPrintWriter.writeStartElement("style");
					prettyPrintWriter.writeStartElement("style-rule");
					prettyPrintWriter.writeAttribute("element", "mark");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "mark-labels-cull");
					prettyPrintWriter.writeAttribute("value", "true");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "mark-labels-show");
					prettyPrintWriter.writeAttribute("value", "true");
					prettyPrintWriter.writeEndElement(); // style-rule
					prettyPrintWriter.writeEndElement(); // style\
					break;

				}
//				if (vstr.getType().equalsIgnoreCase("linear gauge")) {
				if (vstr.getType().equalsIgnoreCase(CHART_TYPE.LINEAR_GAUGE.getValue())) {
					prettyPrintWriter.writeStartElement("style");
					prettyPrintWriter.writeStartElement("style-rule");
					prettyPrintWriter.writeAttribute("element", "mark");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "size");
					prettyPrintWriter.writeAttribute("value", "1.2");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "mark-color");
					prettyPrintWriter.writeAttribute("value", "#69aaa5");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "mark-transparency");
					prettyPrintWriter.writeAttribute("value", "132");
					prettyPrintWriter.writeEndElement(); // style-rule
					prettyPrintWriter.writeEndElement(); // style\

//				} else if (vstr.getType().equalsIgnoreCase("waterfall")) {
				} else if (vstr.getType().equalsIgnoreCase(CHART_TYPE.WATERFALL.getValue())) {
					prettyPrintWriter.writeStartElement("style");
					prettyPrintWriter.writeStartElement("style-rule");
					prettyPrintWriter.writeAttribute("element", "mark");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "mark-labels-cull");
					prettyPrintWriter.writeAttribute("value", "true");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "mark-labels-show");
					prettyPrintWriter.writeAttribute("value", "true");
					prettyPrintWriter.writeEndElement(); // style-rule
					prettyPrintWriter.writeEndElement(); // style\

				} else if (vstr.getType().equalsIgnoreCase(CHART_TYPE.DONUT.getValue())) {
					prettyPrintWriter.writeStartElement("style");
					prettyPrintWriter.writeStartElement("style-rule");
					prettyPrintWriter.writeAttribute("element", "mark");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "mark-labels-cull");
					prettyPrintWriter.writeAttribute("value", "true");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "mark-labels-show");
					prettyPrintWriter.writeAttribute("value", "true");
					prettyPrintWriter.writeEmptyElement("format");
					prettyPrintWriter.writeAttribute("attr", "size");
					prettyPrintWriter.writeAttribute("value", "4.1456303596496582");
//					prettyPrintWriter.writeEmptyElement("format");
//					prettyPrintWriter.writeAttribute("attr", "mark-color");
//					prettyPrintWriter.writeAttribute("value", "#d4d4d4");
					prettyPrintWriter.writeEndElement(); // style-rule
					prettyPrintWriter.writeEndElement(); // style\

				}

				prettyPrintWriter.writeEndElement(); // pane
			}

			if (vstr.getType().equalsIgnoreCase(CHART_TYPE.DONUT.getValue())) {
				prettyPrintWriter.writeStartElement("pane");
				prettyPrintWriter.writeAttribute("id", "3");
				prettyPrintWriter.writeAttribute("selection-relaxation-option", "selection-relaxation-allow");
				prettyPrintWriter.writeAttribute("x-axis-name",
						"[federated." + datasrc.getTableId() + "].[usr:" + Calculation_2 + ":qk]");
				prettyPrintWriter.writeStartElement("view");
				prettyPrintWriter.writeEmptyElement("breakdown");
				prettyPrintWriter.writeAttribute("value", "auto");
				prettyPrintWriter.writeEndElement(); // view
				prettyPrintWriter.writeEmptyElement("mark");
				prettyPrintWriter.writeAttribute("class", CHART_TYPE.PIE.getValue());
				prettyPrintWriter.writeEmptyElement("mark-sizing");
				prettyPrintWriter.writeAttribute("mark-sizing-setting", "marks-scaling-off");
				prettyPrintWriter.writeStartElement("customized-tooltip");
				prettyPrintWriter.writeEmptyElement("formatted-text");
				prettyPrintWriter.writeEndElement(); // customized-tooltip

				prettyPrintWriter.writeStartElement("style");
				prettyPrintWriter.writeStartElement("style-rule");
				prettyPrintWriter.writeAttribute("element", "mark");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "mark-labels-cull");
				prettyPrintWriter.writeAttribute("value", "true");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "mark-labels-show");
				prettyPrintWriter.writeAttribute("value", "false");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "size");
				prettyPrintWriter.writeAttribute("value", "2.9060773849487305");
				prettyPrintWriter.writeEmptyElement("format");
				prettyPrintWriter.writeAttribute("attr", "mark-color");
				prettyPrintWriter.writeAttribute("value", "#ffffff");
				prettyPrintWriter.writeEndElement(); // style-rule
				prettyPrintWriter.writeEndElement(); // style\
				prettyPrintWriter.writeEndElement(); // pane

			}

			prettyPrintWriter.writeEndElement(); // panes

			if (vstr.getRows() != null && !vstr.getType().equalsIgnoreCase(CHART_TYPE.DONUT.getValue())) {
				if (vstr.getType().equalsIgnoreCase("map")) {
					prettyPrintWriter.writeStartElement("rows");
					prettyPrintWriter
							.writeCharacters("[federated." + datasrc.getTableId() + "].[Latitude (generated)]");
					prettyPrintWriter.writeEndElement();// rows
				}

				else {
					String b = "(";
					int count = 0;
					for (String a : vstr.getRows()) {
						if (strmeta.contains(a)) {

							switch (metadata.get(a)[0]) {
							case "string":
								b = b + "[federated." + datasrc.getTableId() + "].[none:" + a + ":nk]";
								break;
							default:
								if (vstr.getType().equalsIgnoreCase("waterfall")) {
									b = b + "[federated." + datasrc.getTableId() + "].[cum:sum:" + a + ":qk]";

								} else {
									if (vstr.getType().equalsIgnoreCase(CHART_TYPE.TABLE.getValue())
											&& vstr.getRowsOrdinal() != null) {
										for (String rowsOrdinal : vstr.getRowsOrdinal()) {
											if (a.equalsIgnoreCase(rowsOrdinal)) {
												b = b + "[federated." + datasrc.getTableId() + "].[sum:" + a + ":ok]";
												break;
											}
										}
									} else {
										b = b + "[federated." + datasrc.getTableId() + "].[sum:" + a + ":qk]";
									}
								}
								break;
							}

						} else {
							if (a.equalsIgnoreCase(MeasureNames)) {
								b = b + "[federated." + datasrc.getTableId() + "].[:" + a + "]";
							} else {
								b = b + "[federated." + datasrc.getTableId() + "].[usr:" + a + "qk]";
							}
						}
						count++;
						if (count <= (vstr.getRows().size() - 1)) {
							if (vstr.getType() != null
									&& vstr.getType().equalsIgnoreCase(CHART_TYPE.TABLE.getValue())) {
								b = b + " / ";
							} else {
								b = b + " + ";
							}
						}

						if (!vstr.getType().equalsIgnoreCase(CHART_TYPE.TABLE.getValue()) || vstr.getType() == null) {
							prettyPrintWriter.writeStartElement("rows");
							prettyPrintWriter.writeCharacters(b + ")");

							prettyPrintWriter.writeEndElement();// rows)
						} else if (vstr.getType().equalsIgnoreCase(CHART_TYPE.TABLE.getValue())
								&& count == vstr.getRows().size()) {
							prettyPrintWriter.writeStartElement("rows");
							prettyPrintWriter.writeCharacters(b + ")");

							prettyPrintWriter.writeEndElement();// rows
						}

					}
				}
			} else {
				prettyPrintWriter.writeEmptyElement("rows");
			}

			if (vstr.getCols() != null && !vstr.getType().equalsIgnoreCase(CHART_TYPE.DONUT.getValue())) {

				if (vstr.getType().equalsIgnoreCase("map")) {
					prettyPrintWriter.writeStartElement("cols");
					prettyPrintWriter
							.writeCharacters("[federated." + datasrc.getTableId() + "].[Longitude (generated)]");
					prettyPrintWriter.writeEndElement();// cols
				}

				else {
					String b = "(";
					int count = 0;
					for (String a : vstr.getCols()) {
						if (strmeta.contains(a)) {
							switch (metadata.get(a)[0]) {
							case "string":
								b = b + "[federated." + datasrc.getTableId() + "].[none:" + a + ":nk]";
								break;
							default:
								if (a.equalsIgnoreCase("Mth")) {
									b = b + "[federated." + datasrc.getTableId() + "].[none:" + a + ":qk]";
								} else {
									if (vstr.getType().equalsIgnoreCase(CHART_TYPE.TABLE.getValue())
											&& vstr.getRowsOrdinal() != null) {
										for (String colsOrdinal : vstr.getColsOrdinal()) {
											if (a.equalsIgnoreCase(colsOrdinal)) {
												b = b + "[federated." + datasrc.getTableId() + "].[sum:" + a + ":ok]";
												break;
											}
										}
									} else {
										b = b + "[federated." + datasrc.getTableId() + "].[sum:" + a + ":qk]";
									}
								}

								break;
							}

						} else {
							if (a.equalsIgnoreCase(MeasureNames)) {
								b = b + "[federated." + datasrc.getTableId() + "].[:" + a + "]";
							} else {
								b = b + "[federated." + datasrc.getTableId() + "].[usr:" + a + ":qk]";
							}
						}
						count++;
						int k = vstr.getCols().size();
						if (count <= (vstr.getCols().size() - 1)) {
							if (vstr.getType().equalsIgnoreCase(CHART_TYPE.TABLE.getValue())) {
								b = b + " / ";
							} else {
								b = b + " + ";
							}
						}
					}
					if (!vstr.getType().equalsIgnoreCase(CHART_TYPE.TABLE.getValue()) || vstr.getType() == null) {
						prettyPrintWriter.writeStartElement("cols");
						prettyPrintWriter.writeCharacters(b + ")");
						prettyPrintWriter.writeEndElement();
					} else if (vstr.getType().equalsIgnoreCase(CHART_TYPE.TABLE.getValue())
							&& count == vstr.getCols().size()) {
						prettyPrintWriter.writeStartElement("cols");
						prettyPrintWriter.writeCharacters(b + ")");
						prettyPrintWriter.writeEndElement();
					}
				}
			} else if (vstr.getType().equalsIgnoreCase(CHART_TYPE.DONUT.getValue())) {
				prettyPrintWriter.writeStartElement("cols");
				prettyPrintWriter.writeCharacters("(" + "[federated." + datasrc.getTableId() + "].[usr:" + Calculation_1
						+ ":qk] / " + "[federated." + datasrc.getTableId() + "].[usr:" + Calculation_2 + ":qk]" + ")");
				prettyPrintWriter.writeEndElement();// rows

			}

			else {
				prettyPrintWriter.writeEmptyElement("cols");
			}

			prettyPrintWriter.writeEndElement(); // table
			prettyPrintWriter.writeEmptyElement("simple-id");
			prettyPrintWriter.writeAttribute("uuid",
					"{" + getAlphaNumericString(8) + "-" + getAlphaNumericString(4) + "-" + getAlphaNumericString(4)
							+ "-" + getAlphaNumericString(4) + "-" + getAlphaNumericString(12) + "}");
			prettyPrintWriter.writeEndElement();

		}
		prettyPrintWriter.writeEndElement();
	}

}
