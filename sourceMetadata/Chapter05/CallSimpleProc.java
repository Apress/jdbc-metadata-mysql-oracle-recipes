import java.sql.*;
import jcb.util.DatabaseUtil;

public class CallSimpleProc {

	public static Connection getConnection() throws Exception {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost/octopus";
		String username = "root";
		String password = "root";
		Class.forName(driver); // load MySQL driver
		return DriverManager.getConnection(url, username, password);
	}

	public static void main(String[] args) {
		Connection conn = null;
		try {
			//
			// Step-1: get a database connection
			//
			conn = getConnection();
			System.out.println("conn="+conn);
			//
			// Step-2: identify the stored procedure
			//
			String proc3StoredProcedure = "{ call simpleproc(?) }";
			//
			// Step-3: prepare the callable statement
			//
			CallableStatement cs = conn.prepareCall(proc3StoredProcedure);
			//
			// Step-4: set input parameters ... NONE
			//
			//
			// Step-5: register output parameters ...
			//
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			//
			// Step-6: execute the stored procedures: proc3
			//
			cs.execute();
			//
			// Step-7: extract the output parameters
			//
			int param1 = cs.getInt(1);
			System.out.println("param1="+param1);
			System.out.println("------------------------------------");
			//
			// Step-8: get ParameterMetaData
			//
			ParameterMetaData pmeta = cs.getParameterMetaData();
			if (pmeta == null) {
				System.out.println("Vendor does not support ParameterMetaData");
			}
			else {
				System.out.println(pmeta.getParameterType(1));
			}
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
