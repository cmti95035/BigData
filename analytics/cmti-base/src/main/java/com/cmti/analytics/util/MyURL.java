package com.cmti.analytics.util;
//package com.gric.util;

import java.net.*;
import java.io.*;
import java.util.*;

public class MyURL{
    private static final boolean DEBUG=false;

    public static final int RELATIVE=0;
    public static final int HOMEHTTP=1;
    public static final int OTHERHTTP=2;
    public static final int FTP=3;
    public static final int GOPHER=4;
    //  public static final int OTHER_PROTOCOL=5;//not used
    public static final int UNKNOWN=6;
    public static final int HTTPS=7;
    public static final int MAILTO=8;
    public static final int NEWS=9;

	// FIXME currently only replace the 1st http

    public static String toHtmlDisplay(String str) {
		if (str == null)
			return null;
		ArrayList<String> alist = new ArrayList<>();
		StringTokenizer st= new StringTokenizer(str);
		
		while(st.hasMoreTokens()){
			String t = st.nextToken();
			if(t.startsWith("http://")){
				alist.add(t);
			}			
		}
		//if contains 2 urls: "url1111#A" and "url1111", we have problem
		
		for(String link:alist){
			str=MyString.replace(str, link, "<a href=\"" + link + "\">" + link + "</a>");
		}

		str = MyString.replace(str, "\n", "<br>");
		return str;
	}
    
    public static String toHtmlDisplay_(String str) {
		if (str == null)
			return null;

		int i = str.indexOf("http://");
		if (i > -1) {
			int j = str.indexOf(" ", i);
			String link = null;
			if (j > -1) {
				link = str.substring(i, j);
			} else {
				link = str.substring(i);
			}
			StringBuffer sb = new StringBuffer();
			sb.append(str.substring(0, i));
			sb.append("<a href=\"" + link + "\">" + link.substring(0, Math.min(40, link.length())) + "...</a>");
			if (j > -1)
				sb.append(str.substring(j));
			str = sb.toString();
		}

		str = MyString.replace(str, "\n", "<br>");
		return str;
	}
    
    public static String getHostWithoutWWW(String url) {
		int ind1 = url.indexOf("//");
		int ind2 = url.indexOf("/", ind1 + 2);
		String host = ind2<0 ? url.substring(ind1 + 2):url.substring(ind1 + 2, ind2);
		host = host.toLowerCase().replaceAll("^www\\.", "");
		return host;
	}
/*
    public static String getHost(String url) {
		int ind1 = url.indexOf("//");
		int ind2 = url.indexOf("/", ind1 + 2);
		String host = url.substring(ind1 + 2, ind2).toLowerCase().replaceAll("^www\\.", "");
		return host;
	}
*/    
    public static String convertWhiteSpace(String url) {
		return url.replaceAll(" ", "%20");	
	}

    public static String createLink(String text, String url){
      return "<a href=\""+url+"\">"+text+"</a>";
    }

    public static String createLink(String url){
      return createLink(url,url);
    }

    public static int whatLink(String lin, String home){

        lin=lin.toLowerCase();

        if(!isFullURL(lin))
            return RELATIVE;

        if(lin.startsWith("http:")) {
            if(lin.indexOf(home.toLowerCase())>=0){
                return HOMEHTTP;
            }else{
                return OTHERHTTP;
            }
        }

        if(lin.startsWith("ftp:"))
            return FTP;

        if(lin.startsWith("gopher:"))
            return GOPHER;

        if(lin.startsWith("https:"))
            return HTTPS;

        if(lin.startsWith("mailto:"))
            return MAILTO;

        if(lin.startsWith("news:"))
            return NEWS;

        //    System.out.println("MyURL.whatLink(): what's this? "+lin);
        return UNKNOWN;
    }

    private static URL getHome(URL url){
        int i;

        try{
            String currenturl=url.toExternalForm();
            i=currenturl.indexOf(":");
            if(DEBUG)System.out.println(currenturl.substring(0,i));

            i=currenturl.indexOf("//",i);

            i=currenturl.indexOf("/",i+2);
            if(DEBUG)System.out.println(currenturl.substring(0,i+1));

            return new URL(currenturl.substring(0,i+1));
        }catch(Exception e){
            System.err.println("MyURL"+e+":"+url);
            e.printStackTrace();
            return null;
        }

    }


    public static URL combine(URL currenturl, String str){
        String ret;
        int k1,k2;
        String base;

        if(str.startsWith("/")){
            base=getHome(currenturl).toExternalForm();
            str=str.substring(1);
        }else{
            base=currenturl.toExternalForm();
        }

        k1=base.lastIndexOf("/")+1;
        ret=base.substring(0,k1)+str;
        try{
            return new URL(ret);
        }catch(Exception e){
            return null;
        }
    }

    public static boolean isFullURL(String url){
        int col=url.indexOf(':');
        if(col<=0)return false;
        for(int i=0;i<col;i++){
            if( !Character.isLetter(url.charAt(i)) )
                return false;
        }
        return true;
    }

