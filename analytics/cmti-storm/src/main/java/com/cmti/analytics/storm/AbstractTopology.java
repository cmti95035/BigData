package com.cmti.analytics.storm;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;

import static com.cmti.analytics.storm.StormConstants.STORM_LOCAL_MODE;
import static com.cmti.analytics.storm.StormConstants.STORM_DEBUG;
import static com.cmti.analytics.storm.StormConstants.STORM_TOPOLOGY_NAME;
import static com.cmti.analytics.storm.StormConstants.STORM_NIMBUS_HOST;	
import static com.cmti.analytics.storm.StormConstants.STORM_NUM_WORKER;
import static com.cmti.analytics.storm.StormConstants.STORM_NUM_ACKER;
import static com.cmti.analytics.storm.StormConstants.MAX_SPOUT_PENDING ;
import static com.cmti.analytics.storm.StormConstants.STORM_THREAD_PER_BOLT;
import static com.cmti.analytics.storm.StormConstants.STORM_MESG_TIMEOUT_SEC;
import static com.cmti.analytics.storm.StormConstants.ZOOKEEPER_SERVER;

public abstract class AbstractTopology {

	private static final Logger logger = Logger.getLogger(AbstractTopology.class); 
	//CommandLine line;
	protected Config stormConfig;//StormConfig	
	protected Configuration appConfig; 
	protected Options options;
	
	public AbstractTopology(String[] args){
		buildOptions();
		parseArgs(args);
	}	

	public abstract StormTopology buildTopology(); 
 
	// set distribute mode specific config properties
	protected void buildStormConfig(){
		stormConfig = new Config();

		stormConfig.setDebug(appConfig.getBoolean(STORM_DEBUG, false));
		
		stormConfig.setNumWorkers(appConfig.getInt(STORM_NUM_WORKER ,1));
		stormConfig.setNumAckers(appConfig.getInt(STORM_NUM_ACKER ,1));

		int maxSpoutPending = appConfig.getInt(MAX_SPOUT_PENDING , 0);
		if(maxSpoutPending > 0){
			stormConfig.setMaxSpoutPending(maxSpoutPending);			
		}else{	
			stormConfig.setMaxSpoutPending(appConfig.getInt(STORM_THREAD_PER_BOLT, 10)*100);		
		}
		
		stormConfig.setMessageTimeoutSecs(appConfig.getInt(STORM_MESG_TIMEOUT_SEC ,180));

		stormConfig.put(Config.NIMBUS_HOST, appConfig.getString(STORM_NIMBUS_HOST, null));
		
		if(appConfig.containsKey(ZOOKEEPER_SERVER)){
			String catZk=appConfig.getString(ZOOKEEPER_SERVER);
			stormConfig.put(Config.TOPOLOGY_WORKER_CHILDOPTS, String.format("-D%s=%s", encode(ZOOKEEPER_SERVER), catZk));
		}
	}

	public static String encode(String str){
		return str.replaceAll("\\.", "_");
	}

	public static String decode(String str){
		return str.replaceAll("_",".");
	}

	protected static void addOption(Options options, String name, String desc){		
		Option opt = new Option(encode(name), desc );
		options.addOption( opt );
	}

	@SuppressWarnings("static-access")
	protected static void addOption(Options options, String name, String desc, String argName){
		Option opt = OptionBuilder.withArgName(argName)
        .hasArg()
        .withDescription(desc )
        .create(encode(name) ); 
		options.addOption( opt );

	}
	
	//any option needs to be overrode in command line, must list here
	//option invalid if contains '.', so need to encode it.
	public void buildOptions(){
		options = new Options();

		addOption(options, "help", "print this message" );
		addOption(options, ZOOKEEPER_SERVER, "zookeeper server", "hosts" );
		
		addOption(options, STORM_DEBUG, "print debugging information" ,"true|false");		
		addOption(options, STORM_LOCAL_MODE, "run in local mode" ,"true|false");		
		
		addOption(options, STORM_TOPOLOGY_NAME, "topology name in cluster.", "name"); 

		addOption(options, STORM_NIMBUS_HOST, "Nimbus Host.", "host"); 		 
	}
	
	public void run() throws AlreadyAliveException, InvalidTopologyException{
		buildStormConfig();
		
		if(appConfig.getBoolean("help", false)){
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(this.getClass().getSimpleName(), options );
			return;
		}
		
		String name = appConfig.getString(STORM_TOPOLOGY_NAME, this.toString());
//Topology name cannot contain any of the following: #{"." "/" ":" "\\"}
		name = encode(name);
		
		if(appConfig.getBoolean(STORM_LOCAL_MODE, false) ) { 
			logger.info("Running Storm in Local mode. Submit topology with name: "+name);
			
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(name, stormConfig, buildTopology());
			try {
				// shutdown after 15 minutes so we don't leave a bunch of
				// orphaned localmode topologies
				Thread.sleep(1000L * 60L * 15L);// 15 mins TODO configurable
			} catch (InterruptedException e) {
				// ignore interrupt and continue to shutdown topology
			}
			cluster.killTopology(name);
			cluster.shutdown();
		} else {
			logger.info("Running Storm in Distributed mode.");
			StormSubmitter.submitTopology(name, stormConfig, buildTopology());
		}
	}

	public void parseArgs(String[] args){
	    // create the parser
	    CommandLineParser parser = new BasicParser();
	    try {
	        // parse the command line arguments
	    	CommandLine line = parser.parse( options, args );
	    	
	    	String value = line.getOptionValue(encode(ZOOKEEPER_SERVER));
	    	if(StringUtils.isNotBlank(value)){
	    		System.setProperty(encode(ZOOKEEPER_SERVER), value);
	    	}
	    	
			appConfig = com.cmti.analytics.conf.Config.getConfig();
	        
	        for(Option opt : line.getOptions()){
	        	String name = opt.getOpt();
	        	if(opt.hasArg()){
	        		value = opt.getValue();
	        		appConfig.setProperty(decode(name),  value);
	        	}else{
	        		appConfig.setProperty(decode(name),  "true");	        		
	        	}
	        }
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	        logger.error( "Parsing failed.", exp);
	        throw new RuntimeException(exp);
	    }	    
	}
	
	public Config getStormConfig() {
		return stormConfig;
	}


	public void setStormConfig(Config stormConf) {
		this.stormConfig = stormConf;
	}


	public Configuration getAppConfig() {
		return appConfig;
	}


	public void setAppConfig(Configuration appConfig) {
		this.appConfig = appConfig;
	}


	public static void main(String[] args) throws Exception {

//		PtitleVerifyTopology topology = new PtitleVerifyTopology(args);
//		topology.run();

	}

}
