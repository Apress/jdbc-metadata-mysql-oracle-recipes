package jcb.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * This class provides very basic connection management
 * for demonstration purposes only. In production environment,
 * you should replace these with production quality "connection
 * pool management" functionality (such as Apache's Excalibur).
 *
 * @author Mahmoud Parsian
 */
public class VeryBasicConnectionManager {

    static final String DB_VENDOR_MYSQL  = "mysql";
    static final String DB_VENDOR_ORACLE = "oracle";
    static final String DB_VENDOR_ODBC = "odbc";

    static final String JDBC_DRIVER_MYSQL  =
       "org.gjt.mm.mysql.Driver";
    static final String JDBC_DRIVER_ORACLE =
       "oracle.jdbc.driver.OracleDriver";
    static final String JDBC_DRIVER_ODBC =
       "sun.jdbc.odbc.JdbcOdbcDriver";

    public static Connection getConnection_MySQL() throws Exception {
        //String url = "jdbc:mysql://localhost/octopus";
        String url = "jdbc:mysql://10.0.100.72/octopus";
        String username = "root";
        String password = "root";
        Class.forName(JDBC_DRIVER_MYSQL);  // load MySQL driver
        return DriverManager.getConnection(url, username, password);
    }

    public static Connection getConnection_Oracle() throws Exception {
        String url = "jdbc:oracle:thin:@localhost:1521:caspian";
        String username = "scott";
        String password = "tiger";
        Class.forName(JDBC_DRIVER_ORACLE);  // load Oracle driver
        return DriverManager.getConnection(url, username, password);
    }

    public static Connection getConnection_JDBC_ODBC() throws Exception {
        String url = "jdbc:odbc:northwind";
        String username = "";
        String password = "";
        Class.forName(JDBC_DRIVER_ODBC);  // load bridge driver
        return DriverManager.getConnection(url, username, password);
    }

    public static Connection getConnection(String dbVendor)
        throws Exception {
        if (dbVendor.equalsIgnoreCase(DB_VENDOR_MYSQL)) {
            return getConnection_MySQL();
        }
        else if (dbVendor.equalsIgnoreCase(DB_VENDOR_ORACLE)) {
            return getConnection_Oracle();
        }
        else if (dbVendor.equalsIgnoreCase(DB_VENDOR_ODBC)) {
            return getConnection_JDBC_ODBC();
        }
        //else if (dbVendor.equalsIgnoreCase("XXX")) {
        //    return getConnection_XXX();
        //}
        else {
            throw new Exception("unknown db vendor");
        }
    }
}