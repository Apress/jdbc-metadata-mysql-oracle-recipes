import java.io.*;
import java.sql.*;
import javax.sql.*;

import javax.sql.rowset.WebRowSet;
import com.sun.rowset.WebRowSetImpl;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class WebRowSetExample {
   WebRowSet webRS;

   public static void main(String[] args) {
       String dbVendor = args[0]; // {"mysql", "oracle", "odbc"}
       String id = args[1];       // PK to ztest table
       WebRowSetExample wrse = new WebRowSetExample();
       Connection conn = null;
       try {
           conn = VeryBasicConnectionManager.getConnection(dbVendor);
           wrse.populateRowSet(conn, id);
           wrse.writeXml(id);
       }
       catch (Exception e) {
          e.printStackTrace();
       }
   }

   void populateRowSet(Connection conn, String id)
      throws Exception {
      ResultSet rs = null;
      Statement stmt = null;
      try {
         stmt = conn.createStatement();
         String sqlCount = "SELECT count(*) FROM ztest WHERE " +
              "id='" + id + "'";
         System.out.println("sqlCount="+sqlCount);
         rs = stmt.executeQuery(sqlCount);
         int count = 0;
         if (rs.next()) {
            count = rs.getInt(1);
         }

         webRS = null;
         if (count > 0) {
            System.out.println("Found " + count + " IDs for id " + id);
            System.out.println("Querying database for track data...");
            String sqlQuery = "SELECT * FROM ztest WHERE " + "id='" + id + "'";
            System.out.println("sqlQuery="+sqlQuery);

            webRS = new WebRowSetImpl();
            webRS.setCommand(sqlQuery);
            webRS.execute(conn);
            //RowSetMetaData rsMD = (RowSetMetaData) webRS.getMetaData();
            //System.out.println("rsMD="+rsMD);
         }
      }
      finally {
         DatabaseUtil.close(rs);
         DatabaseUtil.close(stmt);
         DatabaseUtil.close(conn);
      }
   }

   void writeXml(String id) throws SQLException, IOException {
      if (webRS == null) {
         System.out.println("No emp data found for id="+id);
         return;
      }
      FileWriter fw = null;
      try {
         File file = new File(id + ".xml");
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



