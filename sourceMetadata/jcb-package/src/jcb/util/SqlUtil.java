package jcb.util;


import java.util.*;


/**
 * SqlUtility is a helper class for SQL Adapter.
 */
public class SqlUtil {

	private static final String DATABASE_USER = "user";
	private static final String DATABASE_PASSWORD = "password";

	private static final Hashtable JAVA_SQL_TYPES = new Hashtable();
	private static final Hashtable REVERSED_JAVA_SQL_TYPES = new Hashtable();

	public static final String JAVA_SQL_TYPE_ARRAY = "ARRAY";
	public static final String JAVA_SQL_TYPE_BIGINT = "BIGINT";
	public static final String JAVA_SQL_TYPE_BINARY = "BINARY";
	public static final String JAVA_SQL_TYPE_BIT = "BIT";
	public static final String JAVA_SQL_TYPE_BLOB = "BLOB";
	public static final String JAVA_SQL_TYPE_BOOLEAN = "BOOLEAN";
	public static final String JAVA_SQL_TYPE_CHAR = "CHAR";
	public static final String JAVA_SQL_TYPE_CLOB = "CLOB";
	public static final String JAVA_SQL_TYPE_DATALINK = "DATALINK";
	public static final String JAVA_SQL_TYPE_DATE = "DATE";
	public static final String JAVA_SQL_TYPE_DECIMAL = "DECIMAL";
	public static final String JAVA_SQL_TYPE_DISTINCT = "DISTINCT";
	public static final String JAVA_SQL_TYPE_DOUBLE = "DOUBLE";
	public static final String JAVA_SQL_TYPE_FLOAT = "FLOAT";
	public static final String JAVA_SQL_TYPE_INTEGER = "INTEGER";
	public static final String JAVA_SQL_TYPE_JAVA_OBJECT = "JAVA_OBJECT";
	public static final String JAVA_SQL_TYPE_LONGVARBINARY = "LONGVARBINARY";
	public static final String JAVA_SQL_TYPE_LONGVARCHAR = "LONGVARCHAR";
	public static final String JAVA_SQL_TYPE_NULL = "NULL";
	public static final String JAVA_SQL_TYPE_NUMERIC = "NUMERIC";
	public static final String JAVA_SQL_TYPE_OTHER = "OTHER";
	public static final String JAVA_SQL_TYPE_REAL = "REAL";
	public static final String JAVA_SQL_TYPE_REF = "REF";
	public static final String JAVA_SQL_TYPE_SMALLINT = "SMALLINT";
	public static final String JAVA_SQL_TYPE_STRUCT = "STRUCT";
	public static final String JAVA_SQL_TYPE_TIME = "TIME";
	public static final String JAVA_SQL_TYPE_TIMESTAMP = "TIMESTAMP";
	public static final String JAVA_SQL_TYPE_TINYINT = "TINYINT";
	public static final String JAVA_SQL_TYPE_VARBINARY = "VARBINARY";
	public static final String JAVA_SQL_TYPE_VARCHAR = "VARCHAR";

	public static final String JAVA_SQL_TYPE_STRING = "STRING"; // added for convenience
	public static final String JAVA_SQL_TYPE_INT = "INT"; 		// added for convenience

	//
	// Oracle CURSOR type for handling ResultSets
	//
	public static final String ORACLE_CURSOR_TYPE = "ORACLE.TYPES.CURSOR";

