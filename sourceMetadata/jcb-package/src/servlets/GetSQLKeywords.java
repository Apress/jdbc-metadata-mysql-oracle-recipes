import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import java.io.PrintWriter;
import java.io.IOException;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class GetSQLKeywords extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
        Connection conn = null;
        try {
            String dbVendor = request.getParameter("vendor").trim();
            String outputFormat = request.getParameter("format").trim();
            conn = VeryBasicConnectionManager.getConnection(dbVendor);
            List<String> sqlKeywords = getSQLKeywords(conn);
            if (outputFormat.equals("xml")) {
                printXML(response, sqlKeywords);
            }
            else {
                printHTML(response, sqlKeywords);
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
                                  List<String> sqlKeywords)
        throws Exception  {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>SQL Keywords</TH></TR>");
        for (int i=0; i < sqlKeywords.size(); i++) {
           buffer.append("<TR><TD>");
           buffer.append(sqlKeywords.get(i));
           buffer.append("</TD></TR>");
        }
        buffer.append("</table></body></html>");
        out.println(buffer.toString());
    }
    private static void printXML(HttpServletResponse response,
                                 List<String> sqlKeywords)
        throws Exception  {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        buffer.append("<sqlKeywords>");
        for (int i=0; i < sqlKeywords.size(); i++) {
           buffer.append("<keyword>");
           buffer.append(sqlKeywords.get(i));
           buffer.append("</keyword>");
        }
        buffer.append("</sqlKeywords>");
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
     public static List<String> getSQLKeywords(Connection conn)
        throws Exception {
         DatabaseMetaData meta = conn.getMetaData();
         if (meta == null) {
             return null;
         }

         String sqlKeywords = meta.getSQLKeywords();
         if ((sqlKeywords == null) || (sqlKeywords.length() == 0)) {
             return null;
         }

         List<String> list = new ArrayList<String>();
         // SQL keywords are separated by ","
         StringTokenizer st = new StringTokenizer(sqlKeywords, ",");
         while(st.hasMoreTokens()) {
             list.add(st.nextToken().trim());
         }
         System.out.println("--------------");
         return list;
    }
}



