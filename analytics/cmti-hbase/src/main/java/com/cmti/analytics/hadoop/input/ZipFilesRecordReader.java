package com.cmti.analytics.hadoop.input;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 
 * used by ZipFilesFileInputFormat
 * 
 * @author Guobiao Mo
 *
 */
 

public class ZipFilesRecordReader extends RecordReader<LongWritable, Text> {
	protected final Logger logger = LogManager.getLogger(ZipFilesRecordReader.class); 

   private FileSplit fileSplit;
   private Configuration conf;
   private Text value = new Text();
   private long currentCount;
   private long totalLength;
   private long processedLength;
   
   private FSDataInputStream in;
   private ZipInputStream zis;

   @Override
   public float getProgress() throws IOException {
       return 1.0f * processedLength / totalLength;
   }

   @Override
   public void close() throws IOException {
	   IOUtils.closeStream(in);
	   org.apache.commons.io.IOUtils.closeQuietly(zis);
   }

@Override
public void initialize(InputSplit split, TaskAttemptContext context)
		throws IOException, InterruptedException { 
	this.fileSplit = (FileSplit) split;
        this.conf = context.getConfiguration();	

        totalLength = fileSplit.getLength();
        
        Path file = fileSplit.getPath();
        FileSystem fs = file.getFileSystem(conf);
        in = fs.open(file);
    	zis = new ZipInputStream(in);//convert to ZipInputStream
}

@Override
public boolean nextKeyValue() throws IOException, InterruptedException {

	ZipEntry ze = null;
	if((ze=zis.getNextEntry())!=null){//for each xml file in the zip file
		if(ze.isDirectory()){
			return nextKeyValue();
		}
		long size = ze.getSize();//size of the xml file
		byte[] buffer = new byte[(int)size];
		org.apache.commons.io.IOUtils.readFully(zis, buffer);//read the xml into 'buffer'
		
//		String text = new String(buffer);
		value.set(buffer);
		
		currentCount++;
		processedLength += size;

        return true;
	}else{
		return false;
	}    
}

@Override
public LongWritable getCurrentKey() throws IOException, InterruptedException {
	return new LongWritable(currentCount);
}

@Override
public Text getCurrentValue() throws IOException, InterruptedException {
	return value;
}
} 
