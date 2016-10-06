import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class DemoGetImportedKeys_MySQL {

	public static Connection getConnection() throws Exception {
		String driver = "org.gjt.mm.mysql.Driver";
		String url = "jdbc:mysql://localhost/octopus";
		String username = "root";
		String password = "root";
		Class.forName(driver);  // load MySQL driver
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	public static void main(String[] args) {
    	Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
    	try {
      		System.out.println("------DemoGetImportedKeys_MySQL begin---------");

      		conn = getConnection();
      		System.out.println("DemoGetImportedKeys_MySQL: conn="+conn);
			String tableName = args[0];
			System.out.println("tableName=" + tableName);
			String importedKeysAsXML = DatabaseMetaDataTool.getImportedKeys(conn, "octopus", null, tableName);
			System.out.println("importedKeysAsXML=" + importedKeysAsXML);
      		System.out.println("------DemoGetImportedKeys_MySQL end---------");
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