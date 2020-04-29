package cn.ac.iie.bolt;

import cn.ac.iie.bean.voipSipCount.*;
import cn.ac.iie.dao.DbConnect;
import cn.ac.iie.utils.TupleUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SipRealTimeMergeBoltDC extends BaseBasicBolt {

    private static Logger logger = Logger.getLogger(SipRealTimeMergeBoltDC.class);

    private static DbConnect manager = DbConnect.getInstance();
    private Connection connection = null;
    private PreparedStatement pstmt = null;

    private final Integer tickFreqSecs;

    private Map<String, VoipServiceDomain> serviceDomainCountResult;
    private Map<String, VoipServer> serverCountResult;
    private Map<String, VoipUa> uaCountResult;
    private Map<String, VoipIpLocation> ipLocationCountResult;
    private Map<String, VoipIpType> ipTypeCountResult;
    private Map<String, VoipMethod> methodCountResult;
    private Map<String, VoipResStat> resStatCountResult;
    private Map<String, VoipCoding> codingCountResult;


    public SipRealTimeMergeBoltDC(Integer tickFreqSecs) {
        this.tickFreqSecs = tickFreqSecs;
    }


    @Override
    public void prepare(Map stormConf, TopologyContext context) {

        serviceDomainCountResult = new HashMap<>();
        serverCountResult = new HashMap<>();
        uaCountResult = new HashMap<>();
        ipLocationCountResult = new HashMap<>();
        ipTypeCountResult = new HashMap<>();
        methodCountResult = new HashMap<>();
        resStatCountResult = new HashMap<>();
        codingCountResult = new HashMap<>();

    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {


        if (TupleUtils.isTick(input)) {

            long time = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(time);

            if (!serviceDomainCountResult.isEmpty()) {
                Map<String, VoipServiceDomain> tmpMap = new HashMap<String, VoipServiceDomain>(serviceDomainCountResult);
                serviceDomainCountResult.clear();
                batchInsertServiceDomain(tmpMap, currentTime);
            }
            if (!serverCountResult.isEmpty()) {
                Map<String, VoipServer> tmpMap = new HashMap<String, VoipServer>(serverCountResult);
                serverCountResult.clear();
                batchInsertServer(tmpMap, currentTime);
            }
            if (!uaCountResult.isEmpty()) {
                Map<String, VoipUa> tmpMap = new HashMap<String, VoipUa>(uaCountResult);
                uaCountResult.clear();
                batchInsertUa(tmpMap, currentTime);
            }
            if (!ipLocationCountResult.isEmpty()) {
                Map<String, VoipIpLocation> tmpMap = new HashMap<String, VoipIpLocation>(ipLocationCountResult);
                ipLocationCountResult.clear();
                batchInsertIpLocation(tmpMap, currentTime);
            }
            if (!ipTypeCountResult.isEmpty()) {
                Map<String, VoipIpType> tmpMap = new HashMap<String, VoipIpType>(ipTypeCountResult);
                ipTypeCountResult.clear();
                batchInsertIpType(tmpMap, currentTime);
            }
            if (!methodCountResult.isEmpty()) {
                Map<String, VoipMethod> tmpMap = new HashMap<String, VoipMethod>(methodCountResult);
                methodCountResult.clear();
                batchInsertMethod(tmpMap, currentTime);
            }
            if (!resStatCountResult.isEmpty()) {
                Map<String, VoipResStat> tmpMap = new HashMap<String, VoipResStat>(resStatCountResult);
                resStatCountResult.clear();
                batchInsertResStat(tmpMap, currentTime);
            }
            if (!codingCountResult.isEmpty()) {
                Map<String, VoipCoding> tmpMap = new HashMap<String, VoipCoding>(codingCountResult);
                codingCountResult.clear();
                batchInsertCoding(tmpMap, currentTime);
            }

            logger.warn("Real time count result Insert Clickhouse execute at " + currentTime);

        } else {

            String countType = input.getStringByField("countType");
            String jsonCount = input.getStringByField("jsonCount");
            String currentTime = input.getStringByField("currentTime");
            JSONObject jsonObject = JSONObject.parseObject(jsonCount);
            Map<String, Object> hmCount = (Map<String, Object>) jsonObject;


            switch (countType) {
                case "service":
                    mergeServiceDomainResult(hmCount, serviceDomainCountResult, currentTime);
                    break;
                case "server":
                    mergeServerResult(hmCount, serverCountResult, currentTime);
                    break;
                case "ua":
                    mergeUaResult(hmCount, uaCountResult, currentTime);
                    break;
                case "location":
                    mergeIpLocationResult(hmCount, ipLocationCountResult, currentTime);
                    break;
                case "type":
                    mergeIpTypeResult(hmCount, ipTypeCountResult, currentTime);
                    break;
                case "method":
                    mergeMethodResult(hmCount, methodCountResult, currentTime);
                    break;
                case "resStat":
                    mergeResStatResult(hmCount, resStatCountResult, currentTime);
                    break;
                case "coding":
                    mergeCodingResult(hmCount, codingCountResult, currentTime);
                    break;

            }

        }

    }

    private String removeBlank(String IP) {
        while (IP.startsWith(" ")) {
            IP = IP.substring(1, IP.length()).trim();
        }
        while (IP.endsWith(" ")) {
            IP = IP.substring(0, IP.length() - 1).trim();
        }
        return IP;
    }

    /**
     * 判断IP
     */
    private boolean isIp(String IP) {
        if (null == IP) {
            return false;
        }
        IP = this.removeBlank(IP);
        if (IP.length() < 7 || IP.length() > 21) {
            return false;
        }
        if (IP.contains(".")) {
            String[] arr = IP.split("[.:]");
            if (!(arr.length == 4 || arr.length == 5)) {
                return false;
            }
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < arr[i].length(); j++) {
                    char temp = arr[i].charAt(j);
                    if (!(temp >= '0' && temp <= '9')) {
                        return false;
                    }
                }
            }
            for (int i = 0; i < 4; i++) {
                if("" == arr[i] || arr[i].length() > 3)
                {
                    return false;
                }
                int temp = Integer.parseInt(arr[i]);
                if (temp < 0 || temp > 255) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private void mergeServiceDomainResult(Map<String, Object> hmCount, Map<String, VoipServiceDomain> hm, String currentTime) {

        Iterator<Map.Entry<String, Object>> iter = hmCount.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            String key = entry.getKey();
            Object val = entry.getValue();
            String str = String.valueOf(val);
            Long count = Long.valueOf(str);

            if (hm.containsKey(key)) {
                VoipServiceDomain object = hm.get(key);
                object.setCount(object.getCount() + count);
            } else {
                VoipServiceDomain object = new VoipServiceDomain();
                object.setService(key);
                if (isIp(key)) {
                    object.setType("0");
                } else {
                    object.setType("1");
                }
                object.setCount(count);
                object.setInterval_time(currentTime);
                hm.put(key, object);
            }
        }
    }

    private void mergeServerResult(Map<String, Object> hmCount, Map<String, VoipServer> hm, String currentTime) {

        Iterator<Map.Entry<String, Object>> iter = hmCount.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            String key = entry.getKey();
            Object val = entry.getValue();
            String str = String.valueOf(val);
            Long count = Long.parseLong(str);

            if (hm.containsKey(key)) {
                VoipServer object = hm.get(key);
                object.setCount(object.getCount() + count);
            } else {
                VoipServer object = new VoipServer();
                object.setServer(key);
                object.setCount(count);
                object.setInterval_time(currentTime);
                hm.put(key, object);
            }
        }
    }

    private void mergeUaResult(Map<String, Object> hmCount, Map<String, VoipUa> hm, String currentTime) {

        Iterator<Map.Entry<String, Object>> iter = hmCount.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            String key = entry.getKey();
            Object val = entry.getValue();
            String str = String.valueOf(val);
            Long count = Long.valueOf(str);

            if (hm.containsKey(key)) {
                VoipUa object = hm.get(key);
                object.setCount(object.getCount() + count);
            } else {
                VoipUa object = new VoipUa();
                object.setUa(key);
                object.setCount(count);
                object.setInterval_time(currentTime);
                hm.put(key, object);
            }
        }
    }

    private void mergeIpLocationResult(Map<String, Object> hmCount, Map<String, VoipIpLocation> hm, String currentTime) {

        Iterator<Map.Entry<String, Object>> iter = hmCount.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            String key = entry.getKey();
            Object val = entry.getValue();
            String str = String.valueOf(val);
            Long count = Long.valueOf(str);

            if (hm.containsKey(key)) {
                VoipIpLocation object = hm.get(key);
                object.setCount(object.getCount() + count);
            } else {
                VoipIpLocation object = new VoipIpLocation();
                String[] items = key.split("\\+");
                String country = items[0];
                String region = items[1];
                String code = items[2];
                object.setCountry(country);
                object.setRegion(region);
                object.setNationCode(code);
                object.setCount(count);
                object.setInterval_time(currentTime);
                hm.put(key, object);
            }
        }
    }

    private void mergeIpTypeResult(Map<String, Object> hmCount, Map<String, VoipIpType> hm, String currentTime) {

        Iterator<Map.Entry<String, Object>> iter = hmCount.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            String key = entry.getKey();
            Object val = entry.getValue();
            String str = String.valueOf(val);
            Long count = Long.valueOf(str);

            if (hm.containsKey(key)) {
                VoipIpType object = hm.get(key);
                object.setCount(object.getCount() + count);
            } else {
                VoipIpType object = new VoipIpType();
                object.setType(key);
                object.setCount(count);
                object.setInterval_time(currentTime);
                hm.put(key, object);
            }
        }
    }

    private void mergeMethodResult(Map<String, Object> hmCount, Map<String, VoipMethod> hm, String currentTime) {

        Iterator<Map.Entry<String, Object>> iter = hmCount.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            String key = entry.getKey();
            Object val = entry.getValue();
            String str = String.valueOf(val);
            Long count = Long.valueOf(str);

            if (hm.containsKey(key)) {
                VoipMethod object = hm.get(key);
                object.setCount(object.getCount() + count);
            } else {
                VoipMethod object = new VoipMethod();
                object.setMethod(key);
                object.setCount(count);
                object.setInterval_time(currentTime);
                hm.put(key, object);
            }
        }
    }

    private void mergeResStatResult(Map<String, Object> hmCount, Map<String, VoipResStat> hm, String currentTime) {

        Iterator<Map.Entry<String, Object>> iter = hmCount.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            String key = entry.getKey();
            Object val = entry.getValue();
            String str = String.valueOf(val);
            Long count = Long.valueOf(str);

            if (hm.containsKey(key)) {
                VoipResStat object = hm.get(key);
                object.setCount(object.getCount() + count);
            } else {
                VoipResStat object = new VoipResStat();
                String[] items = key.split("\\+");
                String res_stat = items[0];
                String cseq = items[1];
                object.setRes_stat(res_stat);
                object.setCseq(cseq);
                object.setCount(count);
                object.setInterval_time(currentTime);
                hm.put(key, object);
            }
        }
    }

    private void mergeCodingResult(Map<String, Object> hmCount, Map<String, VoipCoding> hm, String currentTime) {

        Iterator<Map.Entry<String, Object>> iter = hmCount.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            String key = entry.getKey();
            Object val = entry.getValue();
            String str = String.valueOf(val);
            Long count = Long.valueOf(str);

            if (hm.containsKey(key)) {
                VoipCoding object = hm.get(key);
                object.setCount(object.getCount() + count);
            } else {
                VoipCoding object = new VoipCoding();
                object.setCoding(key);
                object.setCount(count);
                object.setInterval_time(currentTime);
                hm.put(key, object);
            }
        }
    }

    private void batchInsertServiceDomain(Map<String, VoipServiceDomain> hm, String currentTime) {
        String sql = "insert into voip_knowledge.voip_service_domain values(?, ?, ?, ?)";
        try {
            connection = manager.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            for (Map.Entry<String, VoipServiceDomain> entry : hm.entrySet()) {
                VoipServiceDomain object = entry.getValue();
                pstmt.setString(1, object.getService());
                pstmt.setString(2, object.getType());
                pstmt.setLong(3, object.getCount());
                pstmt.setString(4, currentTime);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
        } catch (Exception e) {
            logger.error("+++++++++insert to voip_service_domain Log write failed!!!+++++++++");
            e.printStackTrace();
        } finally {
            manager.clear(pstmt, connection);
        }
    }

    private void batchInsertCoding(Map<String, VoipCoding> hm, String currentTime) {
        String sql = "insert into voip_knowledge.voip_coding values(?, ?, ?)";
        try {
            connection = manager.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            for (Map.Entry<String, VoipCoding> entry : hm.entrySet()) {
                VoipCoding object = entry.getValue();
                pstmt.setString(1, object.getCoding());
                pstmt.setLong(2, object.getCount());
                pstmt.setString(3, currentTime);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
        } catch (Exception e) {
            logger.error("+++++++++insert to voip_ua Log write failed!!!+++++++++");
            e.printStackTrace();
        } finally {
            manager.clear(pstmt, connection);
        }
    }

    private void batchInsertIpLocation(Map<String, VoipIpLocation> hm, String currentTime) {
        String sql = "insert into voip_knowledge.voip_ip_location values(?, ?, ?, ?, ?)";
        try {
            connection = manager.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            for (Map.Entry<String, VoipIpLocation> entry : hm.entrySet()) {
                VoipIpLocation object = entry.getValue();
                pstmt.setString(1, object.getCountry());
                pstmt.setString(2, object.getRegion());
                pstmt.setString(3, object.getNationCode());
                pstmt.setLong(4, object.getCount());
                pstmt.setString(5, currentTime);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
        } catch (Exception e) {
            logger.error("+++++++++insert to voip_ip_location Log write failed!!!+++++++++");
            e.printStackTrace();
        } finally {
            manager.clear(pstmt, connection);
        }
    }

    private void batchInsertIpType(Map<String, VoipIpType> hm, String currentTime) {
        String sql = "insert into voip_knowledge.voip_ip_type values(?, ?, ?)";
        try {
            connection = manager.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            for (Map.Entry<String, VoipIpType> entry : hm.entrySet()) {
                VoipIpType object = entry.getValue();
                pstmt.setString(1, object.getType());
                pstmt.setLong(2, object.getCount());
                pstmt.setString(3, currentTime);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
        } catch (Exception e) {
            logger.error("+++++++++insert to voip_ip_type Log write failed!!!+++++++++");
            e.printStackTrace();
        } finally {
            manager.clear(pstmt, connection);
        }
    }

    private void batchInsertMethod(Map<String, VoipMethod> hm, String currentTime) {
        String sql = "insert into voip_knowledge.voip_method values(?, ?, ?)";
        try {
            connection = manager.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            for (Map.Entry<String, VoipMethod> entry : hm.entrySet()) {
                VoipMethod object = entry.getValue();
                pstmt.setString(1, object.getMethod());
                pstmt.setLong(2, object.getCount());
                pstmt.setString(3, currentTime);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
        } catch (Exception e) {
            logger.error("+++++++++insert to voip_method Log write failed!!!+++++++++");
            e.printStackTrace();
        } finally {
            manager.clear(pstmt, connection);
        }
    }

    private void batchInsertResStat(Map<String, VoipResStat> hm, String currentTime) {
        String sql = "insert into voip_knowledge.voip_res_stat values(?, ?, ?, ?)";
        try {
            connection = manager.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            for (Map.Entry<String, VoipResStat> entry : hm.entrySet()) {
                VoipResStat object = entry.getValue();
                pstmt.setString(1, object.getRes_stat());
                pstmt.setString(2, object.getCseq());
                pstmt.setLong(3, object.getCount());
                pstmt.setString(4, currentTime);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
        } catch (Exception e) {
            logger.error("+++++++++insert to voip_res_stat Log write failed!!!+++++++++");
            e.printStackTrace();
        } finally {
            manager.clear(pstmt, connection);
        }
    }

    private void batchInsertServer(Map<String, VoipServer> hm, String currentTime) {
        String sql = "insert into voip_knowledge.voip_server values(?, ?, ?)";
        try {
            connection = manager.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            for (Map.Entry<String, VoipServer> entry : hm.entrySet()) {
                VoipServer object = entry.getValue();
                pstmt.setString(1, object.getServer());
                pstmt.setLong(2, object.getCount());
                pstmt.setString(3, currentTime);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
        } catch (Exception e) {
            logger.error("+++++++++insert to voip_server Log write failed!!!+++++++++");
            e.printStackTrace();
        } finally {
            manager.clear(pstmt, connection);
        }
    }

    private void batchInsertUa(Map<String, VoipUa> hm, String currentTime) {
        String sql = "insert into voip_knowledge.voip_ua values(?, ?, ?)";
        try {
            connection = manager.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            for (Map.Entry<String, VoipUa> entry : hm.entrySet()) {
                VoipUa object = entry.getValue();
                pstmt.setString(1, object.getUa());
                pstmt.setLong(2, object.getCount());
                pstmt.setString(3, currentTime);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
        } catch (Exception e) {
            logger.error("+++++++++insert to voip_ua Log write failed!!!+++++++++");
            e.printStackTrace();
        } finally {
            manager.clear(pstmt, connection);
        }
    }


    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<>();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, tickFreqSecs);
        return conf;
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
