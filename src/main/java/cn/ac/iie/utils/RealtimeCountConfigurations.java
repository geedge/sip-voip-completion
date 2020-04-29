package cn.ac.iie.utils;

import java.util.Properties;

//import com.nis.util.StringUtil;
public final class RealtimeCountConfigurations {

	private static Properties propCommon = new Properties();//0
	private static Properties propService = new Properties();//1

	public static String getStringProperty(Integer type, String key) {
		if(type == 0){
			return propCommon.getProperty(key);
		} else if(type == 1){
			return propService.getProperty(key);
		} else {
			return null;
		}
	}
	

	public static Integer getIntProperty(Integer type, String key) {
		if(type == 0){
			return Integer.parseInt(propCommon.getProperty(key));
		} else if(type == 1){
			return Integer.parseInt(propService.getProperty(key));
		} else {
			return null;
		}
	}

	public static Long getLongProperty(Integer type, String key) {
		if(type == 0){
			return Long.parseLong(propCommon.getProperty(key));
		} else if(type == 1){
			return Long.parseLong(propService.getProperty(key));
		} else {
			return null;
		}
	}
	
	public static Boolean getBooleanProperty(Integer type, String key) {
		if(type == 0){
			return propCommon.getProperty(key).toLowerCase().trim().equals("true");
		} else if(type == 1){
			return propService.getProperty(key).toLowerCase().trim().equals("true");
		} else {
			return null;
		}
	}

	static {
		try {
			propCommon.load(RealtimeCountConfigurations.class.getClassLoader().getResourceAsStream("realtime_config.properties"));//0
			propService.load(RealtimeCountConfigurations.class.getClassLoader().getResourceAsStream("storm_config.properties"));//1
			/*prop.load(new FileInputStream(System.getProperty("user.dir") 
					+ File.separator + "config"+File.separator + "config.properties"));*/
			System.out.println("realtime_config.properties加载成功");
			System.out.println("storm_config.properties加载成功");
					
		} catch (Exception e) {
			propCommon = null;
			propService = null;
			System.err.println("RealtimeCountConfigurations配置文件加载失败");
		}
	}
}
