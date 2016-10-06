import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.IOException;

import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class GetSPColumns extends HttpServlet {

    private static final String  STORED_PROCEDURE_RETURNS_RESULT =
       "procedureReturnsResult";
    private static final String  STORED_PROCEDURE_NO_RESULT =
       "procedureNoResult";
    private static final String  STORED_PROCEDURE_RESULT_UNKNOWN =
       "procedureResultUnknown";

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
        Connection conn = null;
        ResultSet storedProcedureColumns = null;
        try {
            String dbVendor = request.getParameter("vendor").trim();
            String outputFormat = request.getParameter("format").trim();
            String procedure = request.getParameter("procedure").trim();
            conn = VeryBasicConnectionManager.getConnection(dbVendor);
            if (dbVendor.equals("mysql")) {
                String catalog = request.getParameter("catalog").trim();
                storedProcedureColumns =
                   getStoredProcedureColumns(conn,
                                             catalog, // catalog,
                                             "%",     // schema Pattern,
                                             "%");    // proc. Pattern
            }
            else if (dbVendor.equals("oracle")) {
                String schema = request.getParameter("schema").trim();
                storedProcedureColumns =
                   getStoredProcedureColumns(conn,
                                             null,   // catalog
                                             schema, // schema Pattern,
                               "%"+procedure+"%");   // proc. Pattern
            }
            else {
               printError(response, "unknown db vendor");
               return;
            }

            if (outputFormat.equals("xml")) {
                printXML(response, storedProcedureColumns);
            }
            else {
                printHTML(response, storedProcedureColumns);
            }

        }
        catch(Exception e) {
            printError(response, e.getMessage());
        }
        finally {
            DatabaseUtil.close(storedProcedureColumns);
            DatabaseUtil.close(conn);
        }

    } // end doGet

    private static void printHTML(HttpServletResponse response,
                                  ResultSet spColumns)
        throws Exception  {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>Catalog</TH>");
        buffer.append("<TH>Schema</TH>");
        buffer.append("<TH>Procedure Name</TH>");
        buffer.append("<TH>Column Name</TH>");
        buffer.append("<TH>Column Type</TH>");
        buffer.append("<TH>Data Type</TH>");
        buffer.append("<TH>Type Name</TH>");
        buffer.append("<TH>Nullable</TH></TR>");
        while (spColumns.next()) {
            buffer.append("<TR><TD>");
            buffer.append(spColumns.getString("PROCEDURE_CAT"));
            buffer.append("</TD><TD>");
            buffer.append(spColumns.getString("PROCEDURE_SCHEM"));
            buffer.append("</TD><TD>");
            buffer.append(spColumns.getString("PROCEDURE_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(spColumns.getString("COLUMN_NAME"));
            buffer.append("</TD><TD>");
            short columnType = spColumns.getShort("COLUMN_TYPE");
            buffer.append(getColumnType(columnType));
            buffer.append("</TD><TD>");
            buffer.append(spColumns.getString("DATA_TYPE"));
            buffer.append("</TD><TD>");
            buffer.append(spColumns.getString("TYPE_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(spColumns.getShort("NULLABLE"));
            buffer.append("</TD><TR>");
       }
       buffer.append("</table></body></html>");
       out.println(buffer.toString());
    }

    private static void printXML(HttpServletResponse response,
                                 ResultSet spColumns)
        throws Exception  {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<storedProcedureColumns>");
        while (spColumns.next()) {
            buffer.append("<column><catalog>");
            buffer.append(spColumns.getString("PROCEDURE_CAT"));
            buffer.append("</catalog><schema>");
            buffer.append(spColumns.getString("PROCEDURE_SCHEM"));
            buffer.append("</schema><procedureName>");
            buffer.append(spColumns.getString("PROCEDURE_NAME"));
            buffer.append("</procedureName><columnName>");
            buffer.append(spColumns.getString("COLUMN_NAME"));
            buffer.append("</columnName><columnType>");
            short columnType = spColumns.getShort("COLUMN_TYPE");
            buffer.append(getColumnType(columnType));
            buffer.append("</columnType><dataType>");
            buffer.append(spColumns.getString("COLUMN_NAME"));
            buffer.append("</dataType><typeName>");
            buffer.append(spColumns.getString("TYPE_NAME"));
            buffer.append("</typeName><nullable>");
            buffer.append(spColumns.getShort("NULLABLE"));
            buffer.append("</nullable></column>");
        }
        buffer.append("</storedProcedureColumns>");
        out.println(buffer.toString());
    }

    private static void printError(HttpServletResponse response,
                                   String message) {
        try {
            PrintWriter out = response.getWriter();
            StringBuilder buffer = new StringBuilder();
            buffer.append("<html><body>");
            buffer.append(message);
            buffer.append("</body></html>");
            out.println(buffer.toString());
        }
        catch(Exception ignore) {
        }
    }

    /**
     * Get the stored-procedures names.
     * @param conn the Connection object
     * @catalog database catalog name
     * @schemaPattern database schema pattern
     * @procedureNamePattern database procedure name pattern
     * @return a table of stored procedures names
     * as a ResultSet object.
     * Each element of XML document will have the name and
     * type of a stored procedure.
     *
     */
    public static ResultSet getStoredProcedureColumns
        (java.sql.Connection conn,
         String catalog,
         String schemaPattern,
         String procedureNamePattern)
         throws Exception {

         DatabaseMetaData meta = conn.getMetaData();
         if (meta == null) {
             return null;
         }

         return meta.getProcedureColumns(catalog,
                                         schemaPattern,
                                         procedureNamePattern,
                                         "%");
     }

     private static String getColumnType(short columnType) {
        if (columnType == DatabaseMetaData.procedureColumnIn) {
            return "IN parameter";
        }
        else if (columnType == DatabaseMetaData.procedureColumnInOut) {
            return "INOUT parameter";
        }
        else if (columnType == DatabaseMetaData.procedureColumnOut) {
            return "OUT parameter";
        }
        else if (columnType == DatabaseMetaData.procedureColumnReturn) {
            return "procedure return value";
        }
        else if (columnType == DatabaseMetaData.procedureColumnResult) {
            return "result column in ResultSet";
        }
        else {
            return "nobody knows";
        }
     }
}