import java.io.PrintWriter;
import java.io.IOException;

import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.List;
import java.util.ArrayList;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class GetTables extends HttpServlet {

    private static final String  ORACLE_TABLES =
        "select object_name from user_objects where object_type = 'TABLE'";
    private static final String[] DB_TABLE_TYPES =
        { "TABLE" };
    private static final String COLUMN_NAME_TABLE_NAME =
        "TABLE_NAME";

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
        Connection conn = null;
        try {
            String dbVendor = request.getParameter("vendor").trim();
            String outputFormat = request.getParameter("format").trim();
            conn = VeryBasicConnectionManager.getConnection(dbVendor);
            List<String> tables = null;

            if (dbVendor.equals("oracle")) {
                tables = getOracleTableNames(conn);
            }
            else {
                tables = getTableNames(conn);
            }

            if (outputFormat.equals("xml")) {
                printXML(response, tables);
            }
            else {
                printHTML(response, tables);
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
        throws Exception  {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>Table Name</TH></TR>");
        for (int i=0; i < tables.size(); i++) {
           buffer.append("<TR><TD>");
           buffer.append(tables.get(i));
           buffer.append("</TD></TR>");
        }
        buffer.append("</table></body></html>");
        out.println(buffer.toString());
    }
    private static void printXML(HttpServletResponse response,
                                 List<String> tables)
        throws Exception  {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        buffer.append("<tables>");
        for (int i=0; i < tables.size(); i++) {
           buffer.append("<name>");
           buffer.append(tables.get(i));
           buffer.append("</name>");
        }
        buffer.append("</tables>");
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
    public static List<String> getOracleTableNames(Connection conn)
        throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(ORACLE_TABLES);
            if (rs == null) {
                return null;
            }

            List<String> list = new ArrayList<String>();
            while (rs.next()) {
                String tableName = DatabaseUtil.getTrimmedString(rs, 1);
                System.out.println("tableName="+tableName);
                if (tableName != null) {
                     list.add(tableName);
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
     public static List<String> getTableNames(Connection conn)
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

             List<String> list = new ArrayList<String>();
             System.out.println("getTableNames(): --------------");
             while (rs.next()) {
                 String tableName =
                    DatabaseUtil.getTrimmedString(rs, COLUMN_NAME_TABLE_NAME);
                 System.out.println("tableName="+tableName);
                 if (tableName != null) {
                     list.add(tableName);
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
