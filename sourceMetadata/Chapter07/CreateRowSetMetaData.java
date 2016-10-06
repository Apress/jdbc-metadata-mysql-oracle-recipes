import java.sql.Connection;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.WebRowSet;
import com.sun.rowset.WebRowSetImpl;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class CreateRowSetMetaData {

   public static void main(String[] args) {
       String dbVendor = args[0]; // {"mysql", "oracle", "odbc"}
       CreateRowSetMetaData wrse = new CreateRowSetMetaData();
       WebRowSet webRS=null;
       Connection conn = null;
       try {
           // create and populate a row set object
           conn = VeryBasicConnectionManager.getConnection(dbVendor);
           webRS = populateRowSet(conn);

           // create RowSetMetaData object
           RowSetMetaData rsMD =  (RowSetMetaData) webRS.getMetaData();
           System.out.println("rsMD="+rsMD);
           if (rsMD == null) {
              System.out.println("vendor does not support RowSetMetaData");
           }
           else {
              int columnCount = rsMD.getColumnCount();
              System.out.println("columnCount="+columnCount);
           }
       }
       catch (Exception e) {
          e.printStackTrace();
       }
       finally {
          DatabaseUtil.close(conn);
       }
   }

   static WebRowSet populateRowSet(Connection conn)
      throws Exception {
      System.out.println("Querying database for track data...");
      String sqlQuery = "SELECT * FROM ztest";
      System.out.println("sqlQuery="+sqlQuery);

      WebRowSet webRS = new WebRowSetImpl();
      webRS.setCommand(sqlQuery);
      webRS.execute(conn);
      return webRS;
   }
}


