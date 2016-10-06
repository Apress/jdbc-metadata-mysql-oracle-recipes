import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.List;
import java.util.ArrayList;

import java.io.PrintWriter;
import java.io.IOException;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class GetTableTypes extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
        Connection conn = null;
        try {
            String dbVendor = request.getParameter("vendor").trim();
            String outputFormat = request.getParameter("format").trim();
            conn = VeryBasicConnectionManager.getConnection(dbVendor);
            List<String> tableTypes = getTableTypes(conn);
            if (outputFormat.equals("xml")) {
                printXML(response, tableTypes);
            }
            else {
                printHTML(response, tableTypes);
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
                                  List<String> tableTypes)
        throws Exception  {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>Table Type</TH></TR>");
        for (int i=0; i < tableTypes.size(); i++) {
           buffer.append("<TR><TD>");
           buffer.append(tableTypes.get(i));
           buffer.append("</TD></TR>");
        }
        buffer.append("</table></body></html>");
        out.println(buffer.toString());
    }
    private static void printXML(HttpServletResponse response,
                                 List<String> tableTypes)
        throws Exception  {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        buffer.append("<tableTypes>");
        for (int i=0; i < tableTypes.size(); i++) {
           buffer.append("<type>");
           buffer.append(tableTypes.get(i));
           buffer.append("</type>");
        }
        buffer.append("</tableTypes>");
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
     * Get the table names for a given connection object.
     * @param conn the Connection object
     * @return the list of table names as a List.
     * @exception Failed to get the table names from the database.
     */
     public static List<String> getTableTypes(Connection conn)
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

             List<String> list = new ArrayList<String>();
             System.out.println("getTableTypes(): --------------");
             while (rs.next()) {
                 String type = rs.getString(1);
                 System.out.println("type="+type);
                 if (type != null) {
                     list.add(type);
                 }
             }
             System.out.println("--------------");
             return list;
         }
         finally {
             DatabaseUtil.close(rs);
         }
    }
}



