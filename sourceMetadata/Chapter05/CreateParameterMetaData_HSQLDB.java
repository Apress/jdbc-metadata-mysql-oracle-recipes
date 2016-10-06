import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.util.DatabaseUtil;

public class CreateParameterMetaData_HSQLDB {
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
                                           + dbName,   // filename
                                           "sa",       // username
                                           "");        // password
    }

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ParameterMetaData paramMetaData = null;
        String query = "select id, str_col, num_col " +
           "from sample_table where id > ? and num_col = ?";
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
