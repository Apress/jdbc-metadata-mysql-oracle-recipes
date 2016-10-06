import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;


public class GetBestRowIdentifier extends HttpServlet {
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
        throws ServletException, IOException {
        ResultSet bestRowIdentifier = null;
        Connection conn = null;

        try {
            String dbVendor = request.getParameter("vendor").trim();
            String table = request.getParameter("table").trim();
            String outputFormat = request.getParameter("format").trim();
            String scope = request.getParameter("scope").trim();
            String nullable = request.getParameter("nullable").trim();

            conn = VeryBasicConnectionManager.getConnection(dbVendor);
            if (dbVendor.equals("mysql")) {
                bestRowIdentifier = getBestRowIdentifier(conn, conn.getCatalog(), // catalog,
                        "", // schema
                        table, // table
                        scope, // scope
                        nullable);
            }
            else if (dbVendor.equals("oracle")) {
                String schema = request.getParameter("schema").trim();

                bestRowIdentifier = getBestRowIdentifier(conn, conn.getCatalog(), // catalog,
                        schema, // schema
                        table, // table
                        scope, // scope
                        nullable);
            }
            else {
                printError(response, "unknown db vendor");
                return;
            }
            if (outputFormat.equals("xml")) {
                printXML(response, bestRowIdentifier);
            }
            else {
                printHTML(response, bestRowIdentifier);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            printError(response, e.getMessage());
        }
        finally {
            DatabaseUtil.close(bestRowIdentifier);
            DatabaseUtil.close(conn);
        }
    } // end doGet

    private static void printHTML(HttpServletResponse response,
            ResultSet bestRowIdentifier)
        throws Exception {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();

        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>Scope</TH>");
        buffer.append("<TH>Column Name</TH>");
        buffer.append("<TH>Data Type</TH>");
        buffer.append("<TH>Type Name</TH>");
        buffer.append("<TH>Column Size</TH>");
        buffer.append("<TH>Decimal Digits</TH>");
        buffer.append("<TH>Pseudo Column</TR>");
        while (bestRowIdentifier.next()) {
            buffer.append("<TR><TD>");
            short scope = bestRowIdentifier.getShort("SCOPE");

            buffer.append(getScope(scope));
            buffer.append("</TD><TD>");
            buffer.append(bestRowIdentifier.getString("COLUMN_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(bestRowIdentifier.getInt("DATA_TYPE"));
            buffer.append("</TD><TD>");
            buffer.append(bestRowIdentifier.getString("TYPE_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(bestRowIdentifier.getInt("COLUMN_SIZE"));
            buffer.append("</TD><TD>");
            buffer.append(bestRowIdentifier.getShort("DECIMAL_DIGITS"));
            buffer.append("</TD><TD>");
            short pseudoColumn = bestRowIdentifier.getShort("PSEUDO_COLUMN");

            buffer.append(getPseudoColumn(pseudoColumn));
            buffer.append("</TD></TR>");
        }
        buffer.append("</table></body></html>");
        out.println(buffer.toString());
    }

    private static void printXML(HttpServletResponse response,
            ResultSet bestRowIdentifier)
        throws Exception {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();

        // buffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        buffer.append("<?xml version=\"1.0\"?>");
        buffer.append("<bestRowIdentifier>");
        while (bestRowIdentifier.next()) {
            buffer.append("<identifier><scope>");
            short scope = bestRowIdentifier.getShort("SCOPE");

            buffer.append(getScope(scope));
            buffer.append("</scope><columnName>");
            buffer.append(bestRowIdentifier.getString("COLUMN_NAME"));
            buffer.append("</columnName><dataType>");
            buffer.append(bestRowIdentifier.getInt("DATA_TYPE"));
            buffer.append("</dataType><typeName>");
            buffer.append(bestRowIdentifier.getString("TYPE_NAME"));
            buffer.append("</typeName><columnSize>");
            buffer.append(bestRowIdentifier.getInt("COLUMN_SIZE"));
            buffer.append("</columnSize><decimalDigits>");
            buffer.append(bestRowIdentifier.getShort("DECIMAL_DIGITS"));
            buffer.append("</decimalDigits><pseudoColumn>");
            short pseudoColumn = bestRowIdentifier.getShort("PSEUDO_COLUMN");

            buffer.append(getPseudoColumn(pseudoColumn));
            buffer.append("</pseudoColumn></identifier>");
        }
        buffer.append("</bestRowIdentifier>");
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
        catch (Exception ignore) {
		}
    }

    /**
     * Retrieves a description of the given table's BestRowIdentifier.
     * @param conn the Connection object
     * @param catalog a catalog.
     * @param schema a schema.
     * @param table name of a table in the database.
     * @param scope the scope of interest; use same values as SCOPE
     * @param nullable include bestRowIdentifier that are nullable.
     * @return the BestRowIdentifier as a ResultSet object
     * @exception Failed to get the table's bestRowIdentifier.
     */
    public static ResultSet getBestRowIdentifier(Connection conn,
            String catalog,
            String schema,
            String table,
            String scope,
            String nullable)
        throws Exception {
        if ((table == null) || (table.length() == 0)) {
            return null;
        }
        DatabaseMetaData meta = conn.getMetaData();

        if (meta == null) {
            return null;
        }
        int theScope = DatabaseMetaData.bestRowSession;

        if (scope.equals("bestRowTemporary")) {
            theScope = DatabaseMetaData.bestRowTemporary;
        }
        else if (scope.equals("bestRowTransaction")) {
            theScope = DatabaseMetaData.bestRowTransaction;
        }
        boolean isNullable = false;

        if (nullable.equals("true")) {
            isNullable = true;
        }
        //
        // The Oracle database stores its table names as
        // Upper-Case; if you pass a table name in lowercase
        // characters, it will not work. MySQL database does
        // not care if table name is uppercase/lowercase.
        //
        return meta.getBestRowIdentifier(catalog, schema, table.toUpperCase(),
                theScope, isNullable);
    }

    public static String getScope(short scope) {
        if (scope == DatabaseMetaData.bestRowSession) {
            return "bestRowSession";
        }
        else if (scope == DatabaseMetaData.bestRowTemporary) {
            return "bestRowTemporary";
        }
        else if (scope == DatabaseMetaData.bestRowTransaction) {
            return "bestRowTransaction";
        }
        else {
            return "scope is unknown";
        }
    }

    public static String getPseudoColumn(short pseudoColumn) {
        if (pseudoColumn == DatabaseMetaData.bestRowNotPseudo) {
            return "bestRowNotPseudo";
        }
        else if (pseudoColumn == DatabaseMetaData.bestRowPseudo) {
            return "bestRowPseudo";
        }
        else {
            return "bestRowUnknown";
        }
    }
}

