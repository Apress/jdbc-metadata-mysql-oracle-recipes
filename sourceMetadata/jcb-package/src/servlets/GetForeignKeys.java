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

public class GetForeignKeys extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
        Connection conn = null;
        ResultSet foreignKeys = null;
        try {
            String dbVendor = request.getParameter("vendor").trim();
            String outputFormat = request.getParameter("format").trim();
            String table = request.getParameter("table").trim();
            conn = VeryBasicConnectionManager.getConnection(dbVendor);

            if (dbVendor.equals("mysql")) {
                String catalog = request.getParameter("catalog").trim();
                foreignKeys = getForeignKeys(conn,
                                             catalog, // catalog,
                                             null,    // schema
                                             table);
            }
            else if (dbVendor.equals("oracle")) {
                String schema = request.getParameter("schema").trim();
                foreignKeys = getForeignKeys(conn,
                                             null,   // catalog,
                                             schema, // schema
                                             table);
            }
            else {
               printError(response, "unknown db vendor");
               return;
            }

            if (outputFormat.equals("xml")) {
                printXML(response, foreignKeys);
            }
            else {
                printHTML(response, foreignKeys);
            }

        }
        catch(Exception e) {
            e.printStackTrace();
            printError(response, e.getMessage());
        }
        finally {
            DatabaseUtil.close(foreignKeys);
            DatabaseUtil.close(conn);
        }

    } // end doGet


    private static void printHTML(HttpServletResponse response,
                                  ResultSet foreignKeys)
        throws Exception  {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>PK Catalog</TH>");
        buffer.append("<TH>PK Schema</TH>");
        buffer.append("<TH>PK Table</TH>");
        buffer.append("<TH>PK Column</TH>");
        buffer.append("<TH>FK Catalog</TH>");
        buffer.append("<TH>FK Schema</TH>");
        buffer.append("<TH>FK Table</TH>");
        buffer.append("<TH>FK Column</TH>");
        buffer.append("<TH>FK Seq.</TH>");
        buffer.append("<TH>Update Rule</TH>");
        buffer.append("<TH>Delete Rule</TH>");
        buffer.append("<TH>FK Name</TH>");
        buffer.append("<TH>PK Name</TH>");
        buffer.append("<TH>Deferrability</TH></TR>");
        while (foreignKeys.next()) {
            buffer.append("<TR><TD>");
            buffer.append(foreignKeys.getString("PKTABLE_CAT"));
            buffer.append("</TD><TD>");
            buffer.append(foreignKeys.getString("PKTABLE_SCHEM"));
            buffer.append("</TD><TD>");
            buffer.append(foreignKeys.getString("PKTABLE_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(foreignKeys.getString("PKCOLUMN_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(foreignKeys.getString("FKTABLE_CAT"));
            buffer.append("</TD><TD>");
            buffer.append(foreignKeys.getString("FKTABLE_SCHEM"));
            buffer.append("</TD><TD>");
            buffer.append(foreignKeys.getString("FKTABLE_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(foreignKeys.getString("FKCOLUMN_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(foreignKeys.getShort("KEY_SEQ"));
            buffer.append("</TD><TD>");
            short updateRule = foreignKeys.getShort("UPDATE_RULE");
            buffer.append(getUpdateRule(updateRule));
            buffer.append("</TD><TD>");
            short deleteRule = foreignKeys.getShort("DELETE_RULE");
            buffer.append(getDeleteRule(deleteRule));
            buffer.append("</TD><TD>");
            buffer.append(foreignKeys.getString("FK_NAME"));
            buffer.append("</TD><TD>");
            buffer.append(foreignKeys.getString("PK_NAME"));
            buffer.append("</TD><TD>");
            short deferrability = foreignKeys.getShort("DEFERRABILITY");
            buffer.append(getDeferrability(deferrability));
            buffer.append("</TD><TR>");
       }
       buffer.append("</table></body></html>");
       out.println(buffer.toString());
    }

    private static void printXML(HttpServletResponse response,
                                 ResultSet foreignKeys)
        throws Exception  {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<exported_keys>");
        while (foreignKeys.next()) {
            buffer.append("<exported_key><pk_table_catalog>");
            buffer.append(foreignKeys.getString("PKTABLE_CAT"));
            buffer.append("</pk_table_catalog><pk_table_schema>");
            buffer.append(foreignKeys.getString("PKTABLE_SCHEM"));
            buffer.append("</pk_table_schema><pk_table_name>");
            buffer.append(foreignKeys.getString("PKTABLE_NAME"));
            buffer.append("</pk_table_name><pk_column_name>");
            buffer.append(foreignKeys.getString("PKCOLUMN_NAME"));
            buffer.append("</pk_column_name><fk_table_catalog>");
            buffer.append(foreignKeys.getString("FKTABLE_CAT"));
            buffer.append("</fk_table_catalog><fk_table_schema>");
            buffer.append(foreignKeys.getString("FKTABLE_SCHEM"));
            buffer.append("</fk_table_schema><fk_table_name>");
            buffer.append(foreignKeys.getString("FKTABLE_NAME"));
            buffer.append("</fk_table_name><fk_column_name>");
            buffer.append(foreignKeys.getString("FKCOLUMN_NAME"));
            buffer.append("</fk_column_name><key_sequence>");
            buffer.append(foreignKeys.getString("KEY_SEQ"));
            buffer.append("</key_sequence><update_rule>");
            short updateRule = foreignKeys.getShort("UPDATE_RULE");
            buffer.append(getUpdateRule(updateRule));
            buffer.append("</update_rule><delete_rule>");
            short deleteRule = foreignKeys.getShort("DELETE_RULE");
            buffer.append(getDeleteRule(deleteRule));
            buffer.append("</delete_rule><fk_name>");
            buffer.append(foreignKeys.getString("FK_NAME"));
            buffer.append("</fk_name><pk_name>");
            buffer.append(foreignKeys.getString("PK_NAME"));
            buffer.append("</pk_name><deferrability>");
            short deferrability = foreignKeys.getShort("DEFERRABILITY");
            buffer.append(getDeferrability(deferrability));
            buffer.append("</deferrability></exported_key>");
        }
        buffer.append("</exported_keys>");
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
     * Get the Foreign Keys.
     * @param conn the Connection object
     * @catalog database catalog name
     * @schemaPattern database schema pattern
     * @procedureNamePattern database procedure name pattern
     * @return a table of Foreign Keys as a ResultSet object.
     *
     */
    public static ResultSet getForeignKeys(Connection conn,
                                           String catalog,
                                           String schema,
                                           String table)
         throws Exception {

         if (table == null) {
             return null;
         }

         DatabaseMetaData meta = conn.getMetaData();
         if (meta == null) {
             return null;
         }

         return meta.getImportedKeys(catalog, schema, table.toUpperCase());
     }


     private static String getUpdateRule(short updateRule) {
        if (updateRule == DatabaseMetaData.importedKeyNoAction) {
            return "importedKeyNoAction";
        }
        else if (updateRule == DatabaseMetaData.importedKeyCascade) {
            return "importedKeyCascade";
        }
        else if (updateRule == DatabaseMetaData.importedKeySetNull) {
            return "importedKeySetNull";
        }
        else if (updateRule == DatabaseMetaData.importedKeySetDefault) {
            return "importedKeySetDefault";
        }
        else if (updateRule == DatabaseMetaData.importedKeyRestrict) {
            return "importedKeyRestrict";
        }
        else {
            return "nobody knows";
        }
     }

     private static String getDeleteRule(short deleteRule) {
        if (deleteRule == DatabaseMetaData.importedKeyNoAction) {
            return "importedKeyNoAction";
        }
        else if (deleteRule == DatabaseMetaData.importedKeyCascade) {
            return "importedKeyCascade";
        }
        else if (deleteRule == DatabaseMetaData.importedKeySetNull) {
            return "importedKeySetNull";
        }
        else if (deleteRule == DatabaseMetaData.importedKeyRestrict) {
            return "importedKeyRestrict";
        }
        else if (deleteRule == DatabaseMetaData.importedKeySetDefault) {
            return "importedKeySetDefault";
        }
        else {
            return "nobody knows";
        }
     }

     private static String getDeferrability(short deferrability) {
        if (deferrability == DatabaseMetaData.importedKeyInitiallyDeferred) {
            return "importedKeyInitiallyDeferred";
        }
        else if (deferrability == DatabaseMetaData.importedKeyInitiallyImmediate) {
            return "importedKeyInitiallyImmediate";
        }
        else if (deferrability == DatabaseMetaData.importedKeyNotDeferrable) {
            return "importedKeyNotDeferrable";
        }
        else {
            return "nobody knows";
        }
     }
}
