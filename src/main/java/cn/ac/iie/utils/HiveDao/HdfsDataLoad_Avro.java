package cn.ac.iie.utils.HiveDao;

import cn.ac.iie.bean.voipSipOrigin.SipOriginALL;
import cn.ac.iie.common.RealtimeCountConfig;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import com.zdjizhi.utils.StringUtil;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.net.URI;
import java.sql.Connection;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.UUID;

/**
 * HDFS-Hive-Avro格式载入类
 *
 * @author Colbert
 */
public class HdfsDataLoad_Avro {

    private static Logger logger = Logger.getLogger(HdfsDataLoad_Avro.class);
    private static DruidDataSource ds = HiveDataSourceUtil.getHiveDataSource();
    private Connection con = null;
    private Statement stmt = null;
    private FSDataOutputStream outputStream_avro = null;

    private DataFileWriter<GenericRecord> fileWriter = null;

    private static HdfsDataLoad_Avro hdfsDataLoad_avro = null;
    private static FileSystem fileSystem = null;

    private HdfsDataLoad_Avro() {
        getHdfsConnection();
    }

    public static HdfsDataLoad_Avro getHdfsInstance() {
        if (hdfsDataLoad_avro == null || fileSystem == null) {
            hdfsDataLoad_avro = new HdfsDataLoad_Avro();
        }
        return hdfsDataLoad_avro;
    }

    /**
     * 创建HDFS连接
     */
    private void getHdfsConnection() {
        try {
            Configuration conf = new Configuration();
            conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            conf.setBoolean("dfs.support.append", true);
            conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
            conf.setBoolean("dfs.client.block.write.replace-datanode-on-failure.enable", true);
            fileSystem = FileSystem.get(new URI(RealtimeCountConfig.HDFS_URL), conf, RealtimeCountConfig.HDFS_USER);
        } catch (Exception e) {
            logger.error("HdfsDataLoad_Avro getHdfsConnection method is error !!!--->{" + e + "}<---");
            e.printStackTrace();
        }
    }


