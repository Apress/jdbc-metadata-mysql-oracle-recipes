import java.util.*;
import java.io.*;
import java.sql.*;
import jcb.util.DatabaseUtil;
import jcb.meta.DatabaseMetaDataTool;
import jcb.db.VeryBasicConnectionManager;

public class TestDatabaseMetaDataTool_DatabaseInformation {
	public static void main(String[] args) {
		String dbVendor = args[0]; // { "mysql", "oracle" }
		Connection conn = null;
		try {
			conn = VeryBasicConnectionManager.getConnection(dbVendor);
			System.out.println("-------- getDatabaseformation -------------");
			System.out.println("conn="+conn);
			String dbInfo = DatabaseMetaDataTool.getDatabaseInformation(conn);
			System.out.println(dbInfo);
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