import java.util.*;
import java.io.*;
import java.sql.*;
import jcb.util.DatabaseUtil;

public class ExamineParameterMetaData_HSQLDB {
	public static Connection getConnection(String dbName)
	throws Exception {
		// load the HSQL Database Engine JDBC driver
		// hsqldb.jar should be in the class path
		Class.forName("org.hsqldb.jdbcDriver");
		// connect to the database. This will load the
		// db files and start the database if it is not
		// already running. dbName is used to open or
		// create files that hold the state of the db.
		return DriverManager.getConnection("jdbc:hsqldb:"
		+ dbName, // filename
		"sa", // username
		""); // password
	}

	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ParameterMetaData paramMetaData = null;
		String query = "select id, str_col, num_col " +
		"from sample_table where id > ? and str_col = ? and num_col = ?";
		try {
			conn = getConnection("db_file"); // db file name
			System.out.println("conn="+conn);
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
				System.out.println("-------------------");
				for (int param=1; param <= paramCount; param++) {
					System.out.println("param number="+param);
					int sqlTypeCode = paramMetaData.getParameterType(param);
					System.out.println("param SQL type code="+ sqlTypeCode);
					String paramTypeName = paramMetaData.getParameterTypeName(param);
					System.out.println("param SQL type name="+ paramTypeName);
					String paramClassName = paramMetaData.getParameterClassName(param);
					System.out.println("param class name="+ paramClassName);
					int paramMode = paramMetaData.getParameterMode(param);
					System.out.println("param mode="+ paramMode);
					if (paramMode == ParameterMetaData.parameterModeOut){
						System.out.println("the parameter's mode is OUT.");
					}
					else if (paramMode == ParameterMetaData.parameterModeIn){
						System.out.println("the parameter's mode is IN.");
					}
					else if (paramMode == ParameterMetaData.parameterModeInOut){
						System.out.println("the parameter's mode is INOUT.");
					}
					else {
						System.out.println("the mode of a parameter is unknown.");
					}

					int nullable = paramMetaData.isNullable(param);
					if (nullable == ParameterMetaData.parameterNoNulls){
						System.out.println("parameter will not allow NULL values.");
					}
					else if (nullable == ParameterMetaData.parameterNullable){
						System.out.println("parameter will allow NULL values.");
					}
					else {
						System.out.println("nullability of a parameter is unknown.");
					}
					System.out.println("-------------------");
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			// release database resources
			DatabaseUtil.close(pstmt);
			DatabaseUtil.close(conn);
		}
	}
}