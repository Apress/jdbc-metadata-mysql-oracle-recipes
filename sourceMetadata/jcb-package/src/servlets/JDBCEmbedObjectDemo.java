import java.sql.*;
import java.io.*;

public class JDBCEmbedObjectDemo {

    private static final String hostname = "localhost";
    private static final String port = "3306";
    private static final String database = "db";
    private static final String username = "username";
    private static final String password = "password";

	JDBCEmbedObjectDemo() {
		Connection c = getConnection();

		// see if the table exists and create it if needed.
		if ( !tableExists("object_demo",c) )
			createObjectTable(c);

		// the id,string and object to store.
		String str = new String("I am an object called String.");
		int id = 1;
		String base64encoding = new String(); // a place to store the object

		// turn it into an Object byte array to Base64 String.
	   try {
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream( o );
			oos.writeObject( str );
			oos.flush();
			base64encoding = Base64.encode( o.toByteArray() );
		} catch ( Exception e ) {
			System.out.println("base64 encoding exception");
			e.printStackTrace();
		}

		// insert the object, string etc.
		try {
			Statement s = (Statement)c.createStatement();
			s.executeUpdate("INSERT INTO object_demo ( id, string, object ) VALUES (" + id +
								 ",'" + str + "', '" + base64encoding + "')");
		} catch ( Exception e ) {
			System.out.println("insert exception");
			e.printStackTrace();
		}

		// retreive the object
		String objectFromDB = new String();
		try {
			Statement s = (Statement)c.createStatement();
			ResultSet r = s.executeQuery("SELECT object FROM object_demo WHERE id=" + id );
			objectFromDB = r.getString("object");
		} catch ( Exception e ) {
			System.out.println("select exception");
			e.printStackTrace();
		}


		// revive the object and display
		try {
			byte b[] = Base64.decode( objectFromDB );
			ByteArrayInputStream in = new ByteArrayInputStream( b );
			ObjectInputStream ois = new ObjectInputStream( in );
			System.out.println("object from db is \"" +
									 (String)ois.readObject() + "\".");
		} catch ( Exception e ) {
			System.out.println("base64 decoding exception");
			e.printStackTrace();
		}

		// close the db
		try { c.close(); } catch (Exception e) {}
	}


	public void createObjectTable( Connection c ) {

		try {
			Statement s = (Statement)c.createStatement();
			s.executeUpdate("CREATE TABLE object_demo ( " +
								 "id INT PRIMARY KEY, " +
								 "string CHAR(25), " +
								 "object BLOB )");
		} catch ( Exception e ) {
			System.out.println("create exception");
			e.printStackTrace();
		}

	}

	public boolean tableExists( String tableName, Connection c ) {
		boolean returnVal = false;

		try {
			Statement s = (Statement)c.createStatement();
			ResultSet r = s.executeQuery("SHOW TABLES");

			while ( r.next() ) {
				if ( !r.wasNull() && r.getString(1).equals( tableName ) ) {
					returnVal = true;
					break;
				}
			}

		} catch ( Exception e ) {
			System.out.println("show exception");
			e.printStackTrace();
		}

		return returnVal;
	}


   private Connection getConnection() {
		try {
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
		} catch ( Exception e ) {
			e.printStackTrace();
		}

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
		new JDBCEmbedObjectDemo();
	}

}
