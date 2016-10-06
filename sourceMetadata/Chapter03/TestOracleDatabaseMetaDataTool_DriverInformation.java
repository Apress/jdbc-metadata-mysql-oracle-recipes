import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class TestOracleDatabaseMetaDataTool_DriverInformation {

	public static Connection getConnection() throws Exception {

		String driver = "oracle.jdbc.driver.OracleDriver";
    	String url = "jdbc:oracle:thin:@localhost:1521:maui";   // office
    	//String url = "jdbc:oracle:thin:@localhost:1521:scorpian"; // home
    	String username = "octopus";  //office
    	String password = "octopus";  // office

		Class.forName(driver);	// load Oracle driver
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	public static void main(String[] args) {

    	Connection conn = null;
    	try {
      		conn = getConnection();
      		System.out.println("-------- getDriverInformation -------------");
      		System.out.println("conn="+conn);
			String driverInfo = DatabaseMetaDataTool.getDriverInformation(conn);
      		System.out.println(driverInfo);
      		System.out.println("------------------------------------");
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
