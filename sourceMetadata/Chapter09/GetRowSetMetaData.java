import java.sql.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import javax.sql.rowset.WebRowSet;
import com.sun.rowset.WebRowSetImpl;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class GetRowSetMetaData extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
        Connection conn = null;
        try {
            String dbVendor = request.getParameter("vendor").trim();
            String sqlQuery = request.getParameter("query").trim();
            conn = VeryBasicConnectionManager.getConnection(dbVendor);

            // create metadata
            String metadata = getMetaData(conn, sqlQuery);

            // write xml to client
            response.setContentType("text/xml");
            PrintWriter out = response.getWriter();
            out.println(metadata);
        }
        catch(Exception e) {
            printError(response, e.getMessage());
        }
        finally {
            DatabaseUtil.close(conn);
        }
    } // end doGet

    private static String getMetaData(Connection conn,
                                      String sqlQuery)
      throws Exception {
      StringWriter writer = null;
      try {
         WebRowSet webRS = new WebRowSetImpl();
         webRS.setCommand(sqlQuery);
         webRS.execute(conn);

         // writer to hold and manipulate XML data.
         writer = new StringWriter();

         // generate the XML document
         webRS.writeXml(writer);

         // Convert the writer object data to a XML String.
         return writer.toString();
      }
      finally {
         // Close the Writer object.
         writer.close();
      }
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