    public void dataSipToHdfsAvro(String partition, LinkedList<String> data, String topicName, String logType, Long timeSend) {
        long time = timeSend;
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
        String hdfs_path = RealtimeCountConfig.HDFS_URL + RealtimeCountConfig.HDFS_PATH + topicName.toLowerCase() + "/" + partition + "/" + topicName.toLowerCase() + "-" + uuid + ".avro";//格式  hdfs://ns1/input/frag-media-expire-log/20190730/frag-media-expire-log-d84772e8257048f3be1ca82f8e35f215.avro
        try {
            Path path = new Path(hdfs_path);
            fileSystem.createNewFile(path);
            outputStream_avro = fileSystem.append(path);

            switch (logType) {
                case "origin":
                    String schemaJsonSip = "{\"type\": \"record\",\"name\": \"siplog\",\"fields\": [{\"name\": \"call_id\", \"type\": [\"string\", \"null\"]},{\"name\": \"clj_ip\", \"type\": [\"string\", \"null\"]},{\"name\": \"found_time\", \"type\": \"int\"},{\"name\": \"src_ip\", \"type\": [\"string\", \"null\"]},{\"name\": \"src_location_nation\", \"type\": [\"string\", \"null\"]},{\"name\": \"src_location_nation_code\", \"type\": [\"string\", \"null\"]},{\"name\": \"src_location_region\", \"type\": [\"string\", \"null\"]},{\"name\": \"src_port\", \"type\": \"int\"},{\"name\": \"dst_ip\", \"type\": [\"string\", \"null\"]},{\"name\": \"ip_type\", \"type\": [\"string\", \"null\"]},{\"name\": \"dst_location_nation\", \"type\": [\"string\", \"null\"]},{\"name\": \"dst_location_nation_code\", \"type\": [\"string\", \"null\"]},{\"name\": \"dst_location_region\", \"type\": [\"string\", \"null\"]},{\"name\": \"dst_port\", \"type\": \"int\"},{\"name\": \"method\", \"type\": [\"string\", \"null\"]},{\"name\": \"request_uri\", \"type\": [\"string\", \"null\"]},{\"name\": \"user_name\", \"type\": [\"string\", \"null\"]},{\"name\": \"service_domain\", \"type\": [\"string\", \"null\"]},{\"name\": \"service_domain_valid\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_stat\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_stat_format\", \"type\": [\"string\", \"null\"]},{\"name\": \"from\", \"type\": [\"string\", \"null\"]},{\"name\": \"from_nickname\", \"type\": [\"string\", \"null\"]},{\"name\": \"from_usr_name\", \"type\": [\"string\", \"null\"]},{\"name\": \"from_ser_domain\", \"type\": [\"string\", \"null\"]},{\"name\": \"from_ser_domain_valid\", \"type\": [\"string\", \"null\"]},{\"name\": \"from_tag\", \"type\": [\"string\", \"null\"]},{\"name\": \"to\", \"type\": [\"string\", \"null\"]},{\"name\": \"to_nickname\", \"type\": [\"string\", \"null\"]},{\"name\": \"to_usr_name\", \"type\": [\"string\", \"null\"]},{\"name\": \"to_ser_domain\", \"type\": [\"string\", \"null\"]},{\"name\": \"to_ser_domain_valid\", \"type\": [\"string\", \"null\"]},{\"name\": \"to_tag\", \"type\": [\"string\", \"null\"]},{\"name\": \"cseq\", \"type\": [\"string\", \"null\"]},{\"name\": \"cseq_method\", \"type\": [\"string\", \"null\"]},{\"name\": \"user_agent\", \"type\": [\"string\", \"null\"]},{\"name\": \"device_type\", \"type\": [\"string\", \"null\"]},{\"name\": \"max_forwards\", \"type\": [\"string\", \"null\"]},{\"name\": \"server\", \"type\": [\"string\", \"null\"]},{\"name\": \"server_type\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_via_json\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_contact\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_contact_nickname\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_contact_usr_name\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_contact_ser_domain\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_ser_domain_valid\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_record_route_json\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_route_json\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_expires\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_others\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_content_type\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_content\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_via_json\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_contact\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_contact_nickname\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_contact_usr_name\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_contact_ser_domain\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_ser_domain_valid\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_record_route_json\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_route_json\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_expires\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_others\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_content_type\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_content\", \"type\": [\"string\", \"null\"]},{\"name\": \"req_coding\", \"type\": [\"string\", \"null\"]},{\"name\": \"res_coding\", \"type\": [\"string\", \"null\"]},{\"name\": \"stat_time\", \"type\": \"int\"}]}";
                    Schema schemaSip = new Schema.Parser().parse(schemaJsonSip);
                    fileWriter = new DataFileWriter<GenericRecord>(new GenericDatumWriter<GenericRecord>(schemaSip)).setSyncInterval(100);
                    fileWriter.setCodec(CodecFactory.snappyCodec());
                    fileWriter.create(schemaSip, outputStream_avro);
                    for (String dataJson : data) {
                        SipOriginALL sipOriginAllLog = JSONObject.parseObject(dataJson, SipOriginALL.class);
                        sipOriginAllLog.setStat_time(time);
                        GenericRecord recordSip = new GenericData.Record(schemaSip);

                        recordSip.put("call_id", sipOriginAllLog.getCall_ID());
                        recordSip.put("clj_ip", sipOriginAllLog.getCLJ_IP());
                        recordSip.put("found_time", sipOriginAllLog.getFound_Time());
                        recordSip.put("src_ip", sipOriginAllLog.getSRC_IP());
                        recordSip.put("src_location_nation", sipOriginAllLog.getSRC_LOCATION_NATION());
                        recordSip.put("src_location_nation_code", sipOriginAllLog.getSRC_LOCATION_NATION_CODE());
                        recordSip.put("src_location_region", sipOriginAllLog.getSRC_LOCATION_REGION());
                        recordSip.put("src_port", sipOriginAllLog.getSRC_PORT());
                        recordSip.put("dst_ip", sipOriginAllLog.getDST_IP());
                        recordSip.put("ip_type", sipOriginAllLog.getIP_TYPE());
                        recordSip.put("dst_location_nation", sipOriginAllLog.getDST_LOCATION_NATION());
                        recordSip.put("dst_location_nation_code", sipOriginAllLog.getDST_LOCATION_NATION_CODE());
                        recordSip.put("dst_location_region", sipOriginAllLog.getDST_LOCATION_REGION());
                        recordSip.put("dst_port", sipOriginAllLog.getDST_PORT());
                        recordSip.put("method", sipOriginAllLog.getMethod());
                        recordSip.put("request_uri", sipOriginAllLog.getRequest_URI());
                        recordSip.put("user_name", sipOriginAllLog.getUser_name());
                        recordSip.put("service_domain", sipOriginAllLog.getService_domain());
                        recordSip.put("service_domain_valid", sipOriginAllLog.getService_domain_valid());
                        recordSip.put("res_stat", sipOriginAllLog.getRes_stat());
                        recordSip.put("res_stat_format", sipOriginAllLog.getRes_stat_format());
                        recordSip.put("from", sipOriginAllLog.getFrom());
                        recordSip.put("from_nickname", sipOriginAllLog.getFrom_Nickname());
                        recordSip.put("from_usr_name", sipOriginAllLog.getFrom_usr_name());
                        recordSip.put("from_ser_domain", sipOriginAllLog.getFrom_ser_domain());
                        recordSip.put("from_ser_domain_valid", sipOriginAllLog.getFrom_ser_domain_valid());
                        recordSip.put("from_tag", sipOriginAllLog.getFrom_tag());
                        recordSip.put("to", sipOriginAllLog.getTo());
                        recordSip.put("to_nickname", sipOriginAllLog.getTo_Nickname());
                        recordSip.put("to_usr_name", sipOriginAllLog.getTo_usr_name());
                        recordSip.put("to_ser_domain", sipOriginAllLog.getTo_ser_domain());
                        recordSip.put("to_ser_domain_valid", sipOriginAllLog.getTo_ser_domain_valid());
                        recordSip.put("to_tag", sipOriginAllLog.getTo_tag());
                        recordSip.put("cseq", sipOriginAllLog.getCseq());
                        recordSip.put("cseq_method", sipOriginAllLog.getCseq_method());
                        recordSip.put("user_agent", sipOriginAllLog.getUser_Agent());
                        recordSip.put("device_type", sipOriginAllLog.getDevice_type());
                        recordSip.put("max_forwards", sipOriginAllLog.getMax_Forwards());
                        recordSip.put("server", sipOriginAllLog.getServer());
                        recordSip.put("server_type", sipOriginAllLog.getServer_type());
                        recordSip.put("req_via_json", sipOriginAllLog.getReq_Via_Json());
                        recordSip.put("req_contact", sipOriginAllLog.getReq_Contact());
                        recordSip.put("req_contact_nickname", sipOriginAllLog.getReq_Contact_Nickname());
                        recordSip.put("req_contact_usr_name", sipOriginAllLog.getReq_Contact_usr_name());
                        recordSip.put("req_contact_ser_domain", sipOriginAllLog.getReq_Contact_ser_domain());
                        recordSip.put("req_ser_domain_valid", sipOriginAllLog.getReq_ser_domain_valid());
                        recordSip.put("req_record_route_json", sipOriginAllLog.getReq_Record_Route_Json());
                        recordSip.put("req_route_json", sipOriginAllLog.getReq_Route_Json());
                        recordSip.put("req_expires", sipOriginAllLog.getReq_Expires());
                        recordSip.put("req_others", sipOriginAllLog.getReq_Others());
                        recordSip.put("req_content_type", sipOriginAllLog.getReq_Content_Type());
                        recordSip.put("req_content", sipOriginAllLog.getReq_Content());
                        recordSip.put("res_via_json", sipOriginAllLog.getRes_Via_Json());
                        recordSip.put("res_contact", sipOriginAllLog.getRes_Contact());
                        recordSip.put("res_contact_nickname", sipOriginAllLog.getRes_Contact_Nickname());
                        recordSip.put("res_contact_usr_name", sipOriginAllLog.getRes_Contact_usr_name());
                        recordSip.put("res_contact_ser_domain", sipOriginAllLog.getRes_Contact_ser_domain());
                        recordSip.put("res_ser_domain_valid", sipOriginAllLog.getRes_ser_domain_valid());
                        recordSip.put("res_record_route_json", sipOriginAllLog.getRes_Record_Route_Json());
                        recordSip.put("res_route_json", sipOriginAllLog.getRes_Route_Json());
                        recordSip.put("res_expires", sipOriginAllLog.getRes_Expires());
                        recordSip.put("res_others", sipOriginAllLog.getRes_Others());
                        recordSip.put("res_content_type", sipOriginAllLog.getRes_Content_Type());
                        recordSip.put("res_content", sipOriginAllLog.getRes_Content());
                        recordSip.put("req_coding", sipOriginAllLog.getReq_coding());
                        recordSip.put("res_coding", sipOriginAllLog.getRes_coding());
                        recordSip.put("stat_time", sipOriginAllLog.getStat_time());

                        fileWriter.append(recordSip);
                        fileWriter.flush();
                    }
                    break;
                default:
                    logger.error("HdfsDataLoad_Avro toHdfs logType is error !!!This logType is--->{" + logType + "}<---");
                    break;
            }

            logger.warn("HdfsDataLoad_Avro data to HDFS Successful,hdfs_path is -->{" + hdfs_path + "}<---");

            if (fileWriter != null) {
                fileWriter.close();
                fileWriter = null;
            }
            if (outputStream_avro != null) {
                outputStream_avro.close();
                outputStream_avro = null;
            }

            switch (logType) {
                case "origin":
                    String tablenameSip = RealtimeCountConfig.HIVE_SIP_CLEAN_TABLE;
                    loadDataToHiveAvro(tablenameSip, partition, hdfs_path);
                    break;
//                case "route":
//                    String tablenameFrag = RealtimeCountConfig.HIVE_SIP_ROUTE_TABLE;
//                    loadDataToHiveAvro(tablenameFrag, partition, hdfs_path);
//                    break;
                default:
                    logger.error("HdfsDataLoad_Avro toHive logType is error !!!This logType is--->{" + logType + "}<---");
                    break;
            }

        } catch (Exception e) {
            logger.error("HdfsDataLoad_Avro dataToHdfs method is error !!!--->{" + e + "}<---");
            e.printStackTrace();
        }
    }

    public void loadDataToHiveAvro(String tablename, String partition, String hdfs_path) {
        String dataUrl = hdfs_path;
        StringBuffer sb = new StringBuffer();
        try {
            con = ds.getConnection();
            stmt = con.createStatement();
            sb.append("load data inpath ").append("'").append(dataUrl).append("'")
                    .append(" into table ").append(tablename).append(" partition( time_partition=").append(partition).append(")");
            stmt.execute(sb.toString());

            logger.warn("HdfsDataLoad_Avro data to Hive Successful,dataUrl is -->{" + dataUrl + "}<---");

        } catch (Exception e) {
            logger.error("HdfsDataLoad_Avro loadDataToHive method is error !!!--->{" + e + "}<---");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
                if (con != null) {
                    con.close();
                    con = null;
                }
            } catch (Exception e) {
                logger.error("HdfsDataLoad_Avro loadDataToHive when close is error !!!--->{" + e + "}<---");
                e.printStackTrace();
            }
        }
    }

    private String strDefaultValue(String str) {
        return (StringUtil.isBlank(str)
                || str == null
                || str.equals("")
                || str.length() == 0) ? "$#$" : str;
    }
}
