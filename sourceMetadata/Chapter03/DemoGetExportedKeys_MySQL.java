import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class DemoGetExportedKeys_MySQL {

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
      		System.out.println("------DemoGetExportedKeys_MySQL begin---------");

      		conn = getConnection();
      		System.out.println("DemoGetExportedKeys_MySQL: conn="+conn);

			String exportedKeysAsXML = DatabaseMetaDataTool.getExportedKeys(conn, "octopus", null, "DEPT_TABLE");
			System.out.println("exportedKeysAsXML=" + exportedKeysAsXML);
      		System.out.println("------DemoGetExportedKeys_MySQL end---------");
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