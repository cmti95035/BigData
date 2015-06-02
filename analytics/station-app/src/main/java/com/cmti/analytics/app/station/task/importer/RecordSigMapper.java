package com.cmti.analytics.app.station.task.importer;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.station.hbase.dao.RecordSigDao;
import com.cmti.analytics.app.station.hbase.domain.RecordSig;
import com.cmti.analytics.app.station.task.storm.sig.SigKafkaAutoGenProducer;
import com.cmti.analytics.hbase.loader.BulkLoaderMapper;


  public class RecordSigMapper extends BulkLoaderMapper<RecordSig, Integer> {

	protected final Logger logger = LogManager.getLogger(RecordSigMapper.class); 
		
	private RecordSigDao dao;
		
	@Override
	protected void setup(Context context) throws IOException {
		dao = new RecordSigDao();
		dao.open();		
	}	 

	@Override
	public void map(LongWritable offset, Text value, Context context) throws IOException {
		String line = value.toString();
		logger.error("got  line {}", line);
		
        SigKafkaAutoGenProducer producer = new SigKafkaAutoGenProducer(7, 300000);

        for (long nEvents = 0; nEvents < 10000000; nEvents++) { 
        	 String msg = producer.getLine();
        	 RecordSig sig = dao.parseLine(msg, context);
			
			byte[] bRowKey = dao.getKey(sig);
			ImmutableBytesWritable rowKey = new ImmutableBytesWritable(bRowKey);
			try{
				Put p = dao.getPut(sig);
				context.write(rowKey, p);
//			Thread.sleep(100L);
			}catch(Exception e){
				logger.error(line, e);
			}
		}
	}
}