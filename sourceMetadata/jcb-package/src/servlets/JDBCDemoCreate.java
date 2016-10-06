import java.util.*;
import java.sql.*;


public class JDBCDemoCreate {

    private static final String hostname = "localhost";
    private static final String port = "3306";
    private static final String database = "db";
    private static final String username = "username";
    private static final String password = "password";

	public JDBCDemoCreate() {

		Connection c = getConnection();

		try {
			Statement s = (Statement)c.createStatement();
			s.executeUpdate("CREATE TABLE rich ( id int primary key, name char(25) )");
		} catch ( Exception e ) {
			e.printStackTrace();
		}

	}

   private Connection getConnection() {
		// Register the driver using Class.forName()
		try {
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

		// Register the driver via the system properties variable "jdbc.drivers"
		// Properties p = System.getProperties();
		// p.put("jdbc.drivers", "org.gjt.mm.mysql.Driver");
		// System.setProperties(p);

		// ask the DriverManager for a connection to the database
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + hostname +
														  ":"+ port +"/" + database +
														  "?user=" + username +
														  "&password=" + password );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void main( String[] args ) {
		new JDBCDemoCreate();
	}
}
