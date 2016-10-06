package jcb.meta;

import java.util.*;
import java.io.*;
import java.sql.*;
import java.lang.reflect.*;

import jcb.db.*;
import jcb.util.*;

/**
 * This class provides class-level methods
 * for getting database metadata.
 *
 */
 public class DatabaseMetaDataTool {

	private static final String[] DB_TABLE_TYPES = { "TABLE" };
	private static final String[] DB_VIEW_TYPES = { "VIEW" };
	private static final String[] DB_MIXED_TYPES = { "TABLE", "VIEW" };

	private static final String COLUMN_NAME_TABLE_NAME = "TABLE_NAME";
	private static final String COLUMN_NAME_COLUMN_NAME = "COLUMN_NAME";
	private static final String COLUMN_NAME_TYPE = "TYPE";
	private static final String COLUMN_NAME_DATA_TYPE = "DATA_TYPE";
 	private static final String COLUMN_NAME_VIEW_NAME = "VIEW_NAME";
 	private static final String COLUMN_NAME_TYPE_NAME = "TYPE_NAME";
 	private static final String COLUMN_NAME_COLUMN_SIZE = "COLUMN_SIZE";
 	private static final String COLUMN_NAME_NULLABLE = "NULLABLE";
 	private static final String COLUMN_NAME_ORDINAL_POSITION = "ORDINAL_POSITION";
 	private static final String COLUMN_NAME_TABLE_CATALOG = "TABLE_CAT";
 	private static final String COLUMN_NAME_TABLE_SCHEMA = "TABLE_SCHEM";
 	private static final String COLUMN_NAME_PRIVILEGE = "PRIVILEGE";
 	private static final String COLUMN_NAME_GRANTOR = "GRANTOR";
 	private static final String COLUMN_NAME_IS_GRANTABLE = "IS_GRANTABLE";
 	private static final String COLUMN_NAME_GRANTEE = "GRANTEE";
 	private static final String COLUMN_NAME_ASC_OR_DESC = "ASC_OR_DESC";
 	private static final String COLUMN_NAME_CARDINALITY = "CARDINALITY";
 	private static final String COLUMN_NAME_PAGES = "PAGES";
 	private static final String COLUMN_NAME_FILTER_CONDITION = "FILTER_CONDITION";
 	private static final String COLUMN_NAME_NON_UNIQUE = "NON_UNIQUE";
 	private static final String COLUMN_NAME_INDEX_QUALIFIER = "INDEX_QUALIFIER";
 	private static final String COLUMN_NAME_INDEX_NAME = "INDEX_NAME";
 	private static final String COLUMN_NAME_SCOPE = "SCOPE";
 	private static final String COLUMN_NAME_DECIMAL_DIGITS = "DECIMAL_DIGITS";
 	private static final String COLUMN_NAME_PSEUDO_COLUMN  = "PSEUDO_COLUMN";

	private static final String  ORACLE_VIEWS =
		"select object_name from user_objects where object_type = 'VIEW'";
	private static final String  ORACLE_TABLES =
		"select object_name from user_objects where object_type = 'TABLE'";
	private static final String  ORACLE_TABLES_AND_VIEWS =
		"select object_name from user_objects where object_type = 'TABLE' or object_type = 'VIEW'";

	private static final String  STORED_PROCEDURE_RETURNS_RESULT = "procedureReturnsResult";
	private static final String  STORED_PROCEDURE_NO_RESULT = "procedureNoResult";
	private static final String  STORED_PROCEDURE_RESULT_UNKNOWN = "procedureResultUnknown";

    static final Map JDBC_TYPE_NAME_MAP = new HashMap();
    static {
		// Get all fields in java.sql.Types
		Field[] fields = java.sql.Types.class.getFields();
		for (int i=0; i<fields.length; i++) {
			try {
				// Get field name
				String name = fields[i].getName();

				// Get field value
				Integer value = (Integer)fields[i].get(null);

				// Add to map
				JDBC_TYPE_NAME_MAP.put(value, name);
			}
			catch (IllegalAccessException e) {
				// ignore
			}
		}
	}

	/**
	 * Table Exist: Solution 1
	 * Check whether a given table (identified by a tableName
	 * parameter) exist for a given connection object.
	 * @param conn the Connection object
	 * @param tableName the table name (to check to see if it exist)
	 * @return true if table exists, otherwise return false.
	 */
	 public static boolean tableExist1(java.sql.Connection conn,
	                                   String tableName) {

		if ((tableName == null) || (tableName.length() == 0)) {
			return false;
		}

		try {
			java.util.List allTables = getTableNames(conn);
			for (int i = 0; i < allTables.size(); i++) {
				String dbTable = (String) allTables.get(i);
				if (dbTable != null) {
					if (dbTable.equalsIgnoreCase(tableName)) {
						return true;
					}
				}
			}

			// table does not exist
			return false;
		}
		catch(Exception e) {
			//e.printStackTrace();
			// table does not exist or some other problem
			return false;
		}
	}

	/**
	 * Table Exist: Solution 2
	 * Check whether a given table (identified by a tableName
	 * parameter) exist for a given connection object.
	 * @param conn the Connection object
	 * @param tableName the table name (to check to see if it exist)
	 * @return true if table exists, otherwise return false.
	 */
	 public static boolean tableExist2(java.sql.Connection conn,
	                                   String tableName) {

		if ((tableName == null) || (tableName.length() == 0)) {
			return false;
		}

		String query = "select * from " + tableName + " where 1=0";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			return true;
		}
		catch (Exception e ) {
			// table does not exist or some other problem
			//e.printStackTrace();
			return false;
		}
		finally {
			DatabaseUtil.close(rs);
			DatabaseUtil.close(stmt);
		}

	}

	/**
	 * Table Exist: Solution 3
	 * Check whether a given table (identified by a tableName
	 * parameter) exist for a given connection object.
	 * @param conn the Connection object
	 * @param tableName the table name (to check to see if it exist)
	 * @return true if table exists, otherwise return false.
	 */
	 public static boolean tableExist3(java.sql.Connection conn,
	                                   String tableName) {

		if ((tableName == null) || (tableName.length() == 0)) {
			return false;
		}

		String query = "select count(*) from " + tableName;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			return true;
		}
		catch (Exception e ) {
			// table does not exist or some other problem
			//e.printStackTrace();
			return false;
		}
		finally {
			DatabaseUtil.close(rs);
			DatabaseUtil.close(stmt);
		}

	}


	/**
	 * Get the SQL Keywords for a database.
	 * @param conn the Connection object.
	 * @return the list SQL Keywords for a database.
	 * Each element in the list is a SQL keyword.
	 * @exception Failed to get the SQL Keywords for a database.
	 */
	 public static java.util.List getSQLKeywords(java.sql.Connection conn)
		throws Exception {

		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 String sqlKeywords = meta.getSQLKeywords();
			 if ((sqlKeywords == null) || (sqlKeywords.length() == 0)) {
				 return null;
			 }

			 java.util.List list = new java.util.ArrayList();

			 // SQL keywords are separated by ","
			 StringTokenizer st = new StringTokenizer(sqlKeywords, ",");
			 while(st.hasMoreTokens()) {
				 list.add(st.nextToken().trim());
			 }
			 return list;
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw new Exception(e.toString());
		 }

	}

	/**
	 * Get the SQL Keywords for a database.
	 * @param conn the Connection object.
	 * @return the list SQL Keywords for a database as an XML.
	 * @exception Failed to get the SQL Keywords for a database.
	 */
	 public static String getSQLKeywordsAsXML(java.sql.Connection conn)
		throws Exception {

		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 String sqlKeywords = meta.getSQLKeywords();
			 if ((sqlKeywords == null) || (sqlKeywords.length() == 0)) {
				 return null;
			 }

		  	 StringBuffer sb = new StringBuffer("<?xml version='1.0'>");
		  	 sb.append("<sql_keywords>");


			 // SQL keywords are separated by ","
			 StringTokenizer st = new StringTokenizer(sqlKeywords, ",");
			 while(st.hasMoreTokens()) {
				sb.append("<keyword>");
				sb.append(st.nextToken().trim());
				sb.append("</keyword>");
			 }
			 sb.append("</sql_keywords>");
			 return sb.toString();
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw new Exception(e.toString());
		 }

	}

	/**
	 * Get the table types for a database.
	 * @param conn the Connection object.
	 * @return the list of table types as a List.
	 * @exception Failed to get the table types from the database.
	 */
	 public static java.util.List getTableTypes(java.sql.Connection conn)
		throws Exception {

		ResultSet rs = null;
		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 rs = meta.getTableTypes();
			 if (rs == null) {
				 return null;
			 }

			 java.util.List list = new java.util.ArrayList();
			 //System.out.println("getTableTypes(): --------------");
			 while (rs.next()) {
				 String tableType = DatabaseUtil.getTrimmedString(rs, 1);
				 //System.out.println("tableType="+tableType);
				 if (tableType != null) {
					 list.add(tableType);
				 }
			 }
			 //System.out.println("--------------");
			 return list;
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw new Exception(e.toString());
		 }
		 finally {
			 DatabaseUtil.close(rs);
		 }

	}

	/**
	 * Get the table names for a given connection object.
	 * @param conn the Connection object
	 * @return the list of table names as a List.
	 * @exception Failed to get the table names from the database.
	 */
	 public static java.util.List getTableNames(java.sql.Connection conn)
		throws Exception {

		ResultSet rs = null;
		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 rs = meta.getTables(null, null, null, DB_TABLE_TYPES);
			 if (rs == null) {
				 return null;
			 }

			 java.util.List list = new java.util.ArrayList();
			 //System.out.println("getTableNames(): --------------");
			 while (rs.next()) {
				 String tableName = DatabaseUtil.getTrimmedString(rs, COLUMN_NAME_TABLE_NAME);
				 //System.out.println("tableName="+tableName);
				 if (tableName != null) {
					 list.add(tableName);
				 }
			 }
			 //System.out.println("--------------");
			 return list;
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw new Exception(e.toString());
		 }
		 finally {
			 DatabaseUtil.close(rs);
		 }

	}

	/**
	 * Get the Oracle table names for a given connection object.
	 * If you use the getTableNames() for an Oracle database, you
	 * will get lots of auxiliary tables, which belong to the user,
	 * but user is not interested in seeing them.
	 *
	 * @param conn the Connection object
	 * @return the list of table names as a List.
	 * @exception Failed to get the table names from the database.
	 */
	public static java.util.List getOracleTableNames(java.sql.Connection conn)
		throws Exception {

            Statement stmt = null;
            ResultSet rs = null;
            try {

                stmt = conn.createStatement();
                rs = stmt.executeQuery(ORACLE_TABLES);

                if (rs == null) {
                    return null;
                }

 			 	java.util.List list = new java.util.ArrayList();
                while (rs.next()) {
                    String tableName = DatabaseUtil.getTrimmedString(rs, 1);
                    //System.out.println("tableName="+tableName);
				 	if (tableName != null) {
						 list.add(tableName);
				 	}
                }

                return list;
            }
            catch (Exception e ) {
                e.printStackTrace();
                throw new Exception(e.toString());
            }
            finally {
                DatabaseUtil.close(rs);
                DatabaseUtil.close(stmt);
            }

	}


	/**
	 * Get the view names for a given connection object.
	 * @param conn the Connection object
	 * @return the list of view names as a List.
	 * @exception Failed to get the view names from the database.
	 */
	public static java.util.List getViewNames(java.sql.Connection conn)
		throws Exception {

		ResultSet rs = null;
		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 rs = meta.getTables(null, null, null, DB_VIEW_TYPES);
			 if (rs == null) {
				 return null;
			 }

			 java.util.List list = new java.util.ArrayList();
			 while (rs.next()) {
				 String viewName = DatabaseUtil.getTrimmedString(rs, COLUMN_NAME_TABLE_NAME);
				 if (viewName != null) {
					 list.add(viewName);
				 }

			 }

			 return list;
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw new Exception(e.toString());
		 }
		 finally {
			 DatabaseUtil.close(rs);
		 }

	}


	/**
	 * Get the Oracle view names for a given connection object.
	 * If you use the getViewNames() for an Oracle database, you
	 * will get lots of auxiliary views, which belong to the user,
	 * but user is not interested in seeing them.
	 *
	 * @param conn the Connection object
	 * @return the list of view names as a List.
	 * @exception Failed to get the view names from the database.
	 */
	public static java.util.List getOracleViewNames(java.sql.Connection conn)
		throws Exception {

            Statement stmt = null;
            ResultSet rs = null;
            try {

                stmt = conn.createStatement();
                rs = stmt.executeQuery(ORACLE_VIEWS);

                if (rs == null) {
                    return null;
                }

 			 	java.util.List list = new java.util.ArrayList();
                while (rs.next()) {
                    String viewName = DatabaseUtil.getTrimmedString(rs, 1);
                    System.out.println("viewName="+viewName);
				 	if (viewName != null) {
						 list.add(viewName);
				 	}
                }

                return list;
            }
            catch (Exception e ) {
                e.printStackTrace();
                throw new Exception(e.toString());
            }
            finally {
                DatabaseUtil.close(rs);
                DatabaseUtil.close(stmt);
            }

	}

	/**
	 * Get the table and view names for a given connection object.
	 * @param conn the Connection object
	 * @return the list of table and view names as a List.
	 * @exception Failed to get the table and view names from the database.
	 */
	 public static java.util.List getTableAndViewNames(java.sql.Connection conn)
		throws Exception {

		ResultSet rs = null;
		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 rs = meta.getTables(null, null, null, DB_MIXED_TYPES);
			 if (rs == null) {
				 return null;
			 }

			 java.util.List list = new java.util.ArrayList();
			 while (rs.next()) {
				 String tableOrViewName = DatabaseUtil.getTrimmedString(rs, COLUMN_NAME_TABLE_NAME);
				 System.out.println("getTableNames(): tableOrViewName="+tableOrViewName);
				 if (tableOrViewName != null) {
					 list.add(tableOrViewName);
				 }
			 }

			 return list;
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw new Exception(e.toString());
		 }
		 finally {
			 DatabaseUtil.close(rs);
		 }

	}

	/**
	 * Get the Oracle table and view names for a given connection object.
	 * If you use the getTableAndViewNames() for an Oracle database, you
	 * will get lots of auxiliary tables and views, which belong to the user,
	 * but user is not interested in seeing them.
	 *
	 * @param conn the Connection object
	 * @return the list of table and view  names as a List.
	 * @exception Failed to get the table and view names from the database.
	 */
	public static java.util.List getOracleTableAndViewNames(java.sql.Connection conn)
		throws Exception {

            Statement stmt = null;
            ResultSet rs = null;
            try {

                stmt = conn.createStatement();
                rs = stmt.executeQuery(ORACLE_TABLES_AND_VIEWS);

                if (rs == null) {
                    return null;
                }

 			 	java.util.List list = new java.util.ArrayList();
                while (rs.next()) {
                    String tableOrViewName = DatabaseUtil.getTrimmedString(rs, 1);
                    System.out.println("tableOrViewName="+tableOrViewName);
				 	if (tableOrViewName != null) {
						 list.add(tableOrViewName);
				 	}
                }

                return list;
            }
            catch (Exception e ) {
                e.printStackTrace();
                throw new Exception(e.toString());
            }
            finally {
                DatabaseUtil.close(rs);
                DatabaseUtil.close(stmt);
            }

	}

	/**
	 * Retrieves the name of this JDBC driver for a given connection object.
	 *
	 * @param conn the Connection object.
	 * @return the name of this JDBC driver.
	 * @exception Failed to get the name of this JDBC driver from the database.
	 */
	public static String getDriverName(java.sql.Connection conn)
		throws Exception {

		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 return meta.getDriverName();
		 }
         catch (Exception e ) {
                e.printStackTrace();
                throw new Exception(e.toString());
         }

	}
	/**
	 * Retrieves the JDBC driver version for a given connection object.
	 *
	 * @param conn the Connection object.
	 * @return the JDBC driver version.
	 * @exception Failed to get the the JDBC driver version from the database.
	 */
	public static String getDriverVersion(java.sql.Connection conn)
		throws Exception {

		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 return meta.getDriverVersion();
		 }
         catch (Exception e ) {
                e.printStackTrace();
                throw new Exception(e.toString());
         }

	}

	/**
	 * Retrieves a description of the given table's primary key columns.
	 * @param conn the Connection object
	 * @param tableName name of a table in the database.
	 * @return the list of column names (which form the Primary Key) as a List.
	 * @exception Failed to get the PrimaryKeys for a given table.
	 */
	 public static java.util.List getPrimaryKeys(java.sql.Connection conn,
	 											 String tableName)
		throws Exception {

		ResultSet rs = null;
		try {
			 if ((tableName == null) || (tableName.length() == 0)) {
				 return null;
			 }

			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 //
			 // The Oracle database stores its table names as Upper-Case,
			 // if you pass a table name in lowercase characters, it will not work.
			 // MySQL database does not care if table name is uppercase/lowercase.
			 //
			 rs = meta.getPrimaryKeys(null, null, tableName.toUpperCase());
			 if (rs == null) {
				 return null;
			 }

			 java.util.List list = new java.util.ArrayList();
			 while (rs.next()) {
				 String columnName = DatabaseUtil.getTrimmedString(rs, COLUMN_NAME_COLUMN_NAME);
				 System.out.println("getPrimaryKeys(): columnName="+columnName);
				 if (columnName != null) {
					 list.add(columnName);
				 }
			 }

			 return list;
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw new Exception(e.toString());
		 }
		 finally {
			 DatabaseUtil.close(rs);
		 }

	}

	/**
	 * Retrieves a description of the foreign key columns that
	 * reference the given table's primary key columns (the foreign
	 * keys exported by a table). They are ordered by FKTABLE_CAT,
	 * FKTABLE_SCHEM, FKTABLE_NAME, and KEY_SEQ.
	 *
	 * @param conn the Connection object
	 * @param catalog database catalog.
	 * @param schema database schema.
	 * @param tableName name of a table in the database.
	 * @return the list (as an XML string) of the foreign key columns
	 * that reference the given table's primary key columns
	 *
	 * @exception Failed to get the ExportedKeys for a given table.
	 */
	 public static String getExportedKeys(java.sql.Connection conn,
	 											  String catalog,
	 											  String schema,
	 											  String tableName)
		throws Exception {

		ResultSet rs = null;
		try {
			 if ((tableName == null) || (tableName.length() == 0)) {
				 return null;
			 }

			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 //
			 // The Oracle database stores its table names as Upper-Case,
			 // if you pass a table name in lowercase characters, it will not work.
			 // MySQL database does not care if table name is uppercase/lowercase.
			 //
			 rs = meta.getExportedKeys(catalog, schema, tableName.toUpperCase());
			 if (rs == null) {
				 return null;
			 }

			 StringBuffer buffer = new StringBuffer();
			 buffer.append("<exportedKeys>");
			 while (rs.next()) {
				 String fkTableName = DatabaseUtil.getTrimmedString(rs, "FKTABLE_NAME");
				 String fkColumnName = DatabaseUtil.getTrimmedString(rs, "FKCOLUMN_NAME");
				 int fkSequence = rs.getInt("KEY_SEQ");
				 //System.out.println("getExportedKeys(): fkTableName="+fkTableName);
				 //System.out.println("getExportedKeys(): fkColumnName="+fkColumnName);
				 //System.out.println("getExportedKeys(): fkSequence="+fkSequence);
			 	 buffer.append("<exportedKey>");
			 	 buffer.append("<catalog>");
			 	 buffer.append(catalog);
			 	 buffer.append("</catalog>");
			 	 buffer.append("<schema>");
			 	 buffer.append(schema);
			 	 buffer.append("</schema>");
			 	 buffer.append("<tableName>");
			 	 buffer.append(tableName);
			 	 buffer.append("</tableName>");
			 	 buffer.append("<fkTableName>");
			 	 buffer.append(fkTableName);
			 	 buffer.append("</fkTableName>");
			 	 buffer.append("<fkColumnName>");
			 	 buffer.append(fkColumnName);
			 	 buffer.append("</fkColumnName>");
			 	 buffer.append("<fkSequence>");
			 	 buffer.append(fkSequence);
			 	 buffer.append("</fkSequence>");
			 	 buffer.append("</exportedKey>");
			 }
			 buffer.append("</exportedKeys>");
			 return buffer.toString();
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw new Exception(e.toString());
		 }
		 finally {
			 DatabaseUtil.close(rs);
		 }

	}

	/**
	 * Retrieves a description of the primary key columns that are
	 * referenced by a table's foreign key columns (the primary keys
	 * imported by a table). They are ordered by PKTABLE_CAT,
	 * PKTABLE_SCHEM, PKTABLE_NAME, and KEY_SEQ.
	 *
	 * @param conn the Connection object
	 * @param catalog database catalog.
	 * @param schema database schema.
	 * @param tableName name of a table in the database.
	 * @return the list (as an XML string) of the primary key columns
	 * that are referenced by a table's foreign key columns
	 *
	 * @exception Failed to get the ExportedKeys for a given table.
	 */
	 public static String getImportedKeys(java.sql.Connection conn,
	 											  String catalog,
	 											  String schema,
	 											  String tableName)
		throws Exception {

		ResultSet rs = null;
		try {
			 if ((tableName == null) || (tableName.length() == 0)) {
				 return null;
			 }

			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 //
			 // The Oracle database stores its table names as Upper-Case,
			 // if you pass a table name in lowercase characters, it will not work.
			 // MySQL database does not care if table name is uppercase/lowercase.
			 //
			 rs = meta.getImportedKeys(catalog, schema, tableName.toUpperCase());
			 if (rs == null) {
				 return null;
			 }

			 StringBuffer buffer = new StringBuffer();
			 buffer.append("<importedKeys>");
			 while (rs.next()) {

				 String pkTableName = DatabaseUtil.getTrimmedString(rs, "PKTABLE_NAME");
				 String pkColumnName = DatabaseUtil.getTrimmedString(rs, "PKCOLUMN_NAME");
				 String fkTableName = DatabaseUtil.getTrimmedString(rs, "FKTABLE_NAME");
				 String fkColumnName = DatabaseUtil.getTrimmedString(rs, "FKCOLUMN_NAME");
				 int fkSequence = rs.getInt("KEY_SEQ");

       			 buffer.append("<importedKey>");
			 	 buffer.append("<catalog>");
			 	 buffer.append(catalog);
			 	 buffer.append("</catalog>");
			 	 buffer.append("<schema>");
			 	 buffer.append(schema);
			 	 buffer.append("</schema>");
			 	 buffer.append("<tableName>");
			 	 buffer.append(tableName);
			 	 buffer.append("</tableName>");
			 	 buffer.append("<pkTableName>");
			 	 buffer.append(pkTableName);
			 	 buffer.append("</pkTableName>");
			 	 buffer.append("<pkColumnName>");
			 	 buffer.append(pkColumnName);
			 	 buffer.append("</pkColumnName>");
			 	 buffer.append("<fkTableName>");
			 	 buffer.append(fkTableName);
			 	 buffer.append("</fkTableName>");
			 	 buffer.append("<fkColumnName>");
			 	 buffer.append(fkColumnName);
			 	 buffer.append("</fkColumnName>");
			 	 buffer.append("<fkSequence>");
			 	 buffer.append(fkSequence);
			 	 buffer.append("</fkSequence>");
			 	 buffer.append("</importedKey>");
			 }
			 buffer.append("</importedKeys>");
			 return buffer.toString();
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw new Exception(e.toString());
		 }
		 finally {
			 DatabaseUtil.close(rs);
		 }

	}

	/**
	 * Get column names and their associated types. The result
	 * is returned as an Hashtable, where key is "column name"
	 * and value is "column type". If table name is null/empty
	 * it returns null.
	 *
	 * @param conn the Connection object
	 * @param tableName name of a table in the database.
	 * @return an Hashtable, where key is "column name"
	 * and value is "column type".
	 * @exception Failed to get the column names for a given table.
	 */
	public static java.util.Hashtable getColumnNames(java.sql.Connection conn,
													 String tableName)
		throws Exception {

		ResultSet rsColumns = null;
		try {
			 if ((tableName == null) || (tableName.length() == 0)) {
				 return null;
			 }

			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 rsColumns = meta.getColumns(null, null, tableName.toUpperCase(), null);
			 Hashtable columns = new Hashtable();
			 while (rsColumns.next()) {
				 String columnType = rsColumns.getString(COLUMN_NAME_TYPE_NAME);
				 String columnName = rsColumns.getString(COLUMN_NAME_COLUMN_NAME);
				 if (columnName != null) {
					 columns.put(columnName, columnType);
			 	 }
			 }
			 return columns;
		 }
		 catch(Exception e) {
			 throw new Exception("Error: could not get column names: "+e.toString());
		 }
		 finally {
			 DatabaseUtil.close(rsColumns);
		 }
	}

	/**
	 * Get column names and their associated attributes (type,
	 * size, nullable, ordinal position). The result is returned
	 * as an XML (as a string object);  if table name is null/empty
	 * it returns null.
	 *
	 * @param conn the Connection object
	 * @param tableName name of a table in the database.
	 * @return an XML (column names and their associated attributes:
	 * type, size, nullable, ordinal position).
	 * @exception Failed to get the column details for a given table.
	 */
	public static String getColumnDetails(java.sql.Connection conn,
													 String tableName)
		throws Exception {

		ResultSet rsColumns = null;
		StringBuffer sb = new StringBuffer();
		try {
			 if ((tableName == null) || (tableName.length() == 0)) {
				 return null;
			 }

			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 rsColumns = meta.getColumns(null, null, tableName.toUpperCase(), null);
			 sb.append("<columns>");
			 while (rsColumns.next()) {

				 String columnType = rsColumns.getString(COLUMN_NAME_TYPE_NAME);
				 String columnName = rsColumns.getString(COLUMN_NAME_COLUMN_NAME);
				 int size = rsColumns.getInt(COLUMN_NAME_COLUMN_SIZE);
				 int nullable = rsColumns.getInt(COLUMN_NAME_NULLABLE);
				 int position = rsColumns.getInt(COLUMN_NAME_ORDINAL_POSITION);

				 sb.append("<column name=\"");
				 sb.append(columnName);
				 sb.append("\"><type>");
				 sb.append(columnType);
				 sb.append("</type><size>");
				 sb.append(size);
				 sb.append("</size><nullable>");
				 if (nullable == DatabaseMetaData.columnNullable) {
					 sb.append("true");
				 }
				 else {
					 sb.append("false");
				 }
				 sb.append("</nullable><position>");
				 sb.append(position);
				 sb.append("</position></column>");
			 }
			 sb.append("</columns>");
			 return sb.toString();
		 }
		 catch(Exception e) {
			 throw new Exception("Error: could not get column names: "+e.toString());
		 }
		 finally {
			 DatabaseUtil.close(rsColumns);
		 }
	}

	/**
	 * Get column names and their associated attributes (type,
	 * size, nullable). The result is returned as an XML (as
	 * a string object);  if table name is null/empty
	 * it returns null.
	 *
	 * @param conn the Connection object
	 * @param tableName name of a table in the database.
	 * @param includeType if true, then include type information.
	 * @param includeSize if true, then include size information.
	 * @param includeNullable if true, then include nullable information.
	 * @param includePosition if true, then include ordinal position information.
	 * @return an XML: column names and their associated attributes: type,
	 * size, nullable.
	 * @exception Failed to get the column details for a given table.
	 */
	public static String getColumnDetails(java.sql.Connection conn,
										  String tableName,
										  boolean includeType,
										  boolean includeSize,
										  boolean includeNullable,
										  boolean includePosition)
		throws Exception {

		ResultSet rsColumns = null;
		StringBuffer sb = new StringBuffer();
		try {
			 if ((tableName == null) || (tableName.length() == 0)) {
				 return null;
			 }

			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 rsColumns = meta.getColumns(null, null, tableName.toUpperCase(), null);
			 sb.append("<columns>");
			 while (rsColumns.next()) {

				 String columnName = rsColumns.getString(COLUMN_NAME_COLUMN_NAME);
				 sb.append("<column name=\"");
				 sb.append(columnName);
				 sb.append("\">");
				 if (includeType) {
					 String columnType = rsColumns.getString(COLUMN_NAME_TYPE_NAME);
					 sb.append("<type>");
				 	 sb.append(columnType);
				 	 sb.append("</type>");
				 }

				 if (includeSize) {
					 int size = rsColumns.getInt(COLUMN_NAME_COLUMN_SIZE);
					 sb.append("<size>");
				 	 sb.append(size);
				 	 sb.append("</size>");
				 }

				 if (includeNullable) {
				 	int nullable = rsColumns.getInt(COLUMN_NAME_NULLABLE);sb.append("<nullable>");
				 	if (nullable == DatabaseMetaData.columnNullable) {
					 	sb.append("true");
				 	}
				 	else {
					 	sb.append("false");
				 	}
				 	sb.append("</nullable>");
				 }

				 if (includePosition) {
					 int position = rsColumns.getInt(COLUMN_NAME_ORDINAL_POSITION);
					 sb.append("<position>");
					 sb.append(position);
					 sb.append("</position>");
				 }

				 sb.append("</column>");
			 }
			 sb.append("</columns>");
			 return sb.toString();
		 }
		 catch(Exception e) {
			 throw new Exception("Error: could not get column names: "+e.toString());
		 }
		 finally {
			 DatabaseUtil.close(rsColumns);
		 }
	}

	/**
	 * Get Table Privileges: retrieves a description of the access
	 * rights for each table available in a catalog. Note that a
	 * table privilege applies to one or more columns in the table.
	 * It would be wrong to assume that this privilege applies to
	 * all columns (this may be true for some systems but is not
	 * true for all.)  The result is returned as an XML (as a string
	 * object);  if table name is null/empty it returns null.
	 *
	 * In JDBC, Each privilige description has the following columns:
	 *
	 * TABLE_CAT String => table catalog (may be null)
	 * TABLE_SCHEM String => table schema (may be null)
	 * TABLE_NAME String => table name
	 * GRANTOR => grantor of access (may be null)
	 * GRANTEE String => grantee of access
	 * PRIVILEGE String => name of access (SELECT, INSERT,
	 *		UPDATE, REFRENCES, ...)
	 * IS_GRANTABLE String => "YES" if grantee is permitted to grant
	 *		to others; "NO" if not; null if unknown
	 *
	 *
	 * @param conn the Connection object
	 * @param catalogPattern a catalog pattern.
	 * @param schemaPattern a schema pattern.
	 * @param tableNamePattern a table name pattern; must match
	 *  the table name as it is stored in the database .
	 * @return an XML.
	 * @exception Failed to get the Get Table Privileges.
	 */
	public static String getTablePrivileges(java.sql.Connection conn,
											String catalogPattern,
											String schemaPattern,
										    String tableNamePattern)
		throws Exception {

		ResultSet privileges = null;
		StringBuffer sb = new StringBuffer();
		try {
			 if ((tableNamePattern == null) ||
			     (tableNamePattern.length() == 0)) {
				 return null;
			 }

			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 // The '_' character represents any single character.
			 // The '%' character represents any sequence of zero
			 // or more characters.
			 privileges = meta.getTablePrivileges(catalogPattern,
                                                  schemaPattern,
                                                  tableNamePattern);
			 sb.append("<privileges>");
			 while (privileges.next()) {

				 String catalog = privileges.getString(COLUMN_NAME_TABLE_CATALOG);
				 String schema = privileges.getString(COLUMN_NAME_TABLE_SCHEMA);
				 String tableName = privileges.getString(COLUMN_NAME_TABLE_NAME);
				 String privilege = privileges.getString(COLUMN_NAME_PRIVILEGE);
				 String grantor = privileges.getString(COLUMN_NAME_GRANTOR);
				 String grantee = privileges.getString(COLUMN_NAME_GRANTEE);
				 String isGrantable = privileges.getString(COLUMN_NAME_IS_GRANTABLE);

				 sb.append("<table name=\"");
				 sb.append(tableName);
				 sb.append("\"><catalog>");
				 sb.append(catalog);
				 sb.append("</catalog><schema>");
				 sb.append(schema);
				 sb.append("</schema><privilege>");
				 sb.append(privilege);
				 sb.append("</privilege><grantor>");
				 sb.append(grantor);
				 sb.append("</grantor><isGrantable>");
				 sb.append(isGrantable);
		 		 sb.append("</isGrantable><grantee>");
				 sb.append(grantee);
				 sb.append("</grantee></table>");
			 }
			 sb.append("</privileges>");
			 return sb.toString();
		 }
		 catch(Exception e) {
			 throw new Exception("Error: could not get table privileges: "+e.toString());
		 }
		 finally {
			 DatabaseUtil.close(privileges);
		 }
	}

	/**
	 * Get Table's Columns Privileges: retrieves a description
	 * of the access rights for a table's columns available in
	 * a catalog.  The result is returned as an XML (as a string
	 * object);  if table name is null/empty it returns null.
	 *
	 * In JDBC, Each privilige description has the following columns:
	 *
	 * TABLE_CAT String => table catalog (may be null)
	 * TABLE_SCHEM String => table schema (may be null)
	 * TABLE_NAME String => table name
	 * COLUMN_NAME String => column name
	 * GRANTOR => grantor of access (may be null)
	 * GRANTEE String => grantee of access
	 * PRIVILEGE String => name of access (SELECT, INSERT,
	 *		UPDATE, REFRENCES, ...)
	 * IS_GRANTABLE String => "YES" if grantee is permitted to grant
	 *		to others; "NO" if not; null if unknown
	 *
	 *
	 * @param conn the Connection object
	 * @param catalog a catalog.
	 * @param schema a schema.
	 * @param tableName a table name; must match
	 *  the table name as it is stored in the database .
	 * @param columnNamePattern a column name pattern.
	 * @return an XML.
	 * @exception Failed to get the Get Table's Column Privileges.
	 */
	public static String getColumnPrivileges(java.sql.Connection conn,
											String catalog,
											String schema,
										    String tableName,
										    String columnNamePattern)
		throws Exception {

		ResultSet privileges = null;
		StringBuffer sb = new StringBuffer();
		try {
			 if ((tableName == null) ||
			     (tableName.length() == 0)) {
				 return null;
			 }

			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 // The '_' character represents any single character.
			 // The '%' character represents any sequence of zero
			 // or more characters.
			 privileges = meta.getColumnPrivileges(catalog,
                                                   schema,
                                                   tableName,
                                                   columnNamePattern);
			 sb.append("<privileges>");
			 while (privileges.next()) {

				 String dbCatalog = privileges.getString(COLUMN_NAME_TABLE_CATALOG);
				 String dbSchema = privileges.getString(COLUMN_NAME_TABLE_SCHEMA);
				 String dbTable = privileges.getString(COLUMN_NAME_TABLE_NAME);
				 String dbColumn = privileges.getString(COLUMN_NAME_COLUMN_NAME);
				 String dbPrivilege = privileges.getString(COLUMN_NAME_PRIVILEGE);
				 String dbGrantor = privileges.getString(COLUMN_NAME_GRANTOR);
				 String dbGrantee = privileges.getString(COLUMN_NAME_GRANTEE);
				 String dbIsGrantable = privileges.getString(COLUMN_NAME_IS_GRANTABLE);

				 sb.append("<column name=\"");
				 sb.append(dbColumn);
				 sb.append("\" table=\"");
				 sb.append(tableName);
				 sb.append("\"><catalog>");
				 sb.append(dbCatalog);
				 sb.append("</catalog><schema>");
				 sb.append(dbSchema);
				 sb.append("</schema><privilege>");
				 sb.append(dbPrivilege);
				 sb.append("</privilege><grantor>");
				 sb.append(dbGrantor);
				 sb.append("</grantor><isGrantable>");
				 sb.append(dbIsGrantable);
		 		 sb.append("</isGrantable><grantee>");
				 sb.append(dbGrantee);
				 sb.append("</grantee></column>");
			 }
			 sb.append("</privileges>");
			 return sb.toString();
		 }
		 catch(Exception e) {
			 throw new Exception("Error: could not get table's column privileges: "+e.toString());
		 }
		 finally {
			 DatabaseUtil.close(privileges);
		 }
	}

	/**
	 * Retrieves a description of a table's optimal set of columns that
	 * uniquely identifies a row. They are ordered by SCOPE  The result
	 * is returned as an XML (as a serialized string object);  if table
	 * name is null/empty it returns null.
	 *
	 * @param conn the Connection object
	 * @param catalog  a catalog name; must match the catalog name
	 *		as it is stored in the database; "" retrieves those without
	 *		a catalog; null means that the catalog name should not be
	 *		used to narrow the search
	 * @param schema a schema name; must match the schema name as it
	 *		is stored in the database; "" retrieves those without a
	 *		schema; null means that the schema name should not be
	 *		used to narrow the search
	 * @param table  a table name; must match the table name as it
	 *		is stored in the database
	 * @param scope  the scope of interest; possible values are:
	 * <pre>
	 *		bestRowTemporary - very temporary, while using row
	 *		bestRowTransaction - valid for remainder of current transaction
	 *		bestRowSession - valid for remainder of current session
	 * </pre>
	 *
	 * @param nullable  include columns that are nullable.
	 * @return the result is returned as an XML (serialized as a String object)
	 * @exception Failed to get the Index Information.
	 */
     public static String getBestRowIdentifier(java.sql.Connection conn,
                                              String catalog,
                                              String schema,
                                              String table,
                                              int scope,
                                              boolean nullable)
		throws Exception {

		ResultSet rs = null;
		try {
			 if ((table == null) || (table.length() == 0)) {
				 return null;
			 }

			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 // The '_' character represents any single character.
			 // The '%' character represents any sequence of zero
			 // or more characters.
			 rs = meta.getBestRowIdentifier(catalog,
                                            schema,
                                            table,
                                            scope,
                                            nullable);
			 StringBuffer sb = new StringBuffer("<?xml version='1.0'>");
			 sb.append("<BestRowIdentifier>");
			 while (rs.next()) {

				 short actualScope = rs.getShort(COLUMN_NAME_SCOPE);
				 String columnName = rs.getString(COLUMN_NAME_COLUMN_NAME);
				 int dataType = rs.getInt(COLUMN_NAME_DATA_TYPE);
				 String typeName = rs.getString(COLUMN_NAME_TYPE_NAME);
				 int columnSize = rs.getInt(COLUMN_NAME_COLUMN_SIZE);
				 short decimalDigits  = rs.getShort(COLUMN_NAME_DECIMAL_DIGITS);
				 short pseudoColumn  = rs.getShort(COLUMN_NAME_PSEUDO_COLUMN);

				 sb.append("<RowIdentifier tableName=\"");
				 sb.append(table);
				 sb.append("\">");
				 appendXMLTag(sb, "scope", actualScope);
				 appendXMLTag(sb, "columnName", columnName);
				 appendXMLTag(sb, "dataType", dataType);
				 appendXMLTag(sb, "typeName", typeName);
				 appendXMLTag(sb, "columnSize", columnSize);
				 appendXMLTag(sb, "decimalDigits", decimalDigits);
				 appendXMLTag(sb, "pseudoColumn", pseudoColumn);
				 sb.append("</RowIdentifier>");
			 }
			 sb.append("</BestRowIdentifier>");
			 return sb.toString();
		 }
		 catch(Exception e) {
			 throw new Exception("Error: could not get table's Best Row Identifier: "+e.toString());
		 }
		 finally {
			 DatabaseUtil.close(rs);
		 }
	}

	/**
	 * Retrieves a description of the given table's indices and
	 * statistics.  The result is returned as an XML (as a string
	 * object);  if table name is null/empty it returns null.
	 *
	 *
	 * @param conn the Connection object
	 * @param catalog a catalog.
	 * @param schema a schema.
	 * @param tableName a table name; must match
	 *  the table name as it is stored in the database .
	 * @param unique when true, return only indices for unique values;
	 * when false, return indices regardless of whether unique or not
	 * @param approximate when true, result is allowed to reflect
	 * approximate or out of data values; when false, results are
	 * requested to be accurate
	 * @return an XML.
	 * @exception Failed to get the Index Information.
	 */
     public static String getIndexInformation(java.sql.Connection conn,
                                              String catalog,
                                              String schema,
                                              String tableName,
                                              boolean unique,
                                              boolean approximate)
		throws Exception {

		ResultSet indexInformation = null;
		try {
			 if ((tableName == null) ||
			     (tableName.length() == 0)) {
				 return null;
			 }

			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 // The '_' character represents any single character.
			 // The '%' character represents any sequence of zero
			 // or more characters.
			 indexInformation = meta.getIndexInfo(catalog,
                                                  schema,
                                                  tableName,
                                                  unique,
                                                  approximate);
			 StringBuffer sb = new StringBuffer("<?xml version='1.0'>");
			 sb.append("<indexInformation>");
			 while (indexInformation.next()) {

				 String dbCatalog = indexInformation.getString(COLUMN_NAME_TABLE_CATALOG);
				 String dbSchema = indexInformation.getString(COLUMN_NAME_TABLE_SCHEMA);
				 String dbTableName = indexInformation.getString(COLUMN_NAME_TABLE_NAME);
				 boolean dbNoneUnique = indexInformation.getBoolean(COLUMN_NAME_NON_UNIQUE);
				 String dbIndexQualifier = indexInformation.getString(COLUMN_NAME_INDEX_QUALIFIER);
				 String dbIndexName = indexInformation.getString(COLUMN_NAME_INDEX_NAME);
				 short dbType = indexInformation.getShort(COLUMN_NAME_TYPE);
				 short dbOrdinalPosition = indexInformation.getShort(COLUMN_NAME_ORDINAL_POSITION);
				 String dbColumnName = indexInformation.getString(COLUMN_NAME_COLUMN_NAME);
				 String dbAscOrDesc = indexInformation.getString(COLUMN_NAME_ASC_OR_DESC);
				 int dbCardinality = indexInformation.getInt(COLUMN_NAME_CARDINALITY);
				 int dbPages = indexInformation.getInt(COLUMN_NAME_PAGES);
				 String dbFilterCondition = indexInformation.getString(COLUMN_NAME_FILTER_CONDITION);

				 sb.append("<index name=\"");
				 sb.append(dbIndexName);
				 sb.append("\" table=\"");
				 sb.append(dbTableName);
				 sb.append("\" column=\"");
				 sb.append(dbColumnName);
				 sb.append("\">");
				 appendXMLTag(sb, "catalog", dbCatalog);
				 appendXMLTag(sb, "schema", dbSchema);
				 appendXMLTag(sb, "nonUnique", dbNoneUnique);
				 appendXMLTag(sb, "indexQualifier", dbIndexQualifier);
				 appendXMLTag(sb, "type", getIndexType(dbType));
				 appendXMLTag(sb, "ordinalPosition", dbOrdinalPosition);
				 appendXMLTag(sb, "ascendingOrDescending", dbAscOrDesc);
				 appendXMLTag(sb, "cardinality", dbCardinality);
				 appendXMLTag(sb, "pages", dbPages);
				 appendXMLTag(sb, "filterCondition", dbFilterCondition);
				 sb.append("</index>");
			 }
			 sb.append("</indexInformation>");
			 return sb.toString();
		 }
		 catch(Exception e) {
			 throw new Exception("Error: could not get table's Index Information: "+e.toString());
		 }
		 finally {
			 DatabaseUtil.close(indexInformation);
		 }
	}

	private static String getIndexType(short type) {

		switch (type) {
			case DatabaseMetaData.tableIndexClustered:
				return "tableIndexClustered";
			case DatabaseMetaData.tableIndexHashed:
				return "tableIndexHashed";
			case DatabaseMetaData.tableIndexOther:
				return "tableIndexOther";
			case DatabaseMetaData.tableIndexStatistic:
				return "tableIndexStatistic";
			default:
				return"tableIndexOther";
		}
	}

	/**
	 * Get Schemas(): Retrieves the schema names available
	 * in this database. The results are ordered by schema name.
	 *
	 *
	 * @param conn the Connection object.
	 * @return an XML.
	 * @exception Failed to get the Get Schemas.
	 */
	public static String getSchemas(java.sql.Connection conn)
		throws Exception {

		ResultSet schemas = null;
		StringBuffer sb = new StringBuffer();
		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 schemas = meta.getSchemas();
			 sb.append("<schemas>");
			 while (schemas.next()) {

				 String tableSchema = schemas.getString(1);    // "TABLE_SCHEM"
				 //String tableCatalog = schemas.getString(2); //"TABLE_CATALOG"

				 sb.append("<tableSchema>");
				 sb.append(tableSchema);
				 sb.append("</tableSchema>");
			 }
			 sb.append("</schemas>");
			 return sb.toString();
		 }
		 catch(Exception e) {
			 throw new Exception("Error: could not get schemas: "+e.toString());
		 }
		 finally {
			 DatabaseUtil.close(schemas);
		 }
	}
	/**
	 * Get Catalogs: Retrieves the catalog names available in
	 * this database.  The results are ordered by catalog name.
	 *
	 *
	 * @param conn the Connection object
	 * @return an XML.
	 * @exception Failed to get the Get Catalogs.
	 */
	public static String getCatalogs(java.sql.Connection conn)
		throws Exception {

		ResultSet catalogs = null;
		StringBuffer sb = new StringBuffer();
		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 catalogs = meta.getCatalogs();
			 sb.append("<catalogs>");
			 while (catalogs.next()) {
				 String catalog = catalogs.getString(1);  //"TABLE_CATALOG"
				 sb.append("<catalog>");
				 sb.append(catalog);
				 sb.append("</catalog>");
			 }
			 sb.append("</catalogs>");
			 return sb.toString();
		 }
		 catch(Exception e) {
			 throw new Exception("Error: could not get catalogs: "+e.toString());
		 }
		 finally {
			 DatabaseUtil.close(catalogs);
		 }
	}

	/**
	 * Listing Available SQL Types Used by a Database. This method
	 * retrieves the SQL data types supported by a database and driver.
	 *
	 *
	 * @param conn the Connection object
	 * @return an XML (as a String object).
	 * @exception Failed to get the Available SQL Types Used by a Database.
	 */
	 public static String getAvailableSqlTypes(Connection conn)
		throws Exception {
		ResultSet rs = null;
		try {
			// Get database meta data
			DatabaseMetaData meta = conn.getMetaData();
			if (meta == null) {
				 return null;
			}

			// Get type infornmation
			rs = meta.getTypeInfo();

			// Retrieve type info from the result set
			StringBuffer sb = new StringBuffer();
			sb.append("<sqlTypes>");
			while (rs.next()) {
				// Get the database-specific type name
				String typeName = rs.getString("TYPE_NAME");

				// Get the java.sql.Types type to which this
				// database-specific type is mapped
				short dataType = rs.getShort("DATA_TYPE");

				// Get the name of the java.sql.Types value.
				String jdbcTypeName = getJdbcTypeName(dataType);

				sb.append("<type name=\"");
				sb.append(typeName);
				sb.append("\" dataType=\"");
				sb.append(dataType);
				sb.append("\" jdbcTypeName=\"");
				sb.append(jdbcTypeName);
				sb.append("\"/>");
			}
			sb.append("</sqlTypes>");
			return sb.toString();
		}
		catch (SQLException e) {
			 throw new Exception("Error: could not the available SQL types used by a database.: "+e.toString());
		}
		finally {
			DatabaseUtil.close(rs);
		}
	}

	/**
	 * Listing Available ResultSet Types Used by a Database. This method
	 * retrieves the ResultSet Types supported by a database and driver.
	 *
	 *
	 * @param conn the Connection object
	 * @return an XML (as a String object).
	 * @exception Failed to get the Available ResultSet Types from Database.
	 */
	 public static String getAvailableResultSetTypes(Connection conn)
		throws Exception {
		try {
			// Get database meta data
			DatabaseMetaData meta = conn.getMetaData();
			if (meta == null) {
				 return null;
			}

			// Retrieve type info from the result set
			StringBuffer sb = new StringBuffer();
			sb.append("<ResultSetTypes>");

			if (meta.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY)) {
				sb.append("<type name=\"TYPE_FORWARD_ONLY\"/>");
			}
			if (meta.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)) {
				sb.append("<type name=\"TYPE_SCROLL_INSENSITIVE\"/>");
			}
			if (meta.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
				sb.append("<type name=\"TYPE_SCROLL_SENSITIVE\"/>");
			}

			sb.append("</ResultSetTypes>");
			return sb.toString();
		}
		catch (SQLException e) {
			 throw new Exception("Error: could not the available ResultSet types from database.: "+e.toString());
		}
	}


	/**
	 * Get the Name of a JDBC Type. This method implements a
	 * convenient method for converting a java.sql.Types integer
	 * value into a printable name. This method is useful for debugging.
	 * The method uses reflection to get all the field names from
	 * java.sql.Types. It then retrieves their values and creates a
	 * map of values to names.
     * This method returns the name of a JDBC type.
     * Returns null if jdbcType is not recognized.
     *
     * @param jdbcType the jdbc type as an interger
     * @return the equivalent JDBC type name
     */
     public static String getJdbcTypeName(int jdbcType) {
        // Return the JDBC type name
        return (String) JDBC_TYPE_NAME_MAP.get(new Integer(jdbcType));
     }

 	 private static String getMsSqlStoredProcedureNames(java.sql.Connection conn) {

		ResultSet rs = null;
		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 rs = meta.getProcedures(null, null, "%");
			 StringBuffer  buffer = new StringBuffer();

			 //System.out.println("--------------------------- Stored Procedure Names");
			 while (rs.next()) {
				 String spName = rs.getString("PROCEDURE_NAME");
				 String spType = getStoredProcedureType(rs.getInt("PROCEDURE_TYPE"));

				 //System.out.println("Stored Procedure Name: " + spName);
				 //System.out.println("Stored Procedure Type: " + spType);
				 //
				 // MS SQL Server appends ";number" to the end of the names
				 //	so, we will chop it
				 //
				 buffer.append("<sp><name>"+chopSemicolon(spName)+"</name><type>"+spType+"</type></sp>");
			 }

			 return buffer.toString();
		 }
		 catch(Exception e) {
			 return null;
		 }
		 finally {
			 try {
				 if (rs != null) {
					 rs.close();
				 }
			 }
			 catch(Exception e){
				 // ignore
			 }
		}

	}

	private static String chopSemicolon(String spName) {
		 if ((spName == null) || (spName.length() == 0)) {
			 return spName;
		 }

		 int semicolonPosition = spName.indexOf(";");
		 if (semicolonPosition == -1) {
			 // then semicolon not found
			 return spName;
		 }
		 else {
			 return  spName.substring(0, semicolonPosition);
		 }
	}

	/**
	 * Get database product name and version information.
	 * This method calls 4 methods (getDatabaseMajorVersion(),
	 * getDatabaseMinorVersion(), getDatabaseProductName(),
	 * getDatabaseProductVersion()) to get the required information
	 * and it represents the information as an XML.
	 *
	 * @param conn the Connection object
	 * @return database product name and version information
	 * as an XML document (represented as a String object).
	 *
	 */
    public static String getDatabaseInformation(java.sql.Connection conn)
    	throws Exception {

		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 StringBuffer sb = new StringBuffer("<?xml version='1.0'>");
			 sb.append("<DatabaseInformation>");

		  	 // Oracle (and some other vendors) do not support
		  	 // some the following methods; therefore, we need
		  	 // to use try-catch block.
		  	 try {
				 int majorVersion = meta.getDatabaseMajorVersion();
				 appendXMLTag(sb, "majorVersion", majorVersion);
			 }
			 catch(Exception e) {
				 appendXMLTag(sb, "majorVersion", "unsupported feature");
			 }

		  	 try {
				 int minorVersion = meta.getDatabaseMinorVersion();
				 appendXMLTag(sb, "minorVersion", minorVersion);
			 }
			 catch(Exception e) {
				 appendXMLTag(sb, "minorVersion", "unsupported feature");
			 }

			 String productName = meta.getDatabaseProductName();
			 String productVersion = meta.getDatabaseProductVersion();
		  	 appendXMLTag(sb, "productName", productName);
		  	 appendXMLTag(sb, "productVersion", productVersion);
			 sb.append("</DatabaseInformation>");

			 return sb.toString();
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw new Exception("could not get the database information:"+
			 					e.toString());
		 }
	}


	/**
	 * Get driver name and version information.
	 * This method calls 4 methods (getDriverName(),
	 * getDriverVersion(), getJDBCMajorVersion(),
	 * getJDBCMinorVersion()) to get the required information
	 * and it returns the information as an XML.
	 *
	 * @param conn the Connection object
	 * @return driver name and version information
	 * as an XML document (represented as a String object).
	 *
	 */
    public static String getDriverInformation(java.sql.Connection conn)
    	throws Exception {

		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 StringBuffer sb = new StringBuffer("<?xml version='1.0'>");
			 sb.append("<DriverInformation>");

		  	 // Oracle (and some other vendors) do not support
		  	 // some the following methods; therefore, we need
		  	 // to use try-catch block.
		  	 try {
				 int jdbcMajorVersion = meta.getJDBCMajorVersion();
				 appendXMLTag(sb, "jdbcMajorVersion", jdbcMajorVersion);
			 }
			 catch(Exception e) {
				 appendXMLTag(sb, "jdbcMajorVersion", "unsupported feature");
			 }

		  	 try {
				 int jdbcMinorVersion = meta.getJDBCMinorVersion();
				 appendXMLTag(sb, "jdbcMinorVersion", jdbcMinorVersion);
			 }
			 catch(Exception e) {
				 appendXMLTag(sb, "jdbcMinorVersion", "unsupported feature");
			 }

			 String driverName = meta.getDriverName();
			 String driverVersion = meta.getDriverVersion();
		  	 appendXMLTag(sb, "driverName", driverName);
		  	 appendXMLTag(sb, "driverVersion", driverVersion);
			 sb.append("</DriverInformation>");

			 return sb.toString();
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw new Exception("could not get the database information:"+
			 					e.toString());
		 }
	}


	/**
	 * Get the stored-procedures names.
	 * @param conn the Connection object
	 * @return a table of stored procedures names
	 * as an XML document (represented as a String object).
	 * Each element of XML document will have the name and
	 * type of a stored procedure.
	 *
	 */
    public static String getStoredProcedureNames
    	(java.sql.Connection conn,
    	 String catalog,
         String schemaPattern,
         String procedureNamePattern)
    	throws Exception {

		ResultSet rs = null;
		try {
			 DatabaseMetaData meta = conn.getMetaData();
			 if (meta == null) {
				 return null;
			 }

			 rs = meta.getProcedures(catalog, schemaPattern, procedureNamePattern);
			 StringBuffer sb = new StringBuffer();
			 sb.append("<storedProcedures>");

			 while (rs.next()) {
				 String spName = rs.getString("PROCEDURE_NAME");
				 String spType = getStoredProcedureType(rs.getInt("PROCEDURE_TYPE"));
				 sb.append("<storedProcedure name=\"");
				 sb.append(spName);
				 sb.append("\" type=\"");
				 sb.append(spType);
				 sb.append("\"/>");
			 }
			 sb.append("</storedProcedures>");
			 return sb.toString();
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw new Exception("could not get the names of stored procedures:"+
			 					e.toString());
		 }
		 finally {
			 DatabaseUtil.close(rs);
		 }
	}

	private static String getStoredProcedureType(int spType) {
		if (spType == DatabaseMetaData.procedureReturnsResult) {
			return STORED_PROCEDURE_RETURNS_RESULT;
		}
		else if (spType == DatabaseMetaData.procedureNoResult) {
			return STORED_PROCEDURE_NO_RESULT;
		}
		else {
			return STORED_PROCEDURE_RESULT_UNKNOWN;
		}
	}

    /**
     * Retrieves a description of the given catalog's stored
     * procedure parameter and result columns.
     *
	 * @param conn the Connection object
	 * @param catalog a catalog.
	 * @param schemaPattern a schema pattern.
	 * @param procedureNamePattern name of a stored procedure
	 * @param columnNamePattern a column name pattern.
	 * @return an XML.
	 * @throws Exception Failed to get the stored procedure's signature.
	 */
	public static String getStoredProcedureSignature(
			java.sql.Connection conn,
    		String catalog,
         	String schemaPattern,
        	String procedureNamePattern,
        	String columnNamePattern)

    	throws Exception {

		  // Get DatabaseMetaData
		  DatabaseMetaData dbMetaData = conn.getMetaData();
		  if (dbMetaData == null) {
			  return null;
		  }

		  ResultSet rs = dbMetaData.getProcedureColumns(catalog,
		  										  schemaPattern,
		  										  procedureNamePattern,
		  										  columnNamePattern);

		  StringBuffer sb = new StringBuffer("<?xml version='1.0'>");
		  sb.append("<stored_procedures_signature>");
		  while(rs.next()) {
			  // get stored procedure metadata
			  String procedureCatalog     = rs.getString(1);
			  String procedureSchema      = rs.getString(2);
			  String procedureName        = rs.getString(3);
			  String columnName           = rs.getString(4);
			  short  columnReturn         = rs.getShort(5);
			  int    columnDataType       = rs.getInt(6);
			  String columnReturnTypeName = rs.getString(7);
			  int    columnPrecision      = rs.getInt(8);
			  int    columnByteLength     = rs.getInt(9);
			  short  columnScale          = rs.getShort(10);
			  short  columnRadix          = rs.getShort(11);
			  short  columnNullable       = rs.getShort(12);
			  String columnRemarks        = rs.getString(13);

		  	  sb.append("<storedProcedure name=\"");
		  	  sb.append(procedureName);
		  	  sb.append("\">");
		  	  appendXMLTag(sb, "catalog", procedureCatalog);
		  	  appendXMLTag(sb, "schema", procedureSchema);
		  	  appendXMLTag(sb, "columnName", columnName);
		  	  appendXMLTag(sb, "columnReturn", getColumnReturn(columnReturn));
		  	  appendXMLTag(sb, "columnDataType", columnDataType);
		  	  appendXMLTag(sb, "columnReturnTypeName", columnReturnTypeName);
		  	  appendXMLTag(sb, "columnPrecision", columnPrecision);
		  	  appendXMLTag(sb, "columnByteLength", columnByteLength);
		  	  appendXMLTag(sb, "columnScale", columnScale);
		  	  appendXMLTag(sb, "columnRadix", columnRadix);
		  	  appendXMLTag(sb, "columnNullable", columnNullable);
		  	  appendXMLTag(sb, "columnRemarks", columnRemarks);
		  	  sb.append("</storedProcedure>");

			  System.out.println("======================================");
			  System.out.println("procedureCatalog=" + procedureCatalog);
			  System.out.println("procedureSchema=" + procedureSchema);
			  System.out.println("procedureName=" + procedureName);
			  System.out.println("columnName=" + columnName);
			  System.out.println("columnReturn=" + columnReturn);
			  System.out.println("columnDataType=" + columnDataType);
			  System.out.println("columnReturnTypeName=" + columnReturnTypeName);
			  System.out.println("columnPrecision=" + columnPrecision);
			  System.out.println("columnByteLength=" + columnByteLength);
			  System.out.println("columnScale=" + columnScale);
			  System.out.println("columnRadix=" + columnRadix);
			  System.out.println("columnNullable=" + columnNullable);
			  System.out.println("columnRemarks=" + columnRemarks);
			  System.out.println("======================================");
		  }
		  sb.append("</stored_procedures_signature>");

		  // Close database resources
		  rs.close();
		  //conn.close();
		  return sb.toString();
	}

	private static String getColumnReturn(short columnReturn) {

		  switch(columnReturn) {
			 case DatabaseMetaData.procedureColumnIn:
				  return "In";
			 case DatabaseMetaData.procedureColumnOut:
				  return "Out";
			 case DatabaseMetaData.procedureColumnInOut:
				  return "In/Out";
			 case DatabaseMetaData.procedureColumnReturn:
				  return "return value";
			 case DatabaseMetaData.procedureColumnResult:
				  return "return ResultSet";
			 default:
			   return "unknown";
		  }

	}

    public static boolean indexExists(java.sql.Connection conn,
                                      String catalog,
                                      String schema,
                                      String tableName,
                                      String indexName)
         throws Exception {

		 if ((tableName == null) || (tableName.length() == 0) ||
		 	 (indexName == null) || (indexName.length() == 0)) {
			  return false;
		 }

		 DatabaseMetaData dbMetaData = conn.getMetaData();
		 if (dbMetaData == null) {
			  return false;
		 }

		 ResultSet rs = dbMetaData.getIndexInfo(catalog,
												schema,
												tableName,
												false,
												true);
		 while (rs.next()) {
			 String dbIndexName = rs.getString(COLUMN_NAME_INDEX_NAME);
			 if (indexName.equals(dbIndexName)) {
				return true;
			 }
		 }
		 return false;

    }

	private static void appendXMLTag(StringBuffer buffer,
	                                 String tagName,
	                                 boolean value) {
			buffer.append("<");
			buffer.append(tagName);
			buffer.append(">");
			buffer.append(value);
			buffer.append("</");
			buffer.append(tagName);
			buffer.append(">");
	}

	private static void appendXMLTag(StringBuffer buffer,
	                                 String tagName,
	                                 int value) {
			buffer.append("<");
			buffer.append(tagName);
			buffer.append(">");
			buffer.append(value);
			buffer.append("</");
			buffer.append(tagName);
			buffer.append(">");
	}

	private static void appendXMLTag(StringBuffer buffer,
	                                 String tagName,
	                                 String value) {
			buffer.append("<");
			buffer.append(tagName);
			buffer.append(">");
			buffer.append(value);
			buffer.append("</");
			buffer.append(tagName);
			buffer.append(">");
	}
}
