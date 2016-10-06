import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import jcb.util.DatabaseUtil;
import jcb.util.IOUtil;


public class UpdateBlobToMySqlServlet extends HttpServlet {

	static String UPDATE_PICTURE =
		"update MyPictures set photo = ? where id = ?";

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

		System.out.println("--- UpdateBlobToMySqlServlet begin---");

		InputStream photoContent = null;
		Connection conn = null;

		String id = trimParameter(request.getParameter("id"));
		String photoAsURL = trimParameter(request.getParameter("photo"));
		ServletOutputStream out = response.getOutputStream();

		response.setContentType("text/html");
		out.println("<html><head><title>Person Photo</title></head>");

		try {
		  conn = getConnection();
		  photoContent = getBlobsContent(photoAsURL);
		  update(conn, id, photoContent);
		  out.println("<body><h4>OK: updated record with id="+id+"</h4></body></html>");
	  	}
	  	catch(Exception e) {
		  out.println("<body><h4>Error: "+e.getMessage()+"</h4></body></html>");
		}
	}

	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {
		doGet(request, response);
	}

	public void update(Connection conn, String id, InputStream photo)
  		throws Exception {

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(UPDATE_PICTURE);
			pstmt.setBinaryStream(1, photo, photo.available());
			pstmt.setString(2, id);
			pstmt.executeUpdate();
			conn.commit();
		}
		finally {
		  DatabaseUtil.close(pstmt);
		}
  	}

	public static InputStream getBlobsContent(String urlAsString)
		throws Exception {

		// we assume that the urlAsString is a URL as a string object
		// urlAsString is like: "http://www.geocities.com/mparsian/tiger1.jpg"

		java.net.URL url = new java.net.URL(urlAsString);
		java.net.URLConnection urlConn = url.openConnection();
		urlConn.connect();
		InputStream content = urlConn.getInputStream();
		return content;
	}

	private static String trimParameter(String s) {
		if ((s == null) || (s.length() == 0)) {
			return s;
		}
		else {
			return s.trim();
		}
	}
}
