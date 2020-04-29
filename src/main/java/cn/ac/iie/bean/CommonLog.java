package cn.ac.iie.bean;

/**
 * 公共字段类
 *
 * @author Administrator
 */
public class CommonLog {

    private int cfg_id;//建表,需要,即配置ID,但数据中没有

    private int found_time;//建表,必需
    private int recv_time;//建表,必需

    private String trans_proto;//建表,必需,即PROTOCOL

    private String d_ip;//建表,必需,即SERVER_IP,服务端IP
    private String s_ip;//建表,必需,即CLIENT_IP,客户端IP
    private int d_port;//建表,必需,即SERVER_PORT,服务端端口
    private int s_port;//建表,必需,即CLIENT_PORT,客户端端口

    private int entrance_id;//建表,必需
    private String cap_ip;//建表,必需

    private String server_locate;//建表,方法获取
    private String client_locate;//建表,方法获取

    //以下为无法接收到的字段或者可以直接置空的字段(即非必要字段)
    private long over_id;//建表
    private String nest_protocol;//建表
    private String nest_server_ip;//建表
    private String nest_client_ip;//建表
    private int nest_server_port;//建表
    private int nest_client_port;//建表
    private int service;//建表,已有
    private String scene_file;//建表
    private String injected_pkt_file;//建表
    private String nest_addr_list;//建表
    private int action;//建表

    private String user_region;//20190507,需要建表,获取reset标记字段
    private int stream_dir;//20190507,需要建表,获取reset标记字段
    private String addr_list;//20190507,需要建表,获取reset标记字段

    //表中没有,即未加入建表的,但数据中有;//20190507旧表新增字段
    private int addr_type;//未建表,目前数据中ipv6特有;//20190507,需要建表,获取reset标记字段


    //user_region内含字段
    private String killed;//20190507新增,killed=0表示未rst;killed=1表示rst过了;
    private String thread;//20190507新增
    private String index;//20190507新增
    private String hash;//20190507新增


//    private int device_id;
//    private int link_id;
//    private int encap_type;
//    private int direction;
//    private String inner_smac;
//    private String inner_dmac;

//    private String s_asn;
//    private String d_asn;
//    private String s_subscribe_id;
//    private String d_subscribe_id;


    public int getCfg_id() {
        return cfg_id;
    }

    public void setCfg_id(int cfg_id) {
        this.cfg_id = cfg_id;
    }

    public int getFound_time() {
        return found_time;
    }

    public void setFound_time(int found_time) {
        this.found_time = found_time;
    }

    public int getRecv_time() {
        return recv_time;
    }

    public void setRecv_time(int recv_time) {
        this.recv_time = recv_time;
    }

    public String getTrans_proto() {
        return trans_proto;
    }

    public void setTrans_proto(String trans_proto) {
        this.trans_proto = trans_proto;
    }

    public String getD_ip() {
        return d_ip;
    }

    public void setD_ip(String d_ip) {
        this.d_ip = d_ip;
    }

    public String getS_ip() {
        return s_ip;
    }

    public void setS_ip(String s_ip) {
        this.s_ip = s_ip;
    }

    public int getD_port() {
        return d_port;
    }

    public void setD_port(int d_port) {
        this.d_port = d_port;
    }

    public int getS_port() {
        return s_port;
    }

    public void setS_port(int s_port) {
        this.s_port = s_port;
    }

    public int getEntrance_id() {
        return entrance_id;
    }

    public void setEntrance_id(int entrance_id) {
        this.entrance_id = entrance_id;
    }

    public String getCap_ip() {
        return cap_ip;
    }

    public void setCap_ip(String cap_ip) {
        this.cap_ip = cap_ip;
    }

    public String getServer_locate() {
        return server_locate;
    }

    public void setServer_locate(String server_locate) {
        this.server_locate = server_locate;
    }

    public String getClient_locate() {
        return client_locate;
    }

    public void setClient_locate(String client_locate) {
        this.client_locate = client_locate;
    }

    public long getOver_id() {
        return over_id;
    }

    public void setOver_id(long over_id) {
        this.over_id = over_id;
    }

    public String getNest_protocol() {
        return nest_protocol;
    }

