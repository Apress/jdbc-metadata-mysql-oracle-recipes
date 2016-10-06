import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class TestMySqlDatabaseMetaDataTool {

	public static void main(String[] args) {
		String driver = "org.gjt.mm.mysql.Driver";
    	String url = "jdbc:mysql://localhost/octopus";
    	String username = "root";
    	String password = "root";
		Connection conn = null;

    	try {
      		Class.forName(driver);
      		System.out.println("ok: loaded MySQL driver.");
    	}
    	catch( Exception e ) {
      		System.out.println("Failed to load MySQL driver.");
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
			java.util.List tables = DatabaseMetaDataTool.getTableNames(conn);
      		System.out.println("--- Got results: list of tables ---");
      		for (int i=0; i < tables.size(); i++) {
				// process results one element at a time
        		String tableName = (String) tables.get(i);
        		System.out.println("table name = " + tableName);
			}

   			//
			// print the list of views
			//
			java.util.List views = DatabaseMetaDataTool.getViewNames(conn);
      		System.out.println("Got results: list of views");
      		for (int i=0; i < views.size(); i++) {
				// process results one element at a time
        		String viewName = (String) views.get(i);
        		System.out.println("view name = " + viewName);
			}

   			//
			// print the list of PKs
			//
			java.util.List pks = DatabaseMetaDataTool.getPrimaryKeys(conn, "group_members");
      		System.out.println("Got results: list of PKs -------------");
      		for (int i=0; i < pks.size(); i++) {
				// process results one element at a time
        		String columnName = (String) pks.get(i);
        		System.out.println("column name = " + columnName);
			}

   			//
			// print the detail of columns for table TestTable77
			//
			String columnDetails = DatabaseMetaDataTool.getColumnDetails(conn, "TestTable77");
      		System.out.println("-------- columnDetails -------------");
      		System.out.println(columnDetails);
      		System.out.println("------------------------------------");

   			//
			// print the detail of table privileges for table TestTable77
			//
      		//System.out.println("sCat="+ sCat);
			String tablePrivileges = DatabaseMetaDataTool.getTablePrivileges
				(conn, // connection
				 conn.getCatalog(), // catalog
				 null, //"%", // schema
				 "%");
      		System.out.println("-------- TablePrivileges -------------");
      		System.out.println(tablePrivileges);
      		System.out.println("------------------------------------");

			String columnPrivileges = DatabaseMetaDataTool.getColumnPrivileges
				(conn, // connection
				 conn.getCatalog(), // catalog
				 null, //"%", // schema
				 "artist",
				 "%");
      		System.out.println("---- Table's Columns Privileges ----");
      		System.out.println(columnPrivileges);
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
			// does table TestTable77 exist: using solution 1
			//
			boolean exist771 = DatabaseMetaDataTool.tableExist1(conn, "TestTable77");
      		System.out.println("-------- does table TestTable77 exist -------");
      		System.out.println(exist771);
      		System.out.println("---------------------------------------------");

   			//
			// does table TestTable88 exist: using solution 1
			//
			boolean exist881 = DatabaseMetaDataTool.tableExist1(conn, "TestTable88");
      		System.out.println("-------- does table TestTable88 exist -------");
      		System.out.println(exist881);
      		System.out.println("---------------------------------------------");

   			//
			// does table TestTable77 exist: using solution 2
			//
			boolean exist772 = DatabaseMetaDataTool.tableExist2(conn, "TestTable77");
      		System.out.println("-------- does table TestTable77 exist -------");
      		System.out.println(exist772);
      		System.out.println("---------------------------------------------");

   			//
			// does table TestTable88 exist: using solution 2
			//
			boolean exist882 = DatabaseMetaDataTool.tableExist2(conn, "TestTable88");
      		System.out.println("-------- does table TestTable88 exist -------");
      		System.out.println(exist882);
      		System.out.println("---------------------------------------------");

   			//
			// does table TestTable77 exist: using solution 3
			//
			boolean exist773 = DatabaseMetaDataTool.tableExist3(conn, "TestTable77");
      		System.out.println("-------- does table TestTable77 exist -------");
      		System.out.println(exist773);
      		System.out.println("---------------------------------------------");

   			//
			// does table TestTable88 exist: using solution 3
			//
			boolean exist883 = DatabaseMetaDataTool.tableExist3(conn, "TestTable88");
      		System.out.println("-------- does table TestTable88 exist -------");
      		System.out.println(exist883);
      		System.out.println("---------------------------------------------");

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
