import java.util.Hashtable;
import java.sql.Types;
import java.sql.SQLException;
import javax.sql.RowSetReader;
import javax.sql.RowSetInternal;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.CachedRowSet;
import com.sun.rowset.CachedRowSetImpl;
import javax.sql.rowset.spi.SyncFactory;

public class DemoCustomRowSet {

  CachedRowSet crs;
  String stringColumn1;
  String stringColumn3;
  int intColumn2;


  public DemoCustomRowSet() {
    try {
      SyncFactory.registerProvider("MySyncProvider");
      Hashtable env = new Hashtable();
      env.put(SyncFactory.ROWSET_SYNC_PROVIDER, "MySyncProvider");
      crs = new CachedRowSetImpl(env);
      crs.execute();  // load data from custom RowSetReader

      System.out.println("Fetching from RowSet...");
      while(crs.next())  {
        displayData();
      }

      if(crs.isAfterLast() == true)  {
        System.out.println("We have reached the end");
        System.out.println("crs row: " + crs.getRow());
      }

      System.out.println("And now backwards...");

      while(crs.previous()) {
        displayData();
      }  // end while previous

      if(crs.isBeforeFirst()) {
          System.out.println("We have reached the start");
      }

      crs.first();
      if(crs.isFirst()) {
          System.out.println("We have moved to first");
      }

      System.out.println("crs row: " + crs.getRow());

      if(!crs.isBeforeFirst()) {
          System.out.println("We aren't before the first row.");
      }

      crs.last();
      if(crs.isLast())  {
          System.out.println("...and now we have moved to the last");
      }

      System.out.println("crs row: " + crs.getRow());

      if(!crs.isAfterLast()) {
        System.out.println("we aren't after the last.");
      }

    } // end try
    catch (SQLException e) {
      e.printStackTrace();
      System.err.println("SQLException: " + e.getMessage());
    }

  }  // end constructor



  public void displayData() throws SQLException {
    stringColumn1 = crs.getString(1);
    if(crs.wasNull()) {
        System.out.println("stringColumn1 is null");
    }
    else {
        System.out.println("stringColumn1: " + stringColumn1);
    }

    intColumn2 = crs.getInt(2);
    if (crs.wasNull()) {
        System.out.println("intColumn2 is null");
    }
    else {
        System.out.println("intColumn2: " + intColumn2);
    }

    stringColumn3 = crs.getString(3);
    if(crs.wasNull()) {
        System.out.println("stringColumn3 is null");
    }
    else {
        System.out.println("stringColumn3: " + stringColumn3);
    }

  }  // end displayData

  public static void main(String args[]) {
    DemoCustomRowSet test = new DemoCustomRowSet();
  }

} // end class DemoCustomRowSet
