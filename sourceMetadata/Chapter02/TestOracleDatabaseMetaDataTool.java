import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class TestOracleDatabaseMetaDataTool {

	public static void main(String[] args) {
		String driver = "oracle.jdbc.driver.OracleDriver";
    	String url = "jdbc:oracle:thin:@localhost:1521:maui"; // office
    	//String url = "jdbc:oracle:thin:@localhost:1521:scorpian"; // home
    	String username = "octopus";  //office
    	String password = "octopus";  // office
		Connection conn = null;

    	try {
      		Class.forName(driver);
      		System.out.println("ok: loaded Oracle driver.");
    	}
    	catch( Exception e ) {
      		System.out.println("Failed to load Oracle driver.");
      		System.exit(1);
    	}

    	try {
      		conn = DriverManager.getConnection(url, username, password);

   			//
			// print the list of table types
			//
			java.util.List tableTypes = DatabaseMetaDataTool.getTableTypes(conn);
      		System.out.println("Got results: list of table types --------------");
      		for (int i=0; i < tableTypes.size(); i++) {
				// process results one element at a time
        		String tableType = (String) tableTypes.get(i);
        		System.out.println("table type = " + tableType);
			}

   			//
			// print the list of tables
			//
			java.util.List tables = DatabaseMetaDataTool.getOracleTableNames(conn);
      		System.out.println("Got results: list of tables --------------");
      		for (int i=0; i < tables.size(); i++) {
				// process results one element at a time
        		String tableName = (String) tables.get(i);
        		System.out.println("table name = " + tableName);
			}


   			//
			// print the list of views
			//
			java.util.List views = DatabaseMetaDataTool.getOracleViewNames(conn);
      		System.out.println("Got results: list of views -----------");
      		for (int i=0; i < views.size(); i++) {
				// process results one element at a time
        		String viewName = (String) views.get(i);
        		System.out.println("view name = " + viewName);
			}

   			//
			// print the list of PKs
			//
			java.util.List pks = DatabaseMetaDataTool.getPrimaryKeys(conn, "USERS");
      		System.out.println("Got results: list of PKs -------------");
      		for (int i=0; i < pks.size(); i++) {
				// process results one element at a time
        		String columnName = (String) pks.get(i);
        		System.out.println("column name = " + columnName);
			}

   			//
			// print the list of Column names for table MyPayrollTable
			//
			java.util.Hashtable result = DatabaseMetaDataTool.getColumnNames(conn, "MyPayrollTable");
      		System.out.println("Got results: list of column names -------------");
      		java.util.Enumeration columns = result.keys();
      		while (columns.hasMoreElements()) {
				Object columnKey = columns.nextElement();
				String columnName = (String) columnKey;
				String columnType = (String) result.get(columnKey);
        		System.out.println("column name = " + columnName);
        		System.out.println("column type = " + columnType);
			}

   			//
			// print the detail of table privileges for table TestTable77
			//
			/***
			 worked, but gets lots of data
			 String tablePrivileges = DatabaseMetaDataTool.getTablePrivileges
				(conn, // connection
				 null, // catalog
				 "%", // schema
				 "%");
			***/
			String tablePrivileges = DatabaseMetaDataTool.getTablePrivileges
				(conn, // connection
				 conn.getCatalog(), //null, // catalog
				 "%", // schema
				 "EMP%");
      		System.out.println("-------- TablePrivileges -------------");
      		System.out.println(tablePrivileges);
      		System.out.println("------------------------------------");

   			//
			// print the detail of databse schemas
			//
			String schemas = DatabaseMetaDataTool.getSchemas(conn);
      		System.out.println("-------- schemas -------------");
      		System.out.println(schemas);
      		System.out.println("------------------------------------");

   			//
			// print the detail of databse catalogs
			//
			String catalogs = DatabaseMetaDataTool.getCatalogs(conn);
      		System.out.println("-------- catalogs -------------");
      		System.out.println(catalogs);
      		System.out.println("------------------------------------");

   			//
			// getAvailableSqlTypes
			//
			String  availableSqlTypes = DatabaseMetaDataTool.getAvailableSqlTypes(conn);
      		System.out.println("-------- availableSqlTypes -------------");
      		System.out.println(availableSqlTypes);
      		System.out.println("------------------------------------");

   			//
			// getStoredProcedureNames
			//
			String catalog = null;
			String schemaPattern = null;
            String procedureNamePattern = "CHECKPROPERTIES%";
			String spNames = DatabaseMetaDataTool.getStoredProcedureNames
                                 (conn,
				                  catalog,
				                  schemaPattern,
				                  procedureNamePattern);
      		System.out.println("-------- getStoredProcedureNames -------------");
      		System.out.println(spNames);
      		System.out.println("+++------------------------------------");




   			//
			// getStoredProcedureColumns
			//
			//String catalog = null;
			//String schemaPattern = null;
            //String procedureNamePattern = "CHECKPROPERTIES%";
			//DatabaseMetaDataTool.getStoredProceduresSignatures
            //                     (conn);
                                 //,
				                 // catalog,
				                 // schemaPattern,
				                 // procedureNamePattern);
      		//System.out.println("-------- getStoredProcedureNames -------------");
      		//System.out.println(spNames);
      		//System.out.println("------------------------------------");

      		System.out.println("+++------------------------------------");




			String columnPrivileges = DatabaseMetaDataTool.getColumnPrivileges
				(conn, // connection
				 conn.getCatalog(), // catalog
				 "%", // "OCTOPUS", //"%", // schema
				 "EMPLOYEES",
				 "%");
      		System.out.println("---- Table's Columns Privileges ----");
      		System.out.println(columnPrivileges);
      		System.out.println("------------------------------------");

   			//
			// Get list of SQL Keywords as a java.util.List
			// print the list of SQL Keywords
			//
			java.util.List list = DatabaseMetaDataTool.getSQLKeywords(conn);
      		System.out.println("--- Got results: list of SQL Keywords ---");
      		for (int i=0; i < list.size(); i++) {
        		String sqlKeyword = (String) list.get(i);
        		System.out.println("sqlKeyword= " + sqlKeyword);
			}

   			//
			// Get list of SQL Keywords as an XML
			// print the list of SQL Keywords
			//
			String listOfSQLKeywords = DatabaseMetaDataTool.getSQLKeywordsAsXML(conn);
      		System.out.println("--- Got results: list of SQL Keywords ---");
        	System.out.println("listOfSQLKeywords= " + listOfSQLKeywords);
        }
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			DatabaseUtil.close(conn);
		}
	}

}
