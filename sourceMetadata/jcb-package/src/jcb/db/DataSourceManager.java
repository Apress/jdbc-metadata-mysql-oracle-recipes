package jcb.db;


import java.util.*;

import jcb.util.*;


/**
 * DataSourceManager is a helper class for data sources.
 *
 * <p>
 * @author: Mahmoud Parsian
 * @since JDK 1.4
 */
public class DataSourceManager {


	public static DataSourceConfig getDataSourceConfig(String dsn) {
		return DataSourceDigester.getDataSourceConfig(dsn);
	}

	/**
	 * For testing/debugging ONLY.
	 *
	 */
	 public static void main(String[] args) {

		    DataSourceConfig dsConfig1 = DataSourceDigester.getDataSourceConfig("mysql");
		    dsConfig1.print();

		    DataSourceConfig dsConfig2 =  DataSourceDigester.getDataSourceConfig("oracle");
    		dsConfig2.print();

		    DataSourceConfig dsConfig3 =  DataSourceDigester.getDataSourceConfig("default");
    		dsConfig3.print();

	}

}

