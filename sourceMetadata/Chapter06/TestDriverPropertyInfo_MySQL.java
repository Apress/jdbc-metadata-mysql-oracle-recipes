import java.sql.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.sql.DriverPropertyInfo;

import jcb.util.DatabaseUtil;

public class TestDriverPropertyInfo_MySQL {

    public static Connection getConnection() throws Exception {
        String driver = "org.gjt.mm.mysql.Driver";
        String url = "jdbc:mysql://localhost/octopus";
        String username = "root";
        String password = "root";
        Class.forName(driver);  // load MySQL driver
        return DriverManager.getConnection(url, username, password);
    }

    public static void loadDriver(String dbVendor) throws Exception {
        String mysqlDriver = "org.gjt.mm.mysql.Driver";
        String oracleDriver = "oracle.jdbc.driver.OracleDriver";
        if (dbVendor.equalsIgnoreCase("mysql")) {
            Class.forName(mysqlDriver);  // load MySQL driver
        }
        else if (dbVendor.equalsIgnoreCase("oracle")) {
            Class.forName(oracleDriver);  // load Oracle driver
        }
        else {
            throw new Exception("db vendor not supported");
        }
    }

    public static String getDriverPropertyInfoAsXML
        (DriverPropertyInfo[] properties) throws Exception {

       // If the driver is poorly implemented,
       //   a null object may be returned.
       if(properties == null) {
           return null;
       }

       // List all properties.
       StringBuilder buffer = new StringBuilder();
       buffer.append("<driver_property_info>");
       for(int i = 0; i < properties.length; i++) {
           // Get the property metadata
           String name = properties[i].name;
           boolean required = properties[i].required;
           buffer.append("<property  name=\"");
           buffer.append(name);
           buffer.append("\"  required=\"");
           buffer.append(required);
           buffer.append("\">");

           String value = properties[i].value;
           buffer.append("<value>");
           buffer.append(value);
           buffer.append("</value>");

           String  description = properties[i].description;
           buffer.append("<description>");
           buffer.append(description);
           buffer.append("</description>");

           String[] choices = properties[i].choices;
           buffer.append("<choices>");

           if(choices != null) {
               for(int j = 0; j < choices.length; j++) {
                   buffer.append("<choice>");
                   buffer.append(choices[j]);
                   buffer.append("</choice>");
               }
           }
           buffer.append("</choices>");
           buffer.append("</property>");

       }
       buffer.append("</driver_property_info>");
       return buffer.toString();
    }

    public static void main(String[] args) {
		Connection conn = null;
        try {
            String dbVendor = args[0]; // { "mysql", "oracle" }
            loadDriver(dbVendor);
            // start with the least amount of information
            // to see the full list of choices
            // we could also enter with a URL and Properties
            // provided by a user.
            String url = null;
            String mysqlURL = "jdbc:mysql://localhost/octopus";
            String oracleURL = "jdbc:oracle:thin:@localhost:1521:caspian";
            if (dbVendor.equalsIgnoreCase("mysql")) {
               url = mysqlURL;
            }
            else if (dbVendor.equalsIgnoreCase("oracle")) {
               url = oracleURL;
            }
            else {
                throw new Exception("db vendor not supported");
            }

            Properties info = new Properties();
            Driver driver = DriverManager.getDriver(url);
            System.out.println("driver="+driver);
            DriverPropertyInfo[] attributes = driver.getPropertyInfo(url, info);
            System.out.println("attributes="+attributes);
            // zero length means a connection attempt can be made

            System.out.println("Resolving properties for: " +
                   driver.getClass().getName());
            System.out.println(getDriverPropertyInfoAsXML(attributes));

            // you can insert code here to process the array, e.g.,
            // display all options in a GUI and allow the user to
            // pick and then set the attributes in info or URL.

            // try the connection
            conn = DriverManager.getConnection(url, info);
            System.out.println("conn="+conn);
            System.out.println("----------");
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        finally {
            DatabaseUtil.close(conn);
        }
    }

}
