import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;

import jcb.util.DatabaseUtil;
import jcb.db.VeryBasicConnectionManager;

public class DemoDynamicParams {

    public static void main(String[] args) throws Exception {

        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement ps =  null;

        // the following SQL query has two dynamic input parameters:
        // first parameter is for “id” column and second one is for “age” column
        String query =
           "select id, name, age from employees where id < ? and age > ?";

        String dbVendor = args[0];
        try {
           // get a valid connection object
           conn = VeryBasicConnectionManager.getConnection(dbVendor);

           // prepare a SQL statement, which can have parameters; note that
           // a PreparedStatement object may be used any number of times
           ps = conn.prepareStatement(query);

           // specify values for all input parameters
           ps.setInt(1, 100);        // set the first parameter: id
           ps.setInt(2, 30);        // set the second parameter: age

           // now, PreparedStatement object is ready to be executed.
           rs = ps.executeQuery();
           // iterate the result set object
           displayResultSet(rs);

           // NOTE: you may use PreparedStatement as many times as you want
           // here we use it for another set of parameters:
           ps.setInt(1, 110);          // set the first parameter: id
           ps.setInt(2, 70);           // set the second parameter: age

           // now, PreparedStatement object is ready to be executed.
           rs = ps.executeQuery();
           // iterate the result set object
           displayResultSet(rs);
        }
        finally {
            // close resources: ResultSet, PreparedStatement, Connection
            DatabaseUtil.close(rs);
            DatabaseUtil.close(ps);
            DatabaseUtil.close(conn);
        }
    }

    public static void displayResultSet(ResultSet rs) throws Exception {
        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            int age = rs.getInt(3);
            System.out.println("[id="+id+"][name="+name+"][age="+age+"]");
        }
        System.out.println("-------------");

    }
}
