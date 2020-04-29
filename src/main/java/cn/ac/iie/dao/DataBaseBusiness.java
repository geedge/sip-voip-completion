package cn.ac.iie.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import cn.ac.iie.common.RealtimeCountConfig;


public final class DataBaseBusiness {
	private static final JdbcConnectionManager manager = JdbcConnectionManager.getInstance();
	private static final Logger logger = Logger.getLogger(DataBaseBusiness.class);
	private  Connection connection;	
	private PreparedStatement pstmt;
	
	public DataBaseBusiness(){
		
	}
	
	private Long generateTimeWithInterval() throws ParseException{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dt = df.format(new Date());
		System.out.println(dt);
		
		String[] s = dt.split(":");
		Integer i = Integer.valueOf(s[1]);
		String ret = "";
		if(i<5){
			ret = s[0]+":00:00";
		} else if(i<10){
			ret = s[0]+":05:00";
		} else if(i<15){
			ret = s[0]+":10:00";
		} else if(i<20){
			ret = s[0]+":15:00";
		} else if(i<25){
			ret = s[0]+":20:00";
		} else if(i<30){
			ret = s[0]+":25:00";
		} else if(i<35){
			ret = s[0]+":30:00";
		} else if(i<40){
			ret = s[0]+":35:00";
		} else if(i<45){
			ret = s[0]+":40:00";
		} else if(i<50){
			ret = s[0]+":45:00";
		} else if(i<55){
			ret = s[0]+":50:00";
		} else if(i<=59){
			ret = s[0]+":55:00";
		} 
		
		Date date=df.parse(ret);
		Long l = date.getTime()+300000;
		return l;
	}
	
	@SuppressWarnings("unused")
	private void setBigDecimalNullable(PreparedStatement pstmts, int index, String str) throws Exception {
		if (str.equals(RealtimeCountConfig.EMPTY_OPTION_CHARACTER) || str.isEmpty()) {
			pstmts.setNull(index, Types.BIGINT);
		} else {
			pstmts.setBigDecimal(index, new BigDecimal(str));
		}
	}
	
	@SuppressWarnings("unused")
	private void setBigDecimal(PreparedStatement pstmts, int index, String str) throws Exception {	
		pstmts.setBigDecimal(index, new BigDecimal(str));
	}
	
	@SuppressWarnings("unused")
	private void setLongNullable(PreparedStatement pstmts, int index, String str) throws Exception {
		if (str.equals(RealtimeCountConfig.EMPTY_OPTION_CHARACTER) || str.isEmpty()) {
			pstmts.setNull(index, Types.BIGINT);
		} else {
			pstmts.setLong(index, Long.parseLong(str));
		}
	}

	private void setLong(PreparedStatement pstmts, int index, String str) throws Exception {
		pstmts.setLong(index, Long.parseLong(str));
	}
	
	@SuppressWarnings("unused")
	private void setIntNullable(PreparedStatement pstmts, int index, String str) throws Exception {
		if (str.equals(RealtimeCountConfig.EMPTY_OPTION_CHARACTER) || str.isEmpty()) {
			pstmts.setNull(index, Types.INTEGER);
		} else {
			pstmts.setInt(index, Integer.parseInt(str));
		}
	}

	private void setInt(PreparedStatement pstmts, int index, String str) throws Exception {
		pstmts.setInt(index, Integer.parseInt(str));
	}
	
	private void setStringNullable(PreparedStatement pstmts, int index, String str, Integer maxLen) throws Exception {
		if(str.equals(RealtimeCountConfig.EMPTY_OPTION_CHARACTER) || str.isEmpty()){
			pstmts.setString(index, "");
		} else {
			int byteLen = str.getBytes("utf-8").length;
			if(byteLen>maxLen){
				pstmts.setString(index, str.substring(0, str.length()-(byteLen-maxLen)));
			} else {
				pstmts.setString(index, str);
			}
		}
	}
	
	private void setString(PreparedStatement pstmts, int index, String str, Integer maxLen) throws Exception {
		if(str.equals(RealtimeCountConfig.EMPTY_OPTION_CHARACTER) || str.isEmpty()){
			throw new SQLException("字符串为空，如字符串可空，请使用setStringNullable函数"); 
		} else {
			int byteLen = str.getBytes("utf-8").length;
			if(byteLen>maxLen){
				pstmts.setString(index, str.substring(0, str.length()-(byteLen-maxLen)));
			} else {
				pstmts.setString(index, str);
			}
		}
	}

