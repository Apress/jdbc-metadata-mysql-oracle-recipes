import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.BatchUpdateException;
import java.sql.DriverManager;

import jcb.util.DatabaseUtil;

public class TestBatchUpdate {

    public static Connection getConnection() throws Exception {
        String driver = "org.gjt.mm.mysql.Driver";
        String url = "jdbc:mysql://localhost/octopus";
        String username = "root";
        String password = "root";
        Class.forName(driver);  // load MySQL driver
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String args[]) {
        ResultSet rs = null;
        Statement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_UPDATABLE);
            conn.setAutoCommit(false);
            stmt.addBatch("INSERT INTO batch_table(id, name) "+
                          "VALUES('11', 'Alex')");
            stmt.addBatch("INSERT INTO batch_table(id, name) "+
                          "VALUES('22', 'Mary')");
            stmt.addBatch("INSERT INTO batch_table(id, name) "+
                          "VALUES('33', 'Bob')");
            int[] updateCounts = stmt.executeBatch();
            conn.commit();
            rs = stmt.executeQuery("SELECT * FROM batch_table");
            System.out.println("-- Table batch_table after insertion --");

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                System.out.println("id="+id +"  name="+name);
            }
        }
        catch(BatchUpdateException b) {
            System.err.println("SQLException: " + b.getMessage());
            System.err.println("SQLState: " + b.getSQLState());
            System.err.println("Message: " + b.getMessage());
            System.err.println("Vendor error code: " + b.getErrorCode());
            System.err.print("Update counts: ");
            int [] updateCounts = b.getUpdateCounts();
            for (int i = 0; i < updateCounts.length; i++) {
                System.err.print(updateCounts[i] + " ");
            }
        }
        catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            System.err.println("SQLState: " + ex.getSQLState());
            System.err.println("Message: " + ex.getMessage());
            System.err.println("Vendor error code: " + ex.getErrorCode());
        }
        catch(Exception e) {
            e.printStackTrace();
            System.err.println("Exception: " + e.getMessage());
        }
        finally {
            DatabaseUtil.close(rs);
            DatabaseUtil.close(stmt);
            DatabaseUtil.close(conn);
        }
    }
}