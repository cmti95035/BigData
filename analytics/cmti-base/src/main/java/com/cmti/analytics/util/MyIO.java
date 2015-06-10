package com.cmti.analytics.util;

//package com.gric.util;

import java.io.*;
import java.net.*;
//import org.apache.regexp.*;
import java.util.regex.*;
import java.util.*;

public class MyIO {
  private static final boolean DEBUG = false;
  private static final int BUFFSIZE = 8192 * 16;

  public static void main(String[] args) throws Exception {
	  File f =new File(args[0]); 

	    BufferedReader br = new BufferedReader(new FileReader(f));
	    PrintWriter out	    = new PrintWriter(new BufferedWriter(new FileWriter(args[1])));
	    
	    int n=0;
	    while(n<2){
		    String line = br.readLine();
		    if(line.indexOf("`url`")>-1||line.indexOf("`title`")>-1){
		    	line = line.replaceAll("NOT NULL", "DEFAULT NULL");
		        System.out.println(line);
		    	n++;
		    } 	
		    out.println(line);
	    }

	    char[] cbuf = new char[BUFFSIZE];
	    int len = -1;
	    while ( (len = br.read(cbuf, 0, BUFFSIZE)) > -1) {
	      out.write(cbuf, 0, len);
	    }
	    out.close();
	  
    //java com.util.MyIO . "My??.java" -- don't forget the double quotes
	    /*
    File[] fs = null;
    if (args.length == 1) {
      fs = list(args[0]);
    }
    else {
      fs = list(args[0], args[1]);
    }
    for (int i = 0; i < fs.length; i++) {
      System.out.println(fs[i]);
    }*/
  }

  public static List<File> list(String filter, boolean subdir) { // throws org.apache.regexp.RESyntaxException {
    int ind = filter.lastIndexOf(File.separator);
    String dir = null;
    if (ind == -1) {
      dir = ".";
    }
    else {
      dir = filter.substring(0, ind + 1);
      filter = filter.substring(ind + 1);
    }
    return list(dir, filter, subdir);
  }

  public static List<File> list(String dir, String filter, boolean subdir) { //list all file in dir, given filter
    File directory = new File(dir); 
    return list(directory, filter, subdir);
  }

  public static List<File> list(File directory, String filter, boolean subdir) {//list all file in dir, given filter 
    filter = MyString.replace(filter, ".", "\\.");
    filter = MyString.replace(filter, "?", ".");
    filter = MyString.replace(filter, "*", ".*");
    filter = "^" + filter + "$";
    
    MyFileFilter myfilter=new MyFileFilter(filter);
    
    return list(directory, myfilter, subdir);
  }

  public static List<File> list(File directory, MyFileFilter myfilter, boolean subdir) { //list all file in dir, given filter 
	    File[] ret = directory.listFiles(myfilter); 
	    List<File> lret= new ArrayList<File>(); 
	    
	    for(File f:ret){
			if(f.isDirectory()==false){
				lret.add(f);
			}    	
	    }
	    
	    if(subdir){
	    	ret = directory.listFiles();
	    	for (File f:ret){
	    		if(f.isDirectory()){
	    			lret.addAll(list(f, myfilter, subdir));
	    		}
	    	} 
	    }
	    return lret;
  }

  public static String readAll(Reader in) throws IOException {
    StringBuffer sb = new StringBuffer(BUFFSIZE);
    BufferedReader br = new BufferedReader(in);
    char[] cbuf = new char[BUFFSIZE];
    int len = -1;
    while ( (len = br.read(cbuf, 0, BUFFSIZE)) > -1) {
      sb.append(cbuf, 0, len);
    }

    return sb.toString();
  }

  public static void saveString(Reader is, File f) throws IOException{

    FileWriter fos = new FileWriter(f);
    saveString( is, fos);
    fos.close();
  }

  public static void saveString(String str, File f) throws
      IOException {
	  FileWriter fos = new FileWriter(f);
	  saveString(str, fos);
	  fos.close();
  }

  public static void saveString(Reader is, Writer wr) throws
  IOException {
	  String str = readAll(is);
	  saveString(str, wr);
	  
}

  public static void saveString(String str, Writer wr) throws
  IOException {
		PrintWriter out=new PrintWriter(wr);
			out.println(str);
		out.flush();
		out.close();
	  
  }
  
  /**Read binary data from is, write to File f.
   */
  public static void saveBinary(int size, InputStream is, File f) throws
      IOException {
	  f.getParentFile().mkdirs();
    FileOutputStream fos = new FileOutputStream(f);
    saveBinary(size, is, fos);
    fos.close();

  }


  public static void saveBinary(int size, InputStream is, OutputStream out) throws
      IOException {
    byte[] b = new byte[BUFFSIZE];
    int left = ( (size >= 0) ? size : Integer.MAX_VALUE);
    int toread, byteread;

    while (left > 0) {
      toread = (left > BUFFSIZE) ? BUFFSIZE : left;
      byteread = is.read(b, 0, toread);
      if (byteread == -1) {
        break;
      }
      out.write(b, 0, byteread);
      left -= byteread;
    }

  }

  public static void sendFile(PrintWriter out, File f) throws IOException {
    String buf;
    BufferedReader in = new BufferedReader(new FileReader(f));
    while ( (buf = in.readLine()) != null) {
      out.println(buf);
    }
    in.close();
  }

  static public boolean deleteDirectory(File path) {
    if( path.exists() ) {
      File[] files = path.listFiles();
      for(int i=0; i<files.length; i++) {
         if(files[i].isDirectory()) {
           deleteDirectory(files[i]);
         }
         else {
           boolean del= files[i].delete();
//           System.err.println(del+":"+files[i].getAbsolutePath());
         }
      }
    }
    boolean del= path.delete();
  //  System.err.println(del+":"+path.getAbsolutePath());
    return( del );
  }
}

class MyFileFilter
    implements FilenameFilter {

  private static final boolean DEBUG = false;
  Pattern p;
//  RE re = null;
//    String filter=null;
  MyFileFilter(String filter) { // throws org.apache.regexp.RESyntaxException {
    //      this.filter=filter;
    if (DEBUG) {
      System.out.println("filter:" + filter);
    }
//    re = new RE(filter);
    p = Pattern.compile(filter);

  }

  public boolean accept(File dir, String name) { // throws org.apache.regexp.RESyntaxException{
    /*    try{
          re = new RE(filter);
        }catch(org.apache.regexp.RESyntaxException e){
          e.printStackTrace();
        }*/
    Matcher m = p.matcher(name);
    boolean ret = m.matches();

//    boolean ret = re.match(name);
//      System.out.println("do:"+name+ret);
    return ret;
  }

}
