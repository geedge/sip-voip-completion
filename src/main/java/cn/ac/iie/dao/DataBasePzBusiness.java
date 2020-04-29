package cn.ac.iie.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Logger;

import cn.ac.iie.bean.PzTable;

public final class DataBasePzBusiness {
	private static final JdbcPzConnectionManager pzManager = JdbcPzConnectionManager.getInstance();
	private static final Logger logger = Logger.getLogger(DataBasePzBusiness.class);
	private Connection connection;
	private Statement stmt = null;

	public DataBasePzBusiness() {

	}

	public void getPzToMap(Long seq) {
		String queryOptions = " COMPILE_ID, CONT_TYPE, ATTR_TYPE, CONT_LABEL, Task_id, Guarantee_ID, AFFAIR_ID, TOPIC_ID, IS_VALID, PROC_SEQ";
		String sql = "select " + queryOptions + " from config_compile where PROC_SEQ > " + seq;
		try {
			connection = pzManager.getConnection("idb");
			connection.setAutoCommit(false);
			stmt = connection.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			pzManager.clear(null, stmt, null);
			if (connection != null) {
				pzManager.freeConnection("idb", connection);
			}
			return;
		}
		try {
			ResultSet rs = stmt.executeQuery(sql);
			Long seqMax = seq;
			while (rs.next()) {
				try {
					String sCOMPILE_ID = String.valueOf(rs.getLong(1));

					Object oCONT_TYPE = rs.getObject(2);
					if (oCONT_TYPE == null)
						oCONT_TYPE = "";
					String sCONT_TYPE = oCONT_TYPE.toString();
					if (sCONT_TYPE == null || sCONT_TYPE.equals(""))
						sCONT_TYPE = "-";

					Object oATTR_TYPE = rs.getObject(3);
					if (oATTR_TYPE == null)
						oATTR_TYPE = "";
					String sATTR_TYPE = oATTR_TYPE.toString();
					if (sATTR_TYPE == null || sATTR_TYPE.equals(""))
						sATTR_TYPE = "-";

					Object oCONT_LABEL = rs.getObject(4);
					if (oCONT_LABEL == null)
						oCONT_LABEL = "";
					String sCONT_LABEL = oCONT_LABEL.toString();
					if (sCONT_LABEL == null || sCONT_LABEL.equals(""))
						sCONT_LABEL = "-";

					Object oTask_id = rs.getObject(5);
					if (oTask_id == null)
						oTask_id = "";
					String sTask_id = oTask_id.toString();
					if (sTask_id == null || sTask_id.equals(""))
						sTask_id = "-";

					Object oGuarantee_ID = rs.getObject(6);
					if (oGuarantee_ID == null)
						oGuarantee_ID = "";
					String sGuarantee_ID = oGuarantee_ID.toString();
					if (sGuarantee_ID == null || sGuarantee_ID.equals(""))
						sGuarantee_ID = "-";

					Object oAFFAIR_ID = rs.getObject(7);
					if (oAFFAIR_ID == null)
						oAFFAIR_ID = "";
					String sAFFAIR_ID = oAFFAIR_ID.toString();
					if (sAFFAIR_ID == null || sAFFAIR_ID.equals(""))
						sAFFAIR_ID = "-";

					Object oTOPIC_ID = rs.getObject(8);
					if (oTOPIC_ID == null)
						oTOPIC_ID = "";
					String sTOPIC_ID = oTOPIC_ID.toString();
					if (sTOPIC_ID == null || sTOPIC_ID.equals(""))
						sTOPIC_ID = "-";

					Integer iIS_VALID = rs.getInt(9);

					Long lPROC_SEQ = rs.getLong(10);

					if (lPROC_SEQ > seqMax) {
						seqMax = lPROC_SEQ;
					}

					if (PzTable.seq == 0 && iIS_VALID == 0) {
						continue;
					}
					String value = sCOMPILE_ID + "\t" + sCONT_TYPE + "\t" + sATTR_TYPE + "\t" + sCONT_LABEL + "\t"
							+ sTask_id + "\t" + sGuarantee_ID + "\t" + sAFFAIR_ID + "\t" + sTOPIC_ID + "\t" + iIS_VALID;
					logger.info("put in map: "+value);
					PzTable.pzMap.put(sCOMPILE_ID, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			PzTable.seq = seqMax;
			logger.info("PZ UPDATE DONE.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pzManager.clear(null, stmt, null);
			if (connection != null) {
				pzManager.freeConnection("idb", connection);
			}
		}
	}
}
