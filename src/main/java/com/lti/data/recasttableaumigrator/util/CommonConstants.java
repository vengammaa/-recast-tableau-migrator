package com.lti.data.recasttableaumigrator.util;

import java.util.ResourceBundle;

public interface CommonConstants {
	
	public static ResourceBundle getProperties()
	{
		ResourceBundle envResource= ResourceBundle.getBundle("Environment");
		return envResource;
	}
	

	public static final String FILEPATH = getProperties().getString("FILEPATH");
	public static final String FILENAME = getProperties().getString("FILENAME");
	public static final String ACCESSFILE = getProperties().getString("ACCESS_FILE");
	public static final String DEFALT_TABLE_ID = "0ul8ra10syb655114svy";
	public static final String QUERY_STATEMENT = "Custom SQL Query";
	public static final String TABLE_QUERY_STATEMENT = "Custom Table"; 
	public static final String MYSQL = "mysql";
	public static final String CONN_ID = "00yliv11v3sqn415skld";
	public static final int ALPHA_NUM_STRING_SIZE = 8;
	public static final String METADATA_ID = "ECC4D6FDB7C64E8FBBF66D2E7AB71470";
	
	//Cross Table Constant 
	public static final String DEFAULT_CROSS_TABLE_ID = "10nnk8d1vgmw8q17yu76";
	public static final String CROSSTAB_CONN_ID = "0lad5au144tqkt16qnaz";
	public static final String CROSSTAB_METADATA_ID = "6D2EF74F348B46BDA976A7AEEA6FB5C9";
	

}
