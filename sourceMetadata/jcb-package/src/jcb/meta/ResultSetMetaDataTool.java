package jcb.meta;

import java.util.*;
import java.io.*;
import java.sql.*;
import javax.sql.*;

import javax.sql.rowset.WebRowSet;
import com.sun.rowset.WebRowSetImpl;

import jcb.db.*;
import jcb.util.*;

/**
 * This class provides class-level methods for getting database metadata.
 *
 */
 public class ResultSetMetaDataTool {

	private static final String XML_METADATA_TAG_COLUMN = "column";
	private static final String XML_METADATA_TAG_CATALOG_NAME = "catalog";
	private static final String XML_METADATA_TAG_SCHEMA_NAME = "schema";
	private static final String XML_METADATA_TAG_COLUMN_DISPLAY_SIZE = "columnDisplaySize";
	private static final String XML_METADATA_TAG_COLUMN_LABEL = "columnLabel";
	private static final String XML_METADATA_TAG_COLUMN_NAME = "columnName";
	private static final String XML_METADATA_TAG_COLUMN_TYPE_NAME = "columnTypeName";
	private static final String XML_METADATA_TAG_COLUMN_CLASS_NAME = "columnClassName";
	private static final String XML_METADATA_TAG_COLUMN_TYPE = "columnType";
	private static final String XML_METADATA_TAG_TABLE_NAME = "tableName";
	private static final String XML_METADATA_TAG_PRECISION = "precision";
	private static final String XML_METADATA_TAG_SCALE = "scale";
	private static final String XML_METADATA_TAG_IS_AUTO_INCREMENT = "isAutoIncrement";
	private static final String XML_METADATA_TAG_IS_CURRENCY = "isCurrency";
	private static final String XML_METADATA_TAG_IS_READ_ONLY = "isReadOnly";
	private static final String XML_METADATA_TAG_IS_SEARCHABLE = "isSearchable";
	private static final String XML_METADATA_TAG_IS_DEFINITELY_WRITABLE = "isDefinitelyWritable";
	private static final String XML_METADATA_TAG_IS_WRITABLE = "isWritable";
	private static final String XML_METADATA_TAG_IS_NULLABLE = "isNullable";
	private static final String XML_METADATA_TAG_IS_SIGNED = "isSigned";
	private static final String XML_METADATA_TAG_IS_CASE_SENSITIVE = "isCaseSensitive";


	/**
	 * Dump ResultSet. For Debugging purposes.
	 * @param rs the ResultSet object
	 * @exception Failed to dump the ResultSet.
	 */
    public static void dumpResultSet(ResultSet rs)
        throws SQLException {

        // Get the ResultSetMetaData.  This will
        // be used for the column headings
        ResultSetMetaData rsmd = rs.getMetaData();
        if (rsmd == null) {
			return;
		}

        // Get the number of columns in the result set
        int numberOfColumns = rsmd.getColumnCount();

        // Display column headings

        for (int i=1; i<=numberOfColumns; i++) {
            if (i > 1) System.out.print(",");
            System.out.print(rsmd.getColumnLabel(i));
        }
        System.out.println("");

        // Display data, fetching until end of the result set

        while (rs.next ()) {

            // Loop through each column, getting the
            // column data and displaying

            for (int i=1; i<=numberOfColumns; i++) {
                if (i > 1) System.out.print(",");
                System.out.print(rs.getString(i));
            }
            System.out.println("");

            // Fetch the next result set row
        }
    }

	/**
	 * Dump ResultSet. For Debugging purposes.
	 * @param rs the ResultSet object
	 * @exception Failed to dump the ResultSet.
	 */
	 public static void dumpResultSet2(ResultSet rs)
		throws Exception {

        try {
            ResultSetMetaData meta = rs.getMetaData();
            if (meta == null) {
				return;
			}

            int numbers = 0;
            int columns = meta.getColumnCount();
            for (int i=1; i<=columns; i++) {
                System.out.println (meta.getColumnLabel(i) + "\t"
                              + meta.getColumnTypeName(i));
                if (meta.isSigned(i)) {
					// is it a signed number?
                    numbers++;
                }
            }
            System.out.println ("Columns: " + columns + " Numeric: " + numbers);
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
	 * Get table's metadata (column names and their associated
	 * attributes (type, size, nullable). The result is returned
	 * as an XML (as a serialized string object);  if table name
	 * is null/empty it returns null.
	 *
	 * This method uses the sun.jdbc.rowset.WebRowSet class to
	 * extract table's metadata according to the DTD defined at:
	 * http://java.sun.com/j2ee/dtds/RowSet.dtd
	 * The WebRowSet class is designed to return data & metadata
	 * for a ResultSet, but we will limit it just to return the
	 * meta data. We will pass the following query, which returns
	 * only metadata (zero number of data records returned).
	 *
	 *      select * from TABLE-NAME where 1 = 0
	 *
	 * By using the WebRowSet class, we donot need to hand craft
	 * the generated XML.
	 *
	 * @param conn the Connection object.
	 * @param tableName the table name.
	 * @return result set's meta data as an XML as string object;
	 * this meta data includes column names and their associated
	 * attributes: type, size, nullable.
	 * @exception Failed to get the result set's meta data as an XML.
	 */
	public static String getTableMetaDataUsingWebRowSet(Connection conn,
										                String tableName)
		throws Exception {

		if ((conn == null) ||
			(tableName == null) ||
			(tableName.length() == 0)) {
			return null;
		}

		String query = "select * from "+ tableName.toUpperCase() + " where 1 = 0";

		WebRowSet webRS = new WebRowSetImpl();
		webRS.setCommand(query);
		webRS.execute(conn);
		if (webRS == null) {
			return null;
		}

		// convert xml to a String object
        StringWriter sw = new StringWriter();
        webRS.writeXml(sw);
        return sw.toString();
	}

	/**
	 * Get table's metadata (column names and their associated attributes
	 * (type, size, nullable). The result is returned as an XML
	 * (as a string object);  if table name is null/empty
	 * it returns null.
	 *
	 * @param conn the Connection object.
	 * @param tableName the table name.
	 * @return result set's meta data as an XML as string object;
	 * this meta data includes column names and their associated
	 * attributes: type, size, nullable.
	 * @exception Failed to get the result set's meta data as an XML.
	 */
	public static String getTableMetaData(Connection conn,
										  String tableName)
		throws Exception {

		if ((conn == null) ||
			(tableName == null) ||
			(tableName.length() == 0)) {
			return null;
		}

		String query = "select * from "+ tableName.toUpperCase() + " where 1 = 0";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

        // retrieves the number, types and properties
        // of this ResultSet object's columns.
		return getResultSetMetaData(rs);
	}



	/**
	 * Get column names and their associated attributes (type,
	 * size, nullable). The result is returned as an XML (as
	 * a string object);  if the result set  is null/empty
	 * it returns null.
	 *
	 * @param rs the result set (ResultSet) object.
	 * @return result set's meta data as an XML as string object;
	 * this meta data includes column names and their associated
	 * attributes: type, size, nullable.
	 * @exception Failed to get the result set's meta data as an XML.
	 */
	public static String getResultSetMetaData(ResultSet rs)
		throws Exception {

		if (rs == null ) {
			return null;
		}

        // retrieves the number, types and properties
        // of this ResultSet object's columns.
		ResultSetMetaData rsMetaData = rs.getMetaData();
		if (rsMetaData == null ) {
			return null;
		}

		StringBuffer sb = new StringBuffer("<?xml version='1.0'>");
		sb.append("<resultSetMetaData columnCount=\"");
		int numberOfColumns = rsMetaData.getColumnCount();
		sb.append(numberOfColumns);
		sb.append("\">");

        for (int i=1; i<=numberOfColumns; i++) {
            sb.append(getColumnMetaData(rsMetaData, i));
        }

		sb.append("</resultSetMetaData>");
		return sb.toString();

	}

	/**
	 * Get specific column's associated attributes
	 * (type, size, nullable). The result is returned
	 * as an XML (represented as a String object).
	 * XML attributes (as constants) are prefixed
	 * with the "XML_METADATA_TAG_".
	 *
	 * @param rsMetaData  the result set meta data object.
	 * @param columnNumber  the column number.
	 * @return result set's meta data as an XML as
	 *  string object; this meta data includes
	 *  column names and their associated attributes:
	 *  type, size, nullable.
	 * @exception Failed to get the result set's
	 *  meta data as an XML.
	 */
	private static String getColumnMetaData
			(ResultSetMetaData rsMetaData,
			 int columnNumber)
		throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("<columnMetaData ");
		append(sb, XML_METADATA_TAG_COLUMN, columnNumber);

  		// indicates the designated column's normal
  		// maximum width in characters
  		append(sb, XML_METADATA_TAG_COLUMN_DISPLAY_SIZE,
  			rsMetaData.getColumnDisplaySize(columnNumber));

  		// gets the designated column's suggested title
  		// for use in printouts and displays.
  		append(sb, XML_METADATA_TAG_COLUMN_LABEL,
  			rsMetaData.getColumnLabel(columnNumber));

  		// get the designated column's name.
  		append(sb, XML_METADATA_TAG_COLUMN_NAME,
  			rsMetaData.getColumnName(columnNumber));

  		// get the designated column's SQL type.
  		append(sb, XML_METADATA_TAG_COLUMN_TYPE,
  			rsMetaData.getColumnType(columnNumber));

  		// get the designated column's SQL type name.
  		append(sb, XML_METADATA_TAG_COLUMN_TYPE_NAME,
  			rsMetaData.getColumnTypeName(columnNumber));

  		// get the designated column's class name.
  		append(sb, XML_METADATA_TAG_COLUMN_CLASS_NAME,
  			rsMetaData.getColumnClassName(columnNumber));

  		// get the designated column's table name.
  		append(sb, XML_METADATA_TAG_TABLE_NAME,
  			rsMetaData.getTableName(columnNumber));

		// get the designated column's number of decimal digits.
  		append(sb, XML_METADATA_TAG_PRECISION,
  			rsMetaData.getPrecision(columnNumber));

		// gets the designated column's number of
		// digits to right of the decimal point.
  		append(sb, XML_METADATA_TAG_SCALE,
  			rsMetaData.getScale(columnNumber));

		// indicates whether the designated column is
		// automatically numbered, thus read-only.
  		append(sb, XML_METADATA_TAG_IS_AUTO_INCREMENT,
  			rsMetaData.isAutoIncrement(columnNumber));

		// indicates whether the designated column is a cash value.
  		append(sb, XML_METADATA_TAG_IS_CURRENCY,
  			rsMetaData.isCurrency(columnNumber));

		// indicates whether a write on the designated
		// column will succeed.
  		append(sb, XML_METADATA_TAG_IS_WRITABLE,
  			rsMetaData.isWritable(columnNumber));

		// indicates whether a write on the designated
		// column will definitely succeed.
  		append(sb, XML_METADATA_TAG_IS_DEFINITELY_WRITABLE,
  			rsMetaData.isDefinitelyWritable(columnNumber));

		// indicates the nullability of values
		// in the designated column.
  		append(sb, XML_METADATA_TAG_IS_NULLABLE,
  			rsMetaData.isNullable(columnNumber));

		// Indicates whether the designated column
		// is definitely not writable.
  		append(sb, XML_METADATA_TAG_IS_READ_ONLY,
  			rsMetaData.isReadOnly(columnNumber));

		// Indicates whether a column's case matters
		// in the designated column.
  		append(sb, XML_METADATA_TAG_IS_CASE_SENSITIVE,
  			rsMetaData.isCaseSensitive(columnNumber));

		// Indicates whether a column's case matters
		// in the designated column.
  		append(sb, XML_METADATA_TAG_IS_SEARCHABLE,
  			rsMetaData.isSearchable(columnNumber));

		// indicates whether values in the designated
		// column are signed numbers.
  		append(sb, XML_METADATA_TAG_IS_SIGNED,
  			rsMetaData.isSigned(columnNumber));

		// Gets the designated column's table's catalog name.
  		append(sb, XML_METADATA_TAG_CATALOG_NAME,
  			rsMetaData.getCatalogName(columnNumber));

		// Gets the designated column's table's schema name.
  		append(sb, XML_METADATA_TAG_SCHEMA_NAME,
  			rsMetaData.getSchemaName(columnNumber));

		sb.append("/>");
		return sb.toString();

	}

	/**
	 * Append attribute=value to the string buffer denoted by sb.
	 * @param sb the string buffer.
	 * @param attribute the attribute name.
	 * @param value the value of the attribute.
	 */
	private static void append(StringBuffer sb,
	                           String attribute,
	                           String value) {
		sb.append(attribute);
		sb.append("=\"");
		sb.append(value);
		sb.append("\" ");
	}

	/**
	 * Append attribute=value to the string buffer denoted by sb.
	 * @param sb the string buffer.
	 * @param attribute the attribute name.
	 * @param value the value of the attribute.
	 */
	private static void append(StringBuffer sb,
	                           String attribute,
	                           int value) {
		sb.append(attribute);
		sb.append("=\"");
		sb.append(value);
		sb.append("\" ");
	}

	/**
	 * Append attribute=value to the string buffer denoted by sb.
	 * @param sb the string buffer.
	 * @param attribute the attribute name.
	 * @param value the value of the attribute.
	 */
	private static void append(StringBuffer sb,
	                           String attribute,
	                           boolean value) {
		sb.append(attribute);
		sb.append("=\"");
		sb.append(value);
		sb.append("\" ");
	}

}