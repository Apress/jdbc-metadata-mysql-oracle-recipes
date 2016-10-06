package jcb.db;

public class DataSourceConfig {

  private String name;
  private String description;
  private String vendor;
  private String driver;
  private String url;
  private String username;
  private String password;
  private String factoryClass;
  private String connectionClass;
  private String checkQuery;
  private int poolGrowAmount;
  private int poolMin;
  private int poolMax;
  private int timeout;

  public DataSourceConfig(String name,
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

    this.name = name;
    this.description = description;
    this.vendor = vendor;
    this.driver = driver;
    this.url = url;
    this.username = username;
    this.password = password;
 	this.factoryClass = factoryClass;
 	this.connectionClass = connectionClass;
 	this.checkQuery = checkQuery;
 	this.poolGrowAmount = poolGrowAmount;
 	this.poolMin = poolMin;
 	this.poolMax = poolMax;
 	this.timeout = timeout;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getVendor() {
    return vendor;
  }

  public String getDriver()  {
    return driver;
  }

  public String getURL()  {
    return url;
  }

  public String getPassword() {
    return password;
  }

  public String getUsername()  {
    return username;
  }

  public String getFactoryClass() {
    return factoryClass;
  }

  public String getConnectionClass()  {
    return connectionClass;
  }

  public String getCheckQuery()  {
    return checkQuery;
  }

  public int getPoolGrowAmount()  {
    return poolGrowAmount;
  }

  public int getPoolMin()  {
    return poolMin;
  }

  public int getPoolMax()  {
    return poolMax;
  }

  public int getTimeout()  {
    return timeout;
  }

  public void print() {
    System.out.println("----------------------");
    System.out.println("DataSourceConfig: name=" + name);
    System.out.println("DataSourceConfig: description=" + description);
    System.out.println("DataSourceConfig: vendor=" + vendor);
    System.out.println("DataSourceConfig: driver=" + driver);
    System.out.println("DataSourceConfig: url=" + url);
    System.out.println("DataSourceConfig: username=" + username);
    System.out.println("DataSourceConfig: password=" + password);
    System.out.println("DataSourceConfig: factoryClass=" + factoryClass);
    System.out.println("DataSourceConfig: connectionClass=" + connectionClass);
    System.out.println("DataSourceConfig: checkQuery=" + checkQuery);
    System.out.println("DataSourceConfig: poolGrowAmount=" + poolGrowAmount);
    System.out.println("DataSourceConfig: poolMin=" + poolMin);
    System.out.println("DataSourceConfig: poolMax=" + poolMax);
    System.out.println("DataSourceConfig: timeout=" + timeout);
    System.out.println("----------------------");
  }

}