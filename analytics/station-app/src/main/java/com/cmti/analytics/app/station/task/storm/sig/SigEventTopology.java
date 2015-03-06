package com.cmti.analytics.app.station.task.storm.sig;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cmti.analytics.app.station.hbase.dao.RecordSigDao;
import com.cmti.analytics.app.station.hbase.domain.RecordSig;
import com.cmti.analytics.app.station.redis.domain.EventCount;
import com.cmti.analytics.storm.AbstractTopology;
import com.cmti.analytics.storm.KafkaSpoutFactory;
import com.cmti.analytics.util.DateUtil;

import redis.clients.jedis.Jedis;
import storm.kafka.KafkaSpout;
import backtype.storm.generated.StormTopology;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import static com.cmti.analytics.storm.StormConstants.KAFKA_TOPIC;

public class SigEventTopology extends AbstractTopology{
/*
	public static class ExclamationBolt extends BaseRichBolt {
	    OutputCollector _collector;

	    @Override
	    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
	      _collector = collector;
	    }

	    @Override
	    public void execute(Tuple tuple) {
	    	RecordSig sig = (RecordSig) tuple.getValue(0);
	      _collector.emit(tuple, new Values(sig + "!!!"));
	      _collector.ack(tuple);
	    }

	    @Override
	    public void declareOutputFields(OutputFieldsDeclarer declarer) {
	      declarer.declare(new Fields("word"));
	    }
	}*/

	public static class EventCountBolt extends BaseBasicBolt { 
		
		//transient  protected final Logger logger = LogManager.getLogger(EventCountBolt.class); 
	    Map<String, Integer> counts = new HashMap<>();

	    transient Jedis jedis;// = new Jedis("localhost");

	    @Override
	    public void prepare(Map stormConf, TopologyContext context) {
	    	jedis = new Jedis("quickstart.cloudera");
	    }

	    @Override
	    public void cleanup() {
	    }    
	    
	    @Override
	    public void execute(Tuple tuple, BasicOutputCollector collector) {
	    	RecordSig sig = (RecordSig) tuple.getValue(1);
	    	int cell = sig.getCell();
	    	
	    	Date date = sig.getEventDate();
	    	int n=10;
	    	//history data

	    	String hkey = DateUtil.getRedisHistoryKey(cell, date);
	    	String history = jedis.get(hkey);
	    	int hcount = Integer.parseInt(history);
	    	int hInterCount = n*hcount/60;
	    	
	    	//current
	    	Date baseDate = DateUtil.getIntervalBase(date, n);
	    	
	    	String key = EventCount.buildKey(cell, baseDate, n);
	    	
	    	jedis.incr(key);
	    	
	    	String c = jedis.get(key);
	    	System.err.println(String.format("key=%s count=%s history key=%s h hour count=%s h interval count =%s", key, c, hkey, hcount, hInterCount));
	    	
	    	/*
	    	Integer count = counts.get(key);
	      if (count == null)
	        count = 0;
	      count++;
	      counts.put(key, count);
	      collector.emit(new Values(key, count));
	      if(count%3==0) System.err.println("cell="+key+" count="+count);*/
	    }

	    @Override
	    public void declareOutputFields(OutputFieldsDeclarer declarer) {
	      declarer.declare(new Fields("cell", "count"));
	    }
	}


	public static class SplitCellBolt extends BaseBasicBolt {
	    transient RecordSigDao dao;// = new Jedis("localhost");

	    @Override
	    public void prepare(Map stormConf, TopologyContext context) {
	    	dao = new RecordSigDao();
	    }

	    @Override
	    public void execute(Tuple tuple, BasicOutputCollector collector) {
	    	String line =   tuple.getString(0); 
	    	RecordSig sig = dao.parseLine(line);
	    	int cell = sig.getCell();

	    	collector.emit(new Values(cell, sig));
	    }

	    @Override
	    public void declareOutputFields(OutputFieldsDeclarer declarer) {
	      declarer.declare(new Fields("cell", "sig_event"));
	    }
	}

	public SigEventTopology(String[] args){
		super(args);
	}

	@Override
	public StormTopology buildTopology(){

		TopologyBuilder builder = new TopologyBuilder();

		KafkaSpout spout = KafkaSpoutFactory.createSpout(getAppConfig()); 

    	builder.setSpout("eventKafkaSpout", spout, 10);
    	

        builder.setBolt("split", new SplitCellBolt(), 8).shuffleGrouping("eventKafkaSpout");
        
    	//builder.setSpout("eventSpout", new SigEventAutoGenSpout(), 10);

        //builder.setBolt("count", new EventCountKafkaSpout(), 10).shuffleGrouping("split");
        builder.setBolt("count", new EventCountBolt(), 10).fieldsGrouping("split", new Fields("cell"));
        
//    	builder.setBolt("count", new ExclamationBolt(), 3).shuffleGrouping("event");
//    	builder.setBolt("exclaim2", new ExclamationBolt(), 2).shuffleGrouping("exclaim1");
		return builder.createTopology();
	}
    /*


	@Override
	public StormTopology buildTopology(){
		SigEventAutoGenSpout
		TopologyBuilder builder = new TopologyBuilder();

    	builder.setSpout("word", new TestWordSpout(), 10);
    	builder.setBolt("exclaim1", new ExclamationBolt(), 3).shuffleGrouping("word");
    	builder.setBolt("exclaim2", new ExclamationBolt(), 2).shuffleGrouping("exclaim1");
		return builder.createTopology();
	}
	
	@Override
	public StormTopology buildTopology1(){
		TopologyBuilder builder = new TopologyBuilder();

		KafkaSpout spout = PtitleKafkaSpoutFactory.createSpout(getNextagConfig()); 
		
		builder.setSpout("hmiKafkaSpout", spout, nextagConfig.getInt(PTITLE_VERIFY_NUM_KAFKA_SPOUT ,1));
		builder.setBolt("ptitleVerifyBolt", new PtitleVerifyBolt(),  nextagConfig.getInt(PTITLE_VERIFY_NUM_BOLT ,6))
			   .shuffleGrouping("hmiKafkaSpout");				

		return builder.createTopology();
	}
*/
	@Override
	public void buildOptions(){
		super.buildOptions();

		addOption(options, KAFKA_TOPIC, "kafka topic" ,"topic");/*
		addOption(options, PTITLE_VERIFY_NUM_KAFKA_SPOUT, "number of kafka spout" ,"n");
		addOption(options, PTITLE_VERIFY_NUM_BOLT, "number of bolt" ,"n");	

		addOption(options, KAFKA_LAST_OFFSET_TIME, 
				"Kafka last offset time can also be -1 or -2. These are special values for time indicating the earliest (-2) and latest (-1) time.",
				"millis"); //TODO move this to AbstractKafkaTopology, and let PtitleVerifyTopology extends AbstractKafkaTopology

		addOption(options, KAFKA_PTITLE_IMPORT_CONSUMER_GROUP_ID, 
				"kafka ptitle import consumer group id",
				"id"); */
	}

	/*
 to run: 
 ~/git/storm-0.8.2/bin/storm jar storm-topologies.jar com.cmti.analytics.app.station.task.storm.sig.SigEventTopology -storm_topology_name mo88 -storm_local_mode false -kafka_last_offset_time '-2'
-kafka_ptitle_import_consumer_group_id motest1

to run locally:
-storm_local_mode true -storm_debug true
 * 
 */
	public static void main(String[] args) throws Exception {
		SigEventTopology topology = new SigEventTopology(args);
		topology.run();
	}

}
