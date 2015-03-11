package com.cmti.analytics.app.station.task.storm.sig;

import java.io.*;
import java.util.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.cmti.analytics.app.station.hbase.domain.RecordSig;
import com.cmti.analytics.util.RandomGenInt;
import com.cmti.analytics.util.RandomGenLong;

import backtype.storm.utils.Utils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class SigKafkaAutoGenProducer {

	RandomGenInt eventTypeGen = new RandomGenInt(0, 4); //0-4
	RandomGenInt imsiGen;
	RandomGenInt resultGen = new RandomGenInt(0, 1); //0-1
	RandomGenInt cellGen = new RandomGenInt(0, 10); //0-10
	RandomGenLong dateGen;

	int id;
	KafkaProducer<String, String> producer;
	
	public SigKafkaAutoGenProducer(int dayRange, int nImsi){
		long current = System.currentTimeMillis();
		dateGen = new RandomGenLong(current-dayRange*24L*3600000L, current);//last dayRange days
		imsiGen = new RandomGenInt(nImsi); 

        //Define properties for how the Producer finds the cluster, serializes 
        //the messages and if appropriate directs the message to a specific 
        //partition.
        Properties props = new Properties();
        props.put("metadata.broker.list", "quickstart.cloudera:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
//        props.put("partitioner.class", "example.producer.SimplePartitioner");
//        props.put("request.required.acks", "1");

        //Define producer object, its a java generic and takes 2 params; first
        //type of partition key, second type of the message
        producer = new KafkaProducer<String, String>(props);
	}
	
	public String getLine(){		
		return String.format("%s,%s,%s,sig session 2,%s,1,%s,90,11,12,%s,1,3,4,5,apn1,apn ip1,22",id++, dateGen.next(),
			imsiGen.next(), eventTypeGen.next(), cellGen.next(), resultGen.next()
		);
	}
	
	public void send(){
        //String msg = "1,431587697,101,sig session 2,10,1,3,90,11,12,0,1,3,4,5,apn1,apn ip1,22";
        String msg = getLine();

        //Finally write the message to broker (here, page_visits is topic
        //name to write to, ip is the partition key and msg is the actual
        //message)
//      KeyedMessage<String, String> data = new KeyedMessage<String, String>("test", msg);//topic=test        
        
        ProducerRecord<String, String> record = new ProducerRecord<>("test", msg);
        producer.send(record);		
	}

	public void close(){
		producer.close();
	}
	
    public static void main(String[] args) {//write to Kafka
        long events = 10;//Long.parseLong(args[0]);
        SigKafkaAutoGenProducer producer = new SigKafkaAutoGenProducer(1, 1000);

        for (long nEvents = 0; nEvents < events; nEvents++) { 
        	producer.send();
            //Utils.sleep(100);
        }
        producer.close();
    }

    public static void main2(String[] args) throws IOException {//output to a local file
    	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/data/sig.txt")));
                
        SigKafkaAutoGenProducer producer = new SigKafkaAutoGenProducer(1, 1000);

        for (long nEvents = 0; nEvents < 1000000; nEvents++) { 
        	 String msg = producer.getLine();
            out.println(msg);
        }
        out.close();
    }
}