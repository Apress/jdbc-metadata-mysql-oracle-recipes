import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;


public class TestMySqlTableResultSetMetaDataTool_WebRowSet {


	public static Connection getConnection() throws Exception {

		String driver = "org.gjt.mm.mysql.Driver";
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
      		System.out.println("---- get Table's MetaData using WebRowSet class ----");
      		System.out.println("conn="+conn);
      		String deptTableName = "zperson";
			String rsMetaData =
				ResultSetMetaDataTool.getTableMetaDataUsingWebRowSet(conn, deptTableName);
      		System.out.println(rsMetaData);
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
