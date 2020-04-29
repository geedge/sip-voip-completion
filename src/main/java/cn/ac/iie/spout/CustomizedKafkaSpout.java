package cn.ac.iie.spout;

import cn.ac.iie.common.RealtimeCountConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public class CustomizedKafkaSpout extends BaseRichSpout{
	private static final long serialVersionUID = -3363788553406229592L;
	private KafkaConsumer<String, String> consumer;
	private SpoutOutputCollector collector = null;
	private TopologyContext context = null;
	private final static Logger logger = Logger.getLogger(CustomizedKafkaSpout.class);
	
	
	private static Properties createConsumerConfig() {
		Properties props = new Properties();
		props.put("bootstrap.servers", RealtimeCountConfig.BOOTSTRAP_SERVERS);
		props.put("group.id", RealtimeCountConfig.GROUP_ID_PREFIX+"-rc-"+ RealtimeCountConfig.GROUP_ID);
		props.put("auto.offset.reset", RealtimeCountConfig.AUTO_OFFSET_RESET);
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return props;
	}
	
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector=collector;
		this.context=context;	
		Properties prop = createConsumerConfig();
		this.consumer = new KafkaConsumer<>(prop);
		this.consumer.subscribe(Arrays.asList(RealtimeCountConfig.KAFKA_TOPIC));
	}

	@Override
	public void close() {
		consumer.close();
	}
	
	@Override
	public void nextTuple() {
		// TODO Auto-generated method stub
		ConsumerRecords<String, String> records = consumer.poll(10000L);
		for (ConsumerRecord<String, String> record : records) {				
			this.collector.emit(new Values(record.value()));
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("source"));
	}
}
