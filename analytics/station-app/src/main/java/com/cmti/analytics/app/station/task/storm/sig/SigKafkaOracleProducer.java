package com.cmti.analytics.app.station.task.storm.sig;

import java.util.*;

import org.apache.commons.configuration.Configuration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.cmti.analytics.conf.Config;
import com.cmti.analytics.database.DataSources;

public class SigKafkaOracleProducer {

	protected final Logger logger = LogManager.getLogger(SigKafkaOracleProducer.class); 
	
	int id;
	KafkaProducer<String, String> producer;
 
	SqlRowSet rset;
	int columnCount;
	Configuration config;
	
	public SigKafkaOracleProducer(){
        //Define properties for how the Producer finds the cluster, serializes 
        //the messages and if appropriate directs the message to a specific 
        //partition.

        config = Config.getConfig();
        
        Properties props = new Properties();
        props.put("bootstrap.servers", config.getString("kafka.servers"));
        props.put("serializer.class", "kafka.serializer.StringEncoder");
//        props.put("partitioner.class", "example.producer.SimplePartitioner");
//      props.put("request.required.acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //Define producer object, its a java generic and takes 2 params; first
        //type of partition key, second type of the message
        producer = new KafkaProducer<String, String>(props);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.getDataSource());
		rset = jdbcTemplate.queryForRowSet("select * from MB_SIG_RECORD");
		columnCount = rset.getMetaData().getColumnCount();		
	}
	
	public String getLine() {
		if(rset.next() == false) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i = 1; i <= columnCount; i++) {
			if(i>1){
				sb.append(',');
			}
			sb.append(rset.getObject(i));
		}
		
		return sb.toString();
	}
	
	public void send() {
        //String msg = "1,431587697,101,sig session 2,10,1,3,90,11,12,0,1,3,4,5,apn1,apn ip1,22";
        String line = getLine();
        if(line==null){
        	logger.warn("no more data, line is null");
        	return;
        }

        ProducerRecord<String, String> record = new ProducerRecord<>(config.getString("kafka.topic.sig"), line);//TODO 'test' in config
        producer.send(record);		
	}

	public void close() {
		producer.close();
	}
	
    public static void main(String[] args) {//write to Kafka
        long events = 1;//Long.parseLong(args[0]);
        SigKafkaOracleProducer producer = new SigKafkaOracleProducer();

        for (long nEvents = 0; nEvents < events; nEvents++) { 
        	producer.send();
            //Utils.sleep(100);
        }
        producer.close();
    }

}