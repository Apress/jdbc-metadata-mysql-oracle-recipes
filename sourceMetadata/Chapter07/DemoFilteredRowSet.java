import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.sql.rowset.FilteredRowSet;

import com.sun.rowset.FilteredRowSetImpl;
import com.sun.rowset.CachedRowSetImpl;

public class DemoFilteredRowSet {

    public static void main(String args[]) {
        try {
            // load the MYSQL driver
            Class.forName("com.mysql.jdbc.Driver");

            // establish a connection to MySQL database
            FilteredRowSet frs = new FilteredRowSetImpl();
            frs.setUsername("root");
            frs.setPassword("root");
            frs.setUrl("jdbc:mysql://localhost/octopus");
            frs.setCommand("SELECT id, name, age FROM employees");
            frs.execute();


            // display records in (un)filtered rowset
            System.out.println("--- Unfiltered RowSet: ---");
            displayRowSet(frs);


            // create a filter that restricts entries in
            // the age column to be between 70 and 100
            AgeFilter filter = new AgeFilter(70, 100, 3);


            // set the filter.
            frs.beforeFirst();
            frs.setFilter(filter);

            // go to the beginning of the Rowset
            System.out.println("--- Filtered RowSet: ---");

            // show filtered data
            displayRowSet(frs);

            System.out.println("--- Try to insert new records ---");

            // Try to add an employee with age = 90 (allowed by filter)
            frs.moveToInsertRow();
            frs.updateString(1,"999");
            frs.updateString(2, "Andre");
            frs.updateInt(3, 90);
            frs.insertRow();
            frs.moveToCurrentRow();
            frs.acceptChanges();

            // try to add an employee with age = 65 (not allowed by filter)
            try {
                frs.moveToInsertRow();
                frs.updateString(1,"123");
                frs.updateString(2,"Jeff");
                frs.updateInt(3, 65);
                frs.insertRow();
                frs.moveToCurrentRow();
                frs.acceptChanges();
            }
            catch (SQLException e) {
                System.out.println("--- Error for inserting record with age=65 ---");
                e.printStackTrace();
            }

            // scroll to first row of rowset
            frs.beforeFirst();
            // display rows in FilteredRowset
            System.out.println("FilteredRowSet after trying to insert Jeff (age 65) and Andre (age 90):");
            displayRowSet(frs);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void displayRowSet(RowSet rs) throws SQLException {
        while (rs.next()) {
           System.out.println(rs.getRow() + " - " +
                        rs.getString("id") + ":" +
                        rs.getString("name") + ":" + rs.getInt("age"));
        }
    }
}