    public static void downloadString(String surl, String localfile) throws Exception {
//        URL url=new URL(surl);
//          URLConnection urlc=url.openConnection();
//        InputStream is=url.openStream();
  //      int size=urlc.getContentLength();//=-1 if unknown length
        File f=new File(localfile);
    //    MyIO.saveBinary(size, is, f); //byte by byte copy
      //  is.close();
      downloadString(surl,f);
    }

    public static void downloadString(String surl, File f) throws Exception {
    	
    	String str= downloadString(surl);
    //    URL url=new URL(surl);
  //      URLConnection urlc=url.openConnection();
//        BufferedReader in=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
        MyIO.saveString (str, f); //byte by byte copy            

      //  in.close();                       
    }

    public static String downloadString(String urlstr) throws IOException{
        URL url=null;//ew URL(pbURL);
        URLConnection urlc=null;//url.openConnection();
        BufferedReader in=null;//ew BufferedReader(new InputStreamReader(urlc.getInputStream()));
        url=new URL(urlstr);
        urlc=url.openConnection();
        in=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
        String ret=MyIO.readAll(in);

        in.close();
        return ret;
    }


        public static void downloadBinary(String surl, String localfile) throws IOException {
//            URL url=new URL(surl);
  //          URLConnection urlc=url.openConnection();
    //        InputStream is=url.openStream();
      //      int size=urlc.getContentLength();//=-1 if unknown length
            File f=new File(localfile);
        //    MyIO.saveBinary(size, is, f); //byte by byte copy
          //  is.close();
          downloadBinary(surl,f);
        }

            public static void downloadBinary(String surl, File f) throws IOException {
                URL url=new URL(surl);
                URLConnection urlc=url.openConnection();
                InputStream is=url.openStream();
                int size=urlc.getContentLength();//=-1 if unknown length
//                File f=new File(localfile);
                MyIO.saveBinary(size, is, f); //byte by byte copy
                is.close();
            }

    public static byte[] downloadBinary(String surl) throws IOException {
        URL url=new URL(surl);
        URLConnection urlc=url.openConnection();
        urlc.setRequestProperty("Accept", "image/bitmap, */*") ;
        InputStream is=url.openStream();
//        BufferedInputStream bin=new BufferedInputStream(is);

        int size=urlc.getContentLength();//=-1 if unknown length
        /*
        byte[] b=new byte[BUFFSIZE];
        int left=((size>=0)?size:Integer.MAX_VALUE);
        int toread, byteread;

        FileOutputStream fos=new FileOutputStream(f);
*/
        ByteArrayOutputStream bao=new ByteArrayOutputStream() ;
        MyIO.saveBinary(size, is, bao); //byte by byte copy

        is.close();
        return bao.toByteArray();
    }

    public static Object downloadObject(String surl) throws Exception {
        URL url=new URL(surl);
        URLConnection urlc=url.openConnection();
        InputStream is=url.openStream();
        ObjectInputStream oin=new ObjectInputStream(is);
        Object ret=oin.readObject();
        is.close();
        return ret;
    }

    public static void trigger(String urlstr) throws IOException{
      TThread tt=new TThread(urlstr);
        tt.start();
        System.out.println("In MyURL.trigger(): sleep...");
//        Log.log("In MyURL.trigger(): sleep...");
      try{
        Thread.sleep(5000);
      }catch(Exception e){
      }
      tt.destroy();//.stop()
//      System.exit(1);
    }

    static class TThread extends Thread{
      String urlstr=null;

      TThread(String _urlstr){
        urlstr=_urlstr;
      }

      public void run() {
//      Thread t=new Th
        System.out.println("In MyURL.TThread.run()");
        URL url=null;//ew URL(pbURL);
        URLConnection urlc=null;//url.openConnection();
        BufferedReader in=null;//ew BufferedReader(new InputStreamReader(urlc.getInputStream()));
      try{
        url=new URL(urlstr);
        urlc=url.openConnection();
        in=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
      }catch(Exception e){
            System.err.println("MyURL.run"+e+":"+urlstr);
//        Log.log("In MyURL.trigger(): sleep...");
            e.printStackTrace();
      }
//        String ret=MyIO.readAll(in);
//        in.close();
        return;
      }
    }

    public static InputStreamReader getInputStreamReader(String surl) throws Exception {
        URL url=new URL(surl);
        return new InputStreamReader(url.openStream());
    }


    public static InputStream getInputStream(String surl) throws Exception {
        URL url=new URL(surl);
        return url.openStream();
    }

    public static void main(String[] args)throws Exception{
      System.out.println("begin...");

//            MyURL.trigger("http://"+tmphost+"/servlet/POPSearchlet?w=36");
      trigger("http://"+args[0]+"/servlet/POPSearchlet?w=36");
      System.out.println("done");
    }



}
