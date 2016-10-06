package jcb.db;

import java.io.IOException;
import java.util.Hashtable;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * This class reads "datasource.xml" file and creates
 * DataSourceConfig objects.
 *
 */
 public class DataSourceDigester {

  private static Hashtable dataSources = new Hashtable();

  private static final String DATA_SOURCE_XML_FILE =
  	"D:/mp/book/src/conf/datasource.xml";

  private static final String[] PARAM_TYPES = {
			  "java.lang.String",	// name
			  "java.lang.String",	// description
			  "java.lang.String",	// vendor
			  "java.lang.String",	// driver
			  "java.lang.String",	// url
			  "java.lang.String",	// username
			  "java.lang.String",	// password
			  "java.lang.String",	// factoryClass
			  "java.lang.String",	// connectionClass
			  "java.lang.String",	// checkQuery
			  "java.lang.Integer",	// poolGrowAmount
			  "java.lang.Integer",	// poolMin
			  "java.lang.Integer",	// poolMax
			  "java.lang.Integer" 	// timeout
  };


  static  {
    	DataSourceDigester dsDigester = new DataSourceDigester();

    	try {
      		dsDigester.run();
    	}
    	catch(Exception e) {
      		e.printStackTrace();
    	}
  }

  public static void main(String[] args) {

    DataSourceDigester dsDigester = new DataSourceDigester();

    try {
      dsDigester.run();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

  }


  /**
   * Get a data source configuration.
   */
  public static DataSourceConfig getDataSourceConfig(String name) {
	  return (DataSourceConfig) dataSources.get(name);
  }

  public void run()
  	throws IOException, SAXException {

    Digester digester = new Digester();

    digester.push(this);


    digester.addCallMethod("datasources/datasource", "addDataSource", 14, PARAM_TYPES);
    digester.addCallParam("datasources/datasource/name", 0);
    digester.addCallParam("datasources/datasource/description", 1);
    digester.addCallParam("datasources/datasource/vendor", 2);
    digester.addCallParam("datasources/datasource/driver", 3);
    digester.addCallParam("datasources/datasource/url", 4);
    digester.addCallParam("datasources/datasource/username", 5);
    digester.addCallParam("datasources/datasource/password", 6);
    digester.addCallParam("datasources/datasource/factoryClass", 7);
    digester.addCallParam("datasources/datasource/connectionClass", 8);
    digester.addCallParam("datasources/datasource/checkQuery", 9);
    digester.addCallParam("datasources/datasource/poolGrowAmount", 10);
    digester.addCallParam("datasources/datasource/poolMin", 11);
    digester.addCallParam("datasources/datasource/poolMax", 12);
    digester.addCallParam("datasources/datasource/timeout", 13);

    digester.parse(DATA_SOURCE_XML_FILE);
  }

  public void addDataSource(String name,
  							String description,
  							String vendor,
                            String driver,
                            String url,
                            String username,
                            String password,
                            String factoryClass,
  							String connectionClass,
  							String checkQuery,
  							int poolGrowAmount,
  							int poolMin,
  							int poolMax,
  							int timeout) {

    DataSourceConfig dataSource = new DataSourceConfig
    									  (name,
    									   description,
    									   vendor,
    									   driver,
    									   url,
    									   username,
    									   password,
    									   factoryClass,
  										   connectionClass,
  										   checkQuery,
  										   poolGrowAmount,
  										   poolMin,
  										   poolMax,
  										   timeout);
    //dataSource.print();
    dataSources.put(name, dataSource);

  }
}