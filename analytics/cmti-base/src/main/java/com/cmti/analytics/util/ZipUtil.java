package com.cmti.analytics.util;

//package com.gric.util;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream; 
//import org.apache.commons.compress.tar.TarEntry;  
//import org.apache.commons.compress.tar.TarInputStream; 

import org.apache.commons.io.IOUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public class ZipUtil {
	private static final boolean DEBUG = false;
	private static final int BUFFER = 2048;

    public static void main(String[] args)throws Exception{
      System.out.println("begin...");

//            MyURL.trigger("http://"+tmphost+"/servlet/POPSearchlet?w=36");
      convertTarToZip(args[0], args[1]);
      System.out.println("done");
    }

	public static void convertTarToZip(String tarFilePath, String zipFilePath) {
		convertTarToZip(tarFilePath, zipFilePath, null, false);
	}

	public static void convertTarToZip(String tarFilePath, String zipFilePath,
			String keepType, boolean del) {//keepType is the only file type we want to keep in the new zip file, like "java".
		try {
			FileOutputStream fos = new FileOutputStream(zipFilePath);
			ZipOutputStream zos = new ZipOutputStream(fos);
			File tarFile=new File(tarFilePath);
			TarInputStream tin = new TarInputStream(new GZIPInputStream(new FileInputStream(tarFile)));
			TarEntry tarEntry = null;
			while ((tarEntry = tin.getNextEntry()) != null) {// create a file
																// with the same
																// name as the
																// tarEntry
				String name = tarEntry.getName();
				if (keepType != null && name.endsWith(keepType) == false) {
					continue;
				}
				ZipEntry zipEntry = new ZipEntry(name);
				// place the zip entry in the ZipOutputStream object
				zos.putNextEntry(zipEntry);
				tin.copyEntryContents(zos);
			}
			tin.close();
			zos.close();
			fos.close();
			if(del)tarFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void trim(String from, String to, String keepType) {
		trim(from, to, keepType, false);
	}
	
	public static void trim(String from, String to, String keepType, boolean del) {
		try {
			File orgFile = new File(from);
			File toFile = new File(to);

			// FileInputStream fis = new FileInputStream(orgFile);
			// ZipInputStream zis = new ZipInputStream(fis);

			FileOutputStream fos = new FileOutputStream(toFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			ZipFile zipfile = new ZipFile(orgFile);
			Enumeration<? extends ZipEntry> e = zipfile.entries();

			while (e.hasMoreElements()) {
				// i++;
				// log.debug(i+"/"+e..size());
				ZipEntry entry = e.nextElement();
				if (entry.getName().endsWith(".java")
						&& !entry.getName().endsWith("package-info.java")
						&& entry.isDirectory() == false) {

					InputStream source = zipfile.getInputStream(entry);
					//zos.putNextEntry(entry);//does not work, see http://www.coderanch.com/t/275390/Streams/java/ZipException-invalid-entry-compressed-size
					ZipEntry destEntry =  new ZipEntry (entry.getName());
					zos.putNextEntry(destEntry);
					IOUtils.copy(source, zos);
					zos.closeEntry();
				}
			}
			zos.close();
			zipfile.close();
			if(del){
				boolean d=orgFile.delete();
			}
//			boolean rename=tmpFile.renameTo(orgFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static InputStream getInputStream(String tarFileName)
			throws Exception {
		System.out.println("Creating an InputStream for the file");
		return new FileInputStream(new File(tarFileName));
	}

	public static void readTar(InputStream in, String untarDir)
			throws IOException {
		TarInputStream tin = new TarInputStream(in);
		TarEntry tarEntry = tin.getNextEntry();
		if (new File(untarDir).exists()) {
			while (tarEntry != null) {
				File destPath = new File(untarDir + File.separatorChar
						+ tarEntry.getName());
				System.out.println("Processing " + destPath.getAbsoluteFile());
				if (!tarEntry.isDirectory()) {
					FileOutputStream fout = new FileOutputStream(destPath);
					tin.copyEntryContents(fout);
					fout.close();
				} else {
					destPath.mkdir();
				}
				tarEntry = tin.getNextEntry();
			}
			tin.close();
		} else {
			System.out.println("That destination directory doesn't exist! "
					+ untarDir);
		}
	}

	// http://www.devx.com/tips/Tip/14049
	public static void zipDir(String dir2zip, String zipfilestr)throws IOException {
		zipDir(dir2zip, zipfilestr, null);
	}

	public static void zipDir(String dir2zip, String zipfilestr, String fileTypeToKeep) throws IOException {
		File of = new File(zipfilestr);
		of.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(of);
		ZipOutputStream zos = new ZipOutputStream(fos);
		zipDir(dir2zip, zos, dir2zip.length(), fileTypeToKeep);
		zos.close();
		fos.close();
	}

	public static void zipDir(String dir2zip, ZipOutputStream zos, int LEN, String fileTypeToKeep)
			throws IOException {
		// create a new File object based on the directory we have to zip
		// File
		File zipDir = new File(dir2zip);
		// get a listing of the directory content
		String[] dirList = zipDir.list();
		byte[] readBuffer = new byte[2156];
		int bytesIn = 0;
		// loop through dirList, and zip the files
		for (int i = 0; i < dirList.length; i++) {
			File f = new File(zipDir, dirList[i]);
			if (f.isDirectory()) {
				// if the File object is a directory, call this
				// function again to add its content recursively
				String filePath = f.getPath();
				zipDir(filePath, zos, LEN, fileTypeToKeep);
				// loop again
				continue;
			}
			if (fileTypeToKeep!=null && f.getPath().endsWith(fileTypeToKeep)==false) {
				continue;
			}
			// if we reached here, the File object f was not a directory
			// create a FileInputStream on top of f
			FileInputStream fis = new FileInputStream(f);
			// create a new zip entry
			String epath = f.getPath().substring(LEN);
			ZipEntry anEntry = new ZipEntry(epath.replace('\\','/'));
			// place the zip entry in the ZipOutputStream object
			zos.putNextEntry(anEntry);
			// now write the content of the file to the ZipOutputStream
			while ((bytesIn = fis.read(readBuffer)) != -1) {
				zos.write(readBuffer, 0, bytesIn);
			}
			// close the Stream
			fis.close();
		}
	}

	//http://www.devx.com/getHelpOn/10MinuteSolution/20447
	public static void unzip(String zipfilestr, String dir2zip){

	    try {
	    	ZipFile  zipFile = new ZipFile(zipfilestr);

	    	Enumeration  entries = zipFile.entries();

	      while(entries.hasMoreElements()) {
	        ZipEntry entry = (ZipEntry)entries.nextElement();

	        if(entry.isDirectory()) {
	          // Assume directories are stored parents first then children.
//	          System.err.println("Extracting directory: " + entry.getName());
	          // This is not robust, just for demonstration purposes.
	          boolean b=(new File(dir2zip+"/"+entry.getName())).mkdirs();
	          continue;
	        }

//	        System.err.println("Extracting file: " + entry.getName());
	        File f= new File(dir2zip+"/"+entry.getName());
	        boolean b=f.getParentFile().mkdirs();
	        FileOutputStream fout = new FileOutputStream(f);
	        BufferedOutputStream out = new BufferedOutputStream(fout);
	        copyInputStream(zipFile.getInputStream(entry), out);
	        out.close();
	        fout.close();
	      }

	      zipFile.close();
	    } catch (IOException ioe) {
	      System.err.println("Unhandled exception:");
	      ioe.printStackTrace();
	      return;
	    }
	}

	  public static final void copyInputStream(InputStream in, OutputStream out)
	  throws IOException
	  {
	    byte[] buffer = new byte[1024];
	    int len;

	    while((len = in.read(buffer)) >= 0)
	      out.write(buffer, 0, len);

	    in.close();
	    out.close();
	  }

}
