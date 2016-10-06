import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import jcb.util.DatabaseUtil;
import jcb.util.IOUtil;
import jcb.util.RandomGUID;

public class DisplayMySqlBlobAsURLServlet extends HttpServlet {

	// directory where blob data will be placed as files.
	private static final String BLOB_DIRECTORY =
		"d:/tomcat/webapps/octopus/blobDir";

	// BLOB_DIRECTORY as a URL
	private static final String BLOB_URL =
		"http://localhost:8000/octopus/blobDir";

	private static final String BLOB_FILE_PREFIX = "/blob-";

	public static Connection getConnection() throws Exception {
		String driver = "org.gjt.mm.mysql.Driver";
    	String url = "jdbc:mysql://localhost/octopus";
    	String username = "root";
    	String password = "root";

		Class.forName(driver);	// load MySQL driver
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

    private static String getBlobAsURL(Blob blob) throws Exception {

    	InputStream in = null;
    	FileOutputStream out = null;
		try {

        	if (blob == null) {
				return null;
			}

			// get a random GUID for blob filename
			String guid = RandomGUID.getGUID();
			String blobFile = BLOB_DIRECTORY + BLOB_FILE_PREFIX + guid;

     		in = blob.getBinaryStream();
         	if (in == null) {
				return null;
			}

     		out = new FileOutputStream(blobFile);
      		int length = (int) blob.length();

      		int bufferSize = 1024;
      		byte[] buffer = new byte[bufferSize];
      		while ((length = in.read(buffer)) != -1) {
        		out.write(buffer, 0, length);
      		}
      		out.flush();

      		return BLOB_URL + BLOB_FILE_PREFIX + guid;
    	}
    	//catch (Exception e) {
      	//	return null;
    	//}
    	finally {
		  IOUtil.close(in);
		  IOUtil.close(out);
    	}

	}

	public void doGet(HttpServletRequest request,
					  HttpServletResponse response)
		throws IOException, ServletException {

		System.out.println("*** DisplayMySqlBlobAsURLServlet begin----------------");

		Blob photo = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String id = request.getParameter("id").trim();
		String query = "select photo from MyPictures where  id = "+id;
		ServletOutputStream out = response.getOutputStream();
		response.setContentType("text/html");

		try {
		  conn = getConnection();
	  	}
	  	catch(Exception e) {
			out.println("<html><head><title>Person Photo</title></head>");
			out.println("<body><h1>Database Connection Problem.</h1></body></html>");
			return;
		}

		try {
		  stmt = conn.createStatement();
		  rs = stmt.executeQuery(query);
		  out.println("<html><head><title>Person Photo</title></head>");

		  if (rs.next()) {
			photo = rs.getBlob(1);
			out.println("<body><h3>photo id="+id+"</h3>"+
						getBlobAsURL(photo)+"</body></html>");
		  }
		  else {
			out.println("<body><h1>No photo found for id="+id+"</h1></body></html>");
			return;
		  }
		}
		catch (Exception e) {
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
