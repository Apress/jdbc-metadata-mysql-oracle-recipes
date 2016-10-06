import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class DemoGetImportedKeys_Oracle {

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
      		System.out.println("------DemoGetImportedKeys_Oracle begin---------");

      		conn = getConnection();
      		System.out.println("DemoGetImportedKeys_Oracle: conn="+conn);
			String tableName = args[0];
			System.out.println("tableName=" + tableName);
			String importedKeysAsXML = DatabaseMetaDataTool.getImportedKeys(conn, null, "SCOTT", tableName);
			System.out.println("importedKeysAsXML=" + importedKeysAsXML);
      		System.out.println("------DemoGetImportedKeys_Oracle end---------");
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