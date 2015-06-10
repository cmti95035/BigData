package com.cmti.analytics.util;

//package com.gric.util;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * This class implements a few methods to manipulate String or String[].
 * 
 * @author Bill Mo
 * @version 1.0
 */
public class MyString {
/*	public static String convert(String instr, String in_code, String out_code){//"UTF8"
		try{
			byte[] inb=instr.getBytes() ;
			ByteArrayInputStream ins=new ByteArrayInputStream(inb);
			InputStreamReader inr = new InputStreamReader(ins, in_code);//"GB2312"
			BufferedReader in =new BufferedReader(inr);
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	*/

	public static String keepLetterOrDigit(String instr, String addToKeep){
		if(instr==null)
			return null;
		instr=instr.trim();
		if(instr.length()==0)
			return "";
		
		StringBuilder stb=new StringBuilder();
		char[] chara=instr.toCharArray();
		for(char ch: chara){
			if(Character.isLetterOrDigit(ch) || addToKeep.indexOf(ch)>-1){
				stb.append(ch);
			}else{
				stb.append(" ");
			}
		}
		
		return removeJunk(stb.toString());		
	}

	public static String removeNonDigit(String instr){
		if(instr==null)
			return null;
		StringBuffer buf =new StringBuffer() ;
		int len = instr.length();
		for(int i=0; i<len; i++){
			char ch=instr.charAt(i);
			if(Character.isDigit(ch)){
				buf.append(ch);
			}
		}
		return buf.toString();
	}
	
	public static boolean containsAny(String title, String str, String delim){
		StringTokenizer st = new StringTokenizer(str, delim);
		while (st.hasMoreTokens()) {
			if(title.indexOf(st.nextToken())>-1)
				return true;					
		}
		return false;
	}
	
	public static boolean containsNonASCII(String str){
		for(int i=0; i<str.length(); i++){
			char c=str.charAt(i);
			if(c<0x20 || c>0x7e ){//http://mindprod.com/jgloss/ascii.html
				return true;
			}
		}
		return false;
	}

	public static String removeNonASCII(String str){
		//Character conversion error: "Malformed UTF-8 char -- is an XML encoding declaration missing?" (line number may be too low).
		if(str==null)
			return null;
		int len=str.length();
		CharArrayWriter cw=new CharArrayWriter(len);
		for(int i=0; i<str.length(); i++){
			char c=str.charAt(i);
			if(c<0x20 || c>0x7e ){//http://mindprod.com/jgloss/ascii.html
				//;
			}else{
				cw.write(c);
			}
		}
		String ret=cw.toString();
		int len2=ret.length();
		return ret;
	}
	
	public static boolean isBlank(String str){
		if (str == null )
			return true;
		if (str.trim().length() == 0) 
			return true;
		return false;

	}

	public static long getNo(String str, int i){
		int start=i;
		int end=i+1;

		for(int j=i-1; j>=0; j-- ){
			if(Character.isDigit(str.charAt(j))){
				start=j;
			}else{
				break;
			}
		}
		for(int j=i+1; j<str.length(); j++ ){
			if(Character.isDigit(str.charAt(j))){
				end=j+1;
			}else{
				break;
			}
		}
		
		return Long.parseLong(str.substring(start, end));
	}

	public static int getNoInt(String str, int i){
		int start=i;
		int end=i+1;

		for(int j=i-1; j>=0; j-- ){
			if(Character.isDigit(str.charAt(j))){
				start=j;
			}else{
				break;
			}
		}
		for(int j=i+1; j<str.length(); j++ ){
			if(Character.isDigit(str.charAt(j))){
				end=j+1;
			}else{
				break;
			}
		}
		
		return Integer.parseInt(str.substring(start, end));
	}
	
	public static String getSpace(int n) {
		if (n<=0) {
			return "";
		}
		char[] a=new char[n];
		Arrays.fill(a,' ');
		
		return new String(a);
	}

