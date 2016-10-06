import java.io.*;
import java.sql.*;
import javax.sql.*;

import javax.sql.rowset.WebRowSet;
import com.sun.rowset.WebRowSetImpl;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class DemoWebRowSet {
   WebRowSet webRS = null;

   public static void main(String[] args) {
       String dbVendor = args[0]; // {"mysql", "oracle", "odbc"}
       DemoWebRowSet demo = new DemoWebRowSet();
       Connection conn = null;
       try {
           conn = VeryBasicConnectionManager.getConnection(dbVendor);
           demo.populateRowSet(conn);
           // note that WebRowSet is a "disconnected" object
           DatabaseUtil.close(conn);
           demo.writeXML();
       }
       catch (Exception e) {
          e.printStackTrace();
       }
   }

   void populateRowSet(Connection conn) throws Exception {
      ResultSet rs = null;
      Statement stmt = null;
      try {
         stmt = conn.createStatement();
         String query = "SELECT id, name, age FROM employees";
         rs = stmt.executeQuery(query);

         webRS = new WebRowSetImpl();
         webRS.setCommand(query);
         webRS.execute(conn);
      }
      finally {
         DatabaseUtil.close(rs);
         DatabaseUtil.close(stmt);
      }
   }

   void writeXML() throws Exception {
      if (webRS == null) {
         System.out.println("No data found");
         return;
      }

      String filename = "c:\\temp\\emps.xml";           // Windows
      // String filename = "/home/alex/myrowset/emps.xml";  // UNIX
      java.io.FileWriter writer = new java.io.FileWriter(filename);
      webRS.writeXml(writer);
   }
}



// webrowset example:
//http://www-128.ibm.com/developerworks/db2/library/techarticle/dm-0503bhogal/