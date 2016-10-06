import java.sql.*;
import jcb.util.DatabaseUtil;

public class CallProc3 {

	public static Connection getConnection() throws Exception {
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@localhost:1521:goofy";
		String username = "scott";
		String password = "tiger";
		Class.forName(driver); // load Oracle driver
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
			String proc3StoredProcedure = "{ call proc3(?, ?, ?) }";
			//
			// Step-3: prepare the callable statement
			//
			CallableStatement cs = conn.prepareCall(proc3StoredProcedure);
			//
			// Step-4: set input parameters ...
			//
			cs.setString(1, "abcd"); // first input argument
			cs.setInt(3, 10); // third input argument
			//
			// Step-5: register output parameters ...
			//
			cs.registerOutParameter(2, java.sql.Types.VARCHAR);
			cs.registerOutParameter(3, java.sql.Types.INTEGER);
			//
			// Step-6: execute the stored procedures: proc3
			//
			cs.execute();
			//
			// Step-7: extract the output parameters
			//
			String param2 = cs.getString(2); // get parameter 2 as output
			int param3 = cs.getInt(3); // get parameter 3 as output
			System.out.println("param2="+param2);
			System.out.println("param3="+param3);
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