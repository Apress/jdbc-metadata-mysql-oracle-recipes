import java.sql.*;
import javax.sql.*;
import javax.sql.rowset.*;
import com.sun.rowset.*;

public class CustomRowSetReader implements RowSetReader {

  public CustomRowSetReader() {
      System.out.println("CustomRowSetReader: constructor.");
  }

  public void readData(RowSetInternal caller) throws SQLException {
      System.out.println("--- CustomRowSetReader: begin. ---");
      if (caller == null) {
          System.out.println("CustomRowSetReader: caller is null.");
          return;
      }

      CachedRowSet crs = (CachedRowSet) caller;
      //CachedRowSet crs = (CachedRowSet) caller.getOriginal();

      RowSetMetaData rsmd = new RowSetMetaDataImpl();

      rsmd.setColumnCount(3);

      rsmd.setColumnType(1, Types.VARCHAR);
      rsmd.setColumnType(2, Types.INTEGER);
      rsmd.setColumnType(3, Types.VARCHAR);

      rsmd.setColumnName(1, "col1");
      rsmd.setColumnName(2, "col2");
      rsmd.setColumnName(3, "col3");

      crs.setMetaData( rsmd );
      System.out.println("CustomRowSetReader: crs.setMetaData( rsmd );");

      crs.moveToInsertRow();

      crs.updateString( 1, "StringCol11" );
      crs.updateInt( 2, 1 );
      crs.updateString( 3, "StringCol31" );
      crs.insertRow();
      System.out.println("CustomRowSetReader: crs.insertRow() 1");

      crs.updateString( 1, "StringCol12" );
      crs.updateInt( 2, 2 );
      crs.updateString( 3, "StringCol32" );
      crs.insertRow();
      System.out.println("CustomRowSetReader: crs.insertRow() 2");

      crs.moveToCurrentRow();
      crs.beforeFirst();
      displayRowSet(crs);
      crs.beforeFirst();
      //crs.acceptChanges();
      System.out.println("CustomRowSetReader: end.");
   } // end readData

    static void displayRowSet(RowSet rs) throws SQLException {
        while (rs.next()) {
           System.out.println(rs.getRow() + " - " +
                        rs.getString("col1") + ":" +
                        rs.getInt("col2") + ":" + rs.getString("col3"));
        }
    }

}
