import java.util.*;
import java.io.*;
import java.sql.*;
import jcb.util.DatabaseUtil;

public class ParameterMetaDataAsXML_HSQLDB {
	public static Connection getConnection(String dbName)
	throws Exception {
		// load the HSQL Database Engine JDBC driver
		// hsqldb.jar should be in the class path
		Class.forName("org.hsqldb.jdbcDriver");
		// connect to the database. This will load the
		// db files and start the database if it is not
		// already running. dbName is used to open or
		// create files that hold the state of the db.
		return DriverManager.getConnection("jdbc:hsqldb:"
		+ dbName, // filename
		"sa", // username
		""); // password
	}

public static String getParameterMetaDataAsXML(ParameterMetaData metadata)
throws SQLException {
if (metadata == null) {
return null;
}
StringBuilder builder = new StringBuilder();
int paramCount = metadata.getParameterCount();
builder.append("<parameterMetaData count=\"");
builder.append(paramCount);
builder.append("\">");
if (paramCount < 1) {
return builder.toString();
}
for (int param=1; param <= paramCount; param++) {
builder.append("<parameter position=\"");
builder.append(param);
builder.append("\">");
int sqlTypeCode = metadata.getParameterType(param);
builder.append("<type>");
builder.append(sqlTypeCode);
builder.append("</type>");
String paramTypeName = metadata.getParameterTypeName(param);
builder.append("<typeName>");
builder.append(paramTypeName);
builder.append("</typeName>");

String paramClassName = metadata.getParameterClassName(param);
builder.append("<className>");
builder.append(paramClassName);
builder.append("</className>");
builder.append("<mode>");
int paramMode = metadata.getParameterMode(param);
if (paramMode == ParameterMetaData.parameterModeOut){
builder.append("OUT");
}
else if (paramMode == ParameterMetaData.parameterModeIn){
builder.append("IN");
}
else if (paramMode == ParameterMetaData.parameterModeInOut){
builder.append("INOUT");
}
else {
builder.append("UNKNOWN");
}
builder.append("</mode>");
builder.append("<isSigned>");
boolean isSigned = metadata.isSigned(param);
builder.append(isSigned);
builder.append("</isSigned>");
builder.append("<precision>");
int precision = metadata.getPrecision(param);
builder.append(precision);
builder.append("</precision>");
builder.append("<scale>");
int scale = metadata.getScale(param);
builder.append(scale);
builder.append("</scale>");
builder.append("<nullable>");
int nullable = metadata.isNullable(param);
if (nullable == ParameterMetaData.parameterNoNulls){
builder.append("false");
}
else if (nullable == ParameterMetaData.parameterNullable){
builder.append("true");
}
else {
builder.append("UNKOWN");
}
builder.append("</nullable>");
builder.append("</parameter>");
}
builder.append("</parameterMetaData>");
return builder.toString();
}
public static void main(String[] args) {
Connection conn = null;
PreparedStatement pstmt = null;
ParameterMetaData paramMetaData = null;
String query = "select id, str_col, num_col " +
"from sample_table where id > ? and str_col = ? and num_col = ?";
try {
conn = getConnection("db_file"); // db file name
System.out.println("conn="+conn);
pstmt = conn.prepareStatement(query);
paramMetaData = pstmt.getParameterMetaData();
if (paramMetaData == null) {
System.out.println("db vendor does NOT support ParameterMetaData");
}
else {
String metadataAsXML = getParameterMetaDataAsXML(paramMetaData);
System.out.println("db vendor supports ParameterMetaData");
System.out.println(metadataAsXML);
}
}
catch(Exception e){
e.printStackTrace();
System.exit(1);
}
finally {
// release database resources
DatabaseUtil.close(pstmt);
DatabaseUtil.close(conn);
}
}
}