    static {
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.ARRAY), JAVA_SQL_TYPE_ARRAY);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.BIGINT), JAVA_SQL_TYPE_BIGINT);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.BINARY), JAVA_SQL_TYPE_BINARY);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.BIT), JAVA_SQL_TYPE_BIT);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.BLOB), JAVA_SQL_TYPE_BLOB);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.BOOLEAN), JAVA_SQL_TYPE_BOOLEAN);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.CHAR), JAVA_SQL_TYPE_CHAR);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.CLOB), JAVA_SQL_TYPE_CLOB);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.DATALINK), JAVA_SQL_TYPE_DATALINK);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.DATE), JAVA_SQL_TYPE_DATE);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.DECIMAL), JAVA_SQL_TYPE_DECIMAL);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.DISTINCT), JAVA_SQL_TYPE_DISTINCT);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.DOUBLE), JAVA_SQL_TYPE_DOUBLE);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.FLOAT), JAVA_SQL_TYPE_FLOAT);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.INTEGER), JAVA_SQL_TYPE_INTEGER);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.JAVA_OBJECT), JAVA_SQL_TYPE_JAVA_OBJECT);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.LONGVARBINARY), JAVA_SQL_TYPE_LONGVARBINARY);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.LONGVARCHAR), JAVA_SQL_TYPE_LONGVARCHAR);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.NULL), JAVA_SQL_TYPE_NULL);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.NUMERIC), JAVA_SQL_TYPE_NUMERIC);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.OTHER), JAVA_SQL_TYPE_OTHER);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.REAL), JAVA_SQL_TYPE_REAL);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.REF), JAVA_SQL_TYPE_REF);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.SMALLINT), JAVA_SQL_TYPE_SMALLINT);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.STRUCT), JAVA_SQL_TYPE_STRUCT);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.TIME), JAVA_SQL_TYPE_TIME);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.TIMESTAMP), JAVA_SQL_TYPE_TIMESTAMP);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.TINYINT), JAVA_SQL_TYPE_TINYINT);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.VARBINARY), JAVA_SQL_TYPE_VARBINARY);
		JAVA_SQL_TYPES.put(new Integer(java.sql.Types.VARCHAR), JAVA_SQL_TYPE_VARCHAR);

		JAVA_SQL_TYPES.put(new Integer(oracle.jdbc.driver.OracleTypes.CURSOR), ORACLE_CURSOR_TYPE);


		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_ARRAY, new Integer(java.sql.Types.ARRAY));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_BIGINT, new Integer(java.sql.Types.BIGINT));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_BINARY, new Integer(java.sql.Types.BINARY));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_BIT, new Integer(java.sql.Types.BIT));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_BLOB, new Integer(java.sql.Types.BLOB));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_BOOLEAN, new Integer(java.sql.Types.BOOLEAN));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_CHAR, new Integer(java.sql.Types.CHAR));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_CLOB, new Integer(java.sql.Types.CLOB));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_DATALINK, new Integer(java.sql.Types.DATALINK));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_DATE, new Integer(java.sql.Types.DATE));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_DECIMAL, new Integer(java.sql.Types.DECIMAL));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_DISTINCT, new Integer(java.sql.Types.DISTINCT));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_DOUBLE, new Integer(java.sql.Types.DOUBLE));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_FLOAT, new Integer(java.sql.Types.FLOAT));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_INTEGER, new Integer(java.sql.Types.INTEGER));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_JAVA_OBJECT, new Integer(java.sql.Types.JAVA_OBJECT));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_LONGVARBINARY, new Integer(java.sql.Types.LONGVARBINARY));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_LONGVARCHAR, new Integer(java.sql.Types.LONGVARCHAR));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_NULL, new Integer(java.sql.Types.NULL));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_NUMERIC, new Integer(java.sql.Types.NUMERIC));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_OTHER, new Integer(java.sql.Types.OTHER));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_REAL, new Integer(java.sql.Types.REAL));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_REF, new Integer(java.sql.Types.REF));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_SMALLINT, new Integer(java.sql.Types.SMALLINT));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_STRUCT, new Integer(java.sql.Types.STRUCT));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_TIME, new Integer(java.sql.Types.TIME));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_TIMESTAMP, new Integer(java.sql.Types.TIMESTAMP));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_TINYINT, new Integer(java.sql.Types.TINYINT));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_VARBINARY, new Integer(java.sql.Types.VARBINARY));
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_VARCHAR, new Integer(java.sql.Types.VARCHAR));

		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_STRING, new Integer(java.sql.Types.VARCHAR));	// added for convenience
		REVERSED_JAVA_SQL_TYPES.put(JAVA_SQL_TYPE_INT, new Integer(java.sql.Types.INTEGER));	// added for convenience

		REVERSED_JAVA_SQL_TYPES.put(ORACLE_CURSOR_TYPE, new Integer(oracle.jdbc.driver.OracleTypes.CURSOR));

	}

    public static String getColumnType(int columnType) {
            String type = (String) JAVA_SQL_TYPES.get(new Integer(columnType));
            if (type == null) {
				// it means it is a non-existent java.sql.Types.XXX type.
				return "VARCHAR";
			}
			else {
				return type;
			}
    }

    public static boolean isJavaSqlType(int columnType) {
            String type = (String) JAVA_SQL_TYPES.get(new Integer(columnType));
            if (type == null) {
				// it means it is a non-existing type
				return false;
			}
			else {
				return true;
			}
    }


    public static int getColumnType(String columnType) {

		if ((columnType == null) || (columnType.length() == 0)) {
			return java.sql.Types.VARCHAR;
		}

		Integer type = (Integer) REVERSED_JAVA_SQL_TYPES.get(columnType.toUpperCase());
		if (type == null) {
			// it is a non-existent type
			return java.sql.Types.VARCHAR;
		}

		return  type.intValue();
    }



    /**
     * get properties with user/password
     *
     * @param user the db username
     * @param password the db password
     *
     */
    public static java.util.Properties getDatabaseProperties(String user, String password) {

        java.util.Properties props = new java.util.Properties();

        if (user == null) {
			props.put(DATABASE_USER, "");
		}
		else {
			props.put(DATABASE_USER, user);
		}

        if (password == null) {
			 props.put(DATABASE_PASSWORD, "");
		}
		else {
			 props.put(DATABASE_PASSWORD, password);
		}

        return props;

    }

    /**
     *  get a java.sql.Driver for a given driver name.
     *
     * @param driver the name of JDBC driver
     *
     */
    public static java.sql.Driver getJavaSqlDriver(String driver) throws Exception {
        java.sql.Driver sqlDriver = null;
		sqlDriver = (java.sql.Driver) Class.forName(driver).newInstance();
        System.out.println("getJavaSqlDriver() is OK. sqlDriver=" + sqlDriver);
        return sqlDriver;
    }
}

