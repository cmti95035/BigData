package com.cmti.analytics.app.station.task.storm.sig;

import backtype.storm.Config;
import backtype.storm.topology.OutputFieldsDeclarer;

import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmti.analytics.app.station.hbase.domain.RecordSig;
import com.cmti.analytics.util.RandomGenInt;
import com.cmti.analytics.util.RandomGenLong;


public class SigOracleSpout extends BaseRichSpout {
    public static Logger LOG = LoggerFactory.getLogger(SigOracleSpout.class);
    boolean _isDistributed;
    SpoutOutputCollector _collector;


	public SigOracleSpout() {
        this(true);
    }

    public SigOracleSpout(boolean isDistributed) {
        _isDistributed = isDistributed;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
    }

    @Override
    public void close() {
        
    }

    @Override
    public void nextTuple() {
        Utils.sleep(100);

    	RandomGenInt eventTypeGen = new RandomGenInt(0, 4); //0-4
    	RandomGenInt imsiGen = new RandomGenInt(1000, 2000); //0-1000
    	RandomGenInt resultGen = new RandomGenInt(0, 1); //0-1
    	RandomGenInt cellGen = new RandomGenInt(0, 10); //0-10
    	
    	long current = System.currentTimeMillis();
    	
    	RandomGenLong dateGen = new RandomGenLong(current-90L*24L*3600000L, current); //last 90 days
    	
		RecordSig sig = new RecordSig();
		sig.setId(current);
		sig.setEventType(eventTypeGen.next());
		sig.setImsi(String.valueOf(imsiGen.next()));
		sig.setResult(resultGen.next());
		sig.setCell(cellGen.next());
		
		sig.setEventDate(new Date(dateGen.next()));
		
        _collector.emit(new Values(sig.getCell(), sig));
    }

    @Override
    public void ack(Object msgId) {

    }

    @Override
    public void fail(Object msgId) {
        
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("cell", "sig_event"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        if(!_isDistributed) {
            Map<String, Object> ret = new HashMap<String, Object>();
            ret.put(Config.TOPOLOGY_MAX_TASK_PARALLELISM, 1);
            return ret;
        } else {
            return null;
        }
    }    
}