package cn.ac.iie.dao;

import cn.ac.iie.bean.voipSipFromToLog.RouteRelationLog;
import cn.ac.iie.bean.voipSipOrigin.SipOriginALL;
import cn.ac.iie.common.RealtimeCountConfig;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Properties;

/**
 * 发送数据的kafka工具类
 */
public class KafkaDB {
    private static Logger logger = Logger.getLogger(KafkaDB.class);

    private static Producer<String, String> producer;

    private static KafkaDB kafkaDB;

    private KafkaDB() {
        getProducer();
    }

    public static KafkaDB getInstance() {
        if (kafkaDB == null) {
            kafkaDB = new KafkaDB();
        }
        return kafkaDB;
    }

    public void siporiLog2KafkaFromSipInsertBoltDC(LinkedList<String> sipOriJsonS) {
        long time = System.currentTimeMillis() / 1000L;
        for (String sipOriJson : sipOriJsonS) {
            try {
                SipOriginALL sipOriginLog = JSONObject.parseObject(sipOriJson, SipOriginALL.class);
                sipOriginLog.setStat_time(time);
                producer.send(new ProducerRecord<>(RealtimeCountConfig.KAFKA_SIP_COMPLEMENT_TOPIC, JSONObject.toJSONString(sipOriginLog)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        producer.flush();
    }

    public void routeRelatLog2KafkaFromSipInsertBoltDC(LinkedList<String> routeJsonS) {
        long time = System.currentTimeMillis() / 1000L;
        for (String routeJson : routeJsonS) {
            try {
                RouteRelationLog routeRelationLog = JSONObject.parseObject(routeJson, RouteRelationLog.class);
                routeRelationLog.setTimestamp(time);
                producer.send(new ProducerRecord<>(RealtimeCountConfig.KAFKA_ROUTE_RELATION_TOPIC, JSONObject.toJSONString(routeRelationLog)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        producer.flush();
    }

    /**
     * 根据kafka生产者配置信息初始化kafka消息生产者,只初始化一次
     */
    private void getProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", RealtimeCountConfig.BOOTSTRAP_OUTPUT_SERVERS);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("acks", "1");
        properties.put("linger.ms", "2");
        properties.put("request.timeout.ms", 20000);
        properties.put("batch.size", 262144);
        properties.put("buffer.memory", 33554432);
        producer = new KafkaProducer<String, String>(properties);
    }

}
