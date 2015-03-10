package com.cmti.analytics.storm;

import static com.cmti.analytics.storm.StormConstants.KAFKA_TOPIC;
import static com.cmti.analytics.storm.StormConstants.ZOOKEEPER_SERVER;

import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import backtype.storm.spout.SchemeAsMultiScheme;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

public class KafkaSpoutFactory {
	private static final Logger logger = Logger.getLogger(KafkaSpoutFactory.class);

	//PtitleKafkaEventReader.java
	//copied from ProductIncrIndexLinearTopology.java
	public static KafkaSpout createSpout(Configuration conf){
		BrokerHosts hosts = new ZkHosts(conf.getString(ZOOKEEPER_SERVER));
		String topicName = conf.getString(KAFKA_TOPIC);
		//https://github.com/nathanmarz/storm-contrib/blob/master/storm-kafka/src/jvm/storm/kafka/SpoutConfig.java
		//https://github.com/apache/storm/tree/master/external/storm-kafka
			
		SpoutConfig spoutConfig  = new SpoutConfig(hosts, topicName, "/" + topicName, UUID.randomUUID().toString());

		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		
		KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
		
		return kafkaSpout;
	}
	
}
