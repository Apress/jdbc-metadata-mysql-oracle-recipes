import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import jcb.util.DatabaseUtil;
import jcb.util.IOUtil;

// add these imports for access to the required Oracle classes
import oracle.jdbc.driver.*;
import oracle.sql.BLOB;

public class UpdateBlobToOracleServlet extends HttpServlet {

		// to write LOB data, the application must acquire a write lock
		// on the LOB object; one way to accomplish this is through a
		// SELECT FOR UPDATE; also, disable auto-commit mode.
		static final String PICTURE_LOCATOR =
        "select photo from  MyPictures where  id = ? for update nowait";

	public static Connection getConnection() throws Exception {

		String driver = "oracle.jdbc.driver.OracleDriver";
    	String url = "jdbc:oracle:thin:@matrix:1521:caspian";
    	String username = "mp";
    	String password = "mp2";

		Class.forName(driver);	// load Oracle driver
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	public void doGet(HttpServletRequest request,
					  HttpServletResponse response)
		throws IOException, ServletException {

		System.out.println("--- UpdateBlobToOracleServlet begin ---");

		InputStream photoContent = null;
		Connection conn = null;

		String id = trimParameter(request.getParameter("id"));
		String photoAsURL = trimParameter(request.getParameter("photo"));
		System.out.println("UpdateBlobToOracleServlet: id="+id);
		System.out.println("UpdateBlobToOracleServlet photoAsURL="+ photoAsURL);
		ServletOutputStream out = response.getOutputStream();

		response.setContentType("text/html");
		out.println("<html><head><title>Person Photo</title></head>");

		System.out.println("-- doGet() 0");
		try {
		  System.out.println("-- doGet() 1");
		  conn = getConnection();
		  System.out.println("-- doGet() 2");
		  photoContent = getBlobsContent(photoAsURL);
		  System.out.println("-- doGet() 3");
		  update(conn, id, photoContent);
		  System.out.println("-- doGet() 4");
		  out.println("<body><h4>OK: updated record with id="+id+"</h4></body></html>");
	  	}
	  	catch(Exception e) {
		  e.printStackTrace();
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

		System.out.println("--0.1");
    	OutputStream out = null;
		java.sql.ResultSet rs = null;
		java.sql.PreparedStatement pstmt = null;
    	oracle.sql.BLOB oracleBlob = null;
		System.out.println("--0.2");
		try {
			conn.setAutoCommit(false);
			System.out.println("--1");
			pstmt = conn.prepareStatement(PICTURE_LOCATOR);
			System.out.println("--2");
			pstmt.setString(1, id);
			System.out.println("--3");
        	rs = pstmt.executeQuery();
			System.out.println("--4");
        	rs.next();
			System.out.println("--5");
        	oracleBlob = ((OracleResultSet)rs).getBLOB(1);
			System.out.println("--6");

      		// Now that we have the locator, lets store the photo
      		out = oracleBlob.getBinaryOutputStream();
      		System.out.println("--7");
			//int photoLength = photo.available();
      		byte[] buffer = new byte[oracleBlob.getBufferSize()];
      		//photo.read(buffer, 0, photoLength);
      		//out.write(buffer);
      		System.out.println("--8");
      		int length = 0;
      		while ((length = photo.read(buffer)) != -1) {
        		out.write(buffer, 0, length);
      		}

      		// You've got to close the output stream before
      		// you commit, or the changes are lost!
      		out.close();
      		photo.close();
      		System.out.println("--9");
	        conn.commit();
	    }
    	finally {
      		DatabaseUtil.close(rs);
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
