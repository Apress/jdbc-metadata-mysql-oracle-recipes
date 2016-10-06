/**
 * file:	PreparedHTML.java
 * author:	Garth Fisher
 *
 * description:
 * This class is a tool for servlet programmers.  Servlets that return web
 * pages often store the static portions of the HTML they return as String objects.
 * Changing the look and feel of the web pages returned by servlets would
 * then require a recompile.  Also, since the HTML is buried in a Java
 * class file it is not as accessible to web page designers who are not
 * familiar with Java.  Using PreparedHTML you can change the look and feel
 * of the web pages returned by servlets without touching the servlet code.
 *
 * PreparedHTML allows a servlet program to keep Java code and HTML separate.
 * The HTML is kept in a standard .html file that contains special HTML comments
 * that represent named place holder tags.  When a PreparedHTML object is
 * constructed it reads the HTML file noting the location of place holders.
 * The servlet code constructs the PreparedHTML object, using a known file name,
 * and then sets the value of the named place holders.  When the PreparedHTML is
 * sent to an OutputStream, using a PrintWriter, the value of each place holder
 * is inserted into the HTML file.  The returned HTML file has been expanded to
 * include dynamic content.
 *
 * Why not use Java Server Pages (JSP)?  JSP files are HTML files with some
 * special tags that can contain Java code.  If you are an HTML developer, who
 * is expanding your skill set, JSP is great.  But if you are an experienced
 * servlet programmer JSP may not be the right choice.  The servlet API
 * is powerful.  Doing things like keeping a persistent database connection
 * pool is easier (in my opinion) with the servlet API.  JSP requires a web
 * server that includes a special JSP engine.  PreparedHTML is much simpler.
 * Also, there is something to be said for keeping Java code in Java files.
 *
 * What does the HTML file look like?   The syntax for a place holder is
 * <!--PreparedHTML.PlaceHolder:placeHolderName--> where placeHolderName
 * is the name used to set the value of the place holder.  Below is an example
 * of a simple PreparedHTML file that contains a place holder called tableRows.
 *
 * <HTML><TITLE>Some Table</TITLE><BODY>
 * <TABLE>
 * <!--PreparedHTML.PlaceHolder:tableRows-->
 * </TABLE>
 * </BODY></HTML>
 *
 * To use this PreparedHTML file, construct a PreparedHTML object passing in
 * the file name, call setPlaceHolder(String name, Object value) with the String
 * "tableRows" and the value that you want inserted into the HTML (value.toString()
 * will be used).  Then call the print method passing in a PrintWriter object
 * and the expanded HTML will be sent to the web browser.  This HTML file could
 * be handed to a web page designer for artistic improvements.
 *
 * PreparedHTML supports multiple occurrences of a place holder name in the same
 * file and you only have to call the setPlaceHolder(...) method once.  Also, feel
 * free to put place holders anywhere, such as on the same line or inside quotes.
 *
 * If the setPlaceHolder(String name, Object value) method is called and the name
 * is unknown nothing happens.  If the place holder of that name already has a
 * value the old value will be lost.  If multiple place holders with that name
 * exist, then each place holder will get the value.  If a place holder's
 * value has not been set then the String "null" will be inserted for that
 * place holder.
 *
 * A PreparedHTML object can be created once and reused by calling the reset()
 * method after each usage.  This method checks if the file has been modified
 * and reads it again if it has changed.  This way changes to the HTML file
 * can be viewed immediately.
 *
 * This Java class is free.  No license is necessary.  If you have ideas on how
 * to improve it please send email to garth_fisher@excite.com.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.*;
import javax.servlet.http.*;


public class PreparedHTML {

	private Vector placeHolders;	// holds all PlaceHolder objects
    private String fileName;		// name of file passed to c'tor
	private String endingHtml;		// the html following the last PlaceHolder
	private long lastModified;		// used to check if the file as changed


	/**
	 * Constructs a PreparedHTML object
	 * @param fileName - the name of the file containing HTML with
	 *                   special place holder tags
	 */
	public PreparedHTML(String fileName) throws IOException {
        	this.fileName = fileName;
			readFile();
	}


	/**
	 * Call this method to set the value of a place holder
	 * @param name  - the name of the place holder
	 * @param value - the place holder is set to value.toString()
	 */
	public void setPlaceHolder(String name, Object value) {
		PlaceHolder ph = new PlaceHolder(name, null);
		int i = -1;
		while ((i = placeHolders.indexOf(ph, i + 1)) != -1) {
			ph = (PlaceHolder)placeHolders.elementAt(i);
			ph.htmlToInsert = value.toString();
		}
	}


	/**
	 * Call this method before reusing a PreparedHTML instance.  If
	 * the file has changed it will be read again.  This method
	 * removes any previous place holder's values.
	 */
	public void reset() throws IOException {
		File file = new File(fileName);
		if (lastModified < file.lastModified())
			readFile();
		else {
			for (int i = 0; i < placeHolders.size(); i++) {
				PlaceHolder ph = (PlaceHolder)placeHolders.elementAt(i);
				ph.htmlToInsert = null;
			}
		}
	}


	/**
	 * Writes the HTML using a PrintWriter.  The value of each
	 * place holder is written where that place holder exists
	 * in the HTML file (the file's name was passed to the c'tor).
	 * @param out - the PrintWriter used to write the HTML
	 */
	public void print(PrintWriter out) {
		for (int i = 0; i < placeHolders.size(); i++) {
			PlaceHolder ph = (PlaceHolder)placeHolders.elementAt(i);
			out.print(ph.precedingHtml);
			out.print(ph.htmlToInsert);
		}
		out.print(endingHtml);
		out.flush();
	}

	/**
	 * Writes the HTML using a HttpServletResponse.
	 * @param response - the HttpServletResponse used to write the HTML
	 */
	public void display(HttpServletResponse response) throws IOException {
		PrintWriter out = new PrintWriter(response.getOutputStream());
		print(out);
	}

	/**
	 * Provides the value of a PreparedHTML instance as a String, which
	 * can be very helpful when debuging.
	 */
	public String toString() {
		StringBuffer html = new StringBuffer();
		for (int i = 0; i < placeHolders.size(); i++) {
			PlaceHolder ph = (PlaceHolder)placeHolders.elementAt(i);
			html.append(ph.precedingHtml);
			html.append(ph.htmlToInsert);
		}
		html.append(endingHtml);
		return html.toString();
	}


	/**
	 * Reads the HTML file (the file's name was passed the the c'tor).
	 */
	private void readFile() throws IOException {
		File file = new File(fileName);
		lastModified = file.lastModified();
		BufferedReader br = new BufferedReader(new FileReader(file));
		placeHolders = new Vector();
		StringBuffer strBuf = new StringBuffer();
          	String line = null;
		boolean foundPlaceHolder = false;
		while (foundPlaceHolder || (line = br.readLine()) != null) {
			foundPlaceHolder = false;
			int index = 0;
			if ((index = line.indexOf("<!--PreparedHTML.PlaceHolder")) != -1) {
				foundPlaceHolder = true;
				strBuf.append(line.substring(0, index));
				String s = line.substring(index, line.length());
				int endIndex = s.indexOf("-->");
				line = s.substring(endIndex + 3, s.length());
				String name = s.substring(0, endIndex);
				StringTokenizer toker = new StringTokenizer(name, ":");
				toker.nextToken();
				name = toker.nextToken();
				placeHolders.addElement(new PlaceHolder(name, strBuf.toString()));
				strBuf = new StringBuffer();
			}
			else
				strBuf.append(line).append("\n");
		}
		endingHtml = strBuf.toString();
	}



	/**
	 * PlaceHolder is a helper class used by PreparedHTML objects.  It does
	 * not have scope outside of this file.
	 */
	class PlaceHolder {
		String name;
		int htmlIndex;
		String precedingHtml;
		String htmlToInsert;


		PlaceHolder(String name, String precedingHtml) {
			this.name = name;
			this.precedingHtml = precedingHtml;
		}

		public boolean equals(Object obj) {
			if (obj instanceof PlaceHolder &&
				name.equals(((PlaceHolder)obj).name))
				return true;
			return false;
		}
	}

}


