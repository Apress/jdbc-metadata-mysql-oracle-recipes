import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class TestMySqlDatabaseMetaDataTool_Index {

	public static void main(String[] args) {
		String driver = "org.gjt.mm.mysql.Driver";
    	String url = "jdbc:mysql://localhost/tiger";
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

      		System.out.println("conn.getCatalog()="+conn.getCatalog());

      		System.out.println("-------- getIndexInformation -------------");
			String indexInformation = DatabaseMetaDataTool.getIndexInformation
                                 (conn,
                                  conn.getCatalog(),
                                  null,
                                  "ACCOUNT",     // table name
                                  true,			 // unique index?
                                  true);
      		System.out.println("-------- getIndexInformation -------------");
      		System.out.println(indexInformation);
      		System.out.println("------------------------------------");

      		System.out.println("-------- getIndexInformation -------------");
			String indexInformation2 = DatabaseMetaDataTool.getIndexInformation
                                 (conn,
                                  conn.getCatalog(),
                                  null,
                                  "ACCOUNT",      // table name
                                  false,			 // unique index?
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
