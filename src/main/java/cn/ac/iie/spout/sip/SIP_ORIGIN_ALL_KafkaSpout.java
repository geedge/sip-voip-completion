package cn.ac.iie.spout.sip;

import cn.ac.iie.bean.voipSipOrigin.SipOriginALL;
import cn.ac.iie.common.RealtimeCountConfig;
import com.alibaba.fastjson.JSONObject;
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

public class SIP_ORIGIN_ALL_KafkaSpout extends BaseRichSpout {
    private static final long serialVersionUID = -3363788553406229592L;
    private KafkaConsumer<String, String> consumer;
    private SpoutOutputCollector collector = null;
    private TopologyContext context = null;
    private final static Logger logger = Logger.getLogger(SIP_ORIGIN_ALL_KafkaSpout.class);

    private static Properties createConsumerConfig() {
        Properties props = new Properties();
        props.put("bootstrap.servers", RealtimeCountConfig.BOOTSTRAP_SERVERS);
        props.put("group.id", RealtimeCountConfig.GROUP_ID_PREFIX + "-sip-" + RealtimeCountConfig.GROUP_ID_SUFFIX);
        props.put("fetch.max.bytes", RealtimeCountConfig.FETCH_MAX_BYTES);//默认52428800
        props.put("max.partition.fetch.bytes", RealtimeCountConfig.MAX_PARTITION_FETCH_BYTES);//默认1048576
        props.put("max.poll.interval.ms", RealtimeCountConfig.MAX_POLL_INTERVAL_MS);//默认300000
        props.put("max.poll.records", RealtimeCountConfig.MAX_POLL_RECORDS);//默认500
        props.put("session.timeout.ms", RealtimeCountConfig.SESSION_TIMEOUT_MS);//默认10000
        props.put("auto.offset.reset", RealtimeCountConfig.AUTO_OFFSET_RESET);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        // TODO Auto-generated method stub
        this.collector = collector;
        this.context = context;
        Properties prop = createConsumerConfig();
        this.consumer = new KafkaConsumer<>(prop);
        this.consumer.subscribe(Arrays.asList(RealtimeCountConfig.KAFKA_SIP_ORIGIN_TOPIC));
    }

    @Override
    public void close() {
        consumer.close();
    }

    @Override
    public void nextTuple() {
        // TODO Auto-generated method stub
        try {
            ConsumerRecords<String, String> records = consumer.poll(10000L);
            for (ConsumerRecord<String, String> record : records) {
                SipOriginALL sipOriginLogDisable = JSONObject.parseObject(record.value(), SipOriginALL.class);
                if (!("OPTIONS".equals(sipOriginLogDisable.getMethod()))) {
                    this.collector.emit(new Values(record.value(), RealtimeCountConfig.KAFKA_SIP_ORIGIN_TOPIC));
                }
            }
        } catch (Exception e) {
            logger.error("SIP_ORIGIN_ALL_KafkaSpout-->filter OPTIONS error ---> " + e + " <---");
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub
        declarer.declare(new Fields("source", "logtype"));
    }
}
