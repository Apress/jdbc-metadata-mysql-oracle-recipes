import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import jcb.db.*;
import jcb.util.IOUtil;
import jcb.util.DatabaseUtil;

public class BlobServletMySql extends HttpServlet {

	public static Connection getConnection() throws Exception {

		String driver = "org.gjt.mm.mysql.Driver";
    	String url = "jdbc:mysql://localhost/octopus";
    	String username = "root";
    	String password = "root";

		Class.forName(driver);	// load MySQL driver
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	public void doPost(HttpServletRequest request,
					   HttpServletResponse response)
		throws IOException, ServletException {
    	doGet(request, response);
    }

	public void doGet(HttpServletRequest request,
					  HttpServletResponse response)
   		throws IOException, ServletException {

		System.out.println("BlobServletMySql begin----------------");

		Blob photo = null;
		java.sql.Connection conn =  null;
		ServletOutputStream out = response.getOutputStream();
		try {
			conn = getConnection();
			System.out.println("conn="+conn);
		}
		catch(Exception e) {
			System.out.println("BlobServletMySql Exception----------------");
			e.printStackTrace();
			response.setContentType("text/html");
			out.println("<html><head><title>Database Error</title></head>");
			out.println("<body><h1>Could not get a database connection.</h1></body></html>");
			return;
		}


		//Connection conn = CacheConnection.checkOut();
		Statement  statement  = null;
		ResultSet  rs  = null;
		String id = request.getParameter("id").trim();
		String     sql        = "select photo from zPerson where  id = "+id;


		try {
		  statement = conn.createStatement();
		  rs = statement.executeQuery(sql);
		  if (rs.next()) {
			photo = rs.getBlob(1);
		  }
		  else {
			response.setContentType("text/html");
			out.println("<html><head><title>Person Photo</title></head>");
			out.println("<body><h1>No data found</h1></body></html>");
			return;
		  }
		  response.setContentType("image/gif");

		  InputStream in = photo.getBinaryStream();
		  System.out.println("after getBinaryStream");

		  int length     = (int)photo.length();
		  System.out.println("lenght of the blob is " + length);

		  int bufferSize = 1024;
		  System.out.println("buffer size is " + bufferSize);

		  byte[] buffer = new byte[bufferSize];

		  while ((length = in.read(buffer)) != -1) {
			System.out.println("writing " + length + " bytes");
			out.write(buffer, 0, length);
		  }

		  System.out.println("written");
		  in.close();
		  out.flush();
		}
		catch (SQLException e) {
		  System.out.println("TestBlobServlet.doGet() SQLException: " +
		   e.getMessage() + "executing ");
		  System.out.println(sql);
		}
		finally {
		  if (rs != null)
			try { rs.close(); } catch (SQLException ignore) { }
		  if (statement != null)
			try { statement.close(); } catch (SQLException ignore) { }

		  try {
			  if (conn != null) {
				conn.close();
			  }
		  }
		  catch (Exception ignore) {
		  }
		}

	}

}
