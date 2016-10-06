import java.io.PrintWriter;
import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.List;
import java.util.ArrayList;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class GetCatalogs extends HttpServlet {
	public void doGet(HttpServletRequest request,
	HttpServletResponse response)
	throws ServletException, IOException {
		Connection conn = null;
		try {
			String dbVendor = request.getParameter("vendor").trim();
			String outputFormat = request.getParameter("format").trim();
			conn = VeryBasicConnectionManager.getConnection(dbVendor);
			List<String> catalogs = getCatalogs(conn);
			if (outputFormat.equals("xml")) {
				printXML(response, catalogs);
			}
			else {
				printHTML(response, catalogs);
			}
		}
		catch(Exception e) {
		printError(response, e.getMessage());
		}
		finally {
		DatabaseUtil.close(conn);
		}
	} // end doGet

	private static void printHTML(HttpServletResponse response,
	List<String> tables)
	throws Exception {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuilder buffer = new StringBuilder();
		buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
		buffer.append("<TR><TH>Catalogs</TH></TR>");
		for (int i=0; i < tables.size(); i++) {
		buffer.append("<TR><TD>");
		buffer.append(tables.get(i));
		buffer.append("</TD></TR>");
		}
		buffer.append("</table></body></html>");
		out.println(buffer.toString());
	}

	private static void printXML(HttpServletResponse response,
	List<String> catalogs)
	throws Exception {
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		StringBuilder buffer = new StringBuilder();
		buffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		buffer.append("<catalogs>");
		for (int i=0; i < catalogs.size(); i++) {
		buffer.append("<name>");
		buffer.append(catalogs.get(i));
		buffer.append("</name>");
		}
		buffer.append("</catalogs>");
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
	* Get Catalogs: Retrieves the catalog names available in
	* this database. The results are ordered by catalog name.
	*
	* @param conn the Connection object
	* @return list of all catalogs.
	* @exception Failed to get the Get Catalogs.
	*/
	public static List<String> getCatalogs(Connection conn)
	throws Exception {
		ResultSet catalogs = null;
		try {
			DatabaseMetaData meta = conn.getMetaData();
			if (meta == null) {
				return null;
			}
			catalogs = meta.getCatalogs();
			if (catalogs == null) {
				return null;
			}
			List<String> list = new ArrayList<String>();
			while (catalogs.next()) {
				String catalog = catalogs.getString(1); //"TABLE_CATALOG"
				if (catalog != null) {
					list.add(catalog);
				}
			}
			return list;
		}
		finally {
			DatabaseUtil.close(catalogs);
		}
	}
}
