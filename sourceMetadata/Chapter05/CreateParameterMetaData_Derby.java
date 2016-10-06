import java.util.*;
import java.io.*;
import java.sql.*;
import jcb.util.DatabaseUtil;

public class CreateParameterMetaData_Derby {
	public static Connection getConnection() throws Exception {
		// in an embedded environment, loading
		// the driver also starts Derby.
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		// connect to the database. This will create the db
		String dbURL = "jdbc:derby:myDB;create=true;user=me;password=mine";
		return DriverManager.getConnection(dbURL);
	}
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ParameterMetaData paramMetaData = null;
		String create = "create table sample_table"+
		"(id VARCHAR(10), str_col VARCHAR(20), num_col int)";
		String query = "select id, str_col, num_col " +
		"from sample_table where id > ? and num_col = ?";
		try {
			conn = getConnection();
			System.out.println("conn="+conn);
			stmt = conn.createStatement();
			stmt.executeUpdate(create);
			pstmt = conn.prepareStatement(query);
			paramMetaData = pstmt.getParameterMetaData();
			if (paramMetaData == null) {
				System.out.println("db vendor does NOT support ParameterMetaData");
			}
			else {
				System.out.println("db vendor supports ParameterMetaData");
				// find out the number of dynamic parameters
				int paramCount = paramMetaData.getParameterCount();
				System.out.println("paramCount="+paramCount);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			// release database resources
			DatabaseUtil.close(stmt);
			DatabaseUtil.close(pstmt);
			DatabaseUtil.close(conn);
		}
	}
}