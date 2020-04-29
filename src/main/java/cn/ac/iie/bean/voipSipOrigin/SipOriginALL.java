package cn.ac.iie.bean.voipSipOrigin;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * SIP的原始日志扩展表
 */
public class SipOriginALL {

    @JSONField(name = "Call_ID")
    private String Call_ID;//Call-ID
    @JSONField(name = "CLJ_IP")
    private String CLJ_IP;
    @JSONField(name = "Found_Time")
    private long Found_Time;
    @JSONField(name = "SRC_IP")
    private String SRC_IP;
    @JSONField(name = "SRC_LOCATION_NATION")
    private String SRC_LOCATION_NATION;
    @JSONField(name = "SRC_LOCATION_NATION_CODE")
    private String SRC_LOCATION_NATION_CODE;
    @JSONField(name = "SRC_LOCATION_REGION")
    private String SRC_LOCATION_REGION;
    @JSONField(name = "SRC_PORT")
    private int SRC_PORT;
    @JSONField(name = "DST_IP")
    private String DST_IP;
    @JSONField(name = "IP_TYPE")
    private String IP_TYPE;
    @JSONField(name = "DST_LOCATION_NATION")
    private String DST_LOCATION_NATION;
    @JSONField(name = "DST_LOCATION_NATION_CODE")
    private String DST_LOCATION_NATION_CODE;
    @JSONField(name = "DST_LOCATION_REGION")
    private String DST_LOCATION_REGION;
    @JSONField(name = "DST_PORT")
    private int DST_PORT;
    @JSONField(name = "Method")
    private String Method;
    @JSONField(name = "Request_URI")
    private String Request_URI;
    @JSONField(name = "User_name")
    private String User_name;
    @JSONField(name = "Service_domain")
    private String Service_domain;
    @JSONField(name = "Service_domain_valid")
    private String Service_domain_valid;
    @JSONField(name = "Res_stat")
    private String Res_stat;
    @JSONField(name = "Res_stat_format")
    private String Res_stat_format;
    @JSONField(name = "From")
    private String From;
    @JSONField(name = "From_Nickname")
    private String From_Nickname;
    @JSONField(name = "From_usr_name")
    private String From_usr_name;
    @JSONField(name = "From_ser_domain")
    private String From_ser_domain;

    @JSONField(name = "From_ser_domain_valid")
    private String From_ser_domain_valid;//新增是否替换

    @JSONField(name = "From_tag")
    private String From_tag;
    @JSONField(name = "To")
    private String To;
    @JSONField(name = "To_Nickname")
    private String To_Nickname;
    @JSONField(name = "To_usr_name")
    private String To_usr_name;
    @JSONField(name = "To_ser_domain")
    private String To_ser_domain;

    @JSONField(name = "To_ser_domain_valid")
    private String To_ser_domain_valid;//新增是否替换

    @JSONField(name = "To_tag")
    private String To_tag;
    @JSONField(name = "Cseq")
    private String Cseq;
    @JSONField(name = "Cseq_method")
    private String Cseq_method;
    @JSONField(name = "User_Agent")
    private String User_Agent;//User-Agent
    @JSONField(name = "Device_type")
    private String Device_type;
    @JSONField(name = "Max_Forwards")
    private String Max_Forwards;//Max-Forwards
    @JSONField(name = "Server")
    private String Server;
    @JSONField(name = "Server_type")
    private String Server_type;
    @JSONField(name = "Req_Via")
    private String[] Req_Via;

    @JSONField(name = "Req_Via_Json")
    private String Req_Via_Json;//新增字段

    @JSONField(name = "Req_Contact")
    private String Req_Contact;
    @JSONField(name = "Req_Contact_Nickname")
    private String Req_Contact_Nickname;
    @JSONField(name = "Req_Contact_usr_name")
    private String Req_Contact_usr_name;
    @JSONField(name = "Req_Contact_ser_domain")
    private String Req_Contact_ser_domain;

