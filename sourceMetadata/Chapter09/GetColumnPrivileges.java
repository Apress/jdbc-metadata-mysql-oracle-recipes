import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;


public class GetColumnPrivileges extends HttpServlet {
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
        throws ServletException, IOException {
        ResultSet columnPrivileges = null;
        Connection conn = null;

        try {
            String dbVendor = request.getParameter("vendor").trim();
            String outputFormat = request.getParameter("format").trim();
            String table = request.getParameter("table").trim();

            conn = VeryBasicConnectionManager.getConnection(dbVendor);
            if (dbVendor.equals("mysql")) {
                columnPrivileges = getColumnPrivileges(conn, conn.getCatalog(), // catalog,
                        null, // schema
                        table, // table
                        "%"); // all columns
            }
            else if (dbVendor.equals("oracle")) {
                String schema = request.getParameter("schema").trim();

                columnPrivileges = getColumnPrivileges(conn, conn.getCatalog(), // catalog,
                        schema, // schema
                        table, // table
                        "%"); // all columns
            }
            else {
                printError(response, "unknown db vendor");
                return;
            }
            if (outputFormat.equals("xml")) {
                printXML(response, columnPrivileges);
            }
            else {
                printHTML(response, columnPrivileges);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            printError(response, e.getMessage());
        }
        finally {
            DatabaseUtil.close(columnPrivileges);
            DatabaseUtil.close(conn);
        }
    } // end doGet

    private static void printHTML(HttpServletResponse response,
            ResultSet columnPrivileges)
        throws Exception {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();

        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>Catalog</TH>");
        buffer.append("<TH>Schema</TH>");
        buffer.append("<TH>Table Name</TH>");
        buffer.append("<TH>Column Name</TH>");
        buffer.append("<TH>Grantor</TH>");
        buffer.append("<TH>Grantee</TH>");
        buffer.append("<TH>Privilege</TH>");
        buffer.append("<TH>Is Grantable</TH></TR>");
        while (columnPrivileges.next()) {
            buffer.append("<TR><TD>");
            buffer.append(columnPrivileges.getString("TABLE_CAT"));
            buffer.append("</TD><TD>");
            buffer.append(columnPrivileges.getString("TABLE_SCHEM"));
            buffer.append("</TD><TD>");
            buffer.append(columnPrivileges.getString("TABLE_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(columnPrivileges.getString("COLUMN_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(columnPrivileges.getString("GRANTOR"));
            buffer.append("</TD><TD>");
            buffer.append(columnPrivileges.getString("GRANTEE"));
            buffer.append("</TD><TD>");
            buffer.append(columnPrivileges.getString("PRIVILEGE"));
            buffer.append("</TD><TD>");
            buffer.append(columnPrivileges.getString("IS_GRANTABLE"));
            buffer.append("</TD></TR>");
        }
        buffer.append("</table></body></html>");
        out.println(buffer.toString());
    }

    private static void printXML(HttpServletResponse response,
            ResultSet columnPrivileges)
        throws Exception {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();

        buffer.append("<?xml version=\"1.0\"?>");
        buffer.append("<table_privileges>");
        while (columnPrivileges.next()) {
            buffer.append("<table_privilege><catalog>");
            buffer.append(columnPrivileges.getString("TABLE_CAT"));
            buffer.append("</catalog><schema>");
            buffer.append(columnPrivileges.getString("TABLE_SCHEM"));
            buffer.append("</schema><tableName>");
            buffer.append(columnPrivileges.getString("TABLE_NAME"));
            buffer.append("</tableName><columnName>");
            buffer.append(columnPrivileges.getString("COLUMN_NAME"));
            buffer.append("</columnName><grantor>");
            buffer.append(columnPrivileges.getString("GRANTOR"));
            buffer.append("</grantor><grantee>");
            buffer.append(columnPrivileges.getString("GRANTEE"));
            buffer.append("</grantee><privilege>");
            buffer.append(columnPrivileges.getString("PRIVILEGE"));
            buffer.append("</privilege><is_grantable>");
            buffer.append(columnPrivileges.getString("IS_GRANTABLE"));
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
        catch (Exception ignore) {
		}
    }

    /**
     * Get Column Privileges: retrieves a description of the access
     * rights for each table available in a catalog. Note that a
     * table privilege applies to one or more columnPrivileges in the table.
     * It would be wrong to assume that this privilege applies to
     * all columnPrivileges (this may be true for some systems but is not
     * true for all.) The result is returned as a ResultSet object.
     *
     * In JDBC, Each privilege description has the following columnPrivileges:
     *
     * TABLE_CAT String => table catalog (may be null)
     * TABLE_SCHEM String => table schema (may be null)
     * TABLE_NAME String => table name
     * COLUMN_NAME String => column name
     * GRANTOR => grantor of access (may be null)
     * GRANTEE String => grantee of access
     * PRIVILEGE String => name of access (SELECT, INSERT,
     * UPDATE, REFERENCES, ...)
     * IS_GRANTABLE String => "YES" if grantee is permitted to grant
     * to others; "NO" if not; null if unknown
     *
     *
     * @param conn the Connection object
     * @param catalog a catalog.
     * @param schema a schema.
     * @param table a table name
     * @param columnNamePattern column name pattern;
     * @return a ResultSet object
     * @exception Failed to get the Get Column Privileges.
     */
    public static ResultSet getColumnPrivileges(Connection conn,
            String catalog,
            String schema,
            String table,
            String columnNamePattern)
        throws Exception {
        if ((table == null) || (table.length() == 0)) {
            return null;
        }
        DatabaseMetaData meta = conn.getMetaData();

        if (meta == null) {
            return null;
        }
        // The '_' character represents any single character.
        // The '%' character represents any sequence of zero
        // or more characters.
        return meta.getColumnPrivileges(catalog, schema, table,
                columnNamePattern);
    }
}
