import java.util.*; // required for registering the driver via the system properties
import java.sql.*;


public class JDBCDemoUpdate {

    private static final String hostname = "localhost";
    private static final String port = "3306";
    private static final String database = "db";
    private static final String username = "username";
    private static final String password = "password";

	public JDBCDemoUpdate() {

		Connection c = getConnection();

		int id = 1;
		String name = "Skate Boards Rule";
		String newName = "Skate Boards Drule";

		// do an INSERT
		try {
			Statement s = (Statement)c.createStatement();
			s.executeUpdate("INSERT INTO rich ( id, name ) VALUES (" + id +
								 ",'" + name + "')");
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		// do an UPDATE
		try {
			Statement s = (Statement)c.createStatement();
			s.executeUpdate("UPDATE rich SET name = '" + newName + "' " +
								 "WHERE id = " + id );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

	}

   private Connection getConnection() {
		// Register the driver using Class.forName()
		try {
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
		} catch ( Exception e ) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void main( String[] args ) {
		new JDBCDemoUpdate();
	}
}
