import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class CreateParameterMetaData_Oracle {

    public static void main(String[] args) {
        String dbVendor = args[0]; // { mysql, oracle }
        Connection conn = null;
        PreparedStatement pstmt = null;
        ParameterMetaData paramMetaData = null;
        String query = "select badge_number, last_name " +
           "from emps where badge_number > ? and dept = ?";
        try {
           conn = VeryBasicConnectionManager.getConnection(dbVendor);
           pstmt = conn.prepareStatement(query);
           oracle.jdbc.OraclePreparedStatement opstmt = (oracle.jdbc.OraclePreparedStatement) pstmt;
           oracle.jdbc.OracleParameterMetaData  oparamMetaData  = opstmt.OracleGetParameterMetaData();
           paramMetaData = (ParameterMetaData) oparamMetaData;
           //paramMetaData = pstmt.getParameterMetaData();
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
