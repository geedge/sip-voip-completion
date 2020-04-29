package cn.ac.iie.common;

import cn.ac.iie.utils.RealtimeCountConfigurations;

import java.io.Serializable;

public class RealtimeCountConfig implements Serializable{
	
	private static final long serialVersionUID = -8649024767966235184L;
	public static final String LOG_STRING_SPLITTER = "\t";
	public static final String BETWEEN_BOLTS_SPLITTER = "~=~";
	public static final String EMPTY_OPTION_CHARACTER = "-";
	/**
	 * 通用log表字段数
	 */
	public static final Integer LOG_COMMON_FIELD_NUM = 23;//公共表字段数（不包括id，因为id前面不传回来,id为自增）

	//-----------------realtime_config.properties------------------
	public static final String BOOTSTRAP_SERVERS = RealtimeCountConfigurations.getStringProperty(0, "bootstrap.servers");
	public static final String BOOTSTRAP_OUTPUT_SERVERS = RealtimeCountConfigurations.getStringProperty(0, "bootstrap.output.servers");
	public static final String ACTIVE_SYSTEM = RealtimeCountConfigurations.getStringProperty(0, "active.system");
	public static final Integer BATCH_INSERT_NUM = RealtimeCountConfigurations.getIntProperty(0, "batch.insert.num");
	public static final String GROUP_ID = RealtimeCountConfigurations.getStringProperty(0, "group.id");
	public static final String KAFKA_TOPIC = RealtimeCountConfigurations.getStringProperty(0, "kafka.topic");
	public static final String KAFKA_NTC_ORI_TOPIC = RealtimeCountConfigurations.getStringProperty(0, "kafka.ntc.ori.topic");
	public static final String KAFKA_SIP_ORIGIN_TOPIC = RealtimeCountConfigurations.getStringProperty(0, "kafka.sip.origin.topic");
	public static final String KAFKA_NTC_KILLED_TOPIC = RealtimeCountConfigurations.getStringProperty(0, "kafka.ntc.killed.topic");
	public static final String KAFKA_SIP_COMPLEMENT_TOPIC = RealtimeCountConfigurations.getStringProperty(0, "kafka.sip.complement.topic");
	public static final String KAFKA_ROUTE_RELATION_TOPIC = RealtimeCountConfigurations.getStringProperty(0, "kafka.route.relation.topic");


	public static final String ALL_LOG_OUTPUT_CONTROLLER = RealtimeCountConfigurations.getStringProperty(0, "all.log.output.controller");//全局catch日志打印控制器
	public static final String PART_LOG_OUTPUT_CONTROLLER = RealtimeCountConfigurations.getStringProperty(0, "part.log.output.controller");//局部日志输出控制器


	public static final String GROUP_ID_PREFIX = RealtimeCountConfigurations.getStringProperty(0, "group.id.prefix");//groupid前缀
	public static final String GROUP_ID_SUFFIX = RealtimeCountConfigurations.getStringProperty(0, "group.id.suffix");//groupid后缀
	public static final String FETCH_MAX_BYTES = RealtimeCountConfigurations.getStringProperty(0, "fetch.max.bytes");
	public static final String MAX_PARTITION_FETCH_BYTES = RealtimeCountConfigurations.getStringProperty(0, "max.partition.fetch.bytes");
	public static final String MAX_POLL_INTERVAL_MS = RealtimeCountConfigurations.getStringProperty(0, "max.poll.interval.ms");
	public static final String MAX_POLL_RECORDS = RealtimeCountConfigurations.getStringProperty(0, "max.poll.records");
	public static final String SESSION_TIMEOUT_MS = RealtimeCountConfigurations.getStringProperty(0, "session.timeout.ms");
	public static final String AUTO_OFFSET_RESET = RealtimeCountConfigurations.getStringProperty(0, "auto.offset.reset");
	public static final String DATACENTER_ADDRS = RealtimeCountConfigurations.getStringProperty(0, "datacenter.addrs");
	public static final String DATACENTER_USERNAME = RealtimeCountConfigurations.getStringProperty(0, "datacenter.username");
	public static final String DATACENTER_PASSWORD = RealtimeCountConfigurations.getStringProperty(0, "datacenter.password");
	public static final String TABLE_NAME = RealtimeCountConfigurations.getStringProperty(0, "table.name");
	public static final String TABLE_KILLED_NAME = RealtimeCountConfigurations.getStringProperty(0, "table.killed.name");
	public static final Integer BATCH_CHINSERT_NUM = RealtimeCountConfigurations.getIntProperty(0, "batch.chinsert.num");//clickhouse批量插入量
	public static final Integer BATCH_KAFKA_INSERT_NUM = RealtimeCountConfigurations.getIntProperty(0, "batch.kafka.insert.num");//kafka批量插入量
	public static final Integer BATCH_CHINSERT_KILLED_NUM = RealtimeCountConfigurations.getIntProperty(0, "batch.chinsert.killed.num");
	public static final String IP_V4_LIBRARY = RealtimeCountConfigurations.getStringProperty(0, "ip.v4.library");//ipv4定位库
	public static final String IP_V6_LIBRARY = RealtimeCountConfigurations.getStringProperty(0, "ip.v6.library");//ipv6定位库
	public static final String IPIP_LIBRARY = RealtimeCountConfigurations.getStringProperty(0, "ipip.library");//ipip定位库

