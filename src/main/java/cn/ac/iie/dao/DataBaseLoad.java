package cn.ac.iie.dao;


import cn.ac.iie.bean.ntc.NTC_CONN_RECORD_LOG;
import cn.ac.iie.common.RealtimeCountConfig;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Map;


/**
 * 日志写入clickHouse insert类
 *
 * @author Administrator
 * @create 2018-10-31 12:35
 */
public class DataBaseLoad {
    private static final Logger logger = Logger.getLogger(DataBaseLoad.class);
    private static DbConnect manger = DbConnect.getInstance();
    private Connection connection;
    private PreparedStatement pstm;

    public DataBaseLoad() {
    }

    public void ntcKilledBatchStorage2CH(LinkedList<String> tmpList) {
        String tableName = RealtimeCountConfig.TABLE_KILLED_NAME;
        String sql = "INSERT INTO " + tableName + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            connection = manger.getConnection();
            connection.setAutoCommit(false);
            pstm = connection.prepareStatement(sql);
            int nums = 0;
            for (String ntcLog : tmpList) {
                NTC_CONN_RECORD_LOG ntcConnRecordLog = JSONObject.parseObject(ntcLog, NTC_CONN_RECORD_LOG.class);
                pstm.setInt(1, ntcConnRecordLog.getCfg_id());
                pstm.setInt(2, ntcConnRecordLog.getFound_time());
                pstm.setInt(3, ntcConnRecordLog.getRecv_time());
                pstm.setLong(4, ntcConnRecordLog.getOver_id());
                pstm.setString(5, ntcConnRecordLog.getTrans_proto());
                pstm.setString(6, ntcConnRecordLog.getD_ip());
                pstm.setString(7, ntcConnRecordLog.getS_ip());
                pstm.setInt(8, ntcConnRecordLog.getD_port());
                pstm.setInt(9, ntcConnRecordLog.getS_port());
                pstm.setString(10, ntcConnRecordLog.getNest_protocol());
                pstm.setString(11, ntcConnRecordLog.getNest_server_ip());
                pstm.setString(12, ntcConnRecordLog.getNest_client_ip());
                pstm.setInt(13, ntcConnRecordLog.getNest_server_port());
                pstm.setInt(14, ntcConnRecordLog.getNest_client_port());
                pstm.setInt(15, ntcConnRecordLog.getService());
                pstm.setInt(16, ntcConnRecordLog.getEntrance_id());
                pstm.setString(17, ntcConnRecordLog.getCap_ip());
                pstm.setString(18, ntcConnRecordLog.getScene_file());
                pstm.setString(19, ntcConnRecordLog.getInjected_pkt_file());
                pstm.setString(20, ntcConnRecordLog.getNest_addr_list());
                pstm.setInt(21, ntcConnRecordLog.getAction());
                pstm.setString(22, ntcConnRecordLog.getServer_locate());
                pstm.setString(23, ntcConnRecordLog.getClient_locate());
                pstm.setString(24, ntcConnRecordLog.getApp_label());
                pstm.setLong(25, ntcConnRecordLog.getC2s_pkt_num());
                pstm.setLong(26, ntcConnRecordLog.getS2c_pkt_num());
                pstm.setLong(27, ntcConnRecordLog.getC2s_byte_num());
                pstm.setLong(28, ntcConnRecordLog.getS2c_byte_num());

                pstm.setString(29, ntcConnRecordLog.getUser_region());
                pstm.setInt(30, ntcConnRecordLog.getStream_dir());
                pstm.setString(31, ntcConnRecordLog.getAddr_list());
                pstm.setInt(32, ntcConnRecordLog.getCreate_time());
                pstm.setInt(33, ntcConnRecordLog.getLastmtime());

                pstm.addBatch();
                nums++;
                if (nums >= RealtimeCountConfig.BATCH_CHINSERT_KILLED_NUM) {
                    pstm.executeBatch();
                    connection.commit();
                    nums = 0;
                }
            }
            if (nums != 0) {
                pstm.executeBatch();
                connection.commit();
                nums = 0;
            }
        } catch (Exception e) {
            logger.error("+++++++++insert to " + RealtimeCountConfig.TABLE_KILLED_NAME + " Log write failed!!!+++++++++");
            e.printStackTrace();
        } finally {
            manger.clear(pstm, connection);
        }
    }




    public void dfPzFlowBatchStorage2CH(LinkedList<String> tmpList) {
        String tableName = RealtimeCountConfig.TABLE_NAME;
        String sql = "INSERT INTO " + tableName + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            connection = manger.getConnection();
            connection.setAutoCommit(false);
            pstm = connection.prepareStatement(sql);
            int nums = 0;
            for (String ntcLog : tmpList) {
                NTC_CONN_RECORD_LOG ntcConnRecordLog = JSONObject.parseObject(ntcLog, NTC_CONN_RECORD_LOG.class);
                pstm.setInt(1, ntcConnRecordLog.getCfg_id());
                pstm.setInt(2, ntcConnRecordLog.getFound_time());
                pstm.setInt(3, ntcConnRecordLog.getRecv_time());
                pstm.setLong(4, ntcConnRecordLog.getOver_id());
                pstm.setString(5, ntcConnRecordLog.getTrans_proto());
                pstm.setString(6, ntcConnRecordLog.getD_ip());
                pstm.setString(7, ntcConnRecordLog.getS_ip());
                pstm.setInt(8, ntcConnRecordLog.getD_port());
                pstm.setInt(9, ntcConnRecordLog.getS_port());
                pstm.setString(10, ntcConnRecordLog.getNest_protocol());
                pstm.setString(11, ntcConnRecordLog.getNest_server_ip());
                pstm.setString(12, ntcConnRecordLog.getNest_client_ip());
                pstm.setInt(13, ntcConnRecordLog.getNest_server_port());
                pstm.setInt(14, ntcConnRecordLog.getNest_client_port());
                pstm.setInt(15, ntcConnRecordLog.getService());
                pstm.setInt(16, ntcConnRecordLog.getEntrance_id());
                pstm.setString(17, ntcConnRecordLog.getCap_ip());
                pstm.setString(18, ntcConnRecordLog.getScene_file());
                pstm.setString(19, ntcConnRecordLog.getInjected_pkt_file());
                pstm.setString(20, ntcConnRecordLog.getNest_addr_list());
                pstm.setInt(21, ntcConnRecordLog.getAction());
                pstm.setString(22, ntcConnRecordLog.getServer_locate());
                pstm.setString(23, ntcConnRecordLog.getClient_locate());
                pstm.setString(24, ntcConnRecordLog.getApp_label());
                pstm.setLong(25, ntcConnRecordLog.getC2s_pkt_num());
                pstm.setLong(26, ntcConnRecordLog.getS2c_pkt_num());
                pstm.setLong(27, ntcConnRecordLog.getC2s_byte_num());
                pstm.setLong(28, ntcConnRecordLog.getS2c_byte_num());

                pstm.setString(29, ntcConnRecordLog.getUser_region());
                pstm.setInt(30, ntcConnRecordLog.getStream_dir());
                pstm.setString(31, ntcConnRecordLog.getAddr_list());
                pstm.setInt(32, ntcConnRecordLog.getCreate_time());
                pstm.setInt(33, ntcConnRecordLog.getLastmtime());

                pstm.addBatch();
                nums++;
                if (nums >= RealtimeCountConfig.BATCH_CHINSERT_NUM) {
                    pstm.executeBatch();
                    connection.commit();
                    nums = 0;
                }
            }
            if (nums != 0) {
                pstm.executeBatch();
                connection.commit();
                nums = 0;
            }
        } catch (Exception e) {
            logger.error("+++++++++insert to " + RealtimeCountConfig.TABLE_NAME + " Log write failed!!!+++++++++");
            e.printStackTrace();
        } finally {
            manger.clear(pstm, connection);
        }
    }

}
