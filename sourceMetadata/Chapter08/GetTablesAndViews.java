import java.io.PrintWriter;
import java.io.IOException;

import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class GetTablesAndViews extends HttpServlet {

    private static final String  ORACLE_TABLES_AND_VIEWS =
        "select object_name, object_type from user_objects "+
        "where object_type = 'TABLE' or object_type = 'VIEW'";
    private static final String[] DB_TABLE_AND_VIEW_TYPES =
        { "TABLE", "VIEW" };
    private static final String COLUMN_NAME_TABLE_NAME = "TABLE_NAME";
    private static final String COLUMN_NAME_TABLE_TYPE = "TABLE_TYPE";

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
        Connection conn = null;
        try {
            String dbVendor = request.getParameter("vendor").trim();
            String outputFormat = request.getParameter("format").trim();
            conn = VeryBasicConnectionManager.getConnection(dbVendor);
            Map<String, String> tablesAndViews = null;

            if (dbVendor.equals("oracle")) {
                tablesAndViews = getOracleTablesAndViews(conn);
            }
            else {
                tablesAndViews = getTablesAndViews(conn);
            }

            if (tablesAndViews == null) {
                printError(response, "NO-TABLES-OR-VIEWS-FOUND");
                return;
            }

            if (outputFormat.equals("xml")) {
                printXML(response, tablesAndViews);
            }
            else {
                printHTML(response, tablesAndViews);
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
                                  Map<String, String> tablesAndViews)
        throws Exception  {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>Table/View Name</TH><TH>Type</TH></TR>");
        for (Map.Entry<String, String> e : tablesAndViews.entrySet()) {
           buffer.append("<TR><TD>");
           buffer.append(e.getKey());
           buffer.append("</TD><TD>");
           buffer.append(e.getValue());
           buffer.append("</TD></TR>");
        }
        buffer.append("</table></body></html>");
        out.println(buffer.toString());
    }
    private static void printXML(HttpServletResponse response,
                                 Map<String, String> tablesAndViews)
        throws Exception  {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        buffer.append("<tables_and_views>");
        for (Map.Entry<String, String> e : tablesAndViews.entrySet()) {
           buffer.append("<name type=\"");
           buffer.append(e.getValue());
           buffer.append("\">");
           buffer.append(e.getKey());
           buffer.append("</name>");
        }
        /*
        buffer.append("<name type=\"VIEW\">BIG_SALARY</name>");
        buffer.append("<name type=\"TABLE\">BONUS</name>");
        buffer.append("<name type=\"TABLE\">EMP</name>");
        buffer.append("<name type=\"TABLE\">DEPT</name>");
        buffer.append("<name type=\"VIEW\">MY_VIEW</name>");
        buffer.append("<name type=\"TABLE\">MYTABLE</name>");
        buffer.append("<name type=\"TABLE\">SALGRADE</name>");
        */

        buffer.append("</tables_and_views>");
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
     * Get the Oracle table names for a given connection object.
     * If you use the getTableNames() for an Oracle database, you
     * will get lots of auxiliary tables, which belong to the user,
     * but user is not interested in seeing them.
     *
     * @param conn the Connection object
     * @return the list of table names as a List.
     * @exception Failed to get the table names from the database.
     */
    public static Map<String, String> getOracleTablesAndViews(Connection conn)
        throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(ORACLE_TABLES_AND_VIEWS);
            if (rs == null) {
                return null;
            }

            Map<String, String> list = new HashMap<String, String>();
            while (rs.next()) {
                String name = DatabaseUtil.getTrimmedString(rs, 1);
                String type = DatabaseUtil.getTrimmedString(rs, 2);
                //System.out.println("name="+name);
                if (name != null) {
                     list.put(name, type);
                }
            }

            return list;
        }
        finally {
            DatabaseUtil.close(rs);
            DatabaseUtil.close(stmt);
        }
    }


    /**
     * Get the table names for a given connection object.
     * @param conn the Connection object
     * @return the list of table names as a List.
     * @exception Failed to get the table names from the database.
     */
     public static Map<String, String> getTablesAndViews(Connection conn)
        throws Exception {
        ResultSet rs = null;
        try {
             DatabaseMetaData meta = conn.getMetaData();
             if (meta == null) {
                 return null;
             }

             rs = meta.getTables(null, null, null, DB_TABLE_AND_VIEW_TYPES);
             if (rs == null) {
                 return null;
             }

             Map<String, String> list = new HashMap<String, String>();
             while (rs.next()) {
                 String name =
                    DatabaseUtil.getTrimmedString(rs, COLUMN_NAME_TABLE_NAME);
                 String type =
                    DatabaseUtil.getTrimmedString(rs, COLUMN_NAME_TABLE_TYPE);
                 if (name != null) {
                     list.put(name, type);
                 }
             }
             return list;
         }
         finally {
             DatabaseUtil.close(rs);
         }
    }
}
