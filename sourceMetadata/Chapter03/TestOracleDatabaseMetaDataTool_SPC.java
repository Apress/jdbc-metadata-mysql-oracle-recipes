import java.util.*;
import java.io.*;
import java.sql.*;

import jcb.db.*;
import jcb.meta.*;
import jcb.util.DatabaseUtil;

public class TestOracleDatabaseMetaDataTool_SPC {

	public static void main(String[] args) {
		String driver = "oracle.jdbc.driver.OracleDriver";
    	String url = "jdbc:oracle:thin:@localhost:1521:maui";   // office
    	//String url = "jdbc:oracle:thin:@localhost:1521:scorpian"; // home
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

   			//
			// print the list of table types
			//
			java.util.List tableTypes = DatabaseMetaDataTool.getTableTypes(conn);
      		System.out.println("Got results: list of table types --------------");
      		for (int i=0; i < tableTypes.size(); i++) {
				// process results one element at a time
        		String tableType = (String) tableTypes.get(i);
        		System.out.println("table type = " + tableType);
			}


      		System.out.println("-------- getStoredProcedureSignature 1-------------");
			String signature = DatabaseMetaDataTool.getStoredProcedureSignature
                                 (conn,
                                 "",
                                 "OCTOPUS",		 // user
                                 "RAISESALARY",  // stored procedure name
                                 "%");			 // all columns
      		System.out.println("-------- getStoredProcedureSignature -------------");
      		System.out.println(signature);
      		System.out.println("------------------------------------");


      		System.out.println("-------- getStoredProcedureSignature 2-------------");
			String signature2 = DatabaseMetaDataTool.getStoredProcedureSignature
                                 (conn,
                                 "",
                                 "OCTOPUS",		 // user
                                 "SHOWUSERS",    // stored procedure name
                                 "%");			 // all columns
      		System.out.println("-------- getStoredProcedureSignature -------------");
      		System.out.println(signature2);
      		System.out.println("------------------------------------");

      		System.out.println("------------------------------------");

			//String catalog = null;
			//String schemaPattern = null;
            //String procedureNamePattern = "CHECKPROPERTIES%";
			String spNames = DatabaseMetaDataTool.getStoredProcedureNames
                                 (conn,
				                  "",
				                  "OCTOPUS",
				                  "%");
      		System.out.println("-------- getStoredProcedureNames -------------");
      		System.out.println(spNames);
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
