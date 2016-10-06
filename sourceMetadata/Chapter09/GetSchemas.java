import java.io.PrintWriter;
import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.List;
import java.util.ArrayList;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class GetSchemas extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		Connection conn = null;
		try {
			String dbVendor = request.getParameter("vendor").trim();
			String outputFormat = request.getParameter("format").trim();
			conn = VeryBasicConnectionManager.getConnection(dbVendor);
			List<String> schemas = getSchemas(conn);
			if (outputFormat.equals("xml")) {
				printXML(response, schemas);
			}
			else {
				printHTML(response, schemas);
			}
		}
		catch(Exception e) {
			printError(response, e.getMessage());
		}
		finally {
			DatabaseUtil.close(conn);
		}
	} // end doGet

	private static void printHTML(HttpServletResponse response, List<String> schemas)
		throws Exception {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuilder buffer = new StringBuilder();
		buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
		buffer.append("<TR><TH>Schemas</TH></TR>");
		for (int i=0; i < schemas.size(); i++) {
			buffer.append("<TR><TD>");
			buffer.append(schemas.get(i));
			buffer.append("</TD></TR>");
		}
		buffer.append("</table></body></html>");
		out.println(buffer.toString());
	}

	private static void printXML(HttpServletResponse response,
		List<String> schemas)
		throws Exception {
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		StringBuilder buffer = new StringBuilder();
		buffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		buffer.append("<schemas>");
		for (int i=0; i < schemas.size(); i++) {
			buffer.append("<name>");
			buffer.append(schemas.get(i));
			buffer.append("</name>");
		}
		buffer.append("</schemas>");
		out.println(buffer.toString());
	}

	private static void printError(HttpServletResponse response,
		String message) {
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
	* Get Schemas: Retrieves the catalog names available in
	* this database. The results are ordered by catalog name.
	*
	* @param conn the Connection object
	* @return list of all schemas.
	* @exception Failed to get the Get Schemas.
	*/
	public static List<String> getSchemas(Connection conn)
		throws Exception {
		ResultSet schemas = null;
		try {
			DatabaseMetaData meta = conn.getMetaData();
			if (meta == null) {
				return null;
			}
			schemas = meta.getSchemas();
			if (schemas == null) {
				return null;
			}
			List<String> list = new ArrayList<String>();
			while (schemas.next()) {
				String schema = schemas.getString(1); //"TABLE_CATALOG"
				if (schema != null) {
					list.add(schema);
				}
			}
			return list;
		}
		finally {
			DatabaseUtil.close(schemas);
		}
	}
}
