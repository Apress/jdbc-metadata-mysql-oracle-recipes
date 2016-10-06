import java.sql.Types;
import java.sql.Connection;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.CachedRowSet;
import com.sun.rowset.CachedRowSetImpl;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class CreateCustomRowSetMetaData {

   public static void main(String[] args) {
       String dbVendor = args[0]; // {"mysql", "oracle", "odbc"}
       CachedRowSet crs = null;
       Connection conn = null;
       RowSetMetaData rsMD = null;
       try {
           // create a custom made RowSetMetaData object
           rsMD = createRowSetMetaData();

           // use a custom made RowSetMetaData object for CachedRowSet object
           crs = new CachedRowSetImpl();
           crs.setMetaData(rsMD);

           crs.moveToInsertRow();
           crs.updateString(1, "1111");
           crs.updateString(2, "alex");
           crs.insertRow();

           crs.moveToInsertRow();
           crs.updateString(1, "2222");
           crs.updateString(2, "jane");
           crs.insertRow();

           //
           // if you want to commit changes from a CachedRowSet
           // object to your desired datasource, then you must
           // create a Connection object.
           //
           conn = VeryBasicConnectionManager.getConnection(dbVendor);

           // moves the cursor to the remembered cursor position, usually
           // the current row. This method has no effect if the cursor is
           // not on the insert row.
           crs.moveToCurrentRow();

           // when the method acceptChanges() is executed, the CachedRowSet
           // object's writer, a RowSetWriterImpl object, is called behind the
           // scenes to write the changes made to the rowset to the underlying
           // data source. The writer is implemented to make a connection to
           // the data source and write updates to it.
           crs.acceptChanges(conn);
       }
       catch (Exception e) {
          e.printStackTrace();
       }
       finally {
          DatabaseUtil.close(conn);
       }
   }

   static RowSetMetaData createRowSetMetaData()
      throws Exception {
      // create a custom made RowSetMetaData object
      RowSetMetaData rsMD = new RowSetMetaDataImpl();
      rsMD.setColumnCount(2);
      rsMD.setColumnName(1, "id");
      rsMD.setColumnType(1, Types.VARCHAR);
      rsMD.setColumnName(2, "name");
      rsMD.setColumnType(2, Types.VARCHAR);
      // sets the designated column's table name, if any, to the given String.
      rsMD.setTableName(1, "ztest");
      rsMD.setTableName(2, "ztest");
      return rsMD;
   }
}