	@SuppressWarnings("unused")
	private void setTimeStampNullable(PreparedStatement pstmts, int index, String str) throws Exception {
		if (str.equals(RealtimeCountConfig.EMPTY_OPTION_CHARACTER) || str.isEmpty()) {
			pstmts.setNull(index, Types.TIMESTAMP);
		} else {
			pstmts.setTimestamp(index, new Timestamp(Long.parseLong(str + "000")));
		}
	}

	@SuppressWarnings("unused")
	private void setTimeStamp(PreparedStatement pstmts, int index, String str) throws Exception {
		if(str.equals(RealtimeCountConfig.EMPTY_OPTION_CHARACTER) || str.isEmpty()){
			throw new SQLException("时间为空，如字符串可空，请使用setTimeStampNullable函数"); 
		} else {
			pstmts.setTimestamp(index, new Timestamp(Long.parseLong(str + "000")));
		}		
	}
	
	private void generateTimeStamp(PreparedStatement pstmts, int index) throws Exception {
		pstmts.setTimestamp(index, new Timestamp(generateTimeWithInterval()));
	}	
		
	public void dfPzBatchStorage(Map<String, Long> pzMap) {
		String sql = " insert into DF_PZ_REPORT(STAT_ID, ACTIVE_SYS, CFG_ID, SERVICE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_PZ_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : pzMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setLong(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				pstmt.setLong(4, pzMap.get(key));				
				generateTimeStamp(pstmt, 5);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	
	public void dfServiceBatchStorage(Map<String, Long> serviceMap) {
		String sql = " insert into DF_SERVICE_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_SERVICE_REPORT.NEXTVAL, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : serviceMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				pstmt.setLong(3, serviceMap.get(key));				
				generateTimeStamp(pstmt, 4);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	
	public void dfTagBatchStorage(Map<String, Long> tagMap) {
		String sql = " insert into DF_TAG_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, TAG, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_TAG_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : tagMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				pstmt.setLong(4, tagMap.get(key));				
				generateTimeStamp(pstmt, 5);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
		
	
	public void dfSrcipDomesticBatchStorage(Map<String, Long> srcipMap) {
		String sql = " insert into DF_SRCIP_DOMESTIC_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, SRC_PROVINCE, SRC_CITY, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_SRCIP_DOMESTIC_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : srcipMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setString(pstmt, 3, options[3], 256);
				setStringNullable(pstmt, 4, options[4], 256);
				pstmt.setLong(5, srcipMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	
	public void dfDestipCountryBatchStorage(Map<String, Long> srcipMap) {
		String sql = " insert into DF_DESTIP_COUNTRY_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, DEST_COUNTRY, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_DESTIP_COUNTRY_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : srcipMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setString(pstmt, 3, options[3], 256);
				pstmt.setLong(4, srcipMap.get(key));				
				generateTimeStamp(pstmt, 5);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	
	public void djPzBatchStorage(Map<String, Long> pzMap) {
		String sql = " insert into DJ_PZ_REPORT(STAT_ID, ACTIVE_SYS, CFG_ID, SERVICE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_PZ_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : pzMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setLong(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				pstmt.setLong(4, pzMap.get(key));				
				generateTimeStamp(pstmt, 5);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	public void dfAttrTypeBatchStorage(Map<String, Long> attrTypeMap) {
		String sql = " insert into DF_ATTR_TYPE_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, ATTR_TYPE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_ATTR_TYPE_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : attrTypeMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				pstmt.setLong(4, attrTypeMap.get(key));				
				generateTimeStamp(pstmt, 5);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	public void dfEntranceBatchStorage(Map<String, Long> entranceMap) {
		String sql = " insert into DF_ENTRANCE_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, ENTRANCE_ID, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_ENTRANCE_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : entranceMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setLong(pstmt, 3, options[3]);
				pstmt.setLong(4, entranceMap.get(key));				
				generateTimeStamp(pstmt, 5);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	public void dfLwhhBatchStorage(Map<String, Long> lwhhMap) {
		String sql = " insert into DF_LWHH_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, LWHH, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_LWHH_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : lwhhMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				pstmt.setLong(4, lwhhMap.get(key));				
				generateTimeStamp(pstmt, 5);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	public void dfLwhhTypeBatchStorage(Map<String, Long> lwhhTypeMap) {
		String sql = " insert into DF_LWHH_TYPE_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, LWHH, ATTR_TYPE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_LWHH_TYPE_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : lwhhTypeMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				setInt(pstmt, 4, options[4]);
				pstmt.setLong(5, lwhhTypeMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	public void dfLwhhTagBatchStorage(Map<String, Long> lwhhTagMap) {
		String sql = " insert into DF_LWHH_TAG_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, LWHH, TAG, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_LWHH_TAG_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : lwhhTagMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				setInt(pstmt, 4, options[4]);
				pstmt.setLong(5, lwhhTagMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	public void dfSrcipTypeBatchStorage(Map<String, Long> srcTypeMap) {
		String sql = " insert into DF_SRCIP_DOMESTIC_TYPE_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, SRC_PROVINCE, ATTR_TYPE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_SRCIP_TYPE_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : srcTypeMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setString(pstmt, 3, options[3], 256);
				setInt(pstmt, 4, options[4]);
				pstmt.setLong(5, srcTypeMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	public void dfSrcipTagBatchStorage(Map<String, Long> srcTagMap) {
		String sql = " insert into DF_SRCIP_DOMESTIC_TAG_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, SRC_PROVINCE, TAG, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_SRCIP_TAG_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : srcTagMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setString(pstmt, 3, options[3], 256);
				setInt(pstmt, 4, options[4]);
				pstmt.setLong(5, srcTagMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}		
		
	public void dfPzAttrBatchStorage(Map<String, Long> pzAttrMap) {
		String sql = " insert into DF_PZ_ATTR_REPORT(STAT_ID, ACTIVE_SYS, CFG_ID, ATTR_TYPE, SERVICE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_PZ_ATTR_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : pzAttrMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setLong(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				setInt(pstmt, 4, options[4]);
				pstmt.setLong(5, pzAttrMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}		
	
	public void dfPzTagBatchStorage(Map<String, Long> pzTagMap) {
		String sql = " insert into DF_PZ_TAG_REPORT(STAT_ID, ACTIVE_SYS, CFG_ID, TAG, SERVICE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DF_PZ_TAG_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : pzTagMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setLong(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				setInt(pstmt, 4, options[4]);
				pstmt.setLong(5, pzTagMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}		
	
	public void djAttrTypeBatchStorage(Map<String, Long> attrTypeMap) {
		String sql = " insert into DJ_ATTR_TYPE_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, ATTR_TYPE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_ATTR_TYPE_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : attrTypeMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				pstmt.setLong(4, attrTypeMap.get(key));				
				generateTimeStamp(pstmt, 5);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	public void djDestipCountryBatchStorage(Map<String, Long> srcipMap) {
		String sql = " insert into DJ_DESTIP_COUNTRY_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, DEST_COUNTRY, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_DESTIP_COUNTRY_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : srcipMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setString(pstmt, 3, options[3], 256);
				pstmt.setLong(4, srcipMap.get(key));				
				generateTimeStamp(pstmt, 5);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	
	public void djEntranceBatchStorage(Map<String, Long> entranceMap) {
		String sql = " insert into DJ_ENTRANCE_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, ENTRANCE_ID, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_ENTRANCE_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : entranceMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setLong(pstmt, 3, options[3]);
				pstmt.setLong(4, entranceMap.get(key));				
				generateTimeStamp(pstmt, 5);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	
	
	public void djLwhhBatchStorage(Map<String, Long> lwhhMap) {
		String sql = " insert into DJ_LWHH_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, LWHH, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_LWHH_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : lwhhMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				pstmt.setLong(4, lwhhMap.get(key));				
				generateTimeStamp(pstmt, 5);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	
	public void djServiceBatchStorage(Map<String, Long> serviceMap) {
		String sql = " insert into DJ_SERVICE_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_SERVICE_REPORT.NEXTVAL, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : serviceMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				pstmt.setLong(3, serviceMap.get(key));				
				generateTimeStamp(pstmt, 4);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	
	public void djSrcipDomesticBatchStorage(Map<String, Long> srcipMap) {
		String sql = " insert into DJ_SRCIP_DOMESTIC_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, SRC_PROVINCE, SRC_CITY, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_SRCIP_DOMESTIC_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : srcipMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setString(pstmt, 3, options[3], 256);
				setStringNullable(pstmt, 4, options[4], 256);
				pstmt.setLong(5, srcipMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	public void djTagBatchStorage(Map<String, Long> tagMap) {
		String sql = " insert into DJ_TAG_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, TAG, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_TAG_REPORT.NEXTVAL, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : tagMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				pstmt.setLong(4, tagMap.get(key));				
				generateTimeStamp(pstmt, 5);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	
	public void djLwhhTypeBatchStorage(Map<String, Long> lwhhTypeMap) {
		String sql = " insert into DJ_LWHH_TYPE_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, LWHH, ATTR_TYPE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_LWHH_TYPE_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : lwhhTypeMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				setInt(pstmt, 4, options[4]);
				pstmt.setLong(5, lwhhTypeMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	public void djLwhhTagBatchStorage(Map<String, Long> lwhhTagMap) {
		String sql = " insert into DJ_LWHH_TAG_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, LWHH, TAG, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_LWHH_TAG_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : lwhhTagMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				setInt(pstmt, 4, options[4]);
				pstmt.setLong(5, lwhhTagMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	public void djSrcipTypeBatchStorage(Map<String, Long> srcTypeMap) {
		String sql = " insert into DJ_SRCIP_DOMESTIC_TYPE_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, SRC_PROVINCE, ATTR_TYPE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_SRCIP_TYPE_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : srcTypeMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setString(pstmt, 3, options[3], 256);
				setInt(pstmt, 4, options[4]);
				pstmt.setLong(5, srcTypeMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
	
	public void djSrcipTagBatchStorage(Map<String, Long> srcTagMap) {
		String sql = " insert into DJ_SRCIP_DOMESTIC_TAG_REPORT(STAT_ID, ACTIVE_SYS, SERVICE, SRC_PROVINCE, TAG, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_SRCIP_TAG_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : srcTagMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setInt(pstmt, 2, options[2]);
				setString(pstmt, 3, options[3], 256);
				setInt(pstmt, 4, options[4]);
				pstmt.setLong(5, srcTagMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}		
	
	public void djPzAttrBatchStorage(Map<String, Long> pzAttrMap) {
		String sql = " insert into DJ_PZ_ATTR_REPORT(STAT_ID, ACTIVE_SYS, CFG_ID, ATTR_TYPE, SERVICE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_PZ_ATTR_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : pzAttrMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setLong(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				setInt(pstmt, 4, options[4]);
				pstmt.setLong(5, pzAttrMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}		
	
	public void djPzTagBatchStorage(Map<String, Long> pzTagMap) {
		String sql = " insert into DJ_PZ_TAG_REPORT(STAT_ID, ACTIVE_SYS, CFG_ID, TAG, SERVICE, SUM, REPORT_TIME) " +
				" VALUES(SEQ_DJ_PZ_TAG_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		
		try {
			connection = manager.getConnection("idb");
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
			return;
		}
		int nums = 0;
		for (String key : pzTagMap.keySet()) {
			try {
				String[] options = key.split(RealtimeCountConfig.BETWEEN_BOLTS_SPLITTER);
				
				setInt(pstmt, 1, options[1]);
				setLong(pstmt, 2, options[2]);
				setInt(pstmt, 3, options[3]);
				setInt(pstmt, 4, options[4]);
				pstmt.setLong(5, pzTagMap.get(key));				
				generateTimeStamp(pstmt, 6);
				
				pstmt.addBatch();
				nums++;
				if(nums % RealtimeCountConfig.BATCH_INSERT_NUM == 0) {
					pstmt.executeBatch();
					connection.commit();
				}	
			} catch (Exception e) {
				logger.error("日志存在非法字段："+key);
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.clear(pstmt, null, null);
			if(connection!=null){
				manager.freeConnection("idb", connection);
			}
		}
	}
}
