package cn.ac.iie.bean;

import java.io.Serializable;
import java.util.ArrayList;

import cn.ac.iie.common.RealtimeCountConfig;

public class ConfigCompile implements Serializable{
	
	private static final long serialVersionUID = 2492616231566889401L;
	private String COMPILE_ID;
	private String CONT_TYPE;
	private String ATTR_TYPE;
	private String CONT_LABEL;
	private String TASK_ID;
	private String GUARANTEE_ID;
	private String AFFAIR_ID;
	private String TOPIC_ID;
	private String IS_VALID;
	private String SERVICE;
	private String LWHH;
	private ArrayList<String> TAG;
	
	
	public ConfigCompile(String[] config){
		this.COMPILE_ID = config[0];
		this.CONT_TYPE = config[1];
		this.ATTR_TYPE = config[2];
		this.CONT_LABEL = config[3];
		this.TASK_ID = config[4];
		this.GUARANTEE_ID = config[5];
		this.AFFAIR_ID = config[6];
		this.TOPIC_ID = config[7];
		this.IS_VALID = config[8];
		String[] tagList = config[3].split(",");
		this.SERVICE = makeService(tagList);
		this.LWHH = makeLwhh(tagList);
		this.TAG = makeTag(tagList);
	}
	
	private String makeService(String[] tagList){	
		for(int i=0; i<tagList.length; i++){
			int aTag = Integer.parseInt(tagList[i].trim());
			if(aTag>0 && aTag<=10000){
				return tagList[i].trim();
			}
		}
		return RealtimeCountConfig.EMPTY_OPTION_CHARACTER;
	}

	private String makeLwhh(String[] tagList){
		for(int i=0; i<tagList.length; i++){
			int aTag = Integer.parseInt(tagList[i].trim());
			if(aTag>=1000000){
				return tagList[i].trim();
			}
		}
		return RealtimeCountConfig.EMPTY_OPTION_CHARACTER;
	}
	
	private ArrayList<String> makeTag(String[] tagList){
		ArrayList<String> al = new ArrayList<String>();
		for(int i=0; i<tagList.length; i++){
			int aTag = Integer.parseInt(tagList[i].trim());
			if(aTag>10000 && aTag<1000000){
				al.add(tagList[i].trim());
			}
		}
		return al;
	}
	
	public String getCOMPILE_ID() {
		return COMPILE_ID;
	}

	public void setCOMPILE_ID(String cOMPILE_ID) {
		COMPILE_ID = cOMPILE_ID;
	}

	public String getCONT_TYPE() {
		return CONT_TYPE;
	}

	public void setCONT_TYPE(String cONT_TYPE) {
		CONT_TYPE = cONT_TYPE;
	}

	public String getATTR_TYPE() {
		return ATTR_TYPE;
	}

	public void setATTR_TYPE(String aTTR_TYPE) {
		ATTR_TYPE = aTTR_TYPE;
	}

	public String getCONT_LABEL() {
		return CONT_LABEL;
	}

	public void setCONT_LABEL(String cONT_LABEL) {
		CONT_LABEL = cONT_LABEL;
	}

	public String getTASK_ID() {
		return TASK_ID;
	}

	public void setTASK_ID(String tASK_ID) {
		TASK_ID = tASK_ID;
	}

	public String getGUARANTEE_ID() {
		return GUARANTEE_ID;
	}

	public void setGUARANTEE_ID(String gUARANTEE_ID) {
		GUARANTEE_ID = gUARANTEE_ID;
	}

	public String getAFFAIR_ID() {
		return AFFAIR_ID;
	}

	public void setAFFAIR_ID(String aFFAIR_ID) {
		AFFAIR_ID = aFFAIR_ID;
	}

	public String getTOPIC_ID() {
		return TOPIC_ID;
	}

	public void setTOPIC_ID(String tOPIC_ID) {
		TOPIC_ID = tOPIC_ID;
	}


	public String getIS_VALID() {
		return IS_VALID;
	}


	public void setIS_VALID(String iS_VALID) {
		IS_VALID = iS_VALID;
	}


	public String getSERVICE() {
		return SERVICE;
	}


	public void setSERVICE(String sERVICE) {
		SERVICE = sERVICE;
	}

	public String getLWHH() {
		return LWHH;
	}

	public void setLWHH(String lWHH) {
		LWHH = lWHH;
	}

	public ArrayList<String> getTAG() {
		return TAG;
	}

	public void setTAG(ArrayList<String> tAG) {
		TAG = tAG;
	}

	
}