    @JSONField(name = "Req_ser_domain_valid")
    private String Req_ser_domain_valid;//新增是否替换

    @JSONField(name = "Req_Record_Route")
    private String[] Req_Record_Route;//Req_Record-Route

    @JSONField(name = "Req_Record_Route_Json")
    private String Req_Record_Route_Json;//新增字段

    @JSONField(name = "Req_Route")
    private String[] Req_Route;

    @JSONField(name = "Req_Route_Json")
    private String Req_Route_Json;//新增字段

    @JSONField(name = "Req_Expires")
    private String Req_Expires;
    @JSONField(name = "Req_Others")
    private String Req_Others;
    @JSONField(name = "Req_Content_Type")
    private String Req_Content_Type;//Req_Content-Type
    @JSONField(name = "Req_Content")
    private String Req_Content;
    @JSONField(name = "Res_Via")
    private String[] Res_Via;

    @JSONField(name = "Res_Via_Json")
    private String Res_Via_Json;//新增字段

    @JSONField(name = "Res_Contact")
    private String Res_Contact;
    @JSONField(name = "Res_Contact_Nickname")
    private String Res_Contact_Nickname;
    @JSONField(name = "Res_Contact_usr_name")
    private String Res_Contact_usr_name;
    @JSONField(name = "Res_Contact_ser_domain")
    private String Res_Contact_ser_domain;

    @JSONField(name = "Res_ser_domain_valid")
    private String Res_ser_domain_valid;//新增是否替换

    @JSONField(name = "Res_Record_Route")
    private String[] Res_Record_Route;//Res_Record-Route

    @JSONField(name = "Res_Record_Route_Json")
    private String Res_Record_Route_Json;//新增字段

    @JSONField(name = "Res_Route")
    private String[] Res_Route;

    @JSONField(name = "Res_Route_Json")
    private String Res_Route_Json;//新增字段

    @JSONField(name = "Res_Expires")
    private String Res_Expires;
    @JSONField(name = "Res_Others")
    private String Res_Others;
    @JSONField(name = "Res_Content_Type")
    private String Res_Content_Type;//Res_Content-Type
    @JSONField(name = "Res_Content")
    private String Res_Content;

    @JSONField(name = "Req_coding")
    private String Req_coding;
    @JSONField(name = "Res_coding")
    private String Res_coding;

    private long stat_time;//新增时间字段


    public String getCall_ID() {
        return Call_ID;
    }

    public void setCall_ID(String call_ID) {
        Call_ID = call_ID;
    }

    public String getCLJ_IP() {
        return CLJ_IP;
    }

    public void setCLJ_IP(String CLJ_IP) {
        this.CLJ_IP = CLJ_IP;
    }

    public long getFound_Time() {
        return Found_Time;
    }

    public void setFound_Time(long found_Time) {
        Found_Time = found_Time;
    }

    public String getSRC_IP() {
        return SRC_IP;
    }

    public void setSRC_IP(String SRC_IP) {
        this.SRC_IP = SRC_IP;
    }

    public String getSRC_LOCATION_NATION() {
        return SRC_LOCATION_NATION;
    }

    public void setSRC_LOCATION_NATION(String SRC_LOCATION_NATION) {
        this.SRC_LOCATION_NATION = SRC_LOCATION_NATION;
    }

    public String getSRC_LOCATION_NATION_CODE() {
        return SRC_LOCATION_NATION_CODE;
    }

    public void setSRC_LOCATION_NATION_CODE(String SRC_LOCATION_NATION_CODE) {
        this.SRC_LOCATION_NATION_CODE = SRC_LOCATION_NATION_CODE;
    }

    public String getSRC_LOCATION_REGION() {
        return SRC_LOCATION_REGION;
    }

    public void setSRC_LOCATION_REGION(String SRC_LOCATION_REGION) {
        this.SRC_LOCATION_REGION = SRC_LOCATION_REGION;
    }