	public static String flip(String str) {
		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str);
		return flip(st);
	}

	public static String flip(String str, String delim) {
		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delim);
		return flip(st);
	}

	private static String flip(StringTokenizer st) {
		int n=st.countTokens();
		String[] ret=new String[n];
		for(int i=n-1; i>=0; i--){
			ret[i]=st.nextToken();
		}
		return MyString.toString(ret, " ");
	}

	public static String upper1st(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public static String[] tokenToArray(String str, String delim) {
		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delim);
		return tokenToArray(st);
	}

	public static String[] tokenToArray(String str) {
		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str);
		return tokenToArray(st);
	}

	public static String[] tokenToArray(StringTokenizer st) {
		Vector v = new Vector();
		while (st.hasMoreTokens()) {
			v.addElement(st.nextToken());
		}
		String[] ret = new String[v.size()];
		v.copyInto(ret);
		return ret;
	}

	private static final boolean DEBUG = false;

	public static boolean containsIgnoreCase(String[] list, String obj) {
		if (list == null || obj == null) {
			return false;
		}

		for (int i = 0; i < list.length; i++) {
			if (obj.equalsIgnoreCase(list[i])) {
				return true;
			}
			;
		}
		return false;
	}

	public static boolean containsIgnoreCase(String[] list, String[] obj) {
		if (list == null || obj == null) {
			return false;
		}

		for (int i = 0; i < obj.length; i++) {
			if (!containsIgnoreCase(list, obj[i])) {
				return false;
			}
			;
		}

		return true;
	}

	public static boolean contains(String[] list, String obj) {
		if (list == null || obj == null) {
			return false;
		}

		for (int i = 0; i < list.length; i++) {
			if (obj.equals(list[i])) {
				return true;
			}
			;
		}
		return false;
	}

	public static boolean contains(String[] list, String[] obj) {
		if (list == null || obj == null) {
			return false;
		}

		for (int i = 0; i < obj.length; i++) {
			if (!contains(list, obj[i])) {
				return false;
			}
			;
		}

		return true;
	}

	/**
	 * This function searches String <code>str</code> starting from position
	 * <code>init</code>, it returns a substring between string
	 * <code>tag0</code> and string <code>tag1</code>. For example,
	 * MyString.between("123abc456ab789ef", "ab", "ef", 5) returns "789".
	 * 
	 * @param str
	 *            input string
	 * @param tag0
	 *            first tag
	 * @param tag1
	 *            second tag
	 * @param init
	 *            the start position to search for <code>tag0</code>,
	 *            <code>tag1</code>
	 * 
	 * @return the substring from the input string between <code>tag0</code>
	 *         and <code>tag1</code> after index <code>init</code>
	 */
	public static String between(String str, String tag0, String tag1, int init) {
		int i = str.indexOf(tag0, init);
		if (i >= 0) {
			int j = str.indexOf(tag1, i + tag0.length());
			if (j > i) {
				return str.substring(i + tag0.length(), j);
			}
		}
		return null;
	}

	public static String between(String str, String tag0, String tag1,
			int init, int maxlen) {
		int i = str.indexOf(tag0, init);
		if (i >= 0) {
			int j = str.indexOf(tag1, i + tag0.length());
			//            System.out.println("i="+i+"j="+j+" "+(i+tag0.length()+maxlen));
			if (j > i && j <= i + tag0.length() + maxlen) {
				return str.substring(i + tag0.length(), j);
			} else if (j > i) {
				return between(str, tag0, tag1, i + tag0.length(), maxlen);
			}
		}
		return null;
	}

	/**
	 * This function does the same as between(String, String, String), except it
	 * starts the searching from the first character in string <code>str</code>.
	 * 
	 * @param str
	 *            input string
	 * @param tag0
	 *            first tag
	 * @param tag1
	 *            second tag
	 * 
	 * @return the substring from the input string between <code>tag0</code>
	 *         and <code>tag1</code>
	 */
	public static String[] allBetween(String str, String tag0, String tag1) {
		int init = 0;
		ArrayList v = new ArrayList();
		String tmp = null;
		do {
			tmp = between(str, tag0, tag1, init);
			if (tmp != null) {
				v.add(tmp);
				String elem = tag0 + tmp + tag1;
				init = str.indexOf(elem, init) + elem.length();
			}
		} while (tmp != null);

		String[] ret = new String[v.size()];
		v.toArray(ret);

		return ret;
	}

	public static String between(String str, String tag0, String tag1) {
		return between(str, tag0, tag1, 0);
	}
	
	public static String replaceBetween(String str, String tag0, String tag1, String insert) {
		int i0 = str.indexOf(tag0);
		if(i0 < 0){
			return str;
		}
		
		i0 += tag0.length();
		
		int i1 = str.indexOf(tag1, i0);
		if(i1 < 0){
			return str;
		}

		return str.substring(0, i0) + insert + str.substring(i1);
	}
	
	/**
	 * This function removes the space character, the tab character, the newline
	 * character, and the carriage-return character.
	 * 
	 * @param in
	 *            input string
	 * 
	 * @return the input string with space, tab, newline, return characters
	 *         removed
	 */
	public static String trimLine(String in) {
		StringTokenizer st = new StringTokenizer(in);
		StringBuffer sb = new StringBuffer();
		while (st.hasMoreTokens()) {
			sb.append(st.nextToken());
		}
		return sb.toString();
	}

	public static String trimLine(String in, String delim) {
		StringTokenizer st = new StringTokenizer(in, delim);
		StringBuffer sb = new StringBuffer();
		while (st.hasMoreTokens()) {
			sb.append(st.nextToken());
		}
		return sb.toString();
	}

	public static String removeJunk(String in) {
		if(in==null)
			return null;
		StringTokenizer st = new StringTokenizer(in);
		return removeJunk(st);
	}

	public static String removeJunk(String in, String delim) {
		if(in==null)
			return null;
		StringTokenizer st = new StringTokenizer(in, delim);
		return removeJunk(st);
	}
	
	private static String removeJunk(StringTokenizer st) {
		StringBuffer sb = new StringBuffer();
		boolean first=true;
		while (st.hasMoreTokens()) {
			if(first){
				first=false;
			}else{
				sb.append(" ");
			}
			sb.append(st.nextToken());
		}
		return sb.toString();		
	}

	/**
	 * This function replaces <code>oldstr</code> with <code>newstr</code>
	 * in string <code>buf</code>.
	 * 
	 * @param buf
	 *            input string
	 * @param oldstr
	 *            substring to be replaced
	 * @param newstr
	 *            substring to replace <code>oldstr</code>
	 * 
	 * @return input string where occurrances of <code>oldstr</code> are
	 *         replaced with <code>newstr</code>
	 */
	public static String replace(String buf, String oldstr, String newstr) {
		StringBuffer ret = new StringBuffer();
		int init = 0, pos;

		while ((pos = buf.indexOf(oldstr, init)) != -1) {
			ret.append(buf.substring(init, pos) + newstr);
			init = pos + oldstr.length();
		}
		ret.append(buf.substring(init));

		return ret.toString();
	}

	/**
	 * This function checks occurance of string <code>c</code> at the
	 * beginning of string <code>ptr</code>.
	 * 
	 * @param ptr
	 *            input string
	 * @param c
	 *            string which might show at the beginning of the input string
	 * 
	 * @return number of occurance of string <code>c</code> at the beginning
	 *         of string <code>ptr</code>.
	 */
	public static int startsWith(String ptr, String c) {
		int i;
		StringBuffer sb = new StringBuffer(1000);

		for (i = 0; i >= 0; i++) {
			sb.append(c);
			if (!ptr.startsWith(sb.toString())) {
				break;
			}
		}

		return i;
	}

	public static int toInt(String qtrwk) {
		int i = count(qtrwk, '0');
		String tmp = qtrwk.substring(i);
		int qtr = (new Integer(tmp)).intValue();
		return qtr;
	}

	/**
	 * This function returns number of occurance of character <code>c</code>
	 * in string <code>ptr</code>.
	 * 
	 * @param ptr
	 *            input string
	 * @param c
	 *            a character that may show up at the beginning of the input
	 *            string
	 * 
	 * @return number of occurance of character <code>c</code> in string
	 *         <code>ptr</code>.
	 */
	public static int count(String ptr, char c) {
		int coun = 0, pos = 0;

		while ((pos = ptr.indexOf(c, pos)) != -1) {
			coun++;
			pos++;
		}

		return coun;
	}

	/**
	 * This function returns number of occurance of string <code>c</code> in
	 * string <code>ptr</code>.
	 * 
	 * @param ptr
	 * @param c
	 * 
	 * @return number of occurance of string <code>c</code> in string
	 *         <code>ptr</code>.
	 */
	public static int count(String ptr, String c) {
		int coun = 0, pos = 0;

		while ((pos = ptr.indexOf(c, pos)) != -1) {
			coun++;
			pos += c.length();
		}

		return coun;
	}

	/**
	 * This function returns the following information on obj: its class name,
	 * superclass name, member variable values of "obj" instance.
	 * 
	 * @param obj
	 *            a instance of any class
	 * 
	 * @return the class name, superclass name, member variable values of
	 *         <code>obj</code>
	 */
	public static String toReflectString(Object obj) {
		Class cl = obj.getClass();
		String r = cl.getName() + "[";
		Class sc = cl.getSuperclass();

		if (!sc.equals(Object.class)) {
			r += sc + ",";
		}
		Field[] fields = cl.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			r += f.getName() + "=";
			try {
				r += f.get(obj);
			} catch (IllegalAccessException e) {
				r += "???";
			}

			if (i < fields.length - 1) {
				r += ",";
			} else {
				r += "]";
			}
		}
		return r;
	}

	/**
	 * This function returns the index of first occurance of string "item" in
	 * the string array "strArray"; it returns -1 if the string "item" doesn't
	 * exist in array "strArray".
	 * 
	 * @param strArray
	 *            input String array
	 * @param item
	 *            the String to be looked for in the input array
	 * 
	 * @return the index of <code>item</code> inside <code>strArray</code>;
	 *         or -1 if <code>item</code> is not found in
	 *         <code>strArray</code>
	 */

	public static int stringAt(String[] strArray, String item) {
		for (int i = 0; i < strArray.length; i++) {
			if (item.equals(strArray[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * This function does similiar checking as Java String.equals(), but this
	 * function allows null string inputs.
	 * 
	 * @param str1
	 *            first input String
	 * @param str2
	 *            second input String
	 * 
	 * @return true if both strings all same or both null, false otherwise
	 */

	public static boolean myEquals(String str1, String str2) {
		if (str1 == null) {
			if (str2 == null) {
				return true;
			} else {
				return false;
			}
		} else {
			if (str2 == null) {
				return false;
			} else {
				return str1.equals(str2);
			}
		}
	}

	/**
	 * This function compares two String arrays and check if they're same.
	 * 
	 * @param str1
	 *            first input String[]
	 * @param str2
	 *            second input String[]
	 * 
	 * @return boolean
	 */
	/*
	 * public static boolean myEqualsNoOrder(String[] str1, String[] str2){
	 * String[] tmp1=(String[])MyClone.clone(str1); String[]
	 * tmp2=(String[])MyClone.clone(str2); Arrays.sort(tmp1); Arrays.sort(tmp2);
	 * return myEquals(tmp1, tmp2); } public static boolean myEquals(String[]
	 * str1, String[] str2) { return Arrays.equals(str1, str2); } /* if (str1 ==
	 * null && str2 == null) return true; if ( (str1 == null && str2 != null) ||
	 * (str1 != null && str2 == null) ) return false; if (str1.length !=
	 * str2.length) return false; for (int i=0; i <str1.length; i++) if (
	 * MyString.myEquals(str1[i], str2[i]) == false) return false; return true;
	 */

	/**
	 * This function compares two non-null two-dimension String arrays, which
	 * have the same dimension sizes.
	 * 
	 * @param str1
	 *            first input String[row][col]
	 * @param str2
	 *            second input String[row][col]
	 * 
	 * @return A int[] which contains all changed column numbers
	 */

	public static int[] findDiffCols(String[][] arr1, String[][] arr2) {
		int[] intArr1 = new int[arr1[0].length];
		int index = 0;

		for (int col = 0; col < arr1[0].length; col++) {
			boolean tmp = true;
			for (int row = 0; row < arr1.length; row++) {
				if (MyString.myEquals(arr1[row][col], arr2[row][col]) == false) {
					tmp = false;
					break;
				}
			}

			if (!tmp) {
				intArr1[index] = col;
				index++;
			}
		}

		int[] ret = new int[index];
		for (int i = 0; i < index; i++) {
			ret[i] = intArr1[i];

		}
		return ret;
	}

	public static String[][] rotate(String[][] tmp) {
		if (tmp == null) {
			return null;
		}
		if (tmp.length == 0) {
			return tmp;
		}

		int len = tmp[0].length;
		String[][] ret = new String[len][tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			for (int j = 0; j < len; j++) {
				ret[j][i] = tmp[i][j];
			}
		}

		return ret;
	}

	public static String toString(Object[] field, String sep) {
		if (field == null) {
			return null;
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < field.length; i++) {
			if (i != 0) {
				buf.append(sep);
			}
			String tmp = (field[i] == null) ? "" : field[i].toString();
			buf.append(tmp);
		}
		return buf.toString();
	}

	public static String toString(Map map, String sep) {
		if (map == null) {
			return null;
		}
		Object[] field = map.values().toArray();

		return toString(field, sep);//buf.toString();
	}

	public static String toString(List list, String sep) {
		if (list == null) {
			return null;
		}
		Object[] field = list.toArray();

		return toString(field, sep);//buf.toString();
	}

	public static String toString(Enumeration e, String sep) {
		if (e == null) {
			return null;
		}
		List field = new ArrayList();
		 while ( e.hasMoreElements() ) {
			 field.add(e.nextElement());
	     }		 

		return toString(field, sep);//buf.toString();
	}

}