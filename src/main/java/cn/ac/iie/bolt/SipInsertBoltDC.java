package cn.ac.iie.bolt;

import cn.ac.iie.common.RealtimeCountConfig;
import cn.ac.iie.dao.KafkaDB;
import cn.ac.iie.utils.HiveDao.HdfsDataLoad_Avro;
import cn.ac.iie.utils.TupleUtils;
import com.zdjizhi.utils.StringUtil;
import org.apache.log4j.Logger;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SipInsertBoltDC extends BaseBasicBolt {

    private static final long serialVersionUID = -6795251425357896415L;
    private static Logger logger = Logger.getLogger(SipInsertBoltDC.class);

    private LinkedList<String> sipOriJsonList;//存放sip原始补全日志字符串
    private LinkedList<String> routeRelationJsonList;//存放voip路由关系日志字符串

    private HdfsDataLoad_Avro hdfsDataLoadAvro;

    private Integer tickFreqSecs;

    public SipInsertBoltDC(Integer tickFreqSecs) {
        this.tickFreqSecs = tickFreqSecs;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map stormConf,
                        TopologyContext context) {
        hdfsDataLoadAvro = HdfsDataLoad_Avro.getHdfsInstance();
        sipOriJsonList = new LinkedList<String>();
        routeRelationJsonList = new LinkedList<String>();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        try {
            if (TupleUtils.isTick(tuple)) {
                long time = System.currentTimeMillis() / 1000L;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String partition = sdf.format(new Date(time * 1000L)).replaceAll("-", "");//格式:20190722
                if (!sipOriJsonList.isEmpty()) {
                    LinkedList<String> tmpListFreq = new LinkedList<String>();
                    tmpListFreq.addAll(sipOriJsonList);
                    sipOriJsonList.clear();
                    hdfsDataLoadAvro.dataSipToHdfsAvro(partition, tmpListFreq, RealtimeCountConfig.KAFKA_SIP_COMPLEMENT_TOPIC, "origin", time);
                }

                //定时写入voip路由关系日志---20190807废弃,路由关系转离线spark清洗获取
//                if (!routeRelationJsonList.isEmpty()) {
////                    Map<String, Long> tmpMap = new HashMap<String, Long>();
//                    LinkedList<String> tmpFragListFreq = new LinkedList<String>();
//                    tmpFragListFreq.addAll(routeRelationJsonList);
//                    routeRelationJsonList.clear();
//                    kafkaDB.routeRelatLog2KafkaFromSipInsertBoltDC(tmpFragListFreq);
////                    dcl.dfPzFlowBatchStorage(tmpMap);//正式用,直接入中心http,已验证可用
////                    dbl.dfPzFlowBatchStorage2CH(tmpMap);//测试入clickhouse20190220
////                    dbl.dfPzFlowBatchStorage2CH(tmpFragListFreq);//测试入clickhouse20190220
//                }
            } else {
                String jsonLog = tuple.getString(0);
                String logType = tuple.getString(1);
                switch (logType) {
                    case "origin":
                        if (StringUtil.isNotBlank(jsonLog)) {
                            sipOriJsonList.add(jsonLog);
                            collector.emit(new Values(jsonLog));
                        }
                        break;
//                    case "route"://存放路由关系数据---20190807废弃,路由关系转离线spark清洗获取
//                        if (StringUtil.isNotBlank(jsonLog)) {
//                            routeRelationJsonList.add(jsonLog);
//                        }
//                        break;
                    default:
                        logger.error("SipInsertBoltDC logType is error !!!This logType is--->{" + logType + "}<---");
                        break;
                }
                long time = System.currentTimeMillis() / 1000L;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String partition = sdf.format(new Date(time * 1000L)).replaceAll("-", "");
                if (sipOriJsonList.size() >= RealtimeCountConfig.BATCH_KAFKA_INSERT_NUM) {
                    LinkedList<String> tmpList = new LinkedList<String>();
                    tmpList.addAll(sipOriJsonList);
                    sipOriJsonList.clear();
                    hdfsDataLoadAvro.dataSipToHdfsAvro(partition, tmpList, RealtimeCountConfig.KAFKA_SIP_COMPLEMENT_TOPIC, "origin", time);
                }
                //写入voip路由关系日志---20190807废弃,路由关系转离线spark清洗获取
//                if (routeRelationJsonList.size() >= RealtimeCountConfig.BATCH_KAFKA_INSERT_NUM) {
//                    LinkedList<String> tmpRouteList = new LinkedList<String>();
//                    tmpRouteList.addAll(routeRelationJsonList);
//                    routeRelationJsonList.clear();
////                    dbl.dfPzFlowBatchStorage2CH(tmpRouteList);//测试入clickhouse20190220
//                    kafkaDB.routeRelatLog2KafkaFromSipInsertBoltDC(tmpRouteList);
//                }

            }
        } catch (Exception e) {
            logger.error("SipInsertBoltDC to insert is error !!!--->{" + e + "}<---");
            e.printStackTrace();
        }
    }

    private void logCount(String key, Map<String, Long> hm) {
        if (hm.containsKey(key)) {
            hm.put(key, hm.get(key) + 1);
        } else {
            hm.put(key, 1l);
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(org.apache.storm.Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, tickFreqSecs);
        return conf;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("countJsonLog"));
    }
}