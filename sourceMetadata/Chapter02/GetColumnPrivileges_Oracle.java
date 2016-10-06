import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class GetColumnPrivileges_Oracle {

    public static Connection getConnection() throws Exception {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@localhost:1521:caspian";
        String username = "system";
        String password = "password";
        Class.forName(driver);  // load Oracle driver
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            System.out.println("conn="+conn);
            System.out.println("---------------");

            String columnPrivileges = DatabaseMetaDataTool.getColumnPrivileges
                (conn,               // connection
                 conn.getCatalog(),  // catalog
                 "SYSTEM",           // schema
                 "HELP",
                 "%");
            System.out.println("---- Table's Columns Privileges ----");
            System.out.println(columnPrivileges);
            System.out.println("------------------------------------");
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