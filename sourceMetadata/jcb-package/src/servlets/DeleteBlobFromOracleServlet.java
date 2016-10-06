import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import jcb.util.DatabaseUtil;
import jcb.util.IOUtil;

public class DeleteBlobFromOracleServlet extends HttpServlet {

	private static final String DELETE_RECORD =
		"delete from MyPictures where id = ?";

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

		System.out.println("--- DeleteBlobFromOracleServlet begin ---");
		Connection conn = null;
		PreparedStatement pstmt = null;
		String id = request.getParameter("id").trim();
		ServletOutputStream out = response.getOutputStream();
		response.setContentType("text/html");
		out.println("<html><head><title>Delete Photo</title></head>");

		try {
		  conn = getConnection();
		  pstmt = conn.prepareStatement(DELETE_RECORD);
		  pstmt.setString(1, id);
		  pstmt.executeUpdate();
		  out.println("<body><h3>deleted photo with id="+id+"</h3></body></html>");
		}
		catch (Exception e) {
			out.println("<body><h1>Error="+e.getMessage()+"</h1></body></html>");
		}
		finally {
		  DatabaseUtil.close(pstmt);
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
