package cn.ac.iie.topology;

import cn.ac.iie.bolt.*;
import cn.ac.iie.common.RealtimeCountConfig;
import cn.ac.iie.spout.sip.SIP_ORIGIN_ALL_KafkaSpout;
import org.apache.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class LogRealtimeCountTopology {
    private static Logger logger = Logger.getLogger(LogRealtimeCountTopology.class);
    private final String topologyName;
    private final Config topologyConfig;
    private TopologyBuilder builder;

    public LogRealtimeCountTopology() {
        this(LogRealtimeCountTopology.class.getSimpleName());
    }

    public LogRealtimeCountTopology(String topologyName) {
        this.topologyName = topologyName;
        topologyConfig = createTopologConfig();
    }

    private Config createTopologConfig() {
        Config conf = new Config();
        conf.setDebug(false);
        conf.setMessageTimeoutSecs(120);
        conf.setMaxSpoutPending(RealtimeCountConfig.TOPOLOGY_CONFIG_MAX_SPOUT_PENDING);
        if (RealtimeCountConfig.TOPOLOGY_NUM_ACKS == 0) {
            conf.setNumAckers(0);
        }
        return conf;
    }

    public void runLocally() throws InterruptedException {
        topologyConfig.setMaxTaskParallelism(1);
        StormRunner.runTopologyLocally(builder, topologyName, topologyConfig, 600);
    }

    public void runRemotely() throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
        topologyConfig.setNumWorkers(RealtimeCountConfig.TOPOLOGY_WORKERS);
        topologyConfig.put(Config.TOPOLOGY_TRANSFER_BUFFER_SIZE, 32);
        topologyConfig.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, 16384);//老版配置为8
        topologyConfig.put(Config.TOPOLOGY_EXECUTOR_SEND_BUFFER_SIZE, 16384);

        StormRunner.runTopologyRemotely(builder, topologyName, topologyConfig);
    }

    public void buildTopology() {
        builder = new TopologyBuilder();
        builder.setSpout("SIP_ORIGIN_ALL_KafkaSpout", new SIP_ORIGIN_ALL_KafkaSpout(), RealtimeCountConfig.SPOUT_PARALLELISM);

        builder.setBolt("FromSpoutBufferBoltDC", new FromSpoutBufferBoltDC(), RealtimeCountConfig.BUFFER_BOLT_PARALLELISM).shuffleGrouping("SIP_ORIGIN_ALL_KafkaSpout");

        builder.setBolt("GetSipOriBoltDC", new GetSipOriBoltDC(), RealtimeCountConfig.FORMAT_BOLT_PARALLELISM).shuffleGrouping("FromSpoutBufferBoltDC");

        if (RealtimeCountConfig.GROUP_STRATEGY == 0) {
            builder.setBolt("SipInsertBoltDC", new SipInsertBoltDC(RealtimeCountConfig.TOPOLOGY_TICK_TUPLE_FREQ_SECS), RealtimeCountConfig.DATABASE_BOLT_PARALLELISM).fieldsGrouping("GetSipOriBoltDC", new Fields("jsonLog"));
        } else {
            builder.setBolt("SipInsertBoltDC", new SipInsertBoltDC(RealtimeCountConfig.TOPOLOGY_TICK_TUPLE_FREQ_SECS), RealtimeCountConfig.DATABASE_BOLT_PARALLELISM).shuffleGrouping("GetSipOriBoltDC");
        }

        builder.setBolt("SipRealTimeCountBoltDC", new SipRealTimeCountBoltDC(RealtimeCountConfig.TOPOLOGY_TICK_TUPLE_COUNT_FREQ_SECS), RealtimeCountConfig.COUNT_BOLT_PARALLELISM).shuffleGrouping("SipInsertBoltDC");

        builder.setBolt("SipRealTimeMergeBoltDC", new SipRealTimeMergeBoltDC(RealtimeCountConfig.TOPOLOGY_TICK_TUPLE_MERGE_FREQ_SECS), RealtimeCountConfig.MERGE_BOLT_PARALLELISM).fieldsGrouping("SipRealTimeCountBoltDC", new Fields("countType"));
    }

    public static void main(String[] args) throws Exception {
        LogRealtimeCountTopology csst = null;
        boolean runLocally = true;
        if (args.length >= 2 && args[1].equalsIgnoreCase("remote")) {
            runLocally = false;
            csst = new LogRealtimeCountTopology(args[0]);
        } else {
            csst = new LogRealtimeCountTopology();
        }

        csst.buildTopology();
        RealtimeCountConfig.configShow();

        if (runLocally) {
            logger.info("执行本地模式...");
            csst.runLocally();
        } else {
            logger.info("执行远程部署模式...");
            csst.runRemotely();
        }
    }
}
