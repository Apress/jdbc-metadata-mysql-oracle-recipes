import java.io.PrintWriter;
import java.io.IOException;

import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.List;
import java.util.ArrayList;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class GetTypes extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
        ResultSet types = null;
        Connection conn = null;
        try {
            String dbVendor = request.getParameter("vendor").trim();
            String outputFormat = request.getParameter("format").trim();
            conn = VeryBasicConnectionManager.getConnection(dbVendor);
            types = getTypes(conn);
            if (outputFormat.equals("xml")) {
                printXML(response, types);
            }
            else {
                printHTML(response, types);
            }
        }
        catch(Exception e) {
            printError(response, e.getMessage());
        }
        finally {
            DatabaseUtil.close(types);
            DatabaseUtil.close(conn);
        }

    } // end doGet

    private static void printHTML(HttpServletResponse response,
                                  ResultSet types)
        throws Exception  {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>Type Name</TH>");
        buffer.append("<TH>Data Type</TH></TR>");
        while (types.next()) {
            buffer.append("<TR><TD>");
            buffer.append(types.getString("TYPE_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(types.getString("DATA_TYPE"));
            buffer.append("</TD><TR>");
       }
       buffer.append("</table></body></html>");
       out.println(buffer.toString());
    }

    private static void printXML(HttpServletResponse response,
                                 ResultSet types)
        throws Exception  {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<types>");
        while (types.next()) {
            buffer.append("<type name=\"");
            buffer.append(types.getString("TYPE_NAME"));
            buffer.append("\" dataType=\"");
            buffer.append(types.getString("DATA_TYPE"));
            buffer.append("\"/>");
        }
        buffer.append("</types>");
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
     * Listing Available SQL Types Used by a Database. This method
     * retrieves the SQL data types supported by a database and driver.
     *
     * @param conn the Connection object
     * @return SQL Types as a ResultSet object.
     * @exception Failed to get the Available SQL Types Used by a Database.
     */
     public static ResultSet getTypes(Connection conn)
        throws Exception {

        // Get database meta data
        DatabaseMetaData meta = conn.getMetaData();
        if (meta == null) {
             return null;
        }

        // Get type infornmation
        return meta.getTypeInfo();
    }
}