    public int getSRC_PORT() {
        return SRC_PORT;
    }

    public void setSRC_PORT(int SRC_PORT) {
        this.SRC_PORT = SRC_PORT;
    }

    public String getDST_IP() {
        return DST_IP;
    }

    public void setDST_IP(String DST_IP) {
        this.DST_IP = DST_IP;
    }

    public String getIP_TYPE() {
        return IP_TYPE;
    }

    public void setIP_TYPE(String IP_TYPE) {
        this.IP_TYPE = IP_TYPE;
    }

    public String getDST_LOCATION_NATION() {
        return DST_LOCATION_NATION;
    }

    public void setDST_LOCATION_NATION(String DST_LOCATION_NATION) {
        this.DST_LOCATION_NATION = DST_LOCATION_NATION;
    }

    public String getDST_LOCATION_NATION_CODE() {
        return DST_LOCATION_NATION_CODE;
    }

    public void setDST_LOCATION_NATION_CODE(String DST_LOCATION_NATION_CODE) {
        this.DST_LOCATION_NATION_CODE = DST_LOCATION_NATION_CODE;
    }

    public String getDST_LOCATION_REGION() {
        return DST_LOCATION_REGION;
    }

    public void setDST_LOCATION_REGION(String DST_LOCATION_REGION) {
        this.DST_LOCATION_REGION = DST_LOCATION_REGION;
    }

    public int getDST_PORT() {
        return DST_PORT;
    }

    public void setDST_PORT(int DST_PORT) {
        this.DST_PORT = DST_PORT;
    }

    public String getMethod() {
        return Method;
    }

    public void setMethod(String method) {
        Method = method;
    }

    public String getRequest_URI() {
        return Request_URI;
    }

    public void setRequest_URI(String request_URI) {
        Request_URI = request_URI;
    }

    public String getUser_name() {
        return User_name;
    }

    public void setUser_name(String user_name) {
        User_name = user_name;
    }

    public String getService_domain() {
        return Service_domain;
    }

    public void setService_domain(String service_domain) {
        Service_domain = service_domain;
    }

    public String getService_domain_valid() {
        return Service_domain_valid;
    }

    public void setService_domain_valid(String service_domain_valid) {
        Service_domain_valid = service_domain_valid;
    }

    public String getRes_stat() {
        return Res_stat;
    }

    public void setRes_stat(String res_stat) {
        Res_stat = res_stat;
    }

    public String getRes_stat_format() {
        return Res_stat_format;
    }

