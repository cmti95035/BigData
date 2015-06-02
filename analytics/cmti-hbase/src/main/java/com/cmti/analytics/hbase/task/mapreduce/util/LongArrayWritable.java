package com.cmti.analytics.hbase.task.mapreduce.util;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.LongWritable;
/**
 * 
 * @author Guobiao Mo
 *
 */
public class LongArrayWritable extends ArrayWritable
{
    public LongArrayWritable() {
        super(LongWritable.class);
    }
    public LongArrayWritable(LongWritable[] values) {
        super(LongWritable.class, values);
    }
}