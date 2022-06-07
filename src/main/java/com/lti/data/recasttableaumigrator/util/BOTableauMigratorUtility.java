package com.lti.data.recasttableaumigrator.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.lti.data.recasttableaumigrator.dto.DatabaseSourceModel;
import com.lti.data.recasttableaumigrator.model.QueryModel;

public interface BOTableauMigratorUtility {
	
	
	public static DatabaseSourceModel prepareDataSrcDetails(List<QueryModel> queryModel)
	{
		//List<DatabaseSourceModel> dataSrcModelLst = new ArrayList<>();
		DatabaseSourceModel dataSrcModel = new DatabaseSourceModel();
		try {
		for(QueryModel qm:queryModel)
		{
			if(qm.getDatabaseName().equalsIgnoreCase("Sample - Superstore"))
			{
				dataSrcModel.setDatabaseName(qm.getDatabaseName()!=null&& !qm.getDatabaseName().trim().isEmpty()?qm.getDatabaseName():"");
				dataSrcModel.setQueryStatement(qm.getQueryStatement()!=null&&!qm.getQueryStatement().trim().isEmpty()?qm.getQueryStatement():"");
				dataSrcModel.setTableName("Orders");
				dataSrcModel.setServerHostName(qm.getHostname()!=null&&!qm.getHostname().trim().isEmpty()?qm.getHostname():"");
				dataSrcModel.setTableId(CommonConstants.DEFAULT_CROSS_TABLE_ID+getAlphaNumericString(CommonConstants.ALPHA_NUM_STRING_SIZE));
				dataSrcModel.setDatabaseType(qm.getDatabaseType().contains(CommonConstants.MYSQL)?CommonConstants.MYSQL:qm.getDatabaseType());
				dataSrcModel.setConnId(CommonConstants.CROSSTAB_CONN_ID+getAlphaNumericString(CommonConstants.ALPHA_NUM_STRING_SIZE));
				dataSrcModel.setMetadataId(CommonConstants.CROSSTAB_METADATA_ID);
			}
			else {
				dataSrcModel.setDatabaseName(qm.getDatabaseName()!=null&& !qm.getDatabaseName().trim().isEmpty()?qm.getDatabaseName():"");
				dataSrcModel.setQueryStatement(qm.getQueryStatement()!=null&&!qm.getQueryStatement().trim().isEmpty()?qm.getQueryStatement():"");
				dataSrcModel.setTableName(qm.getQueryStatement()!=null&&!qm.getQueryStatement().trim().isEmpty()?CommonConstants.QUERY_STATEMENT:CommonConstants.TABLE_QUERY_STATEMENT);
				dataSrcModel.setServerHostName(qm.getHostname()!=null&&!qm.getHostname().trim().isEmpty()?qm.getHostname():"");
				dataSrcModel.setTableId(CommonConstants.DEFALT_TABLE_ID+getAlphaNumericString(CommonConstants.ALPHA_NUM_STRING_SIZE));
				dataSrcModel.setDatabaseType(qm.getDatabaseType().contains(CommonConstants.MYSQL)?CommonConstants.MYSQL:qm.getDatabaseType());
				dataSrcModel.setConnId(CommonConstants.CONN_ID+getAlphaNumericString(CommonConstants.ALPHA_NUM_STRING_SIZE));
				dataSrcModel.setMetadataId(CommonConstants.METADATA_ID);
			}
			
			//dataSrcModelLst.add(dataSrcModel);
		}
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		return dataSrcModel;
		
	}
	
	public static DatabaseSourceModel prepareDataSrcDetailsForCrossTab(List<QueryModel> queryModel)
	{
		//List<DatabaseSourceModel> dataSrcModelLst = new ArrayList<>();
		DatabaseSourceModel dataSrcModel = new DatabaseSourceModel();
		try {
		
			
			dataSrcModel.setDatabaseName("Sample - Superstore");
			dataSrcModel.setQueryStatement("");
			dataSrcModel.setTableName("Orders");
			dataSrcModel.setServerHostName("");
			dataSrcModel.setTableId(CommonConstants.DEFAULT_CROSS_TABLE_ID+getAlphaNumericString(CommonConstants.ALPHA_NUM_STRING_SIZE));
			dataSrcModel.setDatabaseType("excel-direct");
			dataSrcModel.setConnId(CommonConstants.CROSSTAB_CONN_ID+getAlphaNumericString(CommonConstants.ALPHA_NUM_STRING_SIZE));
			dataSrcModel.setMetadataId(CommonConstants.CROSSTAB_METADATA_ID);
			//dataSrcModelLst.add(dataSrcModel);
		
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		return dataSrcModel;
		
	}
	
	static String getAlphaNumericString(int n)
    {
  
        // chose a Character random from this String
        String AlphaNumericString = "abcdefghijklmnopqrstuvwxyz"
                                    + "0123456789";
  
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);
  
        for (int i = 0; i < n; i++) {
  
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                = (int)(AlphaNumericString.length()
                        * Math.random());
  
            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                          .charAt(index));
        }
  
        return sb.toString();
    }
	
	/*
	 * private static void writeWindows(XMLStreamWriter prettyPrintWriter,
	 * MigratorModel migratorModel,DatabaseSourceModel datasrc) throws
	 * XMLStreamException { prettyPrintWriter.writeStartElement("windows");
	 * prettyPrintWriter.writeAttribute("saved-dpi-scale-factor", "1.5");
	 * prettyPrintWriter.writeAttribute("source-height", "44"); for (int j = 0; j <
	 * migratorModel.getVisualModelList().size(); j++) { VisualModel m =
	 * migratorModel.getVisualModelList().get(j); VisualStructure vstr =
	 * visuals.get(m.getReportName());
	 * 
	 * prettyPrintWriter.writeStartElement("window");
	 * prettyPrintWriter.writeAttribute("class", "worksheet");
	 * prettyPrintWriter.writeAttribute("name", m.getReportName());
	 * prettyPrintWriter.writeStartElement("cards");
	 * prettyPrintWriter.writeStartElement("edge");
	 * prettyPrintWriter.writeAttribute("name", "left");
	 * prettyPrintWriter.writeStartElement("strip");
	 * prettyPrintWriter.writeAttribute("size", "160");
	 * prettyPrintWriter.writeEmptyElement("card");
	 * prettyPrintWriter.writeAttribute("type", "pages");
	 * prettyPrintWriter.writeEmptyElement("card");
	 * prettyPrintWriter.writeAttribute("type", "filters");
	 * prettyPrintWriter.writeEmptyElement("card");
	 * prettyPrintWriter.writeAttribute("type", "marks");
	 * 
	 * prettyPrintWriter.writeEndElement();// strip
	 * prettyPrintWriter.writeEndElement();// edge
	 * 
	 * prettyPrintWriter.writeStartElement("edge");
	 * prettyPrintWriter.writeAttribute("name", "top");
	 * prettyPrintWriter.writeStartElement("strip");
	 * prettyPrintWriter.writeAttribute("size", "2147483647");
	 * prettyPrintWriter.writeEmptyElement("card");
	 * prettyPrintWriter.writeAttribute("type", "columns");
	 * prettyPrintWriter.writeEndElement();
	 * prettyPrintWriter.writeStartElement("strip");
	 * prettyPrintWriter.writeAttribute("size", "2147483647");
	 * prettyPrintWriter.writeEmptyElement("card");
	 * prettyPrintWriter.writeAttribute("type", "rows");
	 * prettyPrintWriter.writeEndElement();
	 * prettyPrintWriter.writeStartElement("strip");
	 * prettyPrintWriter.writeAttribute("size", "2147483647");
	 * prettyPrintWriter.writeEmptyElement("card");
	 * prettyPrintWriter.writeAttribute("type", "title");
	 * prettyPrintWriter.writeEndElement(); // strip
	 * prettyPrintWriter.writeEndElement();// edge
	 * 
	 * if (vstr.getRows() != null) { prettyPrintWriter.writeStartElement("edge");
	 * prettyPrintWriter.writeAttribute("name", "right");
	 * prettyPrintWriter.writeStartElement("strip");
	 * prettyPrintWriter.writeAttribute("size", "160"); for (String str :
	 * vstr.getRows()) {
	 * 
	 * prettyPrintWriter.writeEmptyElement("card");
	 * prettyPrintWriter.writeAttribute("pane-specification-id", "0"); if
	 * (strmeta.contains(str)) { switch (metadata.get(str)[0]) { case "string":
	 * prettyPrintWriter.writeAttribute("param", "[federated." +
	 * datasrc.get("tableid1") + "].[none:" + str + ":nk]");
	 * prettyPrintWriter.writeAttribute("type", "color"); break; case "integer":
	 * prettyPrintWriter.writeAttribute("param", "[federated." +
	 * datasrc.get("tableid1") + "].[sum:" + str + ":qk]");
	 * prettyPrintWriter.writeAttribute("type", "color"); break; default:
	 * prettyPrintWriter.writeAttribute("param", "[federated." +
	 * datasrc.get("tableid1") + "].[sum:" + str + ":qk]");
	 * prettyPrintWriter.writeAttribute("type", "color"); break; }
	 * 
	 * } else { prettyPrintWriter.writeAttribute("param", "[federated." +
	 * datasrc.get("tableid1") + "].[:" + str + ":qk]");
	 * prettyPrintWriter.writeAttribute("type", "color"); }
	 * 
	 * } prettyPrintWriter.writeEndElement();// strip
	 * prettyPrintWriter.writeEndElement();// edge
	 * 
	 * }
	 * 
	 * prettyPrintWriter.writeEndElement();// cards
	 * 
	 * prettyPrintWriter.writeEmptyElement("simple-id");
	 * prettyPrintWriter.writeAttribute("uuid", "{" + getAlphaNumericString(8) + "-"
	 * + getAlphaNumericString(4) + "-" + getAlphaNumericString(4) + "-" +
	 * getAlphaNumericString(4) + "-" + getAlphaNumericString(12) + "}");
	 * prettyPrintWriter.writeEndElement();// window
	 * 
	 * }
	 * 
	 * prettyPrintWriter.writeStartElement("window");
	 * prettyPrintWriter.writeAttribute("class", "dashboard");
	 * prettyPrintWriter.writeAttribute("name", "Dashboard");
	 * prettyPrintWriter.writeStartElement("viewpoints"); for (VisualModel vm :
	 * migratorModel.getVisualModelList()) {
	 * prettyPrintWriter.writeStartElement("viewpoint");
	 * prettyPrintWriter.writeAttribute("name", vm.getReportName());
	 * prettyPrintWriter.writeEmptyElement("zoom");
	 * prettyPrintWriter.writeAttribute("type", "entire-view");
	 * prettyPrintWriter.writeEndElement();// viewpoint }
	 * 
	 * prettyPrintWriter.writeEndElement();// viewpoints
	 * prettyPrintWriter.writeEmptyElement("active");
	 * prettyPrintWriter.writeAttribute("id", "-1");
	 * prettyPrintWriter.writeEmptyElement("simple-id");
	 * prettyPrintWriter.writeAttribute("uuid", "{" + getAlphaNumericString(8) + "-"
	 * + getAlphaNumericString(4) + "-" + getAlphaNumericString(4) + "-" +
	 * getAlphaNumericString(4) + "-" + getAlphaNumericString(12) + "}");
	 * prettyPrintWriter.writeEndElement();// window
	 * prettyPrintWriter.writeEndElement(); }
	 */
}