	public static final String HDFS_URL = RealtimeCountConfigurations.getStringProperty(0,"hdfs.url");
	public static final String HDFS_PATH = RealtimeCountConfigurations.getStringProperty(0,"hdfs.path");
	public static final String HDFS_USER = RealtimeCountConfigurations.getStringProperty(0,"hdfs.user");
	//	public static final String HIVE_URL = RealtimeCountConfigurations.getStringProperty(0,"hive.url");
//	public static final String HIVE_USERNAME = RealtimeCountConfigurations.getStringProperty(0,"hive.username");
//	public static final String HIVE_PASSWORD = RealtimeCountConfigurations.getStringProperty(0,"hive.password");
	public static final String HIVE_SIP_CLEAN_TABLE = RealtimeCountConfigurations.getStringProperty(0,"hive.sip.clean.table");
//	public static final String HIVE_SIP_ROUTE_TABLE = RealtimeCountConfigurations.getStringProperty(0,"hive.sip.route.table");

	//---------------storm_config.properties---------------
	public static final Integer SPOUT_PARALLELISM = RealtimeCountConfigurations.getIntProperty(1, "spout.parallelism");
	public static final Integer FORMAT_BOLT_PARALLELISM = RealtimeCountConfigurations.getIntProperty(1, "format.bolt.parallelism");
	public static final Integer BUFFER_BOLT_PARALLELISM = RealtimeCountConfigurations.getIntProperty(1, "buffer.bolt.parallelism");
	public static final Integer DATABASE_BOLT_PARALLELISM = RealtimeCountConfigurations.getIntProperty(1, "database.bolt.parallelism");
	public static final Integer COUNT_BOLT_PARALLELISM = RealtimeCountConfigurations.getIntProperty(1, "count.bolt.parallelism");
	public static final Integer MERGE_BOLT_PARALLELISM = RealtimeCountConfigurations.getIntProperty(1, "merge.bolt.parallelism");
	public static final Integer TOPOLOGY_WORKERS = RealtimeCountConfigurations.getIntProperty(1, "topology.workers");
	public static final Integer GROUP_STRATEGY = RealtimeCountConfigurations.getIntProperty(1, "group.strategy");
	public static final Integer TOPOLOGY_TICK_TUPLE_COMP_FREQ_SECS = RealtimeCountConfigurations.getIntProperty(1, "topology.tick.tuple.comp.freq.secs");
	public static final Integer TOPOLOGY_TICK_TUPLE_FREQ_SECS = RealtimeCountConfigurations.getIntProperty(1, "topology.tick.tuple.freq.secs");
	public static final Integer TOPOLOGY_TICK_TUPLE_COUNT_FREQ_SECS = RealtimeCountConfigurations.getIntProperty(1, "topology.tick.tuple.count.freq.secs");
	public static final Integer TOPOLOGY_TICK_TUPLE_MERGE_FREQ_SECS = RealtimeCountConfigurations.getIntProperty(1, "topology.tick.tuple.merge.freq.secs");
	public static final Integer TOPOLOGY_CONFIG_MAX_SPOUT_PENDING = RealtimeCountConfigurations.getIntProperty(1, "topology.config.max.spout.pending");
	public static final Integer TOPOLOGY_NUM_ACKS = RealtimeCountConfigurations.getIntProperty(1, "topology.num.acks");

	//参数展示
	public static void configShow(){
		System.out.println("BOOTSTRAP_SERVERS: "+BOOTSTRAP_SERVERS);
		System.out.println("KAFKA_TOPIC: "+KAFKA_TOPIC);
		System.out.println("ACTIVE_SYSTEM: "+ACTIVE_SYSTEM);
		System.out.println("GROUP_ID: "+GROUP_ID);
		System.out.println("GROUP_ID_PREFIX: "+GROUP_ID_PREFIX);
		System.out.println("AUTO_OFFSET_RESET: "+AUTO_OFFSET_RESET);
		System.out.println("TOPOLOGY_NUM_ACKS: "+TOPOLOGY_NUM_ACKS);		
		System.out.println("BATCH_INSERT_NUM: "+BATCH_INSERT_NUM);		
		System.out.println("TOPOLOGY_TICK_TUPLE_FREQ_SECS: "+TOPOLOGY_TICK_TUPLE_FREQ_SECS);
		System.out.println("TOPOLOGY_CONFIG_MAX_SPOUT_PENDING: "+TOPOLOGY_CONFIG_MAX_SPOUT_PENDING);
		System.out.println("TOPOLOGY_WORKERS: "+TOPOLOGY_WORKERS);
		System.out.println("SPOUT_PARALLELISM: "+SPOUT_PARALLELISM);
		System.out.println("FORMAT_BOLT_PARALLELISM: "+FORMAT_BOLT_PARALLELISM);
		System.out.println("DATABASE_BOLT_PARALLELISM: "+DATABASE_BOLT_PARALLELISM);
		System.out.println("GROUP_STRATEGY: "+GROUP_STRATEGY);
	}
}