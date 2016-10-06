import java.io.PrintWriter;
import java.io.IOException;

import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class MyDatabaseServlet extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
        String query = "SELECT id, name, age FROM employees";
        ResultSet rs = null;
        Statement stmt = null;
        Connection conn = null;
        try {
            String dbVendor = request.getParameter("vendor").trim();
            conn = VeryBasicConnectionManager.getConnection(dbVendor);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            printResultSet(response, rs );
        }
        catch(Exception e) {
            printError(response, e.getMessage());
        }
        finally {
            DatabaseUtil.close(rs);
            DatabaseUtil.close(stmt);
            DatabaseUtil.close(conn);
        }

    } // end doGet

    private static void printResultSet(HttpServletResponse response,
                                       ResultSet rs)
        throws Exception  {
        PrintWriter out = response.getWriter();
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html><body><table border=1 cellspacing=0 cellpadding=0>");
        buffer.append("<TR><TH>id</TH><TH>name</TH><TH>age</TH></TR>");
        while (rs.next()) {
           int id = rs.getInt(1);
           String name = rs.getString(2);
           int age = rs.getInt(3);
           buffer.append("<TR><TD>"+id+"</TD><TD>"+name+"</TD><TD>"+age+"</TD></TR>");
        }
        buffer.append("</table></body></html>");
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
}
