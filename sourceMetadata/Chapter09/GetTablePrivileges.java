import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;


public class GetTablePrivileges extends HttpServlet {
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
        throws ServletException, IOException {
        ResultSet tablePrivileges = null;
        Connection conn = null;

        try {
            String dbVendor = request.getParameter("vendor").trim();
            String outputFormat = request.getParameter("format").trim();
            String table = request.getParameter("table").trim();

            conn = VeryBasicConnectionManager.getConnection(dbVendor);
            if (dbVendor.equals("mysql")) {
                // String catalog = request.getParameter("catalog").trim();
                tablePrivileges = getTablePrivileges(conn, conn.getCatalog(), // catalog,
                        null, // schema Pattern,
                        "%"); // table. Pattern
            }
            else if (dbVendor.equals("oracle")) {
                // String schema = request.getParameter("schema").trim();
                tablePrivileges = getTablePrivileges(conn, conn.getCatalog(), // catalog
                        "%", // schema Pattern,
                        table + "%"); // table. Pattern
            } else {
                printError(response, "unknown db vendor");
                return;
            }
            if (outputFormat.equals("xml")) {
                printXML(response, tablePrivileges);
            }
            else {
                printHTML(response, tablePrivileges);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            printError(response, e.getMessage());
        }
        finally {
            DatabaseUtil.close(tablePrivileges);
            DatabaseUtil.close(conn);
        }
    } // end doGet

    private static void printHTML(HttpServletResponse response,
            ResultSet tablePrivileges)
        throws Exception {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();

        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>Catalog</TH>");
        buffer.append("<TH>Schema</TH>");
        buffer.append("<TH>Table Name</TH>");
        buffer.append("<TH>Grantor</TH>");
        buffer.append("<TH>Grantee</TH>");
        buffer.append("<TH>Privilege</TH>");
        buffer.append("<TH>Is Grantable</TH></TR>");
        while (tablePrivileges.next()) {
            buffer.append("<TR><TD>");
            buffer.append(tablePrivileges.getString("TABLE_CAT"));
            buffer.append("</TD><TD>");
            buffer.append(tablePrivileges.getString("TABLE_SCHEM"));
            buffer.append("</TD><TD>");
            buffer.append(tablePrivileges.getString("TABLE_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(tablePrivileges.getString("GRANTOR"));
            buffer.append("</TD><TD>");
            buffer.append(tablePrivileges.getString("GRANTEE"));
            buffer.append("</TD><TD>");
            buffer.append(tablePrivileges.getString("PRIVILEGE"));
            buffer.append("</TD><TD>");
            buffer.append(tablePrivileges.getString("IS_GRANTABLE"));
            buffer.append("</TD></TR>");
        }
        buffer.append("</table></body></html>");
        out.println(buffer.toString());
    }

    private static void printXML(HttpServletResponse response,
            ResultSet tablePrivileges)
        throws Exception {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();

        buffer.append("<?xml version=\"1.0\"?>");
        buffer.append("<table_privileges>");
        while (tablePrivileges.next()) {
            buffer.append("<table_privilege><catalog>");
            buffer.append(tablePrivileges.getString("TABLE_CAT"));
            buffer.append("</catalog><schema>");
            buffer.append(tablePrivileges.getString("TABLE_SCHEM"));
            buffer.append("</schema><tableName>");
            buffer.append(tablePrivileges.getString("TABLE_NAME"));
            buffer.append("</tableName><grantor>");
            buffer.append(tablePrivileges.getString("GRANTOR"));
            buffer.append("</grantor><grantee>");
            buffer.append(tablePrivileges.getString("GRANTEE"));
            buffer.append("</grantee><privilege>");
            buffer.append(tablePrivileges.getString("PRIVILEGE"));
            buffer.append("</privilege><is_grantable>");
            buffer.append(tablePrivileges.getString("IS_GRANTABLE"));
            buffer.append("</is_grantable></table_privilege>");
        }
        buffer.append("</table_privileges>");
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
        catch (Exception ignore){
		}
    }

    /**
     * Get Table Privileges: retrieves a description of the access
     * rights for each table available in a catalog. Note that a
     * table privilege applies to one or more tablePrivileges in the table.
     * It would be wrong to assume that this privilege applies to
     * all tablePrivileges (this may be true for some systems but is not
     * true for all.) The result is returned as a ResultSet object.
     *
     * In JDBC, Each privilege description has the following tablePrivileges:
     *
     * TABLE_CAT String => table catalog (may be null)
     * TABLE_SCHEM String => table schema (may be null)
     * TABLE_NAME String => table name
     * GRANTOR => grantor of access (may be null)
     * GRANTEE String => grantee of access
     * PRIVILEGE String => name of access (SELECT, INSERT,
     * UPDATE, REFERENCES, ...)
     * IS_GRANTABLE String => "YES" if grantee is permitted to grant
     * to others; "NO" if not; null if unknown
     *
     *
     * @param conn the Connection object
     * @param catalogPattern a catalog pattern.
     * @param schemaPattern a schema pattern.
     * @param tableNamePattern a table name pattern; must match
     * the table name as it is stored in the database.
     * @return a ResultSet object
     * @exception Failed to get the Get Table Privileges.
     */
    public static ResultSet getTablePrivileges(Connection conn,
            String catalog,
            String schemaPattern,
            String tableNamePattern)
        throws Exception {
        if ((tableNamePattern == null) || (tableNamePattern.length() == 0)) {
            return null;
        }
        DatabaseMetaData meta = conn.getMetaData();

        if (meta == null) {
            return null;
        }
        // The '_' character represents any single character.
        // The '%' character represents any sequence of zero
        // or more characters.
        return meta.getTablePrivileges(catalog, schemaPattern, tableNamePattern);
    }
}

