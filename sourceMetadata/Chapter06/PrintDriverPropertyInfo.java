import java.sql.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.sql.DriverPropertyInfo;
import jcb.util.DatabaseUtil;

public class PrintDriverPropertyInfo {

	// list of drivers to be tested.
	public static final String MYSQL_DRIVER =
	"com.mysql.jdbc.Driver";
	public static final String ORACLE_DRIVER =
	"oracle.jdbc.driver.OracleDriver";
	public static final String JDBC_ODBC_BRIDGE_DRIVER =
	"sun.jdbc.odbc.JdbcOdbcDriver";

	public static void loadDriver(String dbVendor) throws Exception {
		if (dbVendor.equalsIgnoreCase("mysql")) {
		Class.forName(MYSQL_DRIVER); // load MySQL driver
		}
		else if (dbVendor.equalsIgnoreCase("oracle")) {
		Class.forName(ORACLE_DRIVER); // load Oracle driver
		}
		else if (dbVendor.equalsIgnoreCase("jdbc-odbc")) {
		// load JdbcOdbcDriver
		Class.forName(JDBC_ODBC_BRIDGE_DRIVER);
		}
		else {
		throw new Exception("db vendor not supported");
		}
	}

	static void printDriverPropertyInfo(DriverPropertyInfo[] properties)
	throws Exception {
		// if the driver is poorly implemented,
		// a null object may be returned.
		if(properties == null) {
			return;
		}
		// list all connection properties.
		for (int i = 0; i < properties.length; i++) {
		// get the property metadata
		String name = properties[i].name;
		String[] choices = properties[i].choices;
		boolean required = properties[i].required;
		String description = properties[i].description;
		// printout property metadata
		System.out.println("" + name +
		" (Required: " + required + ")");
		if(choices == null) {
			System.out.println(" No choices.");
		}
		else {
			System.out.print(" Choices are: ");
			for(int j = 0; j < choices.length; j++) {
				System.out.print(" " + choices[j]);
			}
		}
		System.out.println(" Description: " + description);
		}
	}

	public static void main(String[] args)throws Exception {
		String dbVendor = args[0]; // { "mysql", "oracle", "jdbc-odbc"}
		loadDriver(dbVendor);
		// start with the least amount of information
		// to see the full list of choices; we could also
		// enter with a URL and Properties provided by a user.
		// mysql URL = "jdbc:mysql://localhost/octopus";
		// oracle URL = "jdbc:oracle:thin:@localhost:1521:caspian";
		// JdbcOdbc URL = "jdbc:odbc:northwind";
		String url = args[1]; // database url
		Properties info = new Properties();
		Driver driver = DriverManager.getDriver(url);
		System.out.println("driver="+driver);
		DriverPropertyInfo[] attributes =
		driver.getPropertyInfo(url, info);
		System.out.println("attributes="+attributes);
		// zero length means a connection attempt can be made
		System.out.println("Resolving properties for: " +
		driver.getClass().getName());
		printDriverPropertyInfo(attributes);
		// you can insert code here to process the array, e.g.,
		// display all options in a GUI and allow the user to
		// pick and then set the attributes in info or URL.
		// try the connection
		// Connection conn = DriverManager.getConnection(url, info);
		// System.out.println("conn="+conn);
		System.out.println("----------");
	}
}

