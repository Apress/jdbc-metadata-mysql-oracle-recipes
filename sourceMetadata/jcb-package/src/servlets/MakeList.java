/**
 * file:	MakeList.java
 *<p>
 * @author:	Mahmoud Parsian
 *<p>
 * Description: This class is a utility class for servlet programmers.
 * It generates HTML lists and elements from specific data structures
 * extracted from users and databases. Each method returns a String
 * object, which will be inserted into an HTML form provides by the
 * PreparedHTML class.
 *
 *
 * This Java class is free.  No license is necessary.  If you have any
 * ideas on how to improve it please send email to mparsian@yahoo.com.
 */

import java.io.*;
import java.util.*;


import javax.servlet.*;
import javax.servlet.http.*;



public class MakeList {


    public static String makeDropdownList2(java.util.List  list) {

		if ((list == null) || (list.size() == 0)) {
				return "";
		}

		StringBuffer selectionList = new StringBuffer();

		for (int i=0; i < list.size(); i++) {
			KeyValuePair kvp = (KeyValuePair) list.get(i);
			selectionList.append(kvp.getHTMLOption());
		}

		return new String(selectionList);
    }

    public static String makeDropdownList2(java.util.List  list, String selectedKey) {

			if ((list == null) || (list.size() == 0)) {
					return "";
			}

			StringBuffer selectionList = new StringBuffer();

			for (int i=0; i < list.size(); i++) {
				KeyValuePair kvp = (KeyValuePair) list.get(i);
				selectionList.append(kvp.getHTMLOption(selectedKey));
			}

			return new String(selectionList);
    }

    public static String makeDropdownList(java.util.List  list) {

		if ((list == null) || (list.size() == 0)) {
				return "";
		}

		StringBuffer selectionList = new StringBuffer();

		for (int i=0; i < list.size(); i++) {
			String s = (String) list.get(i);
			selectionList.append("<OPTION>" + s);
		}

		return new String(selectionList);
    }

   public static String makeYesNoDropdownList(String selectedValue) {

		if ((selectedValue == null) || (selectedValue.length() == 0)) {
			return "<OPTION selected>Yes<OPTION>No";
		}
		else {
			if ( (selectedValue.equalsIgnoreCase("Yes")) || (selectedValue.equalsIgnoreCase("Y")) ){
				return "<OPTION selected>Yes<OPTION>No";
			}
			else {
				return "<OPTION selected>No<OPTION>Yes";
			}
		}

    }

    public static String makeDropdownList(java.util.List  list, String selectedValue) {

			if ((list == null) || (list.size() == 0)) {
					return "";
			}

			StringBuffer selectionList = new StringBuffer();

			for (int i=0; i < list.size(); i++) {
				String s = (String) list.get(i);
				if (s.equals(selectedValue)) {
					selectionList.append("<OPTION selected>" + s);
				}
				else {
					selectionList.append("<OPTION>" + s);
				}
			}

			return new String(selectionList);
    }

    public static String makeDropdownList(String[] array) {

			if ((array == null) || (array.length == 0)) {
				return "";
			}

			StringBuffer selectionList = new StringBuffer();

			for (int i=0; i < array.length; i++) {
				selectionList.append("<OPTION>" + array[i]);
			}

			return new String(selectionList);
    }

    public static String makeDropdownList(String[] array, String selectedValue) {

			if ((array == null) || (array.length == 0)) {
				return "";
			}

			StringBuffer selectionList = new StringBuffer();

			for (int i=0; i < array.length; i++) {

				if (array[i].equals(selectedValue)) {
					selectionList.append("<OPTION selected>" + array[i]);
				}
				else {
					selectionList.append("<OPTION>" + array[i]);
				}
			}

			return new String(selectionList);
    }


    public static String makeBulletList(java.util.List  list) {

		String bulletList = "";

		for (int i=0; i < list.size(); i++) {
			String s = (String) list.get(i);
			bulletList = bulletList + "<LI type=disc>" + s;
		}

		return bulletList;
    }

    public static String makeNumberedList(java.util.List  list) {

		String numberedList = "";

		for (int i=0; i < list.size(); i++) {
			String s = (String) list.get(i);
			numberedList = numberedList + "<LI type=1>" + s;
		}

		return numberedList;
    }


}






