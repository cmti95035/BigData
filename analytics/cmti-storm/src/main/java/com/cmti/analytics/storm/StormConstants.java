package com.cmti.analytics.storm;

public class StormConstants {

	// Zookeeper properties 
	public static final String ZOOKEEPER_SERVER = "zookeeper.server";

	// Kafka property names
	public static final String KAFKA_BROKER_ROOT_PATH = "/brokers";
	public static final String KAFKA_BROKER_ENABLED = "kafka.broker.enabled";
	public static final String KAFKA_ZOOKEEPER_SERVERS = "kafka.zookeeper.servers";
	public static final String KAFKA_ZOOKEEPER_PORT = "kafka.zookeeper.port";
	public static final String KAFKA_TOPIC = "kafka.topic";

	//kafka

	public static final String KAFKA_LAST_OFFSET_TIME = "kafka.last.offset.time";	
	
	
	//general storm config

	public static final String STORM_LOCAL_MODE = "storm.local.mode";
	public static final String STORM_DEBUG = "storm.debug";
	public static final String STORM_TOPOLOGY_NAME = "storm.topology.name";
	public static final String STORM_NIMBUS_HOST = "storm.nimbus.host";	
	public static final String STORM_NUM_WORKER = "storm.num.worker";
	public static final String STORM_NUM_ACKER = "storm.num.acker";
	
	public static final String MAX_SPOUT_PENDING = "storm.max.spout.pending";
	public static final String STORM_THREAD_PER_BOLT = "storm.thread.per.bolt";
	public static final String STORM_MESG_TIMEOUT_SEC = "storm.message.timeout.sec";

}
