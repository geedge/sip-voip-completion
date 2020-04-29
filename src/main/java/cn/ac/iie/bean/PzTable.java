package cn.ac.iie.bean;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.ac.iie.dao.DataBasePzBusiness;
import org.apache.log4j.Logger;

public class PzTable {
	public static HashMap<String, String> pzMap = new HashMap<String, String>();	
	public static Lock trylock = new ReentrantLock();
	private static final Logger logger = Logger.getLogger(PzTable.class);
	public static long lastUpdateTime = 0L;
	public static long seq = 0L;
	
	public static void updatePzMap(){
		if (trylock.tryLock()) {
            try {
               long timeNow = System.currentTimeMillis();
               if(((timeNow-lastUpdateTime)/1000) > 240){
            	   //update pzMap and lastUpdateTime
            	   new DataBasePzBusiness().getPzToMap(seq);
            	   lastUpdateTime = timeNow;
            	   logger.info("pzMap.size: "+pzMap.size());
               }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	trylock.unlock();
            }
        } 
	}
}
