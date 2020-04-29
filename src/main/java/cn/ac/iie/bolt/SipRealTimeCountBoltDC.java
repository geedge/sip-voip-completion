package cn.ac.iie.bolt;

import cn.ac.iie.bean.voipSipOrigin.SipOriginALL;
import cn.ac.iie.utils.TupleUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class SipRealTimeCountBoltDC extends BaseBasicBolt {

    private static Logger logger = Logger.getLogger(SipRealTimeCountBoltDC.class);
    private final Integer tickFreqSecs;

    private Map<String, Long> codingCount = null;
    private Map<String, Long> ipLocationCount = null;
    private Map<String, Long> ipTypeCount = null;
    private Map<String, Long> methodCount = null;
    private Map<String, Long> resStatCount = null;
    private Map<String, Long> serverCount = null;
    private Map<String, Long> serviceDomainCount = null;
    private Map<String, Long> uaCount = null;


    public SipRealTimeCountBoltDC(Integer tickFreqSecs) {
        this.tickFreqSecs = tickFreqSecs;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        codingCount = new HashMap<>();
        ipLocationCount = new HashMap<>();
        ipTypeCount = new HashMap<>();
        methodCount = new HashMap<>();
        resStatCount = new HashMap<>();
        serverCount = new HashMap<>();
        serviceDomainCount = new HashMap<>();
        uaCount = new HashMap<>();

    }

    public void execute(Tuple tuple, BasicOutputCollector collector) {
        if (TupleUtils.isTick(tuple)) {

            Map<String, Long> tmpServiceMap = new HashMap<String, Long>(serviceDomainCount);
            serviceDomainCount.clear();
            sendCount("service", tmpServiceMap, collector);

            Map<String, Long> tmpServerMap = new HashMap<String, Long>(serverCount);
            serverCount.clear();
            sendCount("server", tmpServerMap, collector);

            Map<String, Long> tmpUaMap = new HashMap<String, Long>(uaCount);
            uaCount.clear();
            sendCount("ua", tmpUaMap, collector);

            Map<String, Long> tmpLocationMap = new HashMap<String, Long>(ipLocationCount);
            ipLocationCount.clear();
            sendCount("location", tmpLocationMap, collector);

            Map<String, Long> tmpTypeMap = new HashMap<String, Long>(ipTypeCount);
            ipTypeCount.clear();
            sendCount("type", tmpTypeMap, collector);

            Map<String, Long> tmpMethodMap = new HashMap<String, Long>(methodCount);
            methodCount.clear();
            sendCount("method", tmpMethodMap, collector);

            Map<String, Long> tmpResStatMap = new HashMap<String, Long>(resStatCount);
            resStatCount.clear();
            sendCount("resStat", tmpResStatMap, collector);

            Map<String, Long> tmpCodingMap = new HashMap<String, Long>(codingCount);
            codingCount.clear();
            sendCount("coding", tmpCodingMap, collector);

        } else {
            try {

                String jsonLog = tuple.getString(0);

                SipOriginALL sipOriginLog = JSONObject.parseObject(jsonLog, SipOriginALL.class);

                //预处理cseq
                String cSeq = "NULL";
                String rawCSeq = sipOriginLog.getCseq();
                if(null != rawCSeq) {
                    String[] splitCSeq = rawCSeq.toUpperCase().split("[\\s]+");
                    if(splitCSeq.length > 1) {
                        cSeq = splitCSeq[1];
                    }
                }

                //提取所需的字段
                String service = sipOriginLog.getTo_ser_domain();
                String server = sipOriginLog.getServer();
                String ua = sipOriginLog.getUser_Agent();
                String srcCtyReg = sipOriginLog.getSRC_LOCATION_NATION() + "+" + sipOriginLog.getSRC_LOCATION_REGION() + "+" + sipOriginLog.getSRC_LOCATION_NATION_CODE();
                String dstCtyReg = sipOriginLog.getDST_LOCATION_NATION() + "+" + sipOriginLog.getDST_LOCATION_REGION() + "+" + sipOriginLog.getDST_LOCATION_NATION_CODE();
                String type = sipOriginLog.getIP_TYPE();
                String method = sipOriginLog.getMethod();
                String resStat = sipOriginLog.getRes_stat_format() + "+" + cSeq;
                String reqCodings = sipOriginLog.getReq_coding();
                String resCodings = sipOriginLog.getRes_coding();

                //计数
                logCount(service, serviceDomainCount);
                logCount(server, serverCount);
                logCount(ua, uaCount);
                logCount(srcCtyReg, ipLocationCount);
                logCount(dstCtyReg, ipLocationCount);
                logCount(type, ipTypeCount);
                logCount(method, methodCount);
                logCount(resStat, resStatCount);
                if(null != reqCodings) {
                    String[] reqSplit = reqCodings.split("[,\\[\\]]");
                    for(int i = 1; i < reqSplit.length; i++) {
                        logCount(reqSplit[i].trim(), codingCount);
                    }
                } else {
                    logCount("NULL", codingCount);
                }
                if(null != resCodings) {
                    String[] resSplit = resCodings.split("[,\\[\\]]");
                    for(int j = 1; j < resSplit.length; j++) {
                        logCount(resSplit[j].trim(), codingCount);
                    }
                } else {
                    logCount("NULL", codingCount);
                }
            } catch (Exception e) {
                logger.error("SipRealTimeCountBoltDC error !!!--->{" + e + "}<---");
                logger.error("SipRealTimeCountBoltDC data is !!!--->{" + tuple.getString(0) + "}<---");
                e.printStackTrace();
            }
        }

    }

    private void logCount(String key, Map<String, Long> hm) {
        if (hm.containsKey(key)) {
            hm.put(key, hm.get(key) + 1);
        } else {
            hm.put(key, 1l);
        }
    }

    private void sendCount(String countType, Map<String, Long> hm, BasicOutputCollector collector) {

        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(time);

        String jsonString = JSON.toJSONString(hm);
        collector.emit(new Values(countType, jsonString, currentTime));

    }


    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<>();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, tickFreqSecs);
        return conf;
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("countType", "jsonCount", "currentTime"));

    }
}