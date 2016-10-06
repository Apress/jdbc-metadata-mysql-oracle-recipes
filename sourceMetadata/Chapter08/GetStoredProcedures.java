import java.io.PrintWriter;
import java.io.IOException;

import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class GetStoredProcedures extends HttpServlet {

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
        ResultSet storedProcedures = null;
        try {
            String dbVendor = request.getParameter("vendor").trim();
            String outputFormat = request.getParameter("format").trim();
            conn = VeryBasicConnectionManager.getConnection(dbVendor);
            if (dbVendor.equals("mysql")) {
                String catalog = request.getParameter("catalog").trim();
                storedProcedures = getStoredProcedures(conn,
                                                       catalog, // catalog,
                                                       "%",     // schema Pattern,
                                                       "%");    // proc. Pattern
            }
            else if (dbVendor.equals("oracle")) {
                String schema = request.getParameter("schema").trim();
                storedProcedures = getStoredProcedures(conn,
                                                       null,   // catalog
                                                       schema, // schema Pattern,
                                                       "%");   // proc. Pattern
            }
            else {
               printError(response, "unknown db vendor");
               return;
            }

            if (outputFormat.equals("xml")) {
                printXML(response, storedProcedures);
            }
            else {
                printHTML(response, storedProcedures);
            }

        }
        catch(Exception e) {
            printError(response, e.getMessage());
        }
        finally {
            DatabaseUtil.close(storedProcedures);
            DatabaseUtil.close(conn);
        }

    } // end doGet

    private static void printHTML(HttpServletResponse response,
                                  ResultSet storedProcedures)
        throws Exception  {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>Procedure Name</TH>");
        buffer.append("<TH>Procedure Type</TH></TR>");
        while (storedProcedures.next()) {
            buffer.append("<TR><TD>");
            buffer.append(storedProcedures.getString("PROCEDURE_NAME"));
            buffer.append("</TD><TD>");
            int type = storedProcedures.getInt("PROCEDURE_TYPE");
            buffer.append(getStoredProcedureType(type));
            buffer.append("</TD><TR>");
       }
       buffer.append("</table></body></html>");
       out.println(buffer.toString());
    }

    private static void printXML(HttpServletResponse response,
                                 ResultSet storedProcedures)
        throws Exception  {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<storedProcedures>");
        while (storedProcedures.next()) {
            buffer.append("<procedure name=\"");
            buffer.append(storedProcedures.getString("PROCEDURE_NAME"));
            buffer.append("\" type=\"");
            int type = storedProcedures.getInt("PROCEDURE_TYPE");
            buffer.append(getStoredProcedureType(type));
            buffer.append("\"/>");
        }
        buffer.append("</storedProcedures>");
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
    public static ResultSet getStoredProcedures
        (java.sql.Connection conn,
         String catalog,
         String schemaPattern,
         String procedureNamePattern)
        throws Exception {

         DatabaseMetaData meta = conn.getMetaData();
         if (meta == null) {
             return null;
         }

         return meta.getProcedures(catalog,
                                   schemaPattern,
                                   procedureNamePattern);
     }

     private static String getStoredProcedureType(int spType) {
        if (spType == DatabaseMetaData.procedureReturnsResult) {
            return STORED_PROCEDURE_RETURNS_RESULT;
        }
        else if (spType == DatabaseMetaData.procedureNoResult) {
            return STORED_PROCEDURE_NO_RESULT;
        }
        else {
            return STORED_PROCEDURE_RESULT_UNKNOWN;
        }
     }
}