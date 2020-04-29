package cn.ac.iie.common;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Map;

public class DataCenterLoad {
    private static final String url = HttpManager.getInfoLoadInstance().getAddress();
    private static Logger logger = Logger.getLogger(DataCenterLoad.class);

    public DataCenterLoad() {
    }

    private String generateTimeWithInterval() {
        Long stamp = System.currentTimeMillis() + 300000L;
        Long stamp5 = stamp / 300000 * 300000;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(stamp5);
    }

    public void dfPzFlowBatchStorage(Map<String, Long> pzMap) {
        //String sql = " insert into DF_PZ_REPORT(STAT_ID, ACTIVE_SYS, CFG_ID, SERVICE, SUM, REPORT_TIME) " +
        //		" VALUES(SEQ_DF_PZ_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
        StringBuffer sb = new StringBuffer();
        String time5 = generateTimeWithInterval();
        int nums = 0;
        for (String key : pzMap.keySet()) {
            try {
                String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
                if (options[0] != null && options[0] != "" && options[1] != null && options[1] != "" && options[2] != null && options[2] != "" && options[3] != null && options[3] != "" && options[4] != null && options[4] != "" && options[5] != null && options[5] != "") {
                    String aItem = options[0] + "," + options[1] + "," + options[2] + "," + options[3] + "," + options[4] + "," + options[5] + "," + pzMap.get(key) + "," + time5;
                    sb.append(aItem + "\n");
                    nums++;
                    if (nums >= RealtimeCountConfig.BATCH_INSERT_NUM) {
                        String data = sb.substring(0, sb.length() - 1);
                        //输出的topic
                        logger.info("start to post data to dc---------> " + data);
                        System.out.println("start to post data to dc---------> " + data);
                        HttpManager.getInfoLoadInstance().postToDataCenter(url, "DF_PZ_FLOW_REPORT", data);//原本的方法
                        sb.setLength(0);
                        nums = 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if (nums != 0) {
                String data = sb.substring(0, sb.length() - 1);
                HttpManager.getInfoLoadInstance().postToDataCenter(url, "DF_PZ_FLOW_REPORT", data);
                sb.setLength(0);
                nums = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dfPzBatchStorage(Map<String, Long> pzMap) {
        //String sql = " insert into DF_PZ_REPORT(STAT_ID, ACTIVE_SYS, CFG_ID, SERVICE, SUM, REPORT_TIME) " +
        //		" VALUES(SEQ_DF_PZ_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
        StringBuffer sb = new StringBuffer();
        String time5 = generateTimeWithInterval();
        int nums = 0;
        for (String key : pzMap.keySet()) {
            try {
                String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
                String aItem = options[2] + "\t" + options[1] + "\t" + options[3] + "\t" + pzMap.get(key) + "\t" + time5;
                sb.append(aItem + "\n");
                nums++;
                if (nums >= RealtimeCountConfig.BATCH_INSERT_NUM) {
                    String data = sb.substring(0, sb.length() - 1);
                    HttpManager.getInfoLoadInstance().postToDataCenter(url, "t_xa_df_pz_report_dt", data);
                    sb.setLength(0);
                    nums = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if (nums != 0) {
                String data = sb.substring(0, sb.length() - 1);
                HttpManager.getInfoLoadInstance().postToDataCenter(url, "t_xa_df_pz_report_dt", data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
