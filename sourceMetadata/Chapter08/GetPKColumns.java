import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.PrintWriter;
import java.io.IOException;
import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class GetPKColumns extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		ResultSet primaryKeys = null;
		Connection conn = null;
		try {
			String dbVendor = request.getParameter("vendor").trim();
			String table = request.getParameter("table").trim();
			String outputFormat = request.getParameter("format").trim();
			conn = VeryBasicConnectionManager.getConnection(dbVendor);
			primaryKeys = getPrimaryKeys(conn, table);
			if (outputFormat.equals("xml")) {
				printXML(response, primaryKeys);
			}
			else {
				printHTML(response, primaryKeys);
			}
		}
		catch(Exception e) {
			printError(response, e.getMessage());
		}
		finally {
			DatabaseUtil.close(primaryKeys);
			DatabaseUtil.close(conn);
		}
	} // end doGet

	private static void printHTML(HttpServletResponse response, ResultSet primaryKeys)
		throws Exception {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuilder buffer = new StringBuilder();
		buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
		buffer.append("<TR><TH>Catalog</TH>");
		buffer.append("<TH>Schema</TH>");
		buffer.append("<TH>Table Name</TH>");
		buffer.append("<TH>Column Name</TH>");
		buffer.append("<TH>Key Sequence</TH>");
		buffer.append("<TH>PK Name</TH></TR>");
		while (primaryKeys.next()) {
			buffer.append("<TR><TD>");
			buffer.append(primaryKeys.getString("TABLE_CAT"));
			buffer.append("</TD><TD>");
			buffer.append(primaryKeys.getString("TABLE_SCHEM"));
			buffer.append("</TD><TD>");
			buffer.append(primaryKeys.getString("TABLE_NAME"));
			buffer.append("</TD><TD>");
			buffer.append(primaryKeys.getString("COLUMN_NAME"));
			buffer.append("</TD><TD>");
			buffer.append(primaryKeys.getShort("KEY_SEQ"));
			buffer.append("</TD><TD>");
			buffer.append(primaryKeys.getString("PK_NAME"));
			buffer.append("</TD></TR>");
		}
		buffer.append("</table></body></html>");
		out.println(buffer.toString());
	}

	private static void printXML(HttpServletResponse response, ResultSet primaryKeys)
		throws Exception {
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		StringBuilder buffer = new StringBuilder();
		buffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		buffer.append("<primaryKeys>");
		while (primaryKeys.next()) {
			buffer.append("<pkColumn><catalog>");
			buffer.append(primaryKeys.getString("TABLE_CAT"));
			buffer.append("</catalog><schema>");
			buffer.append(primaryKeys.getString("TABLE_SCHEM"));
			buffer.append("</schema><tableName>");
			buffer.append(primaryKeys.getString("TABLE_NAME"));
			buffer.append("</tableName><columnName>");
			buffer.append(primaryKeys.getString("COLUMN_NAME"));
			buffer.append("</columnName><keySEQ>");
			buffer.append(primaryKeys.getShort("KEY_SEQ"));
			buffer.append("</keySEQ><pkName>");
			buffer.append(primaryKeys.getString("PK_NAME"));
			buffer.append("</pkName></pkColumn>");
		}
		buffer.append("</primaryKeys>");
		out.println(buffer.toString());
	}

	private static void printError(HttpServletResponse response, String message) {
		try {
			PrintWriter out = response.getWriter();
			StringBuffer buffer = new StringBuffer();
			buffer.append("<html><body>");
			buffer.append(message);
			buffer.append("</body></html>");
			out.println(buffer);
		}
		catch(Exception ignore) {
		}
	}

	/**
	* Retrieves a description of the given table's primary key columns.
	* @param conn the Connection object
	* @param tableName name of a table in the database.
	* @return the list of PK columns as a ResultSet object
	* @exception Failed to get the Primary Keys for a given table.
	*/
	public static ResultSet getPrimaryKeys(Connection conn, String tableName)
		throws Exception {
		if ((tableName == null) || (tableName.length() == 0)) {
			return null;
		}
		DatabaseMetaData meta = conn.getMetaData();
		if (meta == null) {
			return null;
		}
		//
		// The Oracle database stores its table names as
		// uppercase; if you pass a table name in lowercase
		// characters, it will not work. MySQL database does
		// not care if the table name is uppercase/lowercase.
		//
		return meta.getPrimaryKeys(null, null, tableName.toUpperCase());
	}
}