    public void setRes_stat_format(String res_stat_format) {
        Res_stat_format = res_stat_format;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getFrom_Nickname() {
        return From_Nickname;
    }

    public void setFrom_Nickname(String from_Nickname) {
        From_Nickname = from_Nickname;
    }

    public String getFrom_usr_name() {
        return From_usr_name;
    }

    public void setFrom_usr_name(String from_usr_name) {
        From_usr_name = from_usr_name;
    }

    public String getFrom_ser_domain() {
        return From_ser_domain;
    }

    public void setFrom_ser_domain(String from_ser_domain) {
        From_ser_domain = from_ser_domain;
    }

    public String getFrom_ser_domain_valid() {
        return From_ser_domain_valid;
    }

    public void setFrom_ser_domain_valid(String from_ser_domain_valid) {
        From_ser_domain_valid = from_ser_domain_valid;
    }

    public String getFrom_tag() {
        return From_tag;
    }

    public void setFrom_tag(String from_tag) {
        From_tag = from_tag;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getTo_Nickname() {
        return To_Nickname;
    }

    public void setTo_Nickname(String to_Nickname) {
        To_Nickname = to_Nickname;
    }

    public String getTo_usr_name() {
        return To_usr_name;
    }

    public void setTo_usr_name(String to_usr_name) {
        To_usr_name = to_usr_name;
    }

    public String getTo_ser_domain() {
        return To_ser_domain;
    }

    public void setTo_ser_domain(String to_ser_domain) {
        To_ser_domain = to_ser_domain;
    }

    public String getTo_ser_domain_valid() {
        return To_ser_domain_valid;
    }

    public void setTo_ser_domain_valid(String to_ser_domain_valid) {
        To_ser_domain_valid = to_ser_domain_valid;
    }

    public String getTo_tag() {
        return To_tag;
    }

    public void setTo_tag(String to_tag) {
        To_tag = to_tag;
    }

    public String getCseq() {
        return Cseq;
    }

    public void setCseq(String cseq) {
        Cseq = cseq;
    }

    public String getCseq_method() {
        return Cseq_method;
    }

    public void setCseq_method(String cseq_method) {
        Cseq_method = cseq_method;
    }

    public String getUser_Agent() {
        return User_Agent;
    }

    public void setUser_Agent(String user_Agent) {
        User_Agent = user_Agent;
    }

    public String getDevice_type() {
        return Device_type;
    }

    public void setDevice_type(String device_type) {
        Device_type = device_type;
    }

    public String getMax_Forwards() {
        return Max_Forwards;
    }

    public void setMax_Forwards(String max_Forwards) {
        Max_Forwards = max_Forwards;
    }

    public String getServer() {
        return Server;
    }

    public void setServer(String server) {
        Server = server;
    }

    public String getServer_type() {
        return Server_type;
    }

    public void setServer_type(String server_type) {
        Server_type = server_type;
    }

    public String[] getReq_Via() {
        return Req_Via;
    }

    public void setReq_Via(String[] req_Via) {
        Req_Via = req_Via;
    }

    public String getReq_Via_Json() {
        return Req_Via_Json;
    }

    public void setReq_Via_Json(String req_Via_Json) {
        Req_Via_Json = req_Via_Json;
    }

    public String getReq_Contact() {
        return Req_Contact;
    }

    public void setReq_Contact(String req_Contact) {
        Req_Contact = req_Contact;
    }

    public String getReq_Contact_Nickname() {
        return Req_Contact_Nickname;
    }

    public void setReq_Contact_Nickname(String req_Contact_Nickname) {
        Req_Contact_Nickname = req_Contact_Nickname;
    }

    public String getReq_Contact_usr_name() {
        return Req_Contact_usr_name;
    }

    public void setReq_Contact_usr_name(String req_Contact_usr_name) {
        Req_Contact_usr_name = req_Contact_usr_name;
    }

    public String getReq_Contact_ser_domain() {
        return Req_Contact_ser_domain;
    }

    public void setReq_Contact_ser_domain(String req_Contact_ser_domain) {
        Req_Contact_ser_domain = req_Contact_ser_domain;
    }

    public String getReq_ser_domain_valid() {
        return Req_ser_domain_valid;
    }

    public void setReq_ser_domain_valid(String req_ser_domain_valid) {
        Req_ser_domain_valid = req_ser_domain_valid;
    }

    public String[] getReq_Record_Route() {
        return Req_Record_Route;
    }

    public void setReq_Record_Route(String[] req_Record_Route) {
        Req_Record_Route = req_Record_Route;
    }

    public String getReq_Record_Route_Json() {
        return Req_Record_Route_Json;
    }

    public void setReq_Record_Route_Json(String req_Record_Route_Json) {
        Req_Record_Route_Json = req_Record_Route_Json;
    }

    public String[] getReq_Route() {
        return Req_Route;
    }

    public void setReq_Route(String[] req_Route) {
        Req_Route = req_Route;
    }

    public String getReq_Route_Json() {
        return Req_Route_Json;
    }

    public void setReq_Route_Json(String req_Route_Json) {
        Req_Route_Json = req_Route_Json;
    }

    public String getReq_Expires() {
        return Req_Expires;
    }

    public void setReq_Expires(String req_Expires) {
        Req_Expires = req_Expires;
    }

    public String getReq_Others() {
        return Req_Others;
    }

    public void setReq_Others(String req_Others) {
        Req_Others = req_Others;
    }

    public String getReq_Content_Type() {
        return Req_Content_Type;
    }

    public void setReq_Content_Type(String req_Content_Type) {
        Req_Content_Type = req_Content_Type;
    }

    public String getReq_Content() {
        return Req_Content;
    }

    public void setReq_Content(String req_Content) {
        Req_Content = req_Content;
    }

    public String[] getRes_Via() {
        return Res_Via;
    }

    public void setRes_Via(String[] res_Via) {
        Res_Via = res_Via;
    }

    public String getRes_Via_Json() {
        return Res_Via_Json;
    }

    public void setRes_Via_Json(String res_Via_Json) {
        Res_Via_Json = res_Via_Json;
    }

    public String getRes_Contact() {
        return Res_Contact;
    }

    public void setRes_Contact(String res_Contact) {
        Res_Contact = res_Contact;
    }

    public String getRes_Contact_Nickname() {
        return Res_Contact_Nickname;
    }

    public void setRes_Contact_Nickname(String res_Contact_Nickname) {
        Res_Contact_Nickname = res_Contact_Nickname;
    }

    public String getRes_Contact_usr_name() {
        return Res_Contact_usr_name;
    }

    public void setRes_Contact_usr_name(String res_Contact_usr_name) {
        Res_Contact_usr_name = res_Contact_usr_name;
    }

    public String getRes_Contact_ser_domain() {
        return Res_Contact_ser_domain;
    }

    public void setRes_Contact_ser_domain(String res_Contact_ser_domain) {
        Res_Contact_ser_domain = res_Contact_ser_domain;
    }

    public String getRes_ser_domain_valid() {
        return Res_ser_domain_valid;
    }

    public void setRes_ser_domain_valid(String res_ser_domain_valid) {
        Res_ser_domain_valid = res_ser_domain_valid;
    }

    public String[] getRes_Record_Route() {
        return Res_Record_Route;
    }

    public void setRes_Record_Route(String[] res_Record_Route) {
        Res_Record_Route = res_Record_Route;
    }

    public String getRes_Record_Route_Json() {
        return Res_Record_Route_Json;
    }

    public void setRes_Record_Route_Json(String res_Record_Route_Json) {
        Res_Record_Route_Json = res_Record_Route_Json;
    }

    public String[] getRes_Route() {
        return Res_Route;
    }

    public void setRes_Route(String[] res_Route) {
        Res_Route = res_Route;
    }

    public String getRes_Route_Json() {
        return Res_Route_Json;
    }

    public void setRes_Route_Json(String res_Route_Json) {
        Res_Route_Json = res_Route_Json;
    }

    public String getRes_Expires() {
        return Res_Expires;
    }

    public void setRes_Expires(String res_Expires) {
        Res_Expires = res_Expires;
    }

    public String getRes_Others() {
        return Res_Others;
    }

    public void setRes_Others(String res_Others) {
        Res_Others = res_Others;
    }

    public String getRes_Content_Type() {
        return Res_Content_Type;
    }

    public void setRes_Content_Type(String res_Content_Type) {
        Res_Content_Type = res_Content_Type;
    }

    public String getRes_Content() {
        return Res_Content;
    }

    public void setRes_Content(String res_Content) {
        Res_Content = res_Content;
    }

    public String getReq_coding() {
        return Req_coding;
    }

    public void setReq_coding(String req_coding) {
        Req_coding = req_coding;
    }

    public String getRes_coding() {
        return Res_coding;
    }

    public void setRes_coding(String res_coding) {
        Res_coding = res_coding;
    }

    public long getStat_time() {
        return stat_time;
    }

    public void setStat_time(long stat_time) {
        this.stat_time = stat_time;
    }
}
