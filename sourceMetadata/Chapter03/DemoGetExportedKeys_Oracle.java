import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class DemoGetExportedKeys_Oracle {

	public static Connection getConnection() throws Exception {
		String driver = "oracle.jdbc.driver.OracleDriver";
    	String url = "jdbc:oracle:thin:@localhost:1521:caspian";
    	String username = "scott";
    	String password = "tiger";

		Class.forName(driver);	// load Oracle driver
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	public static void main(String[] args) {
    	Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
    	try {
      		System.out.println("------DemoGetExportedKeys_Oracle begin---------");

      		conn = getConnection();
      		System.out.println("DemoGetExportedKeys_Oracle: conn="+conn);

			String exportedKeysAsXML = DatabaseMetaDataTool.getExportedKeys(conn, null, "SCOTT", "DEPT_TABLE");
			System.out.println("exportedKeysAsXML=" + exportedKeysAsXML);
      		System.out.println("------DemoGetExportedKeys_Oracle end---------");
        }
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			// release database resources
			DatabaseUtil.close(conn);
		}
	}
}