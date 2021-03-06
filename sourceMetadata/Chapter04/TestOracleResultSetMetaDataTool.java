import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class TestOracleResultSetMetaDataTool {


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
      	Statement stmt = null;
      	ResultSet rs = null;
    	try {
      		conn = getConnection();
      		// Create a result set
      		stmt = conn.createStatement();
      		rs = stmt.executeQuery("SELECT * FROM employees");

      		System.out.println("-------- getResultSetMetaData -------------");
      		System.out.println("conn="+conn);
			String rsMetaData = ResultSetMetaDataTool.getResultSetMetaData(rs);
      		System.out.println(rsMetaData);
      		System.out.println("------------------------------------");
        }
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			DatabaseUtil.close(stmt);
			DatabaseUtil.close(rs);
			DatabaseUtil.close(conn);
		}
	}

}
