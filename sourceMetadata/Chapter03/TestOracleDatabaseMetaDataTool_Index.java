import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class TestOracleDatabaseMetaDataTool_Index {

	public static void main(String[] args) {
		String driver = "oracle.jdbc.driver.OracleDriver";
    	//String url = "jdbc:oracle:thin:@localhost:1521:maui";   // office
    	String url = "jdbc:oracle:thin:@localhost:1521:scorpian"; // home
    	String username = "octopus";  //office
    	String password = "octopus";  // office
		Connection conn = null;

    	try {
      		Class.forName(driver);
      		System.out.println("ok: loaded Oracle driver.");
    	}
    	catch( Exception e ) {
      		System.out.println("Failed to load Oracle driver.");
      		System.exit(1);
    	}

    	try {
      		conn = DriverManager.getConnection(url, username, password);

      		System.out.println("-------- getIndexInformation -------------");
			String indexInformation = DatabaseMetaDataTool.getIndexInformation
                                 (conn,
                                 "",
                                 "OCTOPUS",		 // user
                                 "ACCOUNT",      // table name
                                 true,			 // unique?
                                 true);
      		System.out.println("-------- getIndexInformation -------------");
      		System.out.println(indexInformation);
      		System.out.println("------------------------------------");

      		System.out.println("-------- getIndexInformation -------------");
			String indexInformation2 = DatabaseMetaDataTool.getIndexInformation
                                 (conn,
                                 "",
                                 "OCTOPUS",		 // user
                                 "ACCOUNT",      // table name
                                 false,			 // unique?
                                 true);
      		System.out.println("-------- getIndexInformation -------------");
      		System.out.println(indexInformation2);
      		System.out.println("------------------------------------");

      		System.out.println("-------- Does index exist? -------------");
      		System.out.println("conn="+conn);
      		boolean indexExist = DatabaseMetaDataTool.indexExists
      					(conn,
      		             conn.getCatalog(),		// catalog
      		             null,					// schema
      		             "ACCOUNT",				// table name
      		             "ID_STATUS_INDEX");	// index name
      		System.out.println("Index name: ID_STATUS_INDEX");
      		System.out.println("Table name: ACCOUNT");
      		System.out.println("Index Exist?: " + indexExist);

      		System.out.println("-------- Does index exist? -------------");
      		boolean indexExist22 = DatabaseMetaDataTool.indexExists
      					(conn,
      		             conn.getCatalog(),		// catalog
      		             null,					// schema
      		             "ACCOUNT",			// table name
      		             "ID_STATUS_INDEX22");	// index name
      		System.out.println("Index name: ID_STATUS_INDEX22");
      		System.out.println("Table name: ACCOUNT");
      		System.out.println("Index Exist?: " + indexExist22);


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
