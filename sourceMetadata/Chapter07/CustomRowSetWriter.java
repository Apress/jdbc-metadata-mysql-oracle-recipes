import java.sql.*;
import javax.sql.*;
import javax.sql.rowset.*;
import com.sun.rowset.*;

public class CustomRowSetWriter implements RowSetWriter {

  public CustomRowSetWriter() {
      System.out.println("CustomRowSetWriter: constructor.");
  }

  public boolean writeData(RowSetInternal caller) throws SQLException {
      System.out.println("--- CustomRowSetWriter: begin. ---");
      if (caller == null) {
          System.out.println("CustomRowSetWriter: caller is null.");
          return false;
      }

      CachedRowSet crs = (CachedRowSet) caller;
      // for now do not write any data
      return true;
   }
}