    public void setNest_protocol(String nest_protocol) {
        this.nest_protocol = nest_protocol;
    }

    public String getNest_server_ip() {
        return nest_server_ip;
    }

    public void setNest_server_ip(String nest_server_ip) {
        this.nest_server_ip = nest_server_ip;
    }

    public String getNest_client_ip() {
        return nest_client_ip;
    }

    public void setNest_client_ip(String nest_client_ip) {
        this.nest_client_ip = nest_client_ip;
    }

    public int getNest_server_port() {
        return nest_server_port;
    }

    public void setNest_server_port(int nest_server_port) {
        this.nest_server_port = nest_server_port;
    }

    public int getNest_client_port() {
        return nest_client_port;
    }

    public void setNest_client_port(int nest_client_port) {
        this.nest_client_port = nest_client_port;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public String getScene_file() {
        return scene_file;
    }

    public void setScene_file(String scene_file) {
        this.scene_file = scene_file;
    }

    public String getInjected_pkt_file() {
        return injected_pkt_file;
    }

    public void setInjected_pkt_file(String injected_pkt_file) {
        this.injected_pkt_file = injected_pkt_file;
    }

    public String getNest_addr_list() {
        return nest_addr_list;
    }

    public void setNest_addr_list(String nest_addr_list) {
        this.nest_addr_list = nest_addr_list;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAddr_type() {
        return addr_type;
    }

    public void setAddr_type(int addr_type) {
        this.addr_type = addr_type;
    }

    public int getStream_dir() {
        return stream_dir;
    }

    public void setStream_dir(int stream_dir) {
        this.stream_dir = stream_dir;
    }

    public String getAddr_list() {
        return addr_list;
    }

    public void setAddr_list(String addr_list) {
        this.addr_list = addr_list;
    }

    public String getUser_region() {
        return user_region;
    }

    public void setUser_region(String user_region) {
        this.user_region = user_region;
    }

    public String getKilled() {
        return killed;
    }

    public void setKilled(String killed) {
        this.killed = killed;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return cfg_id + "#" +
                found_time + "#" +
                recv_time + "#" +
                trans_proto + "#" +
                d_ip + "#" +
                s_ip + "#" +
                d_port + "#" +
                s_port + "#" +
                entrance_id + "#" +
                cap_ip + "#" +
                server_locate + "#" +
                client_locate + "#" +
                over_id + "#" +
                nest_protocol + "#" +
                nest_server_ip + "#" +
                nest_client_ip + "#" +
                nest_server_port + "#" +
                nest_client_port + "#" +
                service + "#" +
                scene_file + "#" +
                injected_pkt_file + "#" +
                nest_addr_list + "#" +
                action + "##" +
                addr_type + "##" +
                stream_dir + "##" +
                addr_list + "##" +
                user_region
//                device_id + "#" +
//                link_id + "#" +
//                encap_type + "#" +
//                direction + "#" +
//                inner_smac + "#" +
//                inner_dmac + "#" +
//                s_asn + "#" +
//                d_asn + "#" +
//                s_subscribe_id + "#" +
//                d_subscribe_id + "#" +
                ;
    }

    //获取字段打印
    public String field() {
        return cfg_id + "#" +
                found_time + "#" +
                recv_time + "#" +
                trans_proto + "#" +
                d_ip + "#" +
                s_ip + "#" +
                d_port + "#" +
                s_port + "#" +
                entrance_id + "#" +
                cap_ip + "#" +
                server_locate + "#" +
                client_locate + "#" +
                over_id + "#" +
                nest_protocol + "#" +
                nest_server_ip + "#" +
                nest_client_ip + "#" +
                nest_server_port + "#" +
                nest_client_port + "#" +
                service + "#" +
                scene_file + "#" +
                injected_pkt_file + "#" +
                nest_addr_list + "#" +
                action + "##" +
                addr_type + "##" +
                stream_dir + "##" +
                addr_list + "##" +
                user_region
//                device_id + "#" +
//                link_id + "#" +
//                encap_type + "#" +
//                direction + "#" +
//                inner_smac + "#" +
//                inner_dmac + "#" +
//                s_asn + "#" +
//                d_asn + "#" +
//                s_subscribe_id + "#" +
//                d_subscribe_id + "#" +
                ;
    }
}
