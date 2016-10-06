import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.IOException;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class GetColumns extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
        ResultSet columns = null;
        Connection conn = null;
        try {
            String dbVendor = request.getParameter("vendor").trim();
            String table = request.getParameter("table").trim();
            String outputFormat = request.getParameter("format").trim();
            conn = VeryBasicConnectionManager.getConnection(dbVendor);
            System.out.println("conn="+conn);
            System.out.println("1columns="+columns);
            columns = getColumns(conn, table);
            System.out.println("2columns="+columns);
            if (outputFormat.equals("xml")) {
                printXML(response, columns);
            }
            else {
                printHTML(response, columns);
            }
            System.out.println("done");
        }
        catch(Exception e) {
            printError(response, e.getMessage());
        }
        finally {
            DatabaseUtil.close(columns);
            DatabaseUtil.close(conn);
        }

    } // end doGet

    private static void printHTML(HttpServletResponse response,
                                  ResultSet columns)
        throws Exception  {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>Catalog</TH>");
        buffer.append("<TH>Schema</TH>");
        buffer.append("<TH>Table Name</TH>");
        buffer.append("<TH>Column Name</TH>");
        buffer.append("<TH>Data Type</TH>");
        buffer.append("<TH>Type Name</TH>");
        buffer.append("<TH>Column Size</TH>");
        buffer.append("<TH>Is Nullable?</TH>");
        buffer.append("<TH>Is Nullable?</TH>");
        buffer.append("<TH>Ordinal Position</TH></TR>");
        while (columns.next()) {
            buffer.append("<TR><TD>");
            buffer.append(columns.getString("TABLE_CAT"));
            buffer.append("</TD><TD>");
            buffer.append(columns.getString("TABLE_SCHEM"));
            buffer.append("</TD><TD>");
            buffer.append(columns.getString("TABLE_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(columns.getString("COLUMN_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(columns.getShort("DATA_TYPE"));
            buffer.append("</TD><TD>");
            buffer.append(columns.getString("TYPE_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(columns.getString("COLUMN_SIZE"));
            buffer.append("</TD><TD>");
            int nullable = columns.getInt("NULLABLE");
            if (nullable == DatabaseMetaData.columnNullable) {
                buffer.append("true");
            }
            else if (nullable == DatabaseMetaData.columnNoNulls) {
                buffer.append("false");
            }
            else {
                buffer.append("unknown");
            }
            buffer.append("</TD><TD>");
            buffer.append(columns.getString("IS_NULLABLE"));
            buffer.append("</TD><TD>");
            buffer.append(columns.getString("ORDINAL_POSITION"));
            buffer.append("</TD></TR>");
       }
       buffer.append("</table></body></html>");
       out.println(buffer.toString());
    }

    private static void printXML(HttpServletResponse response,
                                 ResultSet columns)
        throws Exception  {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version=\"1.0\"?>");
        buffer.append("<columns>");
        while (columns.next()) {
            buffer.append("<column><catalog>");
            buffer.append(columns.getString("TABLE_CAT"));
            buffer.append("</catalog><schema>");
            buffer.append(columns.getString("TABLE_SCHEM"));
            buffer.append("</schema><tableName>");
            buffer.append(columns.getString("TABLE_NAME"));
            buffer.append("</tableName><columnName>");
            buffer.append(columns.getString("COLUMN_NAME"));
            buffer.append("</columnName><dataType>");
            buffer.append(columns.getShort("DATA_TYPE"));
            buffer.append("</dataType><typeName>");
            buffer.append(columns.getString("TYPE_NAME"));
            buffer.append("</typeName><columnSize>");
            buffer.append(columns.getString("COLUMN_SIZE"));
            buffer.append("</columnSize><nullable>");
            int nullable = columns.getInt("NULLABLE");
            if (nullable == DatabaseMetaData.columnNullable) {
                buffer.append("true");
            }
            else if (nullable == DatabaseMetaData.columnNoNulls) {
                buffer.append("false");
            }
            else {
                buffer.append("unknown");
            }
            buffer.append("</nullable><isNullable>");
            buffer.append(columns.getString("IS_NULLABLE"));
            buffer.append("</isNullable><ordinalPosition>");
            buffer.append(columns.getString("ORDINAL_POSITION"));
            buffer.append("</ordinalPosition></column>");
        }
        buffer.append("</columns>");
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
     * Retrieves a description of the given table's columns.
     * @param conn the Connection object
     * @param tableName name of a table in the database.
     * @return the list of columns as a ResultSet object
     * @exception Failed to get the table's columns.
     */
     public static ResultSet getColumns(Connection conn,
                                        String tableName)
         throws Exception {
         System.out.println("getColumns begin");
         if ((tableName == null) || (tableName.length() == 0)) {
             return null;
         }

         DatabaseMetaData meta = conn.getMetaData();
         if (meta == null) {
             return null;
         }

         //
         // The Oracle database stores its table names as
         // Upper-Case; if you pass a table name in lowercase
         // characters, it will not work. MySQL database does
         // not care if table name is uppercase/lowercase.
         //
         System.out.println("getColumns begin 2");
         ResultSet columns = meta.getColumns(null, null, tableName.toUpperCase(), null);
         System.out.println("getColumns begin 3 columns ="+columns);
         return columns;
    }
}
