import java.util.*;
import java.io.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;

import oracle.jdbc.pool.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class TestMySqlDataSource {

	public static void main(String[] args) {
		String driver = "org.gjt.mm.mysql.Driver";
    	String url = "jdbc:mysql://localhost/octopus";
    	String username = "root";
    	String password = "root";
		Connection conn = null;

    	try {
      		Class.forName(driver);
      		System.out.println("ok: loaded MySQL driver.");
    	}
    	catch( Exception e ) {
      		System.out.println("Failed to load MySQL driver.");
      		System.exit(1);
    	}

    	try {
      		conn = DriverManager.getConnection(url, username, password);


   			//
			// print the list of tables
			//
			java.util.List tables = DatabaseMetaDataTool.getTableNames(conn);
      		System.out.println("Got results: list of tables");
      		for (int i=0; i < tables.size(); i++) {
				// process results one element at a time
        		String tableName = (String) tables.get(i);
        		System.out.println("table name = " + tableName);
			}
			System.out.println("------------------------------------");

			// Set up the environment for creating the initial context
			Hashtable env = new Hashtable(11);
			env.put(Context.INITIAL_CONTEXT_FACTORY,
			    "com.sun.jndi.fscontext.RefFSContextFactory");
			env.put(Context.PROVIDER_URL, "file:/jdbc");

			Context context = new InitialContext(env);
			NamingEnumeration list = context.list("jdbc");

			while (list.hasMore()) {
				NameClassPair nc = (NameClassPair)list.next();
				System.out.println(nc);
			}
			System.out.println("------------------------------------");


			OracleDataSource ods = new OracleDataSource();
			ods.setDriverType("thin");
			ods.setServerName("localhost");
			ods.setNetworkProtocol("tcp");
			ods.setDatabaseName("maui");
			ods.setPortNumber(1521);
			ods.setUser("system");
			ods.setPassword("system9");
			//...
			//Register the Data Source
			//...
			Context ctx = new InitialContext();
			ctx.bind("file:/jdbc/mydb", ods);

            // Get the initial context of JNDI and lookup the datasource.
            InitialContext ic = new InitialContext();
            javax.sql.DataSource ds1 = (javax.sql.DataSource) ic.lookup("file:/jdbc/mydb");
            // Set the optional printwriter where the trace log is to be directed.
            ds1.setLogWriter(new PrintWriter(new FileOutputStream("d:/mp/book/datasource.log")));
            Connection con1 = ds1.getConnection();
            Connection con2 = ds1.getConnection("octopus","octopus");
			System.out.println("con1="+con1);
			System.out.println("con2="+con2);
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
