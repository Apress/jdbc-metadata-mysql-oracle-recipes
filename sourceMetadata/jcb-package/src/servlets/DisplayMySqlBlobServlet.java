import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import jcb.util.DatabaseUtil;


public class DisplayMySqlBlobServlet extends HttpServlet {

	public static Connection getConnection() throws Exception {

		String driver = "org.gjt.mm.mysql.Driver";
    	String url = "jdbc:mysql://localhost/octopus";
    	String username = "root";
    	String password = "root";

		Class.forName(driver);	// load MySQL driver
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	public void doGet(HttpServletRequest request,
					  HttpServletResponse response)
		throws IOException, ServletException {

		System.out.println("*** DisplayMySqlBlobServlet begin----------------");

		Blob photo = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String id = request.getParameter("id").trim();
		String query = "select photo from MyPictures where  id = "+id;
		ServletOutputStream out = response.getOutputStream();

		try {
		  conn = getConnection();
	  	}
	  	catch(Exception e) {
			response.setContentType("text/html");
			out.println("<html><head><title>Person Photo</title></head>");
			out.println("<body><h1>Database Connection Problem.</h1></body></html>");
			return;
		}

		try {
		  stmt = conn.createStatement();
		  rs = stmt.executeQuery(query);
		  if (rs.next()) {
			photo = rs.getBlob(1);
		  }
		  else {
			response.setContentType("text/html");
			out.println("<html><head><title>Person Photo</title></head>");
			out.println("<body><h1>No photo found for id="+id+"</h1></body></html>");
			return;
		  }

		  response.setContentType("image/gif");
		  InputStream in = photo.getBinaryStream();
		  int length = (int)photo.length();
		  System.out.println("lenght of the blob is " + length);

		  int bufferSize = 1024;
		  byte[] buffer = new byte[bufferSize];

		  while ((length = in.read(buffer)) != -1) {
			System.out.println("writing " + length + " bytes");
			out.write(buffer, 0, length);
		  }

		  System.out.println("writing done.");
		  in.close();
		  out.flush();
		}
		catch (SQLException e) {
			response.setContentType("text/html");
			out.println("<html><head><title>Error: Person Photo</title></head>");
			out.println("<body><h1>Error="+e.getMessage()+"</h1></body></html>");
			return;
		}
		finally {
		  DatabaseUtil.close(rs);
		  DatabaseUtil.close(stmt);
		  DatabaseUtil.close(conn);
		}

	}

	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {
		doGet(request, response);
	}


}
