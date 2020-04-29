package cn.ac.iie.bean.ntc;

import cn.ac.iie.bean.CommonLog;

public class NTC_CONN_RECORD_LOG extends CommonLog {

    private String app_label;
    private int proto_id;
    private int app_id;
    private int os_id;
    private int bs_id;
    private int web_id;
    private int behav_id;
    private long c2s_pkt_num;
    private long s2c_pkt_num;
    private long c2s_byte_num;
    private long s2c_byte_num;

    private int create_time;
    private int lastmtime;

//    private int c2s_pkt_num;
//    private int s2c_pkt_num;
//    private int c2s_byte_num;
//    private int s2c_byte_num;

    public String getApp_label() {
        return app_label;
    }

    public void setApp_label(String app_label) {
        this.app_label = app_label;
    }

    public int getProto_id() {
        return proto_id;
    }

    public void setProto_id(int proto_id) {
        this.proto_id = proto_id;
    }

    public int getApp_id() {
        return app_id;
    }

    public void setApp_id(int app_id) {
        this.app_id = app_id;
    }

    public int getOs_id() {
        return os_id;
    }

    public void setOs_id(int os_id) {
        this.os_id = os_id;
    }

    public int getBs_id() {
        return bs_id;
    }

    public void setBs_id(int bs_id) {
        this.bs_id = bs_id;
    }

    public int getWeb_id() {
        return web_id;
    }

    public void setWeb_id(int web_id) {
        this.web_id = web_id;
    }

    public int getBehav_id() {
        return behav_id;
    }

    public void setBehav_id(int behav_id) {
        this.behav_id = behav_id;
    }

    public long getC2s_pkt_num() {
        return c2s_pkt_num;
    }

    public void setC2s_pkt_num(long c2s_pkt_num) {
        this.c2s_pkt_num = c2s_pkt_num;
    }

    public long getS2c_pkt_num() {
        return s2c_pkt_num;
    }

    public void setS2c_pkt_num(long s2c_pkt_num) {
        this.s2c_pkt_num = s2c_pkt_num;
    }

    public long getC2s_byte_num() {
        return c2s_byte_num;
    }

    public void setC2s_byte_num(long c2s_byte_num) {
        this.c2s_byte_num = c2s_byte_num;
    }

    public long getS2c_byte_num() {
        return s2c_byte_num;
    }

    public void setS2c_byte_num(long s2c_byte_num) {
        this.s2c_byte_num = s2c_byte_num;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public int getLastmtime() {
        return lastmtime;
    }

    public void setLastmtime(int lastmtime) {
        this.lastmtime = lastmtime;
    }

    @Override
    public String toString() {
        return field() +
                app_label + "\t" +
                c2s_pkt_num + "\t" +
                s2c_pkt_num + "\t" +
                c2s_byte_num + "\t" +
                s2c_byte_num + "\t" +
                create_time + "\t" +
                lastmtime + "\n";
    }
}
