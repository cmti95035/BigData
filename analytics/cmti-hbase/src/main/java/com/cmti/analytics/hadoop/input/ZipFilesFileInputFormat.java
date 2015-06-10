package com.cmti.analytics.hadoop.input;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

 
/**
 * used by HdfsMrXmlBulkLoader.
 * 
 * reads zip files that contain multiple text files, which can be in sub directories inside zip files, and emits each file content as text map record.
 * TODO: support other compression formats.
 * 
 * @author Guobiao Mo
 *
 */
 
public class ZipFilesFileInputFormat extends FileInputFormat<LongWritable, Text> {

   @Override
   protected boolean isSplitable(JobContext context, Path filename) {
     return false;
   } 

@Override
public RecordReader<LongWritable, Text> createRecordReader(
		InputSplit split, TaskAttemptContext context) throws IOException,
		InterruptedException {
	ZipFilesRecordReader reader = new ZipFilesRecordReader();   
    reader.initialize(split, context);
    return reader;
}
}
  