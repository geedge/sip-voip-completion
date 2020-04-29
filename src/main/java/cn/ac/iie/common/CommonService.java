package cn.ac.iie.common;

import java.io.Serializable;

import org.apache.log4j.Logger;
import cn.ac.iie.bean.ConfigCompile;;

public class CommonService implements Serializable{
	
	private static final long serialVersionUID = 6106510579752162633L;
	private static Logger logger = Logger.getLogger(CommonService.class);
	private static final Integer COMPILE_OPTION_NUMBER = 9; 

	public String[] splitMessageWithLogType(String message,String logType) {
		Integer ZiYong_LOG_LENGTH = RealtimeCountConfig.LOG_COMMON_FIELD_NUM + HashTableConfig.LOG_SUB_OPTION_NUM_MAP.get(logType);
		if(message!=null && !message.isEmpty()) {
			String[] values = message.split(RealtimeCountConfig.LOG_STRING_SPLITTER);//分割符"\t"
			if(values.length == ZiYong_LOG_LENGTH) {
				return values;
			} else {
				logger.error(logType+"--->message length = " + values.length + " is illegal");//测试用
				return null;
			}
		}
		return null;
	}
	
	public ConfigCompile checkReplyFromRedis(String redisReply){
		if(redisReply == null){
			return null;
		}
		String[] str = redisReply.split("\t");
		if(str.length == COMPILE_OPTION_NUMBER){
			try{
				ConfigCompile cc = new ConfigCompile(str);
				if(cc.getSERVICE().equals(RealtimeCountConfig.EMPTY_OPTION_CHARACTER)){
					logger.error("私有标签为空, 配置id为: "+ cc.getCOMPILE_ID());
					return null;
				}
				return cc;
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}			
		}else{
			return null;
		}
	}
	
	public ConfigCompile checkPz(String pzStr){
		String[] str = pzStr.split("\t");
		if(str.length == COMPILE_OPTION_NUMBER){
			try{
				ConfigCompile cc = new ConfigCompile(str);
				if(cc.getSERVICE().equals(RealtimeCountConfig.EMPTY_OPTION_CHARACTER)){
					logger.error("私有标签为空, 配置id为: "+ cc.getCOMPILE_ID());
					return null;
				}
				return cc;
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}			
		}else{
			return null;
		}
	}
}
