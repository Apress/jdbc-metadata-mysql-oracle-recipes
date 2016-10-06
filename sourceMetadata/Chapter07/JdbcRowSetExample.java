import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

import javax.sql.rowset.JdbcRowSet;
import com.sun.rowset.JdbcRowSetImpl;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class JdbcRowSetExample {


   JdbcRowSet jdbcRS;

   public static void main(String[] args) {
     String dbVendor = args[0]; // {"mysql", "oracle", "odbc"}
     JdbcRowSetExample jrse = new JdbcRowSetExample();
     Connection conn = null;
     try {
        conn = VeryBasicConnectionManager.getConnection(dbVendor);
        jrse.populateRowSet(conn);
        jrse.createEvents();
     }
     catch (Exception e) {
         // handle exception
         e.printStackTrace();
     }
     finally {
        DatabaseUtil.close(jrse.jdbcRS);
        DatabaseUtil.close(conn);
     }
  }

  void populateRowSet(Connection conn)
     throws ClassNotFoundException, SQLException {
     jdbcRS = new JdbcRowSetImpl(conn);
     jdbcRS.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);
     String sql = "SELECT * FROM ztest";
     jdbcRS.setCommand(sql);
     jdbcRS.execute();
     jdbcRS.addRowSetListener(new ExampleListener());
  }

  void createEvents()
     throws SQLException, ClassNotFoundException {
     while (jdbcRS.next()) {
        //each call to next, generates a cursorMoved event
        System.out.println("id="+jdbcRS.getString(1));
        System.out.println("name="+jdbcRS.getString(2));
        System.out.println("-----------");
     }
  }
}
