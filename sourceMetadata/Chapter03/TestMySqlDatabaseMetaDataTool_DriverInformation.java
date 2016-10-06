import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class TestMySqlDatabaseMetaDataTool_DriverInformation {

	public static Connection getConnection() throws Exception {

		String driver = "org.gjt.mm.mysql.Driver";
    	//String url = "jdbc:mysql://localhost/tiger";
    	String url = "jdbc:mysql://localhost/octopus";
    	String username = "root";
    	String password = "root";
		Class.forName(driver);	// load MySQL driver
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
