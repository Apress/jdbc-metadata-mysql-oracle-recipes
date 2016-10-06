import java.io.*;
import java.sql.*;
import javax.sql.*;

import javax.sql.rowset.WebRowSet;
import com.sun.rowset.WebRowSetImpl;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class WebRowSetMetaDataExample {
   WebRowSet webRS;

   public static void main(String[] args) {
       String dbVendor = args[0]; // {"mysql", "oracle", "odbc"}
       WebRowSetMetaDataExample wrse = new WebRowSetMetaDataExample();
       Connection conn = null;
       try {
           conn = VeryBasicConnectionManager.getConnection(dbVendor);
           wrse.populateRowSet(conn);
           wrse.writeXml();
       }
       catch (Exception e) {
          e.printStackTrace();
       }
       finally {
          DatabaseUtil.close(conn);
       }
   }

   void populateRowSet(Connection conn) throws Exception {
       System.out.println("Querying database for metadata only...");
       String sqlQuery = "SELECT * FROM ztest WHERE 1=0";
       System.out.println("sqlQuery="+sqlQuery);

       webRS = new WebRowSetImpl();
       webRS.setCommand(sqlQuery);
       webRS.execute(conn);
   }

   void writeXml() throws SQLException, IOException {
      if (webRS == null) {
         System.out.println("No data found.");
         return;
      }
      FileWriter fw = null;
      try {
         File file = new File("metadata.xml");
         fw = new FileWriter(file);
         System.out.println("Writing db data to file " + file.getAbsolutePath());
         webRS.writeXml(fw);

         // convert xml to a String object
         StringWriter sw = new StringWriter();
         webRS.writeXml(sw);
         System.out.println("==============");
         System.out.println(sw.toString());
         System.out.println("==============");
      }
      finally {
         fw.flush();
         fw.close();
      }
   }
}
